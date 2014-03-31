package com.recallq.parseweblog;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Interface for all classes that output the parsed log data.
 * 
 * @author Jeroen De Swaef <j@recallq.com>
 */
public interface Outputter {
    /**
    * @param logData A list for each log line a map with:
    *    - key: log variable
    *    - value: String/Map<String, String>: value(s) being logged.
    * 
    * @throws java.io.IOException
    */
    void outputData(List<Map<String, Object>> logData) throws IOException ;
}
