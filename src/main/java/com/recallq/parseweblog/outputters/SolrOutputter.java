package com.recallq.parseweblog.outputters;

import com.recallq.parseweblog.Outputter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

/**
 * Sends the log data to Solr via solrj.
 *
 * @author Jeroen De Swaef <j@recallq.com>
 */
public class SolrOutputter implements Outputter {

    private final String solrUrl;

    public SolrOutputter(String solrUrl) {
        this.solrUrl = solrUrl;
    }

    public void outputData(List<Map<String, Object>> logData) throws IOException {
        HttpSolrServer server = new HttpSolrServer(solrUrl);
        server.setMaxRetries(1); // defaults to 0.  > 1 not recommended.
        server.setConnectionTimeout(15000); // 5 seconds to establish TCP
        server.setSoTimeout(5000);  // socket read timeout
        server.setDefaultMaxConnectionsPerHost(100);
        server.setMaxTotalConnections(100);
        server.setFollowRedirects(false);  // defaults to false
        // allowCompression defaults to false.
        // Server side must support gzip or deflate for this to have any effect.
        server.setAllowCompression(true);
        
        try {
            server.deleteByQuery( "*:*" );// CAUTION: deletes everything!
        } catch (SolrServerException ex) {
            Logger.getLogger(SolrOutputter.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        
        for(Map<String, Object> logEntry: logData) {
            SolrInputDocument doc = new SolrInputDocument();
            Long uuid = UUID.randomUUID().getMostSignificantBits();
            doc.addField("id", uuid);
            for (Map.Entry<String, Object> logField: logEntry.entrySet()) {
                Object value = logField.getValue();
                
                if (value instanceof String) {
                    doc.addField(logField.getKey(), value);
                } else if (value instanceof Map) {
                    
                }
            }
           
            docs.add(doc);
        }
        
        try {
            server.add( docs );
            server.commit();
        } catch (SolrServerException ex) {
            Logger.getLogger(SolrOutputter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
