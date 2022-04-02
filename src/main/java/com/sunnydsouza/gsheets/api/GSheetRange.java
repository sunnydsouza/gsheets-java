package com.sunnydsouza.gsheets.api;

/* Class to represent a information about a google sheet range
  @created 01/04/2022 - 9:32 AM
* @author sunnydsouza
*/

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GSheetRange {
  private String sheetName;
  private Integer rangeStartRow;
  private Integer rangeEndRow;
  private String rangeStart;
  private String rangeEnd;

  /**
   * Ctor to create a GSheetRange object Example: a valid range is "Sheet1!A15:B25"
   *
   * @param sheetName name of the sheet. In above example: "Sheet1"
   * @param rangeStart start of the range. In above example: "A"
   * @param rangeStartRow row number of the start of the range. In above example: 15
   * @param rangeEnd end of the range. In above example: "B"
   * @param rangeEndRow row number of the end of the range. In above example: 25
   */
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

  /**
   * @return the {@link GSheetRange} range in the format "Sheet1!A15:B25"
   */
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
