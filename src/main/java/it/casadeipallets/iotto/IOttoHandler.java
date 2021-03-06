package it.casadeipallets.iotto;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.casadeipallets.robot.RobotResponse;
import it.casadeipallets.util.MyProperties;
import it.casadeipallets.util.RobotVariable;

/**
 * This class contains information and handles the robot
 *
 */
public class IOttoHandler {

    private Logger log = null; // log file

    private URL url = null;
    private boolean connected = false;

    /**
     *  constructor
     */
    public IOttoHandler() {
        // get the logger
        log = LogManager.getLogger();

        // create a new "connection" to iOtto
        connect();
    }

    /**
     * Connect to iOtto
     * @return
     */
    private boolean connect() {

        // create a new "connection" to iOtto
        try {
            // creation
            url = new URL(MyProperties.getInstance().IOTTO_URL);

            connected = true;

        } catch (Exception e) {
            log.error(e);

            url = null;
            connected = false;
            return false;
        }

        return true;

    }

    /**
     * Returns the "connection" state of the handler. 
     * Returns:true if the URL object was created correctly. 
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Sends multiple key-values to iOtto
     * @return true if iOtto answered correctly
     */
    public boolean setParamsToiOtto(RobotResponse map) {

        if (map == null) {
            log.info("Received an empty list of values");
            return false;
        }

        try {
            if (!isConnected() && !connect()) {
                return false;
            }
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");

            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            byte[] usrPwd = (MyProperties.getInstance().IOTTO_USR + ":" + MyProperties.getInstance().IOTTO_PWD).getBytes();
            String usrPwdEncoded = Base64.getEncoder().encodeToString(usrPwd);
            con.setRequestProperty("Authorization", "Basic " + usrPwdEncoded);

            con.setDoInput(true);
            con.setDoOutput(true);

            // Send parameters
            OutputStream os = con.getOutputStream();
            JsonElement body = toJson(map);
            os.write(body.toString().getBytes());
            os.close();

            //Read back the response
            JsonElement jsonTree = null;
            try {
                jsonTree = (new JsonParser()).parse(new InputStreamReader(con.getInputStream()));
            } catch (Exception e) {
                log.error(e);
                return false;
            }
            log.info(jsonTree);
            log.info("\tResponse: " + con.getResponseCode() + " - " + con.getResponseMessage());

            if (con.getResponseCode() != 200) {
                return false;
            }

            if (jsonTree != null &&
                    jsonTree.getAsJsonObject() != null &&
                    jsonTree.getAsJsonObject().get("Result") != null &&
                    jsonTree.getAsJsonObject().get("Result").getAsInt() != 0) {
                return false;
            }

        } catch (IOException e) {
            log.error(e);
            return false;
        }

        return true;
    }

    /**
     * Traslate the map in a json object
     * @param map - contains key-values read from the robot 
     * @return json object to send to iOtto
     */
    private JsonElement toJson(RobotResponse map) {

        JsonObject jsonTree = new JsonObject();
        JsonArray streamValues = new JsonArray();
        jsonTree.add("StreamValues", streamValues);

        // for every key-value in the map create a new "stream value"
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String strUTCDateTime = dateFormatter.format(new Date());
        for (Entry<String, RobotVariable> entry : map.entrySet()) {
            JsonObject streamValue = new JsonObject();
            streamValue.addProperty("ExternalReference", entry.getValue().getExternalReference());
            streamValue.addProperty("TimezoneId", "W. Europe Standard Time");
            streamValue.addProperty("UnitId", entry.getValue().getUnits());
            streamValue.addProperty("ValueType", 2);
            streamValue.addProperty("CustomUnit", entry.getValue().getCustomUnits());
            streamValue.addProperty("Name", entry.getValue().getName());
            streamValue.addProperty("NumericType", entry.getValue().getNumericType());
            JsonArray values = new JsonArray();
            JsonObject value = new JsonObject();
            value.addProperty("Value", entry.getValue().getValue());
            value.addProperty("UTCFrom", strUTCDateTime);
            value.addProperty("UTCTo", strUTCDateTime);
            value.addProperty("ValueState", entry.getValue().getValueState());
            values.add(value);
            streamValue.add("Values", values);
            streamValues.add(streamValue);
        }
        log.info(jsonTree);

        return jsonTree;
    }

    /**
     * close the handler
     */
    public void close() {

        /**********
         * closing connection
         */

    }
}

