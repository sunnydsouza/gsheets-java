package com.sunnydsouza.gsheets.api;
/*
 * @created 30/03/2022 - 2:40 PM
 * @author sunnydsouza
 */

import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GRow {
  private String sheetName; // the range to which this sheet belongs
  private String parentRangeStart; // the range to which this sheet belongs
  private String parentRangeEnd; // the range to which this sheet belongs
  private int rowNum;
  private int colNum;
  private Map<String, String> rowColValMap = new LinkedHashMap<>();
  Logger logger = LoggerFactory.getLogger(GRow.class);

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

  public static GRow newRow(int rowNum) {
    return new GRow(rowNum);
  }

  public static GRow newRow(int rowNum, int colNum) {
    return new GRow(rowNum, colNum);
  }

  //  public static GRow newRow() {
  //    return new GRow();
  //  }

  public static GRow newRow(int rowNum, Map<String, String> m) {
    GRow gRow = new GRow(rowNum);
    gRow.rowColValMap = m;
    return gRow;
  }

  public static GRow newRow(int rowNum, List<Object> m) {
    GRow gRow = new GRow(rowNum);
    m.stream()
        .forEach(
            o -> {
              gRow.addCell((String) o);
            });
    return gRow;
  }

  public int getRowNo() {
    return rowNum;
  }

  public Map<String, String> getColValMap() {
    return rowColValMap;
  }

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

  public void updateCell(String columnName, String columnValue) {
    rowColValMap.put(
        columnName,
        columnValue); // overwrite the existing column name with new value. Could also be used to
    // add new column with new value, though not advisable(use addCell for that
    // purpose)
  }

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

  public List<Object> toListObject() {
    return rowColValMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
  }
  /**
   * Compare the current row with the given row
   *
   * @param o
   * @return 0 if the two GRow objects are equal, else -1. log statements will highlight the
   *     unequal/different columns
   */
  public int compareTo(GRow o) {
    int result = 0; // result could be 0 for equal, -1 for not equal

    // compare the number of columns
    if (rowColValMap.size() != o.rowColValMap.size()) {
      result = -1;
      logger.error(
          "FAILED MATCH.Column counts dont match. Expected:{} versus Actual:{}",
          rowColValMap.size(),
          o.rowColValMap.size());
      return result;
    } else {
      logger.debug(
          "SUCCESS MATCH.Column counts match. Expected:{} versus Actual:{} ",
          rowColValMap.size(),
          o.rowColValMap.size());
    }

    // compare the rowNums?
    if (rowNum != o.rowNum) {
      result = -1;
      logger.error(
          "FAILED MATCH.Row numbers dont match. Expected:{} versus Actual:{}", rowNum, o.rowNum);
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

  public GRow updateCell(Map<String, String> updateMap) {
    updateMap.entrySet().stream().forEach(e -> updateCell(e.getKey(), e.getValue()));
    return this;
  }

  public String getRowRange(String sheetRange) {
    GSheetRange pR = inferRange(sheetRange);
    return new GSheetRange(
            pR.getSheetName(), pR.getRangeStart(), getRowNo(), pR.getRangeEnd(), getRowNo())
        .toString();
  }
}

@Getter
@Setter
class GSheetRange {
  private String sheetName;
  private Integer rangeStartRow;
  private Integer rangeEndRow;
  private String rangeStart;
  private String rangeEnd;

  public GSheetRange(
      String sheetName,
      String rangeStart,
      Integer rangeStartRow,
      String rangeEnd,
      Integer rangeEndRow) {
    this.sheetName = sheetName;
    this.rangeStartRow = rangeStartRow;
    this.rangeEndRow = rangeEndRow;
    this.rangeStart = rangeStart;
    this.rangeEnd = rangeEnd;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(sheetName).append("!").append(rangeStart);
    if (rangeStartRow != null)
      sb.append(rangeStartRow); // rowNnumber is optional,so if not present, dont append
    sb.append(":");
    sb.append(rangeEnd);
    if (rangeEndRow != null)
      sb.append(rangeEndRow); // rowNnumber is optional,so if not present, dont append
    return sb.toString();
  }
}
