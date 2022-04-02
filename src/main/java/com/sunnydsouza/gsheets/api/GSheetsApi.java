package com.sunnydsouza.gsheets.api;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Basic wrapper over Google Sheets API, based on examples from
 * https://developers.google.com/sheets/api/quickstart/java Allows for basic CRUD operations on
 * Google Sheets
 *
 * @author sunnydsouza
 */
public class GSheetsApi {
  private static final String APPLICATION_NAME = "Simple Gsheets API wrapper by sunnydsouza";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";
  /**
   * Global instance of the scopes required by this quickstart. If modifying these scopes, delete
   * your previously saved tokens/ folder.
   */
  private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

  // Path to credentials to service account.Please refer
  // https://cloud.google.com/iam/docs/creating-managing-service-account-keys
  // Currently user is expected to create a service account credentials.json file and place it in
  // the /credentials/ folder of project root
  private static final String CREDENTIALS_FILE_PATH =
      System.getProperty("user.dir") + "/credentials/credentials.json";
  final Logger logger = LoggerFactory.getLogger(GSheetsApi.class);
  Sheets service;
  String spreadsheetId;

  private GSheetsApi(String gsheetsId) throws GeneralSecurityException, IOException {
    this.spreadsheetId = gsheetsId;
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    GoogleCredentials googleCredentials;
    try (InputStream credentialsStream = new FileInputStream(CREDENTIALS_FILE_PATH)) {
      googleCredentials = GoogleCredentials.fromStream(credentialsStream).createScoped(SCOPES);
    }
    service =
        new Sheets.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(googleCredentials))
            .setApplicationName(APPLICATION_NAME)
            .build();
  }

  /**
   * Ctor for GSheetsApi
   *
   * @param gsheetsId - the spreadsheet id
   * @return
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public static GSheetsApi spreadsheet(String gsheetsId)
      throws GeneralSecurityException, IOException {

    return new GSheetsApi(gsheetsId);
  }

  /**
   * Gets the values of a Google sheet for a given sheet range
   *
   * @param range The range of the sheet
   * @return result rows in form of list of list of objects
   * @throws IOException
   */
  private List<List<Object>> readSheetValuesRaw(String range) throws IOException {

    ValueRange response = service.spreadsheets().values().get(this.spreadsheetId, range).execute();
    List<List<Object>> values = response.getValues();
    if (values == null || values.isEmpty()) {
      return null;
    } else {
      return values;
    }
  }

  /**
   * Gets the values of a Google sheet for a given sheet range in the form of List<GRow> records
   *
   * @param range The range of the sheet
   * @param header - if true, first row is assumed to be header
   * @return result rows in form of list of {@link GRow} records
   * @throws IOException
   */
  public List<GRow> readSheetValues(String range, boolean header) throws IOException {
    List<List<Object>> sheetValues = readSheetValuesRaw(range);
    List<Object> tableHeader = getTableHeaders(sheetValues);
    List<List<Object>> tableData = getTableData(sheetValues);
    List<GRow> gRows = null;
    if (header) {
      AtomicInteger rowIndex = new AtomicInteger(2); // 1st row is header

      gRows =
          tableData.stream()
              .map(l -> transformToColValMap(tableHeader, l))
              .map(m -> GRow.newRow(rowIndex.getAndIncrement(), m))
              .collect(Collectors.toCollection(LinkedList::new));
    } else {
      AtomicInteger rowIndex = new AtomicInteger(1);
      gRows =
          sheetValues.stream()
              .map(m -> GRow.newRow(rowIndex.getAndIncrement(), m))
              .collect(Collectors.toCollection(LinkedList::new));
    }
    return gRows;
  }

  /**
   * Gets the values of a Google sheet for a given sheet range in the form of List<GRow> records
   * (assumes first row is header)
   *
   * @param range
   * @return
   * @throws IOException
   */
  public List<GRow> readSheetValues(String range) throws IOException {
    return readSheetValues(range, true);
  }

  /**
   * Helper method to create a map of column name and value using the table header and data. Used in
   * {@link GSheetsApi#readSheetValues(String)}
   *
   * @param tableHeader
   * @param row
   * @return
   */
  private Map<String, String> transformToColValMap(List<Object> tableHeader, List<Object> row) {
    Map<String, String> colMap = new LinkedHashMap<>();
    int i = 0;
    for (Object eachColHeader : tableHeader) {
      if (i < row.size()) colMap.put((String) eachColHeader, (String) row.get(i));
      i++;
    }
    return colMap;
  }

  /**
   * Append rows to a google sheet range
   *
   * @param range the range to append to
   * @param rows the rows to append
   * @throws IOException
   */
  public void appendRows(String range, List<GRow> rows) throws IOException {
    List<List<Object>> values = new LinkedList<>();
    rows.forEach(r -> values.add(r.toListObject()));
    ValueRange body = new ValueRange().setMajorDimension("ROWS").setValues(values);
    AppendValuesResponse result =
        service
            .spreadsheets()
            .values()
            .append(spreadsheetId, range, body)
            .setInsertDataOption("INSERT_ROWS")
            .setValueInputOption("USER_ENTERED")
            .execute();
    logger.info("{} cells appended.", result.getUpdates().getUpdatedCells());
  }

  /**
   * Insert rows before a given row range ex: if the range is given as "A2:C2", then the rows will
   * be inserted BEFORE row 2
   *
   * @param sheetId he sheetId in the spreadsheet. The sheetId can be found in the url of the sheet
   *     eg: https://docs.google.com/spreadsheets/d/XXXX/edit#gid=YYYY. Then YYYY is the sheetId
   * @param range the range (row) used as reference for inserting the rows
   * @param rows the rows to insert
   * @throws IOException if there is an error
   */
  public void insertRowsBefore(int sheetId, String range, List<GRow> rows) throws IOException {

    GSheetRange gSheetRange = inferRange(range);

    // Create empty rows before the range so the rows can be inserted
    insertEmptyRow(
        sheetId,
        gSheetRange.getRangeStartRow() - 1,
        gSheetRange.getRangeStartRow() - 1 + rows.size());

    // Prepare the range for the rows to be inserted
    GSheetRange insertRange =
        new GSheetRange(
            gSheetRange.getSheetName(),
            gSheetRange.getRangeStart(),
            gSheetRange.getRangeStartRow(),
            gSheetRange.getRangeEnd(),
            gSheetRange.getRangeEndRow() + rows.size() - 1);

    // insert the rows at given range
    List<List<Object>> values = new LinkedList<>();
    rows.forEach(r -> values.add(r.toListObject()));

    ValueRange body = new ValueRange().setMajorDimension("ROWS").setValues(values);
    UpdateValuesResponse result =
        service
            .spreadsheets()
            .values()
            .update(spreadsheetId, insertRange.toString(), body)
            .setValueInputOption("USER_ENTERED")
            .execute();
    logger.info("{} cells appended.", result.getUpdatedCells());
  }

  /**
   * Insert rows after a given row range ex: if the range is given as "A2:C2", then the rows will be
   * inserted AFTER row 2
   *
   * @param sheetId the sheetId in the spreadsheet. The sheetId can be found in the url of the sheet
   *     eg: https://docs.google.com/spreadsheets/d/XXXX/edit#gid=YYYY. Then YYYY is the sheetId
   * @param range the range (row) used as reference for inserting the rows
   * @param rows the rows to insert
   * @throws IOException if the sheetId is not found
   */
  public void insertRowsAfter(int sheetId, String range, List<GRow> rows) throws IOException {
    GSheetRange gSheetRange = inferRange(range);

    // Create empty rows before the range so the rows can be inserted
    insertEmptyRow(
        sheetId, gSheetRange.getRangeStartRow(), gSheetRange.getRangeStartRow() + rows.size());

    // Prepare the range for the rows to be inserted
    GSheetRange insertRange =
        new GSheetRange(
            gSheetRange.getSheetName(),
            gSheetRange.getRangeStart(),
            gSheetRange.getRangeStartRow() + 1,
            gSheetRange.getRangeEnd(),
            gSheetRange.getRangeEndRow() + rows.size());

    // insert the rows at given range
    List<List<Object>> values = new LinkedList<>();
    rows.forEach(r -> values.add(r.toListObject()));

    ValueRange body = new ValueRange().setMajorDimension("ROWS").setValues(values);
    UpdateValuesResponse result =
        service
            .spreadsheets()
            .values()
            .update(spreadsheetId, insertRange.toString(), body)
            .setValueInputOption("USER_ENTERED")
            .execute();
    logger.info("{} cells appended.", result.getUpdatedCells());
  }

  /**
   * Helper function to insert empty rows in the spreadsheet
   *
   * @param sheetId the sheetId in the spreadsheet. The sheetId can be found in the url of the sheet
   *     eg: https://docs.google.com/spreadsheets/d/XXXX/edit#gid=YYYY. Then YYYY is the sheetId
   * @param startRowIndex the start row index
   * @param endRowIndex the end row index
   * @throws IOException if the request fails
   */
  public void insertEmptyRow(int sheetId, int startRowIndex, int endRowIndex) throws IOException {
    BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();

    List<Request> requests = new ArrayList<>();
    Request request = new Request();
    request.setInsertDimension(
        new InsertDimensionRequest()
            .setRange(
                new DimensionRange()
                    .setSheetId(sheetId)
                    .setDimension("ROWS")
                    .setStartIndex(startRowIndex)
                    .setEndIndex(endRowIndex)));

    // Update row request
    requests.add(request);
    requestBody.setRequests(requests);

    Sheets.Spreadsheets.BatchUpdate insertRequest =
        service.spreadsheets().batchUpdate(spreadsheetId, requestBody);

    BatchUpdateSpreadsheetResponse insertResponse = insertRequest.execute();
    logger.info("{} rows inserted.", insertResponse.getReplies().size());
  }

  /**
   * Delete rows in the spreadsheet
   *
   * @param sheetId the sheetId in the spreadsheet. The sheetId can be found in the url of the sheet
   *     eg: https://docs.google.com/spreadsheets/d/XXXX/edit#gid=YYYY. Then YYYY is the sheetId
   * @param rowsToBeDeleted the rows to be deleted
   * @throws IOException
   */
  public void deleteRows(int sheetId, List<Integer> rowsToBeDeleted) throws IOException {

    List<Request> requests = new ArrayList<>();
    if (rowsToBeDeleted.size() > 0) {

      BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
      for (int i = rowsToBeDeleted.size() - 1;
          i >= 0;
          i--) { // Reverse loop while deleting so that wrong index doesn't get deleted
        Request request = new Request();
        request.setDeleteDimension(
            new DeleteDimensionRequest()
                .setRange(
                    new DimensionRange()
                        .setSheetId(sheetId)
                        .setDimension("ROWS")
                        .setStartIndex(rowsToBeDeleted.get(i) - 1)
                        .setEndIndex(rowsToBeDeleted.get(i))));
        requests.add(request);
      }
      requestBody.setRequests(requests);

      Sheets.Spreadsheets.BatchUpdate deleteRequest =
          service.spreadsheets().batchUpdate(spreadsheetId, requestBody);

      BatchUpdateSpreadsheetResponse deleteResponse = deleteRequest.execute();

      logger.info("{} rows deleted.", deleteResponse.getReplies().size());
    }
  }

  /**
   * Helps find rows matching a set of Predicate conditions Assumes that the first row of range is a
   * header row
   *
   * @param range
   * @param conditions
   * @throws IOException
   * @return
   */
  @Deprecated
  public List<Integer> findRows(String range, GColumnFilters conditions) throws IOException {
    List<GRow> tableRows = readSheetValues(range);
    List<Integer> filteredRowNos =
        IntStream.range(0, tableRows.size())
            .filter(i -> (conditions.apply().test(tableRows.get(i))))
            .mapToObj(
                i -> i + 2) // +2 because the first row is the header row and index starts from 0
            .collect(Collectors.toCollection(LinkedList::new));

    return filteredRowNos;
  }

  /**
   * Helps find rows matching a set of Predicate conditions Assumes that the first row of range is a
   * header row
   *
   * @param range the range of the sheet
   * @param conditions the conditions to be applied {@see GColumnFilters}
   * @return the list of {@link GRow}
   * @throws IOException
   */
  public List<GRow> filterRows(String range, GColumnFilters conditions) throws IOException {
    return readSheetValues(range).stream()
        .filter(conditions.apply())
        .collect(Collectors.toCollection(LinkedList::new));
  }

  /**
   * Updates rows in a Google sheet within a given sheet range and those matching filter conditions
   *
   * @param range the range of the sheet within which the rows are to be updated
   * @param conditions only rows with matching conditions would be updated
   * @param updateMap a map of column name and values to be updated. Map contains keys with ONLY the
   *     column name which are to be updated
   * @throws IOException
   */
  public void updateRows(String range, GColumnFilters conditions, Map<String, String> updateMap)
      throws IOException {

    List<GRow> updatedRows =
        readSheetValues(range).stream()
            .filter(conditions.apply())
            .map(m -> m.updateCell(updateMap))
            .collect(Collectors.toCollection(LinkedList::new));
    logger.debug("Updated rows: {}", updatedRows);

    // Send to update only if there are rows to update :-)
    if (updatedRows.size() > 0) {
      List<ValueRange> data = new ArrayList<>();
      updatedRows.stream().forEach(r -> data.add(r.convertToValueRange(range)));
      logger.debug("After converting to ValueRange: {}", data);

      BatchUpdateValuesRequest body =
          new BatchUpdateValuesRequest().setValueInputOption("USER_ENTERED").setData(data);
      BatchUpdateValuesResponse result =
          service.spreadsheets().values().batchUpdate(spreadsheetId, body).execute();
      logger.info("{} cells updated.", result.getTotalUpdatedCells());

    } else {
      logger.info("No rows matching the filter condition. Hence NOTHING to update!");
    }
  }

  /**
   * Deletes rows in a Google sheet within a given sheet range and those matching filter conditions
   *
   * @param sheetId the sheetId in the spreadsheet. The sheetId can be found in the url of the sheet
   *     eg: https://docs.google.com/spreadsheets/d/XXXX/edit#gid=YYYY. Then YYYY is the sheetId
   * @param range the range of the sheet within which the rows are to be deleted
   * @param conditions only rows with matching conditions would be deleted
   * @return
   * @throws IOException
   */
  public void deleteRows(final int sheetId, String range, GColumnFilters conditions)
      throws IOException {

    List<Integer> rowToBeDeleted =
        readSheetValues(range).stream()
            .filter(conditions.apply())
            .map(GRow::getRowNo)
            .collect(Collectors.toCollection(LinkedList::new));
    logger.debug("Rows to be deleted: {}", rowToBeDeleted);
    deleteRows(sheetId, rowToBeDeleted);
  }

  /**
   * Returns the data (minus the header row, if any refern{@link GSheetsApi#readSheetValues(String,boolean)
   * @param sheetValues the original values list returned from {@link GSheetsApi#readSheetValues(String,boolean) }
   * @return the data (minus the header row, if any) as List<List<Object>>
   */
  private List<List<Object>> getTableData(List<List<Object>> sheetValues) {
    return sheetValues.subList(1, sheetValues.size());
  }

  /**
   * Returns the header row as List<Object>. Only used when header=true in {@link
   * GSheetsApi#readSheetValues(String,boolean) }
   *
   * @param sheetValues the original values list returned from {@link
   *     GSheetsApi#readSheetValues(String,boolean) }
   * @return the header row as List<Object>
   */
  private List<Object> getTableHeaders(List<List<Object>> sheetValues) {
    return sheetValues.get(0);
  }

  /**
   * Helper function used to infer a given Google sheet range ex: an VALID range example would be
   * "TestSheet!A1:B2" In this case, details of the range such as sheetId, sheetName, sheetRange,
   * sheetStartRow, sheetEndRow would be encapsulated within a {@link GSheetRange} object
   *
   * @param range a valid Google Sheet range
   * @return a {@link GSheetRange} object
   */
  GSheetRange inferRange(String range) {
    // Get the sheetName, rangeStart, rangeEnd, rageStartRow, rangeEndRow from range
    try {
      GSheetRange gSheetRange = null;
      Pattern pattern =
          Pattern.compile(
              "(?<sheetName>[A-Za-z0-9 _!]*)!(?<rangeStart>[A-Z]*)(?<rangeStartRow>[0-9]*):(?<rangeEnd>[A-Z]*)(?<rangeEndRow>[0-9]*)");
      Matcher matcher = pattern.matcher(range);
      if (matcher.find()) {
        gSheetRange =
            new GSheetRange(
                matcher.group("sheetName"),
                matcher.group("rangeStart"),
                matcher.group("rangeStartRow").equals("")
                    ? null
                    : Integer.parseInt(matcher.group("rangeStartRow")),
                matcher.group("rangeEnd"),
                matcher.group("rangeEndRow").equals("")
                    ? null
                    : Integer.parseInt(matcher.group("rangeEndRow")));
      }
      return gSheetRange;
    } catch (Exception e) {
      logger.error("Error while inferring range from {}", range);
      throw new RuntimeException(e);
    }
  }
}
