/**
 * THIS PROGRAM IS GENERATED FOR FREE USE.
 * 
 * @createdAt 05-OCT-2019
 *
 */
package com.compression.test;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Scanner;

import com.compression.model.CompressionInput;
import com.compression.model.DecompressionInput;
import com.compression.util.CompressionUtil;

/**
 * Test class for CompressionUtil
 * 
 * @author Vinod Pandey
 *
 */
public class CompressionUtilTest {

	/**
	 * Main entry method
	 * 
	 * @param args
	 *            tool input parameters
	 */
	public static void main(String[] args) {

		try (Scanner scanner = new Scanner(System.in)) {

			System.out.println("**************************************************************");
			System.out.println("*********** Compression/Decompression Tool Running ***********");
			System.out.println("**************************************************************");
			System.out.println("** Choices [ Compression:- 1, Decompression:- 2, Exit:- 0 ] **");
			System.out.println("**************************************************************");

			CompressionUtilTest test = new CompressionUtilTest();

			if (args != null && args.length > 0) {
				CompressionUtil.COMPRESSION_UTIL.setCompressionType(args[0]);
			}

			String choice = null;

			while (true) {

				System.out.print("Enter you choice:");
				choice = scanner.nextLine();

				switch (choice) {

				case "0":
					System.out.println("********* Compression/Decompression Tool Terminated **********");
					System.exit(0);

				case "1":

					test.startCompress(scanner);
					break;

				case "2":

					test.startDecompress(scanner);
					break;

				default:
					System.err.println("Invalid choice!!! Please try again.");
				}

			}
		}
	}

	/**
	 * Handler method for starting compression
	 * 
	 * @param scanner
	 *            scanner instance to handle user inputs
	 */
	private void startCompress(Scanner scanner) {

		CompressionInput compInput = new CompressionInput();

		compInput.setInputDirPath(readInputDirPath(scanner));

		compInput.setOutputDirPath(readOutputDirPath(scanner));

		while (true) {
			try {
				System.out.print("Enter maximum size for compressed files(number in MB): ");
				compInput.setMaxCompressedFileSize(Integer.parseInt(scanner.nextLine()));
				break;
			} catch (NumberFormatException e) {
				System.err.println("Invalid number value. Try again...");
			}
		}

		try {
			CompressionUtil.COMPRESSION_UTIL.compress(compInput);
		} catch (Exception e) {
			System.err.println("Tool failure in compression: " + e.getMessage());
		}

	}

	/**
	 * Handler method for starting decompression
	 * 
	 * @param scanner
	 *            scanner instance to handle user inputs
	 */
	private void startDecompress(Scanner scanner) {

		DecompressionInput decompInput = new DecompressionInput();

		decompInput.setInputDirPath(readInputDirPath(scanner));

		decompInput.setOutputDirPath(readOutputDirPath(scanner));

		try {
			CompressionUtil.COMPRESSION_UTIL.decompress(decompInput);
		} catch (Exception e) {
			System.err.println("Tool failure in decompression: " + e.getMessage());
		}

	}

	/**
	 * Read input directory path from console
	 * 
	 * @param scanner
	 *            scanner instance linked with console
	 * 
	 * @return valid input directory path
	 */
	private String readInputDirPath(Scanner scanner) {

		String path = null;

		while (true) {
			System.out.print("Enter input directory path: ");
			path = scanner.nextLine();
			try {
				if (Files.exists(Paths.get(path))) {
					break;
				} else {
					System.err.println("Input directory does not exists. Try again...");
				}
			} catch (InvalidPathException e) {
				System.err.println("\nInvalid path for input directory. Try again...");
			}
		}

		return path;
	}

	/**
	 * Read output directory path from console
	 * 
	 * @param scanner
	 *            scanner instance linked with console
	 * 
	 * @return valid output directory path
	 */
	private String readOutputDirPath(Scanner scanner) {

		String path = null;

		while (true) {
			System.out.print("Enter output directory path: ");
			path = scanner.nextLine();
			try {
				Paths.get(path);
				break;
			} catch (InvalidPathException e) {
				System.err.println("\nInvalid path for output directory. Try again...");
			}
		}

		return path;
	}

}
