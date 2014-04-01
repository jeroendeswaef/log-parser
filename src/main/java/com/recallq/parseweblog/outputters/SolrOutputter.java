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
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
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
    private final String solrUsername;
    private final String solrPassword;
    
    private class PreemptiveAuthInterceptor implements HttpRequestInterceptor {

        public void process(final HttpRequest request, final HttpContext context)
                throws HttpException, IOException {
            AuthState authState = (AuthState) context
                    .getAttribute(ClientContext.TARGET_AUTH_STATE);

                // If no auth scheme avaialble yet, try to initialize it
            // preemptively
            if (authState.getAuthScheme() == null) {
                CredentialsProvider credsProvider = (CredentialsProvider) context
                        .getAttribute(ClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context
                        .getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                Credentials creds = credsProvider.getCredentials(new AuthScope(
                        targetHost.getHostName(), targetHost.getPort()));
                if (creds == null) {
                    throw new HttpException(
                            "No credentials for preemptive authentication");
                }
                authState.setAuthScheme(new BasicScheme());
                authState.setCredentials(creds);
            }

        }

    }

    public SolrOutputter(String solrUrl) {
        this.solrUrl = solrUrl;
        this.solrUsername = null;
        this.solrPassword = null;
    }

    public SolrOutputter(String solrUrl, String solrUsername, String solrPassword) {
        this.solrUrl = solrUrl;
        this.solrUsername = solrUsername;
        this.solrPassword = solrPassword;
    }

    
    public void outputData(List<Map<String, Object>> logData) throws IOException {
        // Providing a custom http client necessary to provide http authentication
        PoolingClientConnectionManager cxMgr = new PoolingClientConnectionManager(
                SchemeRegistryFactory.createDefault());
        cxMgr.setMaxTotal(100);
        cxMgr.setDefaultMaxPerRoute(20);
        DefaultHttpClient httpclient = new DefaultHttpClient(cxMgr);
        httpclient.addRequestInterceptor(
                new PreemptiveAuthInterceptor(), 0);

        HttpSolrServer server;
        if (this.solrUsername != null) {
            httpclient.getCredentialsProvider().setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(solrUsername, solrPassword));
            server = new HttpSolrServer(solrUrl, httpclient);
        } else {
            server = new HttpSolrServer(solrUrl);
        }

        server.setMaxRetries(1); // defaults to 0.  > 1 not recommended.
        server.setConnectionTimeout(15000); // 5 seconds to establish TCP
        server.setSoTimeout(5000);  // socket read timeout
        server.setFollowRedirects(false);  // defaults to false
        // allowCompression defaults to false.
        // Server side must support gzip or deflate for this to have any effect.
        server.setAllowCompression(true);

        /*try {
            server.deleteByQuery("*:*");// CAUTION: deletes everything!
        } catch (SolrServerException ex) {
            Logger.getLogger(SolrOutputter.class.getName()).log(Level.SEVERE, null, ex);
        }*/

        Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

        for (Map<String, Object> logEntry : logData) {
            SolrInputDocument doc = new SolrInputDocument();
            Long uuid = UUID.randomUUID().getMostSignificantBits();
            doc.addField("id", uuid);
            for (Map.Entry<String, Object> logField : logEntry.entrySet()) {
                Object value = logField.getValue();
                if (value instanceof Map) {
                   for (Map.Entry<String, Object> mapEntry: ((Map<String, Object>) value).entrySet()) {
                       String composedKey = (logField.getKey() + "-" + mapEntry.getKey());
                       doc.addField(composedKey, mapEntry.getValue());
                   }
                } else {
                    doc.addField(logField.getKey(), value);
                }
            }

            docs.add(doc);
        }

        try {
            server.add(docs);
            server.commit();
        } catch (SolrServerException ex) {
            Logger.getLogger(SolrOutputter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
