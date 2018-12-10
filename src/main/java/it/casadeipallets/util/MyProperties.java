package it.casadeipallets.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The MyProperties class is used to read all the properties stored in file needed to start the simulator.
 * There is only one instance of this class (singleton).
 */
public class MyProperties extends Thread {

    private Logger              log = null; // log file
    private static              MyProperties instance = null;
    private boolean             propertiesLoaded = false;
    private WatchService        watchService = null;
    private String              dirPath = null;
    private String              fileName = null;
    private String              fullFileName = null;
    private Properties          prop = null;
    
    public String               ROBOT_IP;
    public int                  ROBOT_PORT;
    public int                  SOCKET_TIMEOUT_MS;
    public int                  SOCKET_MAX_READ_RETRY;
    public int                  QUERY_FREQUENCY_MS;
    public String               ROBOT_VARIABLES;
    public String               IOTTO_URL;
    public String               IOTTO_USR;
    public String               IOTTO_PWD;


    /**
     * The constructor is private so that only an internal method can create a new instance of this class (singleton)
     */
    private MyProperties() {
        super("MyProperties");
        // get the logger
        log = LogManager.getLogger();
        log.debug("Created new instance of MyProperties");
    }

    /**
     * Returns the current instance of this class.
     * If this class was never instantiated before, then will be instantiated.
     * @return the current instance of this class
     */
    public static MyProperties getInstance() {
        // if the instance is not yet instantiated, instantiate now with defaults
        if (instance == null) {
            instance = new MyProperties();
        }
        return instance; 
    }
    
    @Override
    public void run() {
        
        log.debug("###" );
        log.debug("### Started the thread for monitoring properties changes" );
        
        WatchKey key = null;
        boolean keepOn = true;
        while (keepOn) {
            try {
                key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().equals(fileName)) {
                        reloadProperties(fullFileName);
                    }
                }
            } catch (ClosedWatchServiceException e) { 
                /* OK */
                keepOn = false;;
            } catch (Exception e) {
                log.error("Error in watching properties changes", e);
            }

