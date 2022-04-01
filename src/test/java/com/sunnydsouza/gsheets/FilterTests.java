package com.sunnydsouza.gsheets;
/*
 * @created 21/03/2022 - 4:15 PM
 * @author sunnydsouza
 */

import com.sunnydsouza.gsheets.api.GColumnFilters;
import com.sunnydsouza.gsheets.api.GCondition;
import com.sunnydsouza.gsheets.api.GRow;
import com.sunnydsouza.gsheets.api.GSheetsApi;
import com.sunnydsouza.gsheets.utils.PropertyFileReader;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilterTests extends TestHelper {

  //  static final String PROPERTY_FILE = "configuration/expenses.properties";    //for LOCAL
  // testing
  //  static final String PROPERTY_FILE = "configuration/sampletest.properties"; // for
  // GITHUB_ACTIONS
  //  static final String GSHEETS_ID = "GSHEETS_ID";

  Logger logger = LoggerFactory.getLogger(FilterTests.class);

  //  @BeforeAll
  //  public static void readConfiguration() {
  //    PropertyFileReader.readPropertyFiles(PROPERTY_FILE);
  //  }

  @Test
  @Order(1)
  public void filterRowSingleColumnGSheets() throws GeneralSecurityException, IOException {

    List<GRow> filteredResult =
        GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
            .filterRows(
                "Filters!A:F",
                new GColumnFilters()
                    .onCol("RecordedTimestamp")
                    .conditions(GCondition.equals("09/01/2022")));

    logger.debug("filteredResult:{}", filteredResult);

    filteredResult.forEach(
        f -> logger.debug(String.valueOf(f.getRowNo()))); // for debugging purposes

    // Compare the filteredResult with expectedResult
    if (filteredResult.size() != expResFilterRowSingleColumnGSheets.size()) {
      fail(
          "Expected size: "
              + expResFilterRowSingleColumnGSheets.size()
              + " Actual size: "
              + filteredResult.size());
    }
    boolean resultAssertion = true;
    for (int i = 0; i < filteredResult.size(); i++) {
      if (filteredResult.get(i).compareTo(expResFilterRowSingleColumnGSheets.get(i)) != 0) {
        resultAssertion = false;
      }
    }
    assertTrue(resultAssertion, "Verify the filtered results work correctly");
  }

  @Test
  @Order(2)
  public void filterRowMultipleColumnsGSheets() throws GeneralSecurityException, IOException {

    List<GRow> filteredResult =
        GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
            .filterRows(
                "Filters!A:F",
                new GColumnFilters()
                    .onCol("RecordedTimestamp")
                    .conditions(GCondition.equals("09/01/2022").or(GCondition.equals("17/01/2022")))
                    .onCol("ExpenseCategory")
                    .conditions(GCondition.equals("Subscriptions")));
    logger.debug("filteredResult:{}", filteredResult);

    filteredResult.stream()
        .forEach(f -> logger.debug(String.valueOf(f.getRowNo()))); // for debugging purposes

    // Compare the filteredResult with expectedResult
    if (filteredResult.size() != expResFilterRowMultipleColumnsGSheets.size()) {
      fail(
          "Expected size: "
              + expResFilterRowMultipleColumnsGSheets.size()
              + " Actual size: "
              + filteredResult.size());
    }
    boolean resultAssertion = true;
    for (int i = 0; i < filteredResult.size(); i++) {
      if (filteredResult.get(i).compareTo(expResFilterRowMultipleColumnsGSheets.get(i)) != 0) {
        resultAssertion = false;
      }
    }
    assertTrue(resultAssertion, "Verify the filtered results work correctly");
  }

  @Test
  @Order(3)
  public void filterRowNoConditionsMatch() throws GeneralSecurityException, IOException {

    List<GRow> filteredResult =
        GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
            .filterRows(
                "Filters!A:F",
                new GColumnFilters()
                    .onCol("RecordedTimestamp")
                    .conditions(GCondition.equals("01/03/2022").or(GCondition.equals("01/01/2022")))
                    .onCol("ExpenseCategory")
                    .conditions(GCondition.equals("Investments")));
    logger.debug("filteredResult:{}", filteredResult);

    filteredResult.stream()
        .forEach(f -> logger.debug(String.valueOf(f.getRowNo()))); // for debugging purposes

    assertTrue(filteredResult.isEmpty(), "Verify the filtered results shows 0 records");
  }

  /**
   * Test to verify the row numbers returns based on the filter conditions
   *
   * @throws GeneralSecurityException
   * @throws IOException
   */
  @Test
  @Order(4)
  public void filteredRowNoSingleColumnGSheets() throws GeneralSecurityException, IOException {

    List<Integer> rowNos =
        GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
            .filterRows(
                "Filters!A:F",
                new GColumnFilters()
                    .onCol("RecordedTimestamp")
                    .conditions(GCondition.equals("09/01/2022").or(GCondition.equals("17/01/2022")))
                    .onCol("ExpenseCategory")
                    .conditions(GCondition.equals("Subscriptions")))
            .stream()
            .map(GRow::getRowNo)
            .collect(Collectors.toCollection(LinkedList::new));
    logger.debug("Rows returned for filtered conditions:{}", rowNos);

    List<Integer> expRowNos = new LinkedList<>(List.of(14, 16, 18, 21));
    assertEquals(expRowNos, rowNos, "Verify the filtered row numbers match");
  }

  /**
   * Test to verify the filtered condition on date column
   *
   * @throws GeneralSecurityException
   * @throws IOException
   */
  @Test
  @Order(5)
  public void filterRowsDatesGreaterThan() throws GeneralSecurityException, IOException {

    List<Integer> rowNos =
        GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
            .findRows(
                "Filters!A:F",
                new GColumnFilters()
                    .onCol("RecordedTimestamp")
                    .conditions(
                        GCondition.datesGreaterThan("2022-03-01 00:00:00.000", "dd/MM/yyyy")));
    logger.debug("Rows returned for filtered conditions:{}", rowNos);
    List<Integer> expRowNos =
        new LinkedList<>(
            List.of(
                100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116,
                117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133,
                134, 135));
    assertEquals(expRowNos, rowNos);
  }

  /**
   * Test to verify the filtered condition on date column with multiple conditions
   *
   * @throws GeneralSecurityException
   * @throws IOException
   */
  @Test
  @Order(6)
  public void filterRowsDatesGreaterThanOrEquals() throws GeneralSecurityException, IOException {

    List<Integer> rowNos =
        GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
            .findRows(
                "Filters!A:F",
                new GColumnFilters()
                    .onCol("RecordedTimestamp")
                    .conditions(
                        GCondition.datesGreaterThanOrEquals("2022-03-01 00:00:00.000", "dd/MM/yyyy")
                            .and(
                                GCondition.datesLessThanOrEquals(
                                    "2022-03-31 00:00:00.000", "dd/MM/yyyy"))));
    logger.debug("Rows returned for filtered conditions:{}", rowNos);
  }

  /**
   * Test to verify the filtered condition on date column with datesBetween
   *
   * @throws GeneralSecurityException
   * @throws IOException
   */
  @Test
  @Order(7)
  public void filterRowsDatesBetween() throws GeneralSecurityException, IOException {

    List<Integer> rowNos =
        GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
            .findRows(
                "Filters!A:F",
                new GColumnFilters()
                    .onCol("RecordedTimestamp")
                    .conditions(
                        GCondition.datesBetween(
                            "2022-01-01 00:00:00.000", "2022-02-28 00:00:00.000", "dd/MM/yyyy")));
    logger.debug("Rows returned for filtered conditions:{}", rowNos);
  }
}
