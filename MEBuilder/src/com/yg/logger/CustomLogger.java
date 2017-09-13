package com.yg.logger;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.yg.utilities.IOGeneralHelper;

/**
 * Custom logger setup
 * @author Yaroslava Girilishena
 *
 */
public class CustomLogger {
	
	public static FileHandler fileTxt;
	private static SimpleFormatter formatterTxt;
    
    public static void setup(String filename) throws IOException {
    	IOGeneralHelper.createOutDir("/log");
    	
        // Get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        
        File logFile = new File(filename);
		logFile.createNewFile(); // if file already exists will do nothing 

        // Suppress the logging output to the console
//        Logger rootLogger = Logger.getLogger("");
//        Handler[] handlers = rootLogger.getHandlers();
//        if (handlers[0] instanceof ConsoleHandler) {
//            rootLogger.removeHandler(handlers[0]);
//        }

        logger.setLevel(Level.ALL);
        fileTxt = new FileHandler(filename);

        // Create a TXT formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);
    }
}
