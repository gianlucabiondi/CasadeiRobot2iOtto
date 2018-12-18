package it.casadeipallets.robot;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.casadeipallets.util.RobotVariable;

/**
 * This class contains all the variable that should be requested to the robot together with their values (received) and their characteristics
 */
public class RobotResponse extends HashMap<String, RobotVariable> {

    private static final String UNKNOWN = "UNKNOWN";
    private static final String RESPONSE_HEADER = "CMD_READ-REP";
    
    private static final long serialVersionUID = -8358317165723982244L;
    private Logger log = null; // log file
    
    private String responseType = null;

    //constructor

    /**
     * Construct a new command with the byte array received from the client via socket
     */
    public RobotResponse() {
        // get the logger
        log = LogManager.getLogger();

    }

    // getters/setters
    public String getResponseType() {
        return responseType;
    }

    // methods

    /**
     * replaces all the values of the variables in the map.
     */
    public void clearValues() {
        responseType = null;
        for (Map.Entry<String, RobotVariable> entry: this.entrySet()) {
            entry.getValue().setValue(null);
        }
    }
    
    /**
     * Reads a response from the robot. If it is a valid response then
     * @param in - the reader
     * @return
     */
    public boolean receive(BufferedReader in) {
        
        try {
            this.clearValues();
            
            String response = in.readLine();
            log.info("Received from robot: " + response);
            
            if (response == null) {
                log.error("\tEmpty response!");
                return false;
            }
            String[] responseArr = response.split(";");
            
            for (int i = 0; i < responseArr.length; i++) {
                
                if (i == 0 && responseArr[i].equals(RESPONSE_HEADER)) {
                    responseType = responseArr[i];
                    log.info("\t" + responseType);
                } else {
                    String[] keyValue = responseArr[i].split("=");
                    if (this.containsKey(keyValue[0])) {
                        if (!keyValue[1].equals(UNKNOWN)) {
                            this.get(keyValue[0]).setValue(keyValue[1]);
                            log.info("\t" + keyValue[0] + " ==> " + keyValue[1]);
                        } else {
                            this.get(keyValue[0]).setValueError();
                            log.info("\tUnknown variable: " + keyValue[0]);
                        }
                    } else {
                        log.info("\tInvalid variable received: " + keyValue[0] + " = " + keyValue[0]);
                    }
                }
            }
            
        } catch (Exception e) {
            log.error(e);
            return false;
        }
        
        return true;
    }

}
