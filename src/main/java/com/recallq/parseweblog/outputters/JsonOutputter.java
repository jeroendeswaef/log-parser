package com.recallq.parseweblog.outputters;

import com.google.gson.Gson;
import com.recallq.parseweblog.Outputter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Outputs the log data as JSON.
 *
 * @author Jeroen De Swaef <j@recallq.com>
 */
public class JsonOutputter implements Outputter {

    private OutputStream os;
    
    public JsonOutputter(OutputStream os) {
        this.os = os;
    }
    
    @Override
    public void outputData(List<Map<String, Object>> logData) throws IOException {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(logData);
        os.write(jsonStr.getBytes());
    }

}
