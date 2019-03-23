/**
 * THIS PROGRAM IS GENERATED FOR FREE USE.
 * 
 * @createdAt 05-OCT-2019
 *
 */
package com.compression.model;

/**
 * Model class for input parameters for compression process
 * 
 * @author Vinod Pandey
 *
 */
public class CompressionInput {

	private String inputDirPath;
	private String outputDirPath;
	private Integer maxCompressedFileSize;

	/**
	 * Getter for input directory path
	 * 
	 * @return value of inputDirPath
	 */
	public String getInputDirPath() {
		return inputDirPath;
	}

	/**
	 * Setter for input directory path
	 * 
	 * @param inputDirPath
	 *            value of inputDirPath
	 */
	public void setInputDirPath(String inputDirPath) {
		this.inputDirPath = inputDirPath;
	}

	/**
	 * Getter for output directory path
	 * 
	 * @return value of outputDirPath
	 */
	public String getOutputDirPath() {
		return outputDirPath;
	}

	/**
	 * Setter for output directory path
	 * 
	 * @param outputDirPath
	 *            value of outputDirPath
	 */
	public void setOutputDirPath(String outputDirPath) {
		this.outputDirPath = outputDirPath;
	}

	/**
	 * Getter for maximum allowed compressed file size
	 * 
	 * @return value of maxCompressedFileSize
	 */
	public Integer getMaxCompressedFileSize() {
		return maxCompressedFileSize;
	}

	/**
	 * Setter for maximum allowed compressed file size
	 * 
	 * @param maxCompressedFileSize
	 *            value of maxCompressedFileSize
	 */
	public void setMaxCompressedFileSize(Integer maxCompressedFileSize) {
		this.maxCompressedFileSize = maxCompressedFileSize;
	}

}
