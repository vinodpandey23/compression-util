/**
 * THIS PROGRAM IS GENERATED FOR FREE USE.
 * 
 * @createdAt 05-OCT-2019
 *
 */
package com.agoda.compression.factory;

import static com.agoda.compression.constants.CompressionUtilConstants.JAVA_DEFAULT_ZIP;

import org.apache.log4j.Logger;

import com.agoda.compression.Compression;
import com.agoda.compression.impl.ZipJavaDefaultCompression;

/**
 * Factory class for all compression implementation classes
 * 
 * @author Vinod Pandey
 *
 */
public class CompressionImpFactory {

	private static final Logger LOG = Logger.getLogger(CompressionImpFactory.class.getName());

	/**
	 * Factory method for implementation classes. Currently supported only for
	 * Java's default zip compression
	 * 
	 * @param compressionType
	 *            identifier for compression implementation
	 * 
	 * @return compression implementation class object
	 */
	public static Compression getCompressionImp(String compressionType) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("start - creation of compression implementation");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("input value for compressionType: " + compressionType);
		}

		Compression implInstance;

		if (JAVA_DEFAULT_ZIP.equals(compressionType)) {
			implInstance = new ZipJavaDefaultCompression();
		} else {
			implInstance = null;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("identified compression implementation: " + implInstance == null ? "" : implInstance.getClass());
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("complete - creation of compression implementation");
		}

		return implInstance;

	}

}
