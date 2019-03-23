/**
 * THIS PROGRAM IS GENERATED FOR FREE USE.
 * 
 * @createdAt 05-OCT-2019
 *
 */
package com.agoda.compression.model;

/**
 * Model class for input parameters for compression process
 * 
 * @author Vinod Pandey
 *
 */
public class DecompressionInput {

	private String inputDirPath;
	private String outputDirPath;

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
	public void setInputDirPath(String sourceFiles) {
		this.inputDirPath = sourceFiles;
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
	public void setOutputDirPath(String destinationDir) {
		this.outputDirPath = destinationDir;
	}

}
