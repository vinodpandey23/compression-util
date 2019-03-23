/**
 * THIS PROGRAM IS GENERATED FOR FREE USE.
 * 
 * @createdAt 05-OCT-2019
 *
 */
package com.agoda.compression.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Configuration Utility Class
 * 
 * @author Vinod Pandey
 *
 */
public enum ConfigUtil {

	/** singleton instance of configuration utility */
	CONFIG;

	private static final Logger LOG = Logger.getLogger(ConfigUtil.class.getName());

	private Properties properties = null;

	/**
	 * Getter for configuration properties reference
	 * 
	 * @return reference of properties
	 */
	public Properties getProperties() {

		if (properties == null) {

			if (LOG.isDebugEnabled()) {
				LOG.debug("first invocation of configuration usage");
			}

			loadProperties();
		}

		return properties;
	}

	/**
	 * Load properties from configuration file
	 */
	private void loadProperties() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("start - load configuration properties");
		}

		properties = new Properties();

		/**
		 * read property file using input stream and load all configuration properties
		 * that can be used later
		 */
		try (InputStream inStream = Files.newInputStream(Paths.get("resources.properties"))) {
			properties.load(inStream);
		} catch (IOException e) {
			System.err.println("error while reading property file: " + e.getMessage());
			LOG.fatal("error while reading property file", e);
			System.exit(0);
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("start - load configuration properties");
		}

	}

}
