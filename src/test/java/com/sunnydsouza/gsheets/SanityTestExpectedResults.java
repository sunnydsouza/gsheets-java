package com.sunnydsouza.gsheets;
/*
 * @created 31/03/2022 - 9:18 AM
 * @author sunnydsouza
 */

import com.sunnydsouza.gsheets.api.GRow;

import java.util.LinkedList;
import java.util.List;

public class SanityTestExpectedResults {

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
}
