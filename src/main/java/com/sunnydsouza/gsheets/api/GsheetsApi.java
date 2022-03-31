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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Basic wrapper over Google Sheets API, based on examples from
 * https://developers.google.com/sheets/api/quickstart/java Allows for basic CRUD operations on
 * Google Sheets
 *
 * @author sunnydsouza
 */
public class GsheetsApi {
  private static final String APPLICATION_NAME = "Simple Gsheets API wrapper by sunnydsouza";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";
  /**
   * Global instance of the scopes required by this quickstart. If modifying these scopes, delete
   * your previously saved tokens/ folder.
   */
  private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

  private static final String CREDENTIALS_FILE_PATH =
      System.getProperty("user.dir") + "/credentials/credentials.json";
  final Logger logger = LoggerFactory.getLogger(GsheetsApi.class);
  Sheets service;
  String spreadsheetId;

  private GsheetsApi(String gsheetsId) throws GeneralSecurityException, IOException {
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
   * Creates an authorized Credential object.
   *
   * @param HTTP_TRANSPORT The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  /*  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
      throws IOException {
    // Load client secrets.
    // InputStream in = GsheetsApi.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }*/

  /**
   * Build and return an authorized Sheets API client service.
   *
   * @return
   * @throws IOException
   */
  /*  private static Credential getServiceAccountCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {


  // Initializing the service:

      GoogleCredentials googleCredentials;
      try(InputStream credentialsStream = new FileInputStream(CREDENTIALS_FILE_PATH)) {
        googleCredentials = GoogleCredentials.fromStream(credentialsStream).createScoped(SCOPES);
      }
      service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(googleCredentials))
              .setApplicationName(APPLICATION_NAME)
              .build();
    }*/
  public static GsheetsApi spreadsheet(String gsheetsId)
      throws GeneralSecurityException, IOException {

    return new GsheetsApi(gsheetsId);
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
   * Reads the values of a sheet in a Google sheet and return as List<Map<String, String>>
   *
   * @param sheetRange The sheet range
   * @return list of result rows
   * @throws IOException
   */
  /*  private List<Map<String, String>> readSheetValuesAsListMap(String sheetRange) throws IOException {
    List<List<Object>> sheetValues = readSheetValuesRaw(sheetRange);
    List<Object> tableHeader = getTableHeaders(sheetValues);
    List<List<Object>> tableData = getTableData(sheetValues);

    List<Map<String, String>> tableDataMap =
        tableData.stream()
            .map(l -> transformToColValMap(tableHeader, l))
            .collect(Collectors.toCollection(LinkedList::new));

    return tableDataMap;
  }*/

  public List<GRow> readSheetValues(String sheetRange, boolean header) throws IOException {
    List<List<Object>> sheetValues = readSheetValuesRaw(sheetRange);
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

  public List<GRow> readSheetValues(String sheetRange) throws IOException {

    return readSheetValues(sheetRange, true);
  }

  /*  public List<GRow> readSheetValuesWithoutHeaders(String sheetRange) throws IOException {
    List<List<Object>> sheetValues = readSheetValuesRaw(sheetRange);

    AtomicInteger rowIndex = new AtomicInteger(1);
    List<GRow> gRows =
        sheetValues.stream()
            .map(m -> GRow.newRow(rowIndex.getAndIncrement(), m))
            .collect(Collectors.toCollection(LinkedList::new));

    return gRows;
  }*/

  /**
   * Helper method to create a map of column name and value using the table header and data. Used in
   * {@link GsheetsApi#readSheetValues(String)}
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
   * Appends rows to a Google sheet AFTER a given sheet range
   *
   * @param range The range of the sheet
   * @param rowValues The rows to be appended in form of List<Object>
   * @return updated row count
   * @throws IOException
   */
  /*  public void appendRow(String range, List<Object> row) throws IOException {

    List<List<Object>> values = Arrays.asList(row);

    ValueRange body = new ValueRange().setValues(values);
    AppendValuesResponse result =
        service
            .spreadsheets()
            .values()
            .append(spreadsheetId, range, body)
            .setValueInputOption("USER_ENTERED")
            .execute();
    logger.info("{} cells appended.", result.getUpdates().getUpdatedCells());
  }*/

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

  public void insertRows(int sheetId, String sheetRange, List<GRow> rows) throws IOException {
    //    List<List<Object>> values = new LinkedList<>();
    //    rows.forEach(r -> values.add(r.toListObject()));
    //    ValueRange body = new ValueRange().setMajorDimension("ROWS").setValues(values);

    // Create an InsertDimensionRequest object.

    for (int i = rows.size() - 1; i >= 0; i--) {
      insertEmptyRowBelow(sheetId, rows.get(i).getRowNo());

      List<List<Object>> values = new LinkedList<>();
      values.add(rows.get(i).toListObject());

      ValueRange body = new ValueRange().setMajorDimension("ROWS").setValues(values);
      UpdateValuesResponse result =
          service
              .spreadsheets()
              .values()
              .update(spreadsheetId, rows.get(i).getRowRange(sheetRange), body)
              .setValueInputOption("USER_ENTERED")
              .execute();
      logger.info("{} cells appended.", result.getUpdatedCells());
    }
  }

  public void insertEmptyRowBelow(int sheetId, int rowNo) throws IOException {
    BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();

    List<Request> requests = new ArrayList<>();
    Request request = new Request();
    request.setInsertDimension(
        new InsertDimensionRequest()
            .setRange(
                new DimensionRange()
                    .setSheetId(sheetId)
                    .setDimension("ROWS")
                    .setStartIndex(rowNo - 1)
                    .setEndIndex(rowNo)));

    // Update row request

    requests.add(request);
    requestBody.setRequests(requests);

    Sheets.Spreadsheets.BatchUpdate insertRequest =
        service.spreadsheets().batchUpdate(spreadsheetId, requestBody);

    BatchUpdateSpreadsheetResponse insertResponse = insertRequest.execute();
  }

  /**
   * Deletes a row in a Google sheet based on start and end row index
   *
   * @param spreadsheetId The spreadsheet id
   * @param sheetId The sheet id in the spreadsheet
   * @param startIndex The start row index
   * @param endIndex The end row index
   */
  /*private void deleteRow(String spreadsheetId, int sheetId, int startIndex, int endIndex) {

    BatchUpdateSpreadsheetRequest content = new BatchUpdateSpreadsheetRequest();

    DeleteDimensionRequest request =
        new DeleteDimensionRequest()
            .setRange(
                new DimensionRange()
                    .setSheetId(sheetId) // Sheet ID
                    .setDimension("ROWS")
                    .setStartIndex(startIndex)
                    .setEndIndex(endIndex));

    List<Request> requests = new ArrayList<>();
    requests.add(new Request().setDeleteDimension(request));
    content.setRequests(requests);
    System.out.println(content.getRequests());

    try {
      service.spreadsheets().batchUpdate(spreadsheetId, content).execute();
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("Error in deleting row");
    }
  }*/

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

      System.out.println(deleteResponse);
    }
  }

  /**
   * Helps find rows matching a set of Predicate conditions Assumes that the first row of sheetRange
   * is a header row
   *
   * @param sheetRange
   * @param conditions
   * @throws IOException
   * @return
   */
  @Deprecated
  public List<Integer> findRows(String sheetRange, GColumnFilters conditions) throws IOException {
    //    List<Map<String, String>> tableDataMap = readSheetValuesAsListMap(sheetRange);
    List<GRow> tableRows = readSheetValues(sheetRange);
    List<Integer> filteredRowNos =
        IntStream.range(0, tableRows.size())
            .filter(i -> (conditions.apply().test(tableRows.get(i))))
            .mapToObj(
                i -> i + 2) // +2 because the first row is the header row and index starts from 0
            .collect(Collectors.toCollection(LinkedList::new));

    return filteredRowNos;
  }

  public List<GRow> filterRows(String sheetRange, GColumnFilters conditions) throws IOException {

    return readSheetValues(sheetRange).stream()
        .filter(conditions.apply())
        //        .map(m -> GRow.newRow(m))
        .collect(Collectors.toCollection(LinkedList::new));
  }

  /**
   * Updates rows in a Google sheet within a given sheet range and those matching filter conditions
   *
   * @param sheetRange the range of the sheet within which the rows are to be updated
   * @param conditions only rows with matching conditions would be updated
   * @param updateMap a map of column name and values to be updated. Map contains keys with ONLY the
   *     column name which are to be updated
   * @throws IOException
   */
  public void updateRows(
      String sheetRange, GColumnFilters conditions, Map<String, String> updateMap)
      throws IOException {

    List<GRow> updatedRows =
        readSheetValues(sheetRange).stream()
            .filter(conditions.apply())
            .map(m -> m.updateCell(updateMap))
            .collect(Collectors.toCollection(LinkedList::new));
    logger.debug("Updated rows: {}", updatedRows);

    // Send to update only if there are rows to update :-)
    if (updatedRows.size() > 0) {
      List<ValueRange> data = new ArrayList<>();
      updatedRows.stream().forEach(r -> data.add(r.convertToValueRange(sheetRange)));
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
   * @param sheetRange the range of the sheet within which the rows are to be deleted
   * @param conditions only rows with matching conditions would be deleted
   * @return
   * @throws IOException
   */
  public void deleteRows(final int sheetId, String sheetRange, GColumnFilters conditions)
      throws IOException {

    List<Integer> rowToBeDeleted =
        readSheetValues(sheetRange).stream()
            .filter(conditions.apply())
            .map(GRow::getRowNo)
            .collect(Collectors.toCollection(LinkedList::new));
    logger.debug("Rows to be deleted: {}", rowToBeDeleted);
    deleteRows(sheetId, rowToBeDeleted);
  }

  private List<List<Object>> getTableData(List<List<Object>> sheetValues) {
    return sheetValues.subList(1, sheetValues.size());
  }

  private List<Object> getTableHeaders(List<List<Object>> sheetValues) {
    return sheetValues.get(0);
  }
}
