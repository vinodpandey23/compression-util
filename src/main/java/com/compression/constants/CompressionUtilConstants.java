/**
 * THIS PROGRAM IS GENERATED FOR FREE USE.
 * 
 * @createdAt 05-OCT-2019
 *
 */
package com.compression.constants;

import com.compression.util.ConfigUtil;

/**
 * Class for all constants and enums used in CompressionUtil tool
 * 
 * @author Vinod Pandey
 *
 */
public class CompressionUtilConstants {

	/** operation name constants */
	public static final String COMPRESS = "Compression";
	public static final String DECOMPRESS = "Decompression";

	/** compressed zip file name related constants */
	public static final String ZIP_EXTN = ".zip";
	public static final String PART_POSTFIX = ".part.";
	public static final String INDEX_FORMAT = "%03d";

	/** default compression technique related constants */
	public static final String JAVA_DEFAULT_ZIP = "java-default-zip";
	public static final String DEFAULT_COMP = ConfigUtil.CONFIG.getProperties().getProperty("default_compression");

	/** constants for internal processing of tool */
	public static final String SPLIT_FILE_ID = "-split.";
	public static final Long FS_FILE_HEAER_SIZE = 10240L;

}
