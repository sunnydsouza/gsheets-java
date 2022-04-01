package com.sunnydsouza.gsheets.api;/*
 * @created 01/04/2022 - 9:32 AM
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

