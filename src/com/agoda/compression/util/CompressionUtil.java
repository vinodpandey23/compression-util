/**
 * THIS PROGRAM IS GENERATED FOR FREE USE.
 * 
 * @createdAt 05-OCT-2019
 *
 */
package com.agoda.compression.util;

import static com.agoda.compression.constants.CompressionUtilConstants.COMPRESS;
import static com.agoda.compression.constants.CompressionUtilConstants.DECOMPRESS;
import static com.agoda.compression.constants.CompressionUtilConstants.DEFAULT_COMP;

import org.apache.log4j.Logger;

import com.agoda.compression.Compression;
import com.agoda.compression.factory.CompressionImpFactory;
import com.agoda.compression.model.CompressionInput;
import com.agoda.compression.model.DecompressionInput;

/**
 * Compression Utility Class for compression and decompression related
 * functionalities
 * 
 * @author Vinod Pandey
 *
 */
public enum CompressionUtil {

	/** singleton instance of compression utility */
	COMPRESSION_UTIL;

	private static final Logger LOG = Logger.getLogger(CompressionUtil.class.getName());

	private String compressionType = null;

	private Compression compressionImpl = null;

	/**
	 * Setter for identifier of compression implementation
	 * 
	 * @param compressionType
	 *            value of compressionType
	 */
	public void setCompressionType(String compressionType) {
		this.compressionType = compressionType;
		initCompressionImpl();
	}

	/**
	 * Getter for compression implementation
	 * 
	 * @return value of compressionImpl
	 */
	private Compression getCompressionImpl() {
		return compressionImpl;
	}

	/**
	 * Initialize compression implementation based on user provided input or default
	 * compression implementation
	 */
	private void initCompressionImpl() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("start - compression implementation initialization");
		}

		LOG.info("compression implementation initialization started");

		if (compressionType == null || compressionType.trim().isEmpty()) {

			LOG.info("compression type is not provided by user");

			String defaultCompression = DEFAULT_COMP;

			if (defaultCompression == null || defaultCompression.trim().isEmpty()) {
				LOG.fatal("default compresstion type is also not available in configuration");
				throw new IllegalArgumentException("No default compressionn is available in properties file");
			}

			compressionImpl = CompressionImpFactory.getCompressionImp(defaultCompression);

		} else {

			LOG.info("compression type is provided by user: " + compressionType);

			compressionImpl = CompressionImpFactory.getCompressionImp(compressionType);

			if (compressionImpl == null) {
				LOG.fatal("implementation is not supported for used provided compresstion type");
				throw new IllegalArgumentException(
						"implementation is not supported for used provided compresstion type: " + compressionType);
			}
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("complete - compression implementation initialization");
		}

	}

	/**
	 * Prepares input parameters for compression implementation and start operation
	 * with the help of separate thread to avoid user waiting
	 * 
	 * @param compInput
	 *            value of compInput
	 */
	public void compress(CompressionInput compInput) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("start - compression process");
		}

		/**
		 * invoke compression implementation initialization process, if not initialized
		 * earlier because user did not provided compression type
		 */
		if (getCompressionImpl() == null) {
			initCompressionImpl();
		}

		getCompressionImpl().setOperation(COMPRESS);
		getCompressionImpl().setCompInput(compInput);

		Thread thread = new Thread(getCompressionImpl());

		printMsg(COMPRESS, thread.getId());

		LOG.info("decompression started for input directory: " + compInput.getInputDirPath());

		thread.start();

		if (LOG.isTraceEnabled()) {
			LOG.trace("complete - compression process");
		}
	}

	/**
	 * Prepares input parameters for decompression implementation and start
	 * operation with the help of separate thread to avoid user waiting
	 * 
	 * @param dcompInput
	 *            value of dcompInput
	 */
	public void decompress(DecompressionInput dcompInput) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("start - decompression process");
		}

		/**
		 * invoke compression implementation initialization process, if not initialized
		 * earlier because user did not provided compression type
		 */
		if (getCompressionImpl() == null) {
			initCompressionImpl();
		}

		getCompressionImpl().setOperation(DECOMPRESS);
		getCompressionImpl().setDecompInput(dcompInput);

		Thread thread = new Thread(getCompressionImpl());

		printMsg(DECOMPRESS, thread.getId());

		LOG.info("decompression started for input directory: " + dcompInput.getInputDirPath());

		thread.start();

		if (LOG.isTraceEnabled()) {
			LOG.trace("complete - decompression process");
		}

	}

	/**
	 * Print message to console to inform status of operation
	 * 
	 * @param operation
	 *            operation name
	 * @param requestId
	 *            thread id as request identifier
	 */
	private void printMsg(String operation, long requestId) {

		System.out.println(operation + " operation has been submitted.");
		System.out.println("Use request-id " + requestId + " for tracking!!!");
		System.out.println("You can proceed for new operation in parallel...");

	}

}
