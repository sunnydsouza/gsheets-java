package com.sunnydsouza.gsheets;
/* Helper class for testing
 * @created 01/04/2022 - 6:51 AM
 * @author sunnydsouza
 */

import com.sunnydsouza.gsheets.api.GColumnFilters;
import com.sunnydsouza.gsheets.api.GCondition;
import com.sunnydsouza.gsheets.api.GRow;
import com.sunnydsouza.gsheets.api.GSheetsApi;
import com.sunnydsouza.gsheets.utils.PropertyFileReader;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHelper {
  static final String PROPERTY_FILE = "configuration/sampletest.properties"; // for GITHUB_ACTIONS
  static final String GSHEETS_ID = "GSHEETS_ID";
  static final String CRUD_SHEET_ID = "CRUD_SHEET_ID";
  Logger logger = LoggerFactory.getLogger(FilterTests.class);

  List<GRow> expResFilterRowSingleColumnGSheets =
      new LinkedList<>(
          List.of(
              GRow.newRow(13)
                  .addCell("RecordedTimestamp", "09/01/2022")
                  .addCell("Month", "Jan")
                  .addCell("Type", "Debit")
                  .addCell("ExpenseCategory", "Shopping")
                  .addCell("ExpenseSubCategory", "Cred")
                  .addCell("Expense", "113.00"),
              GRow.newRow(14)
                  .addCell("RecordedTimestamp", "09/01/2022")
                  .addCell("Month", "Jan")
                  .addCell("Type", "Debit")
                  .addCell("ExpenseCategory", "Subscriptions")
                  .addCell("ExpenseSubCategory", "bbdaily")
                  .addCell("Expense", "114.00")));

  List<GRow> expResFilterRowMultipleColumnsGSheets =
      new LinkedList<>(
          List.of(
              GRow.newRow(14)
                  .addCell("RecordedTimestamp", "09/01/2022")
                  .addCell("Month", "Jan")
                  .addCell("Type", "Debit")
                  .addCell("ExpenseCategory", "Subscriptions")
                  .addCell("ExpenseSubCategory", "bbdaily")
                  .addCell("Expense", "114.00"),
              GRow.newRow(16)
                  .addCell("RecordedTimestamp", "17/01/2022")
                  .addCell("Month", "Jan")
                  .addCell("Type", "Debit")
                  .addCell("ExpenseCategory", "Subscriptions")
                  .addCell("ExpenseSubCategory", "SonyLiv")
                  .addCell("Expense", "127.00"),
              GRow.newRow(18)
                  .addCell("RecordedTimestamp", "17/01/2022")
                  .addCell("Month", "Jan")
                  .addCell("Type", "Debit")
                  .addCell("ExpenseCategory", "Subscriptions")
                  .addCell("ExpenseSubCategory", "Amazon")
                  .addCell("Expense", "125.00"),
              GRow.newRow(21)
                  .addCell("RecordedTimestamp", "17/01/2022")
                  .addCell("Month", "Jan")
                  .addCell("Type", "Debit")
                  .addCell("ExpenseCategory", "Subscriptions")
                  .addCell("ExpenseSubCategory", "Blah!")
                  .addCell("Expense", "128.50")));

  List<GRow> expResFilterRowNoConditionsMatch = new LinkedList<>(); // no records match

  List<GRow> rowsToBeInserted =
      new LinkedList<>(
          List.of(
              GRow.newRow()
                  .addCell("RecordedTimestamp", "26/03/2022")
                  .addCell("Month", "Mar")
                  .addCell("Type", "Debit")
                  .addCell("ExpenseCategory", "TestInsert")
                  .addCell("ExpenseSubCategory", "InsertCat1")
                  .addCell("Expense", "216.00"),
              GRow.newRow()
                  .addCell("RecordedTimestamp", "27/03/2022")
                  .addCell("Month", "Mar")
                  .addCell("Type", "Debit")
                  .addCell("ExpenseCategory", "TestInsert")
                  .addCell("ExpenseSubCategory", "InsertCat2")
                  .addCell("Expense", "217.00"),
              GRow.newRow()
                  .addCell("RecordedTimestamp", "28/03/2022")
                  .addCell("Month", "Mar")
                  .addCell("Type", "Credit")
                  .addCell("ExpenseCategory", "TestInsert")
                  .addCell("ExpenseSubCategory", "InsertCat3")
                  .addCell("Expense", "-160.00")));

  List<GRow> rowsToBeAppended =
      new LinkedList<>(
          List.of(
              GRow.newRow()
                  .addCell("RecordedTimestamp", "03/03/2022")
                  .addCell("Month", "Mar")
                  .addCell("Type", "Debit")
                  .addCell("ExpenseCategory", "TestAppend")
                  .addCell("ExpenseSubCategory", "AppendCat1")
                  .addCell("Expense", "216.00"),
              GRow.newRow()
                  .addCell("RecordedTimestamp", "03/03/2022")
                  .addCell("Month", "Mar")
                  .addCell("Type", "Debit")
                  .addCell("ExpenseCategory", "TestAppend")
                  .addCell("ExpenseSubCategory", "AppendCat2")
                  .addCell("Expense", "217.00")));

  @BeforeAll
  public static void readConfiguration() {
    PropertyFileReader.readPropertyFiles(PROPERTY_FILE);
    System.out.println(System.getProperty(GSHEETS_ID));
    System.out.println(System.getProperty(CRUD_SHEET_ID));
//    PropertyFileReader.setPropValues(GSHEETS_ID,System.getenv(GSHEETS_ID));
//    PropertyFileReader.setPropValues(CRUD_SHEET_ID,System.getenv(CRUD_SHEET_ID));
  }

  protected void updateRow22And23() throws GeneralSecurityException, IOException {

    // the columns to be updated, with the updated value
    Map<String, String> updatedColumns =
        new HashMap<>(
            Map.of(
                "ExpenseCategory",
                "Booze", // Change Bills to Booze
                "Expense",
                "500")); // Booze is expensive ;-)

    // Find the rows based on GColumnFilters and then update the columns
    GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
        .updateRows(
            "CRUD!A:F",
            new GColumnFilters()
                .onCol("RecordedTimestamp")
                .conditions(GCondition.equals("21/01/2022"))
                .onCol("ExpenseCategory")
                .conditions(GCondition.equals("Bills")),
            updatedColumns);
  }

  protected void verifyUpdatedRow22And23() throws GeneralSecurityException, IOException {

    // Verify the updated value in the 2 rows (Row numbers 22,23)
    List<GRow> updatedRow =
        GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
            .readSheetValues("CRUD!A22:F23", false);
    logger.debug("Updated row:{}", updatedRow);
    assertEquals("Booze", updatedRow.get(0).getColValMap().get("3")); // First record and 4th column
    assertEquals(
        "500.00", updatedRow.get(0).getColValMap().get("5")); // First record and 4th column
    assertEquals("Booze", updatedRow.get(1).getColValMap().get("3")); // First record and 4th column
    assertEquals(
        "500.00", updatedRow.get(1).getColValMap().get("5")); // First record and 4th column
  }

  protected void revertUpdateToRow22And23() throws GeneralSecurityException, IOException {
    // Revert the updated value
    Map<String, String> revertUpdatedColumns =
        new HashMap<>(
            Map.of(
                "ExpenseCategory",
                "Bills", // Change back Booze to Bills
                "Expense",
                "136"));
    GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
        .updateRows(
            "CRUD!A:F",
            new GColumnFilters()
                .onCol("RecordedTimestamp")
                .conditions(GCondition.equals("21/01/2022"))
                .onCol("ExpenseCategory")
                .conditions(GCondition.equals("Booze")),
            revertUpdatedColumns);
  }

  protected void verifyAppendedRows() throws GeneralSecurityException, IOException {
    // Verify the updated value in the 2 rows (Row numbers 22,23)
    List<GRow> appendedRow =
        GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
            .readSheetValues("CRUD!A55:F56", false);
    logger.debug("Appended row:{}", appendedRow);
    assertEquals(
        "AppendCat1", appendedRow.get(0).getColValMap().get("4")); // First record and 4th column
    assertEquals(
        "AppendCat2", appendedRow.get(1).getColValMap().get("4")); // First record and 4th column
  }

  protected void appendRows() throws GeneralSecurityException, IOException {
    GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
        .appendRows("CRUD!A:F", rowsToBeAppended);
  }

  protected void verifyBeforeInsertedRows() throws GeneralSecurityException, IOException {
    // Verify the updated value in the 2 rows (Row numbers 22,23)
    List<GRow> updatedRow =
        GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
            .readSheetValues("CRUD!A30:F32", false);
    logger.debug("Updated row:{}", updatedRow);
    assertEquals(
        "InsertCat1", updatedRow.get(0).getColValMap().get("4")); // First record and 4th column
    assertEquals(
        "InsertCat2", updatedRow.get(1).getColValMap().get("4")); // First record and 4th column
    assertEquals(
        "InsertCat3", updatedRow.get(2).getColValMap().get("4")); // First record and 4th column
  }

  protected void insertRowsBeforeRow30() throws GeneralSecurityException, IOException {
    GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
        .insertRowsBefore(
            Integer.parseInt(PropertyFileReader.getPropValues(CRUD_SHEET_ID)),
            "CRUD!A30:F30",
            rowsToBeInserted);
  }

  protected void insertRowsAfterRow30() throws GeneralSecurityException, IOException {

    GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
        .insertRowsAfter(
            Integer.parseInt(PropertyFileReader.getPropValues(CRUD_SHEET_ID)),
            "CRUD!A30:F30",
            rowsToBeInserted);
  }

  protected void verifyAfterInsertedRows() throws GeneralSecurityException, IOException {

    // Verify the updated value in the 2 rows (Row numbers 22,23)
    List<GRow> updatedRow =
        GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
            .readSheetValues("CRUD!A31:F33", false);
    logger.debug("Updated row:{}", updatedRow);
    assertEquals(
        "InsertCat1", updatedRow.get(0).getColValMap().get("4")); // First record and 4th column
    assertEquals(
        "InsertCat2", updatedRow.get(1).getColValMap().get("4")); // First record and 4th column
    assertEquals(
        "InsertCat3", updatedRow.get(2).getColValMap().get("4")); // First record and 4th column
  }
}
