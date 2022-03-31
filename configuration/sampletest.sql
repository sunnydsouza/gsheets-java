-- expenses.Expense definition

CREATE TABLE `Expense` (
  `RecordedTimestamp` date NOT NULL,
  `Month` varchar(100) DEFAULT NULL,
  `Type` varchar(100) DEFAULT NULL,
  `ExpenseCategory` varchar(100) DEFAULT NULL,
  `ExpenseSubCategory` varchar(100) DEFAULT NULL,
  `Expense` decimal(10,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ExpenseDateTransform` (
  `RecordedTimestamp` date NOT NULL,
  `Month` varchar(100) DEFAULT NULL,
  `Type` varchar(100) DEFAULT NULL,
  `ExpenseCategory` varchar(100) DEFAULT NULL,
  `ExpenseSubCategory` varchar(100) DEFAULT NULL,
  `Expense` decimal(10,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;