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

    private static final String START_OF_COMMAND = "CMD_READ;";
    private static final String END_OF_COMMAND = "\n";
    
    private String command = null;
    private Logger log = null; // log file

    //constructor

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
        if (!cmd.startsWith(START_OF_COMMAND)) {
            command = START_OF_COMMAND + cmd;
        } else {
            command = cmd;
        }
        if (!cmd.endsWith(END_OF_COMMAND)) {
            command = command + END_OF_COMMAND;
        } 
        log.info("Prepared command: <" + command.substring(0, command.length() - END_OF_COMMAND.length()) + ">");
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
                out.write(command.getBytes());
                log.info("Sent command: <" + command.substring(0, command.length() - END_OF_COMMAND.length()) + ">");
                return true;
            } catch (IOException e) {
                log.error(e);
            }
        }

        return false;
    }


}
