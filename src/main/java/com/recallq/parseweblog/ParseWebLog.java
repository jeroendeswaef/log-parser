package com.recallq.parseweblog;

import com.recallq.parseweblog.fieldparser.LocalTimeFieldParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
    
    private static List<String> logFieldNames = new ArrayList<String>();
    
    // Returns a pattern where all punctuation characters are escaped.
    static Pattern escaper = Pattern.compile("([\\[\\]])");

    public static String escapeRE(String str) {
        return escaper.matcher(str).replaceAll("\\\\$1");
    }

    public static void main(String... arguments) {
        String logFilename = null;
        OptionParser parser = new OptionParser();
        parser.accepts(LOGFILE_PARAMETER).withRequiredArg();

        OptionSet options = parser.parse(arguments);
        if (options.has(LOGFILE_PARAMETER)) {
            logFilename = (String) options.valueOf(LOGFILE_PARAMETER);
        }
        Properties prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("config.properties");
        try {
            prop.load(stream);
        } catch (IOException ex) {
            Logger.getLogger(ParseWebLog.class.getName()).log(Level.SEVERE, null, ex);
        }
        String metaPattern = prop.getProperty("aa");
        Pattern pattern = Pattern.compile("\\$[a-zA-Z0-9_]*");
        Matcher matcher = pattern.matcher(metaPattern);

        int parsedPosition = 0;
        StringBuilder parsePatternBuilder = new StringBuilder();

        while (matcher.find()) {
            System.out.print(">" + matcher.start() + "-" + parsedPosition + "<");
            if (parsedPosition < matcher.start()) {
                System.out.print(metaPattern.substring(parsedPosition, matcher.start()));
                String residualPattern = metaPattern.substring(parsedPosition, matcher.start());

                parsePatternBuilder.append(escapeRE(residualPattern));
            }
            String logFieldName = metaPattern.substring(matcher.start() + 1, matcher.end());
            logFieldNames.add(logFieldName);
            System.out.println(metaPattern.substring(matcher.start() + 1, matcher.end()));
            parsedPosition = matcher.end();
            char splitCharacter = metaPattern.charAt(matcher.end());
            parsePatternBuilder.append("([^");
            if (charactersToEscape.contains(splitCharacter)) {
                parsePatternBuilder.append("\\");
            }
            parsePatternBuilder.append(splitCharacter);
            parsePatternBuilder.append("]*)");
            System.out.print("Start index: " + matcher.start());
            System.out.print(" End index: " + matcher.end() + " ");
            System.out.print("--" + metaPattern.charAt(matcher.end()));
            System.out.println(matcher.group());
        }
        parsePatternBuilder.append(metaPattern.substring(parsedPosition, metaPattern.length()));
        System.out.println(parsePatternBuilder.toString());

        if (logFilename != null) {
            Pattern logFilePattern = Pattern.compile(parsePatternBuilder.toString());
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(logFilename));
                String line;
                while ((line = br.readLine()) != null) {
                    Matcher logFileMatcher = logFilePattern.matcher(line);
                    
                    if (logFileMatcher.matches()) {
                        for(int i = 1; i < logFileMatcher.groupCount(); i++) {
                            String logFieldName = logFieldNames.get(i - 1);
                            FieldParser fieldParser = fieldParsers.get(logFieldName);
                            String fieldValue = null;
                            if (fieldParser != null) {
                                fieldValue = fieldParser.parse(logFileMatcher.group(i));
                            } else {
                                fieldValue = logFileMatcher.group(i);
                            }
                            System.out.print(fieldValue + "|");
                        }
                        System.out.print("\n");
                   
                    }
                    
                }
                br.close();
            } catch (Exception ex) {
                Logger.getLogger(ParseWebLog.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
