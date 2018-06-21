package com.concordia.dsd.utils;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.constants.CMSConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Singleton LoggingUtil
 */
public class LoggingUtil {

	private static LoggingUtil instance;
	private LoggingUtil() {
		Path logPath = Paths.get(Paths.get(".").toAbsolutePath().toString(), CMSConstants.LOGS_BASE_PATH);
		File logDir = new File(logPath.toUri());
		if(!logDir.exists()){
			logDir.mkdir();
		}
	}
	public static LoggingUtil getInstance() {
		if(instance==null)
			instance=new LoggingUtil();
		return instance;
	}

	/**
	 * Automatically create required Files and dir if not available for logging
	 * @param location
	 */
	public void mkRequiredFilesAndDir(Location location){
		Path locLogPath = Paths.get(Paths.get(".").toAbsolutePath().toString(), CMSConstants.LOGS_BASE_PATH, location.toString());
		Path filePath = Paths.get(Paths.get(".").toAbsolutePath().toString(), CMSConstants.LOGS_BASE_PATH, location.toString(),
				location.toString() + "_Server.log");
		File locLogDir = new File(locLogPath.toUri());
		File locFile  = new File(filePath.toUri());
		if(!locLogDir.exists()){
			locLogDir.mkdir();
		}
		if(!locFile.exists()){
			try {
				locFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get Logger based on Location
	 * @param location
	 * @return
	 * @throws SecurityException
	 * @throws IOException
	 */
	public Logger getServerLogger(Location location) throws SecurityException, IOException {
		mkRequiredFilesAndDir(location);
		Logger logger = Logger.getLogger(location.toString());
		Path path = Paths.get(Paths.get(".").toAbsolutePath().toString(), CMSConstants.LOGS_BASE_PATH, location.toString(),
				location.toString() + "_Server.log");
		FileHandler fHandler = new FileHandler(path.toString(), true);
		fHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(fHandler);
		logger.setLevel(Level.INFO);
		return logger;
	}

	/**
	 * Get Client Logger based on Manager Id
	 * @param managerId
	 * @return
	 * @throws SecurityException
	 * @throws IOException
	 */
	public Logger getClientLogger(String managerId) throws SecurityException, IOException {
		String location = managerId.substring(0, 3);
		Logger logger = Logger.getLogger(location);
		mkRequiredFilesAndDir(Location.valueOf(location));
		Path path = Paths.get(Paths.get(".").toAbsolutePath().toString(), CMSConstants.LOGS_BASE_PATH, location,
				managerId + "_Client.log").normalize();
		FileHandler fHandler = new FileHandler(path.toString(), true);
		fHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(fHandler);
		logger.setLevel(Level.INFO);
		return logger;
	}

}
