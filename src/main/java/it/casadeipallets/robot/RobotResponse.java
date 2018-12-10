package it.casadeipallets.robot;

import java.io.BufferedReader;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class correspond to a response from the robot
 *
 */
public class RobotResponse extends HashMap<String, String> {

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
     * Reads a response from the robot. If it is a valid response then
     * @param in - the reader
     * @return
     */
    public boolean receive(BufferedReader in) {
        
        try {
            this.clear();
            
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
                    if (!keyValue[1].equals(UNKNOWN)) {
                        this.put(keyValue[0], keyValue[1]);
                        log.info("\t" + keyValue[0] + " ==> " + keyValue[1]);
                    } else {
                        log.info("\tInvalid variable request: " + keyValue[0]);
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
