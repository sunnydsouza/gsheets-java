# gsheets-java
A simple wrapper aroung Google Sheet Api enabling to perform basic CRUD operations on google sheets data. Also allows for filtering data

### Prerequiste: Creating your Google Service account

In order to use this library, you will need a Google service account which ia authenticated access to your Google Sheet

Once you create a Google service account, export the credentials.json and place it in `/credentials/` folder

Then navigate to the Google Sheet and use the Share option to share the sheet with service account email address

![Untitled](Detailed%20d%207a9fa/Untitled.png)

These steps done, you are now ready to use the library 🙂

### The .properties file with sheet details

- Placing the sheet details in a folder like `\configuration\application.properties` file
- Its not necessary to name the properties file as application.properties, could be any .properties file
- Include the details of sheet like `spreadsheetId` and `sheetId`'s of the relevant sheets

       For example, if your sheet url is `*`https://docs.google.com/spreadsheets/d/XXXX/edit#gid=YYYY``*

```java
GSHEETS_ID=XXXX
SAMPLE_SHEET_ID=YYYY
```

### Spreadsheet example for below demonstration

Lets assume we have the below table in Google sheet in `SAMPLE_SHEET` tab (just for column structure reference)

|  | A | B | C | D | E | F |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | RecordedTimestamp | Month | Type | ExpenseCategory | ExpenseSubCategory | Expense |
| 2 | 01/01/2022 | Jan | Debit | Bills | Electricity | 100 |
| 3 | 01/01/2022 | Jan | Debit | Bills | IdeaPostpaid | 101 |
| 4 | 01/01/2022 | Jan | Debit | Bills | Internet | 102 |
| 5 | 01/01/2022 | Jan | Debit | Home Loan | Society maintainence | 103 |
| 6 | 02/01/2022 | Jan | Debit | Hobby | NA | 106 |
| 7 | 05/01/2022 | Jan | Debit | Bills | Gas bill | 107 |

### Reading a spreadsheet range(headers)

```java
//You can use the PropertyFileReader in the library to get the GSHEETS_ID from properties file
List<GRow> readValues = GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
                                   .readSheetValues("SAMPLE_SHEET!A:F");

//Or simply hardcode as required
List<GRow> readValues = GSheetsApi.spreadsheet("XXXX")
                                    .readSheetValues("SAMPLE_SHEET!A:F");
```

### Reading a spreadsheet range(without headers). Set the second argument to `false`

```java
List<GRow> readValues = GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
                                    .readSheetValues("SAMPLE_SHEET!A31:F33", false);

List<GRow> readValues = GSheetsApi.spreadsheet("XXXX")
                                    .readSheetValues("SAMPLE_SHEET!A31:F33", false);
```

### Inserting rows `BEFORE` a range

```java
//Create sample List of GRow to insert
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
                  .addCell("Expense", "217.00")));

GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
        .insertRowsBefore(
            YYYY,
            "SAMPLE_SHEET!A30:F30",
            rowsToBeInserted);
```

### Inserting rows `AFTER` a range

```java
GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
        .insertRowsAfter(
            Integer.parseInt(PropertyFileReader.getPropValues(SAMPLE_SHEET_ID)),
            "SAMPLE_SHEET!A30:F30",
            rowsToBeInserted);
```

### Appending rows on a range

```java
//Create sample List of GRow to insert
List<GRow> rowsToBeAppended =
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
                  .addCell("Expense", "217.00")));
GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
        .appendRows("SAMPLE_SHEET!A:F", rowsToBeAppended);
```

### Filtering Operations on data from sheet range

Its necessary that the range has **headers** in order to have conditions/filters on them

Supported `String` operations

> greaterThan
lessThan
lessThanOrEquals
greaterThanOrEquals
equals
equalsIgnoreCase
notEquals
contains
notContains
startsWith
notStartsWith
in
notIn
isEmpty
isNotEmpty
>
>
> endsWith
> notEndsWith
>

Supported `Date` field operations

> datesBetween
datesGreaterThan
datesLessThan
datesGreaterThanOrEquals
datesLessThanOrEquals
>

Example usages

```java
//Filter on single column
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

```

Example of filtering on multiple column and multiple conditions on a single column

```java
//Filtering on multiple columns
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
```

Please note the                `.conditions(GCondition.equals("09/01/2022").or(GCondition.equals("17/01/2022"))`

Since these are predicates, you can chain in as many `.or` or `.and` `GCondition` conditions

### Getting the row numbers for filtered records

```java
List<GRow> filteredResult rowNos =
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
```

### Filtering on date fields

***Please note** the condition date `GCondition.datesGreaterThan("2022-03-01 00:00:00.000", "dd/MM/yyyy")`

`"2022-03-01 00:00:00.000"` will always be `YYYY-MM-DD HH:mm:SS.SSS` format

`"dd/MM/yyyy"` defines the format in which the date is present in the Google sheet range. As seen in the example [above](https://www.notion.so/Detailed-documentation-on-GSheets-Java-4e643950b8c94996827a09ffa0df53d7)

```java

List<Integer> rowNos =
        GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
            .filterRows(
                "Filters!A:F",
                new GColumnFilters()
                    .onCol("RecordedTimestamp")
                    .conditions(
                        GCondition.datesGreaterThan("2022-03-01 00:00:00.000", "dd/MM/yyyy")));

//Example with multiple conditions on a column
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
```

### Deleting rows based on condition

```java
GSheetsApi.spreadsheet(PropertyFileReader.getPropValues(GSHEETS_ID))
        .deleteRows(
            Integer.parseInt(PropertyFileReader.getPropValues(SAMPLE_SHEET_ID)),
            "SAMPLE_SHEET!A:F",
            new GColumnFilters()
                .onCol("ExpenseCategory")
                .conditions(GCondition.equals("TestInsert").or(GCondition.equals("TestAppend"))));
```