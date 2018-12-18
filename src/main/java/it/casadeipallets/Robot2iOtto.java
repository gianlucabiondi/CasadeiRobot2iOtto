package it.casadeipallets;

import java.io.IOException;
import java.net.SocketException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.casadeipallets.iotto.IOttoHandler;
import it.casadeipallets.robot.RobotHandler;
import it.casadeipallets.robot.RobotResponse;
import it.casadeipallets.util.MyProperties;

/**
 * Class containing only the main method used to start the application
 * 
 * @author gianluca
 *
 */
public class Robot2iOtto {

    private static String version = "0.10";
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
                        try {
                            MyProperties.getInstance().loadProperties(configFile);
                        } catch(Exception e) {
                            log.error(e);
                            return;
                        }
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

        /*******
         * connect to iOtto
         */
        IOttoHandler iOtto = new IOttoHandler();

        /*******
         *  polling
         */
        long timeStart;
        long timeToWaste;
        boolean continueCycling = true;
        while (continueCycling) {
            timeStart = System.currentTimeMillis();

            // read from the robot
            RobotResponse paramsFromRobot = robot.getParamsFromRobot();

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

            // check if user requested to stop polling
            try {
                if (System.in.available() > 0) {
                    byte[] buf = new byte[2 * System.in.available()];
                    System.in.read(buf);
                    continueCycling = false;
                }
            } catch (IOException e) {
                log.error(e);
            }
        }

        /*****
         * close connections
         */
        log.info("Closing application.....");
        robot.close();
        iOtto.close();

    }

}
