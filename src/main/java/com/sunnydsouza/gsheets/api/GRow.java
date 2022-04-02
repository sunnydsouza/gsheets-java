package com.sunnydsouza.gsheets.api;
/*
 * Class to represent a row in a Google Sheet
 * @created 30/03/2022 - 2:40 PM
 * @author sunnydsouza
 */

import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GRow {
  Logger logger = LoggerFactory.getLogger(GRow.class);
  private int rowNum;
  private int colNum;
  private Map<String, String> rowColValMap =
      new LinkedHashMap<>(); // holds the column name and value

  private GRow() {
    this.colNum = 0; // start of a new row. default colNum is 0
  }

  private GRow(int rowNum) {
    this.rowNum = rowNum; // initialize rowNum
    this.colNum = 0; // start of a new row. default colNum is 0
  }

  private GRow(int rowNum, int colNum) {
    this.rowNum = rowNum; // initialize rowNum
    this.colNum = colNum; // start of a new row. default colNum is 0
  }

  // Creates a new row
  public static GRow newRow() {
    return new GRow();
  }

  // Create a new row with the rowNum information
  public static GRow newRow(int rowNum) {
    return new GRow(rowNum);
  }

  // Create a new row with the rowNum and colNum information. TODO check if this is needed
  public static GRow newRow(int rowNum, int colNum) {
    return new GRow(rowNum, colNum);
  }

  //  public static GRow newRow() {
  //    return new GRow();
  //  }

  /**
   * Helps convert a Lis<Map<String,String> into a GRow when used in stream operations Refer usage
   * in {@link GSheetsApi#readSheetValues(String, boolean)}
   *
   * @param rowNum the row number
   * @param m the <Map<String,String>
   * @return
   */
  public static GRow newRow(int rowNum, Map<String, String> m) {
    GRow gRow = new GRow(rowNum);
    gRow.rowColValMap = m;
    return gRow;
  }

  /**
   * Helps convert a Lis<Object> into a GRow when used in stream operations Refer usage in {@link
   * GSheetsApi#readSheetValues(String, boolean)}
   *
   * @param rowNum
   * @param m
   * @return
   */
  public static GRow newRow(int rowNum, List<Object> m) {
    GRow gRow = new GRow(rowNum);
    m.stream()
        .forEach(
            o -> {
              gRow.addCell((String) o);
            });
    return gRow;
  }

  /**
   * @return rowNum represeting the current GRow
   */
  public int getRowNo() {
    return rowNum;
  }

  /**
   * @return the rowColValMap for the current GRow
   */
  public Map<String, String> getColValMap() {
    return rowColValMap;
  }

  /**
   * Adds a new column to the current GRow with the columnName and columnValue
   *
   * @param columnName the name of the column
   * @param columnValue the value of the column
   * @return a cell to the current GRow
   */
  public GRow addCell(String columnName, String columnValue) {
    rowColValMap.put(columnName, columnValue);
    return this;
  }

  /**
   * In case the columnName is not mentioned, the column is identified via a column number
   * (incremental)
   *
   * @param columnValue
   * @return a new column to the current GRow
   */
  public GRow addCell(String columnValue) {
    addCell(String.valueOf(colNum++), columnValue);
    return this;
  }

  /**
   * Every GRow with a valid rowNum and parent sheet range should be able to be represented as a
   * ValueRange object @Example: if the current GRow is 5th row and the parent sheet range is A1:F5,
   * then the ValueRange object would represent the 5th row of the sheet A5:F5
   *
   * @param parentRange requires the parent(encapsulating) range to which this row belongs to
   * @return a ValueRange object representing the current GRow
   */
  public ValueRange convertToValueRange(String parentRange) {

    GSheetRange pR = inferRange(parentRange);
    ValueRange valueRange = new ValueRange();
    valueRange
        .setRange(
            new GSheetRange(
                    pR.getSheetName(), pR.getRangeStart(), getRowNo(), pR.getRangeEnd(), getRowNo())
                .toString())
        .setValues(new ArrayList<>(Collections.singleton(toListObject())));

    return valueRange;
  }

  /**
   * Helper function used to infer a given Google sheet range @Example: an VALID range example would
   * be "TestSheet!A1:B2" In this case, details of the range such as sheetId, sheetName, sheetRange,
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

  /**
   * Converts the current GRow to a List of Objects (Used when calling the Google Sheets API)
   *
   * @return List<List<Object>> rows
   */
  public List<Object> toListObject() {
    return rowColValMap.values().stream().collect(Collectors.toList());
  }

  /**
   * Compare the current row with the expected row
   *
   * @param o The expected row to compare with
   * @return 0 if the two GRow objects are equal, else -1. log statements will highlight the
   *     unequal/different columns
   */
  public int compareTo(GRow o) {
    int result = 0; // result could be 0 for equal, -1 for not equal

    // compare the number of columns
    if (rowColValMap.size() != o.rowColValMap.size()) {
      result = -1;
      logger.error(
          "FAILED MATCH.Column counts dont match. Actual:{} versus Expected:{}",
          rowColValMap.size(),
          o.rowColValMap.size());
      return result;
    } else {
      logger.debug(
          "SUCCESS MATCH.Column counts match. Actual:{} versus Expected:{} ",
          rowColValMap.size(),
          o.rowColValMap.size());
    }

    // compare the rowNums?
    if (rowNum != o.rowNum) {
      result = -1;
      logger.error(
          "FAILED MATCH.Row numbers dont match. Actual:{} versus Expected:{}", rowNum, o.rowNum);
    }

    // Compare all values with the same key
    logger.debug("Comparing row number:{}", getRowNo());
    for (String key : rowColValMap.keySet()) {
      logger.debug("Comparing column:{}", key);
      String value = rowColValMap.getOrDefault(key, null);
      String otherValue = o.rowColValMap.getOrDefault(key, null);
      try {
        if (value.compareTo(otherValue) != 0) {
          logger.error(
              "Values not equal for column: {} Expected: {} .Actual: {}", key, value, otherValue);
          result = -1;
        } else {
          logger.debug(
              "Values equal for column: {} Expected: {} .Actual: {}", key, value, otherValue);
        }
      } catch (NullPointerException e) {
        logger.error(
            "Error comparing for column. Encountered NullPointerException: {} Expected: {} .Actual: {}",
            key,
            value,
            otherValue);
        result = -1;
      }
    }

    return result;
  }

  @Override
  public String toString() {
    return "GRow{"
        + "rowNum="
        + rowNum
        + ", colNum="
        + colNum
        + ", rowCellsMap="
        + rowColValMap
        + '}';
  }

  /**
   * Overwrite the existing column name with new value. Could also be used to add new column with
   * new value, though not advisable(use {@link GRow#addCell(String, String)} for that purpose)
   *
   * @param columnName
   * @param columnValue
   */
  public void updateCell(String columnName, String columnValue) {
    rowColValMap.put(columnName, columnValue);
  }

  /**
   * Overloaded method to update cells with a given map of columnName and values
   *
   * @param updateMap
   * @return
   */
  public GRow updateCell(Map<String, String> updateMap) {
    updateMap.entrySet().forEach(e -> updateCell(e.getKey(), e.getValue()));
    return this;
  }

  /**
   * Helper method to add a new cell to the row
   *
   * @param sheetRange
   * @return
   */
  public String getRowRange(String sheetRange) {
    GSheetRange pR = inferRange(sheetRange);
    return new GSheetRange(
            pR.getSheetName(), pR.getRangeStart(), getRowNo(), pR.getRangeEnd(), getRowNo())
        .toString();
  }
}
