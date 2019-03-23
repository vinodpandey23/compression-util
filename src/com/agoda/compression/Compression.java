/**
 * THIS PROGRAM IS GENERATED FOR FREE USE.
 * 
 * @createdAt 05-OCT-2019
 *
 */
package com.agoda.compression;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.agoda.compression.model.CompressionInput;
import com.agoda.compression.model.DecompressionInput;

/**
 * Abstract class for compression implementations
 * 
 * @author Vinod Pandey
 *
 */
public abstract class Compression implements Runnable {

	private String operation = null;

	private CompressionInput compInput = null;

	private DecompressionInput decompInput = null;

	/**
	 * Getter for operation name
	 * 
	 * @return value of operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * Setter for operation name
	 * 
	 * @param operation
	 *            value of operation
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * Getter for compression input model object
	 * 
	 * @return value of compInput
	 */
	public CompressionInput getCompInput() {
		return compInput;
	}

	/**
	 * Setter for compression input model object
	 * 
	 * @param compInput
	 *            value of compInput
	 */
	public void setCompInput(CompressionInput compInput) {
		this.compInput = compInput;
	}

	/**
	 * Getter for decompression input model object
	 * 
	 * @return value of decompInput
	 */
	public DecompressionInput getDecompInput() {
		return decompInput;
	}

	/**
	 * Setter for decompression input model object
	 * 
	 * @param decompInput
	 *            value of decompInput
	 */
	public void setDecompInput(DecompressionInput decompInput) {
		this.decompInput = decompInput;
	}

	/**
	 * Get estimated file chunk size which is required to suffice maximum allowed
	 * compressed size
	 * 
	 * @param file
	 *            path of file which is used before split
	 * @param compressedSize
	 *            compressed size
	 * @param maxFileSize
	 *            maximum allowed compressed size
	 * 
	 * @return estimated chunk size for files
	 * 
	 * @throws IOException
	 */
	protected Long getEstimatedChunkSize(Path file, Long compressedSize, Long maxFileSize) throws IOException {
		return ((Files.size(file) * maxFileSize) / compressedSize);
	}

	/**
	 * Abstract method for compression functionality
	 * 
	 * @throws Exception
	 * 
	 */
	public abstract void compress() throws Exception;

	/**
	 * Abstract method for decompression functionality
	 * 
	 * @throws Exception
	 * 
	 */
	public abstract void decompress() throws Exception;

}
