package com.sunnydsouza.gsheets; /*
                                  * @created 01/04/2022 - 6:21 AM
                                  * @author sunnydsouza
                                  */

import com.sunnydsouza.gsheets.api.GColumnFilters;
import com.sunnydsouza.gsheets.api.GCondition;
import com.sunnydsouza.gsheets.api.GSheetsApi;
import com.sunnydsouza.gsheets.utils.PropertyFileReader;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.security.GeneralSecurityException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CrudTests extends TestHelper {

  /** Test to verify the update row operation */
  @Test
  @Order(1)
  public void updateRow() throws GeneralSecurityException, IOException {

    try {
      updateRow22And23();
      verifyUpdatedRow22And23();
    } finally {
      revertUpdateToRow22And23();
    }
  }

  /** Test to verify the insert row operation */
  @Test
  @Order(2)
  public void testInsertRowsAfter() throws GeneralSecurityException, IOException {
    insertRowsAfterRow30();
    verifyAfterInsertedRows();
  }

  @Test
  @Order(3)
  public void testInsertRowsBefore() throws GeneralSecurityException, IOException {
    insertRowsBeforeRow30();
    verifyBeforeInsertedRows();
  }

  /** Test to verify the append row operation */
  @Test
  @Order(4)
  public void testAppendRows() throws GeneralSecurityException, IOException {
    appendRows();
    verifyAppendedRows();
  }

  /** Test to verify the delete row operation */
  @Test
  @Order(5)
  public void deleteRows() throws GeneralSecurityException, IOException {

    GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
        .deleteRows(
            Integer.parseInt(PropertyFileReader.getPropValues(CRUD_SHEET_ID)),
            "CRUD!A:F",
            new GColumnFilters()
                .onCol("ExpenseCategory")
                .conditions(GCondition.equals("TestInsert").or(GCondition.equals("TestAppend"))));
  }
}
