package com.recallq.parseweblog;

import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jeroen De Swaef <j@recallq.com>
 */
public class ParseWebLogTest {

    /**
     * Test of parseLog method, of class ParseWebLog.
     */
    @Test
    public void testParseLog() throws IOException {
        InputStream is = ClassLoader.class.getResourceAsStream("/access.log");
        String metaPattern = "$remote_addr - $remote_user [$time_local] \"$request\" $status $body_bytes_sent \"$http_referer\" \"$http_user_agent\" \"$http_x_forwarded_for\"";
        ParseWebLog instance = new ParseWebLog(is, metaPattern);
        instance.parseLog();
        assertEquals(4, instance.getLogData().size());
        assertEquals("1395640768000", instance.getLogData().get(2).get("time_local"));
    }
    
}
