package com.recallq.parseweblog;

import com.recallq.parseweblog.fieldparser.LocalTimeFieldParser;
import com.recallq.parseweblog.outputters.JsonOutputter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 *
 * @author Jeroen De Swaef
 */
public class ParseWebLog {

    private static final Logger logger = Logger.getLogger(ParseWebLog.class.getName());

    private static final String LOGFILE_PARAMETER = "logfile";
    private static final Set<Character> charactersToEscape = new HashSet<Character>() {
        {
            add('[');
            add(']');
        }
    };
    private static final Map<String, FieldParser> fieldParsers = new HashMap<String, FieldParser>() {
        {
            put("time_local", new LocalTimeFieldParser());
        }
    };
    private static final List<String> logFieldNames = new ArrayList<String>();

    // Returns a pattern where all punctuation characters are escaped.
    private static final Pattern escaper = Pattern.compile("([\\[\\]])");
    private static final Pattern extractVariablePattern = Pattern.compile("\\$[a-zA-Z0-9_]*");

    private static String escapeRE(String str) {
        return escaper.matcher(str).replaceAll("\\\\$1");
    }

    private final String metaPattern;
    private final InputStream logStream;

    // each item in the map represents a log line
    // each map entry has as key the name of the nginx log variable
    // the object in the map can be:
    // - a String: for single values
    // - a Map of <String, String>: for values that are being split by FieldParsers
    private List<Map<String, Object>> logData;

    public List<Map<String, Object>> getLogData() {
        return logData;
    }

    public ParseWebLog(InputStream logStream, String metaPattern) {
        this.logStream = logStream;
        this.metaPattern = metaPattern;
    }

    public void parseLog() {
        logData = new ArrayList<Map<String, Object>>();
        Matcher matcher = extractVariablePattern.matcher(metaPattern);
        int parsedPosition = 0;
        StringBuilder parsePatternBuilder = new StringBuilder();

        while (matcher.find()) {
            if (parsedPosition < matcher.start()) {
                String residualPattern = metaPattern.substring(parsedPosition, matcher.start());
                parsePatternBuilder.append(escapeRE(residualPattern));
            }
            String logFieldName = metaPattern.substring(matcher.start() + 1, matcher.end());
            logFieldNames.add(logFieldName);
            parsedPosition = matcher.end();
            char splitCharacter = metaPattern.charAt(matcher.end());
            parsePatternBuilder.append("([^");
            if (charactersToEscape.contains(splitCharacter)) {
                parsePatternBuilder.append("\\");
            }
            parsePatternBuilder.append(splitCharacter);
            parsePatternBuilder.append("]*)");
        }
        parsePatternBuilder.append(metaPattern.substring(parsedPosition, metaPattern.length()));
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, parsePatternBuilder.toString());
        }
        Pattern logFilePattern = Pattern.compile(parsePatternBuilder.toString());
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(logStream));
            String line;
            while ((line = br.readLine()) != null) {
                Map<String, Object> logLine = new HashMap<String, Object>();
                Matcher logFileMatcher = logFilePattern.matcher(line);

                if (logFileMatcher.matches()) {
                    for (int i = 1; i < logFileMatcher.groupCount(); i++) {
                        String logFieldName = logFieldNames.get(i - 1);
                        FieldParser fieldParser = fieldParsers.get(logFieldName);
                        Object fieldValue;
                        if (fieldParser != null) {
                            if (fieldParser instanceof SingleResultFieldParser) {
                                fieldValue = ((SingleResultFieldParser) fieldParser)
                                        .parse(logFileMatcher.group(i));
                            } else {
                                fieldValue = ((MultipleResultFieldParser) fieldParser)
                                        .parse(logFileMatcher.group(i));
                            }
                        } else {
                            fieldValue = logFileMatcher.group(i);
                        }
                        logLine.put(logFieldName, fieldValue);
                    }
                    logData.add(logLine);
                }

            }
            br.close();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error parsing log", ex);
        }
        if (logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, "Parsed {0} log lines", new Object[]{logData.size()});
        }
    }

    public void outputParsedData() {
        JsonOutputter outputter = new JsonOutputter();
        try {
            outputter.outputData(logData, System.out);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error outputting in JSON", ex);
        }
    }

    public static void main(String... arguments) {
        String logFilename = null;
        OptionParser parser = new OptionParser() {
            {
                accepts(LOGFILE_PARAMETER).withRequiredArg().required()
                        .describedAs("Apache/Nginx log file");
            }
        };
        try {
            OptionSet options = parser.parse(arguments);
            logFilename = (String) options.valueOf(LOGFILE_PARAMETER);
        } catch (Exception e) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException ex) {
                Logger.getLogger(ParseWebLog.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.exit(1);
        }

        Properties prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("config.properties");
        try {
            prop.load(stream);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Unable to load config.properties", ex);
        }
        String metaPattern = prop.getProperty("meta-pattern");
        try {
            FileInputStream fstream = new FileInputStream(logFilename);
            ParseWebLog webLogParser = new ParseWebLog(fstream, metaPattern);
            webLogParser.parseLog();
            webLogParser.outputParsedData();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "Unable to find file {1}", new Object[]{logFilename});
        }

    }
}
