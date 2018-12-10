package it.casadeipallets.robot;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class correspond to a request for the robot
 *
 */
public class RobotRequest {

    private static final String END_OF_COMMAND = "\n";
    
    private String command = null;
    private Logger log = null; // log file

    //constructor

    /**
     * Construct a new command using defaults
     */
    public RobotRequest() {
        // get the logger
        log = LogManager.getLogger();

        setCommand("CMD_READ;variabile1;variabile2;");
    }

    /**
     * Construct a new command using the string received
     */
    public RobotRequest(String command) {
        // get the logger
        log = LogManager.getLogger();

        setCommand(command);
    }

    // getters/setters
    /**
     * Return the constructed command
     * @return
     */
    public String getCommand() {
        return command;
    }
    /**
     * Sets the command
     * @return
     */
    public void setCommand(String cmd) {
        command = cmd;
        log.info("Prepared command: <" + command + ">");
    }

    // methods

    /**
     * Send a command to the robot. Returns true if the command is correctly received
     * @param out - the writer
     * @return
     */
    public boolean send(OutputStream out) {
        if (command != null) {
            try {
                out.write((command + END_OF_COMMAND).getBytes());
                log.info("Sent command: <" + command + ">");
                return true;
            } catch (IOException e) {
                log.error(e);
            }
        }

        return false;
    }


}
