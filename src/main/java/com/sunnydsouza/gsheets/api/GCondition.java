package com.sunnydsouza.gsheets.api;
/*
 *  Class to help create conditions for filtering records based on columns {@link GColumnFilters}
 * @created 21/03/2022 - 7:02 PM
 * @author sunnydsouza
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.Predicate;

public class GCondition {

  static Logger logger = LoggerFactory.getLogger(GCondition.class);

  /**
   * Greater than comparison (Currently supports String only) TODO scope to include numeric and date
   * comparsion also
   *
   * @param expectedValue expected value to compare with
   * @param <T> the type of the object - String, Integer, Double, etc
   * @return {@link Predicate}
   */
  public static <T> Predicate<T> greaterThan(String expectedValue) {
    // Currently only supporting comparsion as String
    T t = null;
    if (t instanceof String) {
      logger.debug("expectedValue: {} is instance of String", expectedValue);
      // Placeholder code. TODO implement this
    } else {
      logger.debug("expectedValue: {} is not instance of String", expectedValue);
    }
    Predicate<T> verifyValueGreaterThan = v -> (((String) v).compareTo(expectedValue) > 0);
    return verifyValueGreaterThan;
  }

  /**
   * Less than comparison (Currently supports String only) TODO scope to include numeric and date
   * comparsion also
   *
   * @param expectedValue expected value to compare with
   * @param <T> the type of the object - String, Integer, Double, etc
   * @return {@link Predicate}
   */
  public static <T> Predicate<T> lessThan(String expectedValue) {
    // Currently only supporting comparsion as String

    Predicate<T> verifyValueLessThan = v -> (((String) v).compareTo(expectedValue) < 0);
    return verifyValueLessThan;
  }

  /**
   * Less than or equals comparison (Currently supports String only) TODO scope to include numeric
   * and date comparsion also
   *
   * @param expectedValue expected value to compare with
   * @param <T> the type of the object - String, Integer, Double, etc
   * @return {@link Predicate}
   */
  public static <T> Predicate<T> lessThanOrEquals(String expectedValue) {
    // Currently only supporting comparsion as String

    Predicate<T> verifyValueGreaterThan = v -> (((String) v).compareTo(expectedValue) >= 0);
    return verifyValueGreaterThan;
  }

  /**
   * Greater than or equals comparison (Currently supports String only) TODO scope to include
   * numeric and date comparsion also
   *
   * @param expectedValue expected value to compare with
   * @param <T> the type of the object - String, Integer, Double, etc
   * @return {@link Predicate}
   */
  public static <T> Predicate<T> greaterThanOrEquals(String expectedValue) {
    // Currently only supporting comparsion as String

    Predicate<T> verifyValueLessThan = v -> (((String) v).compareTo(expectedValue) <= 0);
    return verifyValueLessThan;
  }

  /**
   * Equals comparison (Currently supports String only) TODO scope to include numeric and date
   * comparsion also
   *
   * @param expectedValue expected value to compare with
   * @param <T> the type of the object - String, Integer, Double, etc
   * @return {@link Predicate}
   */
  public static <T> Predicate<T> equals(String expectedValue) {
    // Currently only supporting comparsion as String

    Predicate<T> equals = v -> (v.equals(expectedValue));
    return equals;
  }

  /**
   * Equals(case-insensitive) (Currently supports String only) TODO scope to include numeric and
   * date comparsion also
   *
   * @param expectedValue expected value to compare with
   * @param <T> the type of the object - String, Integer, Double, etc
   * @return {@link Predicate}
   */
  public static <T> Predicate<T> equalsIgnoreCase(String expectedValue) {
    // Currently only supporting comparsion as String

    Predicate<T> equalsIgnoreCase = v -> (((String) v).equalsIgnoreCase(expectedValue));
    return equalsIgnoreCase;
  }

  /**
   * Not Equals comparison (Currently supports String only) TODO scope to include numeric and date
   * comparsion also
   *
   * @param expectedValue expected value to compare with
   * @param <T> the type of the object - String, Integer, Double, etc
   * @return {@link Predicate}
   */
  public static <T> Predicate<T> notEquals(String expectedValue) {
    // Currently only supporting comparsion as String

    Predicate<T> notEquals = v -> (!((String) v).equalsIgnoreCase(expectedValue));
    return notEquals;
  }

  /**
   * Contains comparison - for String only
   *
   * @param expectedValue expected value to compare with
   * @return {@link Predicate}
   */
  public static Predicate<String> contains(String expectedValue) {
    return v -> (v.contains(expectedValue));
  }

  /**
   * NotContains comparison - for String only
   *
   * @param expectedValue expected value to compare with
   * @return {@link Predicate}
   */
  public static Predicate<String> notContains(String expectedValue) {
    return v -> (!v.contains(expectedValue));
  }

  /**
   * StartsWith comparison - for String only
   *
   * @param expectedValue expected value to compare with
   * @return {@link Predicate}
   */
  public static Predicate<String> startsWith(String expectedValue) {
    return v -> (v.startsWith(expectedValue));
  }

  /**
   * NotStartsWith comparison - for String only
   *
   * @param expectedValue expected value to compare with
   * @return {@link Predicate}
   */
  public static Predicate<String> notStartsWith(String expectedValue) {
    return v -> (!v.startsWith(expectedValue));
  }

  /**
   * List based comparison - for String only Checks if given string is present in the list of
   * expected values
   *
   * @param expectedValues list of expected String values
   * @return {@link Predicate}
   */
  public static Predicate<String> in(List<String> expectedValues) {
    return v -> (expectedValues.contains(v));
  }

  /**
   * List based comparison - for String only Checks if given string is not present in the list of
   * expected values
   *
   * @param expectedValues list of expected String values
   * @return {@link Predicate}
   */
  public static Predicate<String> notIn(List<String> expectedValues) {
    return v -> (!expectedValues.contains(v));
  }

  /**
   * Checks if given string is empty - for String only
   *
   * @return {@link Predicate}
   */
  public static Predicate<String> isEmpty() {
    return v -> (v.isEmpty());
  }

  /**
   * Checks if given string is not empty - for String only
   *
   * @return {@link Predicate}
   */
  public static Predicate<String> isNotEmpty() {
    return v -> (!v.isEmpty());
  }

  /**
   * Checks if given date is between date1 and date2 (not inclusive) Date based comparison - for
   * Date only
   *
   * @param date1 start date in <b>strictly</b> this format <b>yyyy-MM-dd HH:mm:ss.SSS</b>
   * @param date2 end date in <b>strictly</b> this format <b>yyyy-MM-dd HH:mm:ss.SSS</b>
   * @param colDateFormat date format of the column @Example: <b>dd-MM-yyyy</b>. This is required to
   *     interpret the given date from the google sheet
   * @return {@link Predicate}
   */
  public static Predicate<String> datesBetween(String date1, String date2, String colDateFormat) {
    return v -> {
      try {
        return (new SimpleDateFormat(colDateFormat)
                .parse(v)
                .after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(date1))
            && new SimpleDateFormat(colDateFormat)
                .parse(v)
                .before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(date2)));
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return false;
    };
  }

  /**
   * Checks if given date is greater than expectedDate
   *
   * @param expectedDate date to compare with in <b>strictly</b> this format <b>yyyy-MM-dd
   *     HH:mm:ss.SSS</b>
   * @param colDateFormat date format of the column @Example: <b>dd-MM-yyyy</b>. This is required to
   *     interpret the given date from the google sheet
   * @return
   */
  public static Predicate<String> datesGreaterThan(String expectedDate, String colDateFormat) {
    return v -> {
      try {
        return (new SimpleDateFormat(colDateFormat)
            .parse(v)
            .after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(expectedDate)));
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return false;
    };
  }

  /**
   * Checks if given date is less than expectedDate
   *
   * @param expectedDate date to compare with in <b>strictly</b> this format <b>yyyy-MM-dd
   *     HH:mm:ss.SSS</b>
   * @param colDateFormat date format of the column @Example: <b>dd-MM-yyyy</b>. This is required to
   *     interpret the given date from the google sheet
   * @return
   */
  public static Predicate<String> datesLessThan(String expectedDate, String colDateFormat) {
    return v -> {
      try {
        return (new SimpleDateFormat(colDateFormat)
            .parse(v)
            .before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(expectedDate)));
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return false;
    };
  }

  /**
   * Checks if given date is greater than or equal to expectedDate
   *
   * @param expectedDate date to compare with in <b>strictly</b> this format <b>yyyy-MM-dd
   *     HH:mm:ss.SSS</b>
   * @param colDateFormat date format of the column @Example: <b>dd-MM-yyyy</b>. This is required to
   *     interpret the given date from the google sheet
   * @return {@link Predicate}
   */
  public static Predicate<String> datesGreaterThanOrEquals(
      String expectedDate, String colDateFormat) {
    return v -> {
      try {
        return (new SimpleDateFormat(colDateFormat)
                .parse(v)
                .after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(expectedDate))
            || new SimpleDateFormat(colDateFormat)
                .parse(v)
                .equals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(expectedDate)));
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return false;
    };
  }

  /**
   * Checks if given date is less than or equal to expectedDate
   *
   * @param expectedDate date to compare with in <b>strictly</b> this format <b>yyyy-MM-dd
   *     HH:mm:ss.SSS</b>
   * @param colDateFormat date format of the column @Example: <b>dd-MM-yyyy</b>. This is required to
   *     interpret the given date from the google sheet
   * @return {@link Predicate}
   */
  public static Predicate<String> datesLessThanOrEquals(String expectedDate, String colDateFormat) {
    return v -> {
      try {
        return (new SimpleDateFormat(colDateFormat)
                .parse(v)
                .before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(expectedDate))
            || new SimpleDateFormat(colDateFormat)
                .parse(v)
                .equals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(expectedDate)));
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return false;
    };
  }

  /**
   * EndsWith comparison - for String only
   *
   * @param expectedValue expected value to compare with
   * @return {@link Predicate}
   */
  public Predicate<String> endsWith(String expectedValue) {
    return v -> (v.endsWith(expectedValue));
  }

  /**
   * NotEndsWith comparison - for String only
   *
   * @param expectedValue expected value to compare with
   * @return {@link Predicate}
   */
  public Predicate<String> notEndsWith(String expectedValue) {
    return v -> (!v.endsWith(expectedValue));
  }
}
