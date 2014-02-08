package com.recallq.parseweblog.fieldparser;

import com.recallq.parseweblog.SingleResultFieldParser;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Converts a nginx log date to number of seconds since 1970.
 *
 * @author Jeroen De Swaef
 */
public class ISO8601DateFieldParser extends SingleResultFieldParser {

    @Override
    public String parse(String input) {
        DateTimeFormatter parser2 = ISODateTimeFormat.dateTimeNoMillis();
        return String.valueOf(parser2.parseDateTime(input).toDate().getTime());
    }


}
