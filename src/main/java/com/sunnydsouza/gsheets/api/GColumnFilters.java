package com.sunnydsouza.gsheets.api;

/*
 * @created 21/03/2022 - 5:01 PM
 * @author sunnydsouza
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A class to create/apply filter conditions on column/s in the google sheet range (with headers)
 * Please note that the range MUST HAVE headers to be able to create/apply filters
 *
 * @author sunnydsouza
 */
public class GColumnFilters {
  String columnName;
  Map<String, Predicate> predicateMap = new HashMap<>();

  Logger logger = LoggerFactory.getLogger(GColumnFilters.class);

  public GColumnFilters onCol(String columnName) {
    this.columnName = columnName;
    return this;
  }

  public GColumnFilters conditions(Predicate predicate) {
    predicateMap.put(this.columnName, predicate);
    return this;
  }

  public Map<String, Predicate> getAllConditions() {
    logger.debug("All current GColumnFilters: {}", predicateMap);
    return predicateMap;
  }
  /*  public Predicate<? super Map<String, String>> apply() {
    return r ->
        (r.entrySet().stream()
            .allMatch(m -> predicateMap.getOrDefault(m.getKey(), n -> true).test(m.getValue())));
  }*/

  public Predicate<? super GRow> apply() {
    return r ->
        (r.getColValMap().entrySet().stream()
            .allMatch(m -> predicateMap.getOrDefault(m.getKey(), n -> true).test(m.getValue())));
  }
}
