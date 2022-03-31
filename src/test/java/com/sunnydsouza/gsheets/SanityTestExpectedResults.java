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
              GRow.newRow(98)
                  .addCell("Month", "Mar")
                  .addCell("Type", "Debit")
                  .addCell("RecordedTimestamp", "01/03/2022")
                  .addCell("ExpenseCategory", "Shopping")
                  .addCell("ExpenseSubCategory", "Amazon")
                  .addCell("Expense", "216.00"),
              GRow.newRow(99)
                  .addCell("Month", "Mar")
                  .addCell("Type", "Debit")
                  .addCell("RecordedTimestamp", "01/03/2022")
                  .addCell("ExpenseCategory", "Swiggy")
                  .addCell("ExpenseSubCategory", "Irani Cafe")
                  .addCell("Expense", "217.00"),
              GRow.newRow(101)
                  .addCell("Month", "Mar")
                  .addCell("Type", "Debit")
                  .addCell("RecordedTimestamp", "01/03/2022")
                  .addCell("ExpenseCategory", "Bills")
                  .addCell("ExpenseSubCategory", "Internet")
                  .addCell("Expense", "219.00"),
              GRow.newRow(102)
                  .addCell("Month", "Mar")
                  .addCell("Type", "Debit")
                  .addCell("RecordedTimestamp", "01/03/2022")
                  .addCell("ExpenseCategory", "Bills")
                  .addCell("ExpenseSubCategory", "IdeaPostpaid")
                  .addCell("Expense", "220.00")));

  List<GRow> expResFilterRowMultipleColumnsGSheets =
      new LinkedList<>(
          List.of(
              GRow.newRow(2)
                  .addCell("Month", "Jan")
                  .addCell("Type", "Debit")
                  .addCell("RecordedTimestamp", "01/01/2022")
                  .addCell("ExpenseCategory", "Bills")
                  .addCell("ExpenseSubCategory", "Electricity")
                  .addCell("Expense", "100.00"),
              GRow.newRow(3)
                  .addCell("Month", "Jan")
                  .addCell("Type", "Debit")
                  .addCell("RecordedTimestamp", "01/01/2022")
                  .addCell("ExpenseCategory", "Bills")
                  .addCell("ExpenseSubCategory", "IdeaPostpaid")
                  .addCell("Expense", "101.00"),
              GRow.newRow(4)
                  .addCell("Month", "Jan")
                  .addCell("Type", "Debit")
                  .addCell("RecordedTimestamp", "01/01/2022")
                  .addCell("ExpenseCategory", "Bills")
                  .addCell("ExpenseSubCategory", "Internet")
                  .addCell("Expense", "102.00"),

              GRow.newRow(101)
                  .addCell("Month", "Mar")
                  .addCell("Type", "Debit")
                  .addCell("RecordedTimestamp", "01/03/2022")
                  .addCell("ExpenseCategory", "Bills")
                  .addCell("ExpenseSubCategory", "Internet")
                  .addCell("Expense", "219.00"),
              GRow.newRow(102)
                  .addCell("Month", "Mar")
                  .addCell("Type", "Debit")
                  .addCell("RecordedTimestamp", "01/03/2022")
                  .addCell("ExpenseCategory", "Bills")
                  .addCell("ExpenseSubCategory", "IdeaPostpaid")
                  .addCell("Expense", "220.00")));

  List<GRow> expResFilterRowNoConditionsMatch = new LinkedList<>(); //no records match
}
