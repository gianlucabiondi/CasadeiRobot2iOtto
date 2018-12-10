package it.casadeipallets;

import java.io.IOException;
import java.net.SocketException;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.casadeipallets.iotto.IOttoHandler;
import it.casadeipallets.robot.RobotHandler;
import it.casadeipallets.util.MyProperties;

/**
 * Class containing only the main method used to start the application
 * 
 * @author gianluca
 *
 */
public class Robot2iOtto {

    private static String version = "0.05";
    private static String versionMessage = "Casadei Pallets Robot2iOtto " + version;

    /**
     * Main method used to
     * - parse & check input parameters
     * - instantiate & start a new listener
     * - instantiate & start a new configurator listener
     * 
     * @param args
     * @throws IOException 
     * @throws SocketException 
     */
    public static void main(String[] args) throws SocketException, IOException {

        // get the logger
        Logger log = LogManager.getLogger();
        
        log.log(Level.forName("VERBOSE", 1), versionMessage);
        log.log(Level.forName("VERBOSE", 1), "Parameters [" + args.length + "]:");
        for (int i = 0; i < args.length; i++) {
            log.log(Level.forName("VERBOSE", 1), "\t[" + i + "] - "+ args[i]);
        }

        // cycle all the options and at the end, launch the poller
        try {
            int i = 0;
            while (i < args.length) {
                
                switch (args[i].toLowerCase()) {
                        
                default: // configuration file (only last parameter)
                    if (i == args.length - 1) {
                        String configFile = args[i];
                        log.info("Configuration file is  " + configFile);
                        MyProperties.getInstance().loadProperties(configFile);
                    } else {
                        throw new Exception();
                    }
                    break;
                }

                i++;
            }
        } catch (Exception e) {
            showUsageAndExit();
            return;
        }
        
        // check usage
        if (!MyProperties.getInstance().arePropertiesLoaded()) {
            showUsageAndExit();
            return;
        }
        
        // start polling on the robot...
        log.info("Starting poller......");
        new Robot2iOtto().startPolling();

        // stop all the work
        MyProperties.getInstance().stopWatching();
    }
    
    /**
     * Print the usage help and exit the simulator
     */
    private static void showUsageAndExit() {
        System.err.println("");
        System.err.println(versionMessage);
        System.err.println("Correct Usage: java Robot2iOtto <propertyFile.txt>");
        System.err.println("");
        System.exit(1);
        
    }
    
    private void startPolling() {

        // get the logger
        Logger log = LogManager.getLogger();

        /*******
         * connect to the robot
         */
        RobotHandler robot = new RobotHandler();
        if (!robot.isConnected()) {
            log.error("Unable to connect to the robot. Closing interface");
            return;
        }

        /*******
         * connect to iOtto
         */
        IOttoHandler iOtto = new IOttoHandler();
        if (!iOtto.isConnected()) {
            log.error("Unable to connect to iOtto. Closing interface");
            return;
        }
        
        /*******
         *  polling
         */
        long timeStart;
        long timeToWaste;
        String[] cmd = null;
        if (MyProperties.getInstance().ROBOT_VARIABLES != null && !MyProperties.getInstance().ROBOT_VARIABLES.equals("")) {
            cmd = new String[]{ MyProperties.getInstance().ROBOT_VARIABLES };
        } else {
            cmd = new String[]{ "CMD_READ;variabile1;", 
                    "CMD_READ;variabile1;variabile2;",
                    "CMD_READ;variabile1;variabile2;variabile3;"
            };
        }
        int idx = -1;
        while (true) {
            timeStart = System.currentTimeMillis();
            
            idx = (idx + 1) % cmd.length;
            
            // read from the robot
            Map<String, String> paramsFromRobot;
            paramsFromRobot = robot.getParamsFromRobot(cmd[idx]);
            
            // write to iOtto
            iOtto.setParamsToiOtto(paramsFromRobot);
            
            // maintain the pace
            timeToWaste = MyProperties.getInstance().QUERY_FREQUENCY_MS - (System.currentTimeMillis() - timeStart);
            if (timeToWaste > 0) {
                try {
                    log.info("Waiting " + timeToWaste + "ms .......\n");
                    Thread.sleep(timeToWaste);
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }
        }

        /*****
         * close connections
         */
//        robot.close();
//        iOtto.close();
        
    }
    
}
