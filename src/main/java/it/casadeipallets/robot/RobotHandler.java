package it.casadeipallets.robot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.casadeipallets.util.MyProperties;

/**
 * This class contains information and handles the robot
 *
 */
public class RobotHandler {

    private Logger log = null; // log file

    private Socket sock = null;
    private OutputStream out = null;
    private BufferedReader in = null;
    private boolean connected = false;

    /**
     *  constructor
     */
    public RobotHandler() {
        // get the logger
        log = LogManager.getLogger();

        // connect to the robot
        connect();
    }

    /**
     * Connect the socket
     * @return
     */
    private boolean connect() {

        // create a new socket and connect it to the robot
        try {
            // creation
            sock = new Socket();

            // connection
            log.info("Connecting to the robot (" + MyProperties.getInstance().ROBOT_IP + ":" + MyProperties.getInstance().ROBOT_PORT + ")");
            sock.setSoTimeout( MyProperties.getInstance().SOCKET_TIMEOUT_MS );
            sock.connect( new InetSocketAddress(MyProperties.getInstance().ROBOT_IP, MyProperties.getInstance().ROBOT_PORT) );
            log.info("Socket connected to the robot......");

            // get the streams
            out = sock.getOutputStream();
            in = new BufferedReader( new InputStreamReader(sock.getInputStream()));

            connected = true;

        } catch (Exception e) {
            log.error(e);

            sock = null;
            out = null;
            in = null;
            connected = false;
            return false;
        }

        return true;

    }

    /**
     * Returns the connection state of the handler. 
     * Returns:true if the socket was successfully connected to a server
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Send a new command to the robot and read its answer
     * @return true if the robot answer correctly
     */
    public RobotResponse getParamsFromRobot() {
        try {

            if (!isConnected() && !connect()) {
                return null;
            }

            // send the command to the robot
            RobotRequest command = new RobotRequest(MyProperties.getInstance().VARIABLE_EXTERNAL_REFS_LIST);

            if (!command.send(out)) {
                // if we received an error during communication --> disconnect
                close();
                return null;
            }

            // read the answer from the robot
            RobotResponse response = MyProperties.getInstance().ROBOT_VARIABLES;
            if (!response.receive(in)) {
                // if we received an error during communication --> disconnect
                close();
                return null;
            }

            return response;

        } catch (Exception e) {
            log.error(e);
            return null;
        }

    }

    /**
     * close the robot handler
     */
    public void close() {

        /**********
         * closing socket
         */
        if (connected) {
            try {
                sock.close();
                out = null;
                in = null;
                sock = null;
                connected = false;
            } catch (Exception e) {
                log.error(e);
            }
        }

    }
}
