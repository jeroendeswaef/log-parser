package com.recallq.parseweblog;

import com.recallq.parseweblog.outputters.JsonOutputter;
import com.recallq.parseweblog.outputters.SolrOutputter;
import joptsimple.OptionSet;

/**
 * Creates outputter objects.
 * 
 * @author Jeroen De Swaef <j@recallq.com>
 */
public class OutputterFactory {
    private static OutputterFactory instance = null;
    
    private OutputterFactory() {
        
    }
    
    public static OutputterFactory getInstance() {
        if (instance == null) {
            instance = new OutputterFactory();
        }
        return instance;
    }
    
    public Outputter createFromOptions(OptionSet options) {
        Outputter outputter;
        if (options.hasArgument(Constants.SOLR_PARAMETER)) {
            String endpoint = (String) options.valueOf(Constants.SOLR_PARAMETER);
            if (options.hasArgument(Constants.SOLR_USER_PARAMETER) 
                    && options.hasArgument(Constants.SOLR_PASSWORD_PARAMETER)) {
                String userName = (String) options.valueOf(Constants.SOLR_USER_PARAMETER);
                String password = (String) options.valueOf(Constants.SOLR_PASSWORD_PARAMETER);
                outputter = new SolrOutputter(endpoint, userName, password);
            } else {
                outputter = new SolrOutputter(endpoint);
            }
        } else {
            outputter = new JsonOutputter(System.out);
        }
        return outputter;
    }
}