            if (key != null) {
                boolean reset = key.reset();
                if (!reset) {
                    log.error("Could not reset the watch key");
                    break;
                }
            }
            
        }

    }

    /**
     * stop watching file properties for changes
     */
    public void stopWatching() {
        if (propertiesLoaded) {
            try {
                watchService.close();
                log.debug("Quitting - Removed watch service for folder " + dirPath);
            } catch (IOException e) {
                log.error("Error in removing watch service", e);
            }
            
        }
    }
    
    /**
     * Load properties from property file
     * @param p_propFile - property file name
     * @throws Exception
     */
    public void loadProperties( String p_propFile ) throws Exception {

        log.info("--> Reading project properties");
        
        if (propertiesLoaded) {
            log.info("Properties already loaded");
            return;
        }
        
        // load properties
        load(p_propFile);
        propertiesLoaded = true;

        // create a new watch service
        watchService = FileSystems.getDefault().newWatchService();
        
        // register ENTRY_MODIFY for the folder containing the properties file
        File file = new File(p_propFile);
        fullFileName = file.getAbsolutePath();
        int lastIndex = fullFileName.lastIndexOf(File.separator);
        dirPath = fullFileName.substring(0, lastIndex + 1);
        fileName = fullFileName.substring(lastIndex + 1, fullFileName.length());
        
        Path path = Paths.get(dirPath);
        path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        
        // remove the watch service at shutdown
        Runtime.getRuntime().addShutdownHook(new Thread("MyProperties-ShutDownHook") {
            public void run() {
                //setName("HubSimProp-ShutDownHook");
                stopWatching();
            }
        });
        
        // start the current thread
        this.start();
    }
    
    /**
     * Reload properties from property file
     * @param p_propFile - property file name
     * @throws Exception
     */
    public void reloadProperties( String p_propFile ) throws Exception {

        log.info("--> Reloading project properties");
        
        load(p_propFile);

    }
    
    /**
     * load properties
     * @param propFileName - properties file name
     * @throws Exception
     */
    private void load(String propFileName) throws Exception {
        prop = new Properties();

        // open and read property file
        try {

            File f = new File(propFileName);
            if (f.length() == 0) {
                log.info("configuration file of zero length... skipping reloading");
                return;
            }
            FileInputStream fin = new FileInputStream(f);
            
            prop.load(fin);

        } catch (FileNotFoundException e) {
            log.error("Error in opening propertis file: file not found!", e);
            throw e;
        } catch (IOException e) {
            log.error("Error in opening propertis file!", e);
            throw e;
        } catch (Exception e) {
            log.error("Unknown error in opening propertis file!", e);
            throw e;
        } 

        // read the properties
        readProperties();

    }

    /**
     * Gets a property value
     * @param propertyName
     * @return property vale
     */
    public String getPropValues(String propertyName) {
        String result = null;
        result = prop.getProperty(propertyName);
        return result;
    }

    /**
     * Returns true if the properties have been loaded, false otherwise
     */
    public boolean arePropertiesLoaded() {
        return this.propertiesLoaded;
    }
    
    /**
     * Reads the properties object and store the values in public variables
     * @param prop - properties object
     */
    private void readProperties() {

        //
        try { ROBOT_IP = prop.getProperty("ROBOT_IP").trim(); } 
        catch (Exception e) { 
            ROBOT_IP = null; 
        }
        log.info("ROBOT_IP: " + ROBOT_IP);
        //
        try { ROBOT_PORT = Integer.parseInt(prop.getProperty("ROBOT_PORT", "1101")); } 
        catch (Exception e) { 
            ROBOT_PORT = 1101; 
        }
        log.info("ROBOT_PORT: " + ROBOT_PORT);
        //
        try { SOCKET_TIMEOUT_MS = Integer.parseInt(prop.getProperty("SOCKET_TIMEOUT_MS", "0")); } 
        catch (Exception e) { 
            SOCKET_TIMEOUT_MS = 0; 
        }
        log.info("SOCKET_TIMEOUT_MS: " + SOCKET_TIMEOUT_MS + "ms");
        //
        try { SOCKET_MAX_READ_RETRY = Integer.parseInt(prop.getProperty("SOCKET_MAX_READ_RETRY", "3")); } 
        catch (Exception e) { 
            SOCKET_MAX_READ_RETRY = 3; 
        }
        log.info("SOCKET_MAX_READ_RETRY: " + SOCKET_MAX_READ_RETRY);
        //
        try { QUERY_FREQUENCY_MS = Integer.parseInt(prop.getProperty("QUERY_FREQUENCY_MS", "2000")); } 
        catch (Exception e) { 
            QUERY_FREQUENCY_MS = 2000; 
        }
        log.info("QUERY_FREQUENCY_MS: " + QUERY_FREQUENCY_MS + "ms");
        //
        try { IOTTO_URL = prop.getProperty("IOTTO_URL").trim(); } 
        catch (Exception e) { 
            IOTTO_URL = null; 
        }
        log.info("IOTTO_URL: " + IOTTO_URL);
        //
        try { IOTTO_USR = prop.getProperty("IOTTO_USR").trim(); } 
        catch (Exception e) { 
            IOTTO_USR = null; 
        }
        log.info("IOTTO_USR: " + IOTTO_USR);
        //
        try { IOTTO_PWD = prop.getProperty("IOTTO_PWD").trim(); } 
        catch (Exception e) { 
            IOTTO_PWD = null; 
        }
        log.info("IOTTO_PWD: " + IOTTO_PWD);
        //
        try { 
            ROBOT_VARIABLES = prop.getProperty("ROBOT_VARIABLES").trim();
            if (!ROBOT_VARIABLES.startsWith("CMD_READ;")) {
                ROBOT_VARIABLES = "CMD_READ;" + ROBOT_VARIABLES;
            }
        } 
        catch (Exception e) { 
            ROBOT_VARIABLES = null; 
        }
        log.info("ROBOT_VARIABLES: " + ROBOT_VARIABLES);
        
//
    }

}
