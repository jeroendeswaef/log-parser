package com.recallq.parseweblog.fieldparser;

import com.recallq.parseweblog.FieldParser;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Converts a nginx log date to number of seconds since 1970.
 *
 * @author Jeroen De Swaef
 */
public class ISO8601DateFieldParser implements FieldParser {

    @Override
    public String parse(String input) {
        DateTimeFormatter parser2 = ISODateTimeFormat.dateTimeNoMillis();
        return String.valueOf(parser2.parseDateTime(input).toDate().getTime());
    }

}
