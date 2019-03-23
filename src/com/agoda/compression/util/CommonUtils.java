/**
 * THIS PROGRAM IS GENERATED FOR FREE USE.
 * 
 * @createdAt 05-OCT-2019
 *
 */
package com.agoda.compression.util;

import static com.agoda.compression.constants.CompressionUtilConstants.DEFAULT_COMP;
import static com.agoda.compression.constants.CompressionUtilConstants.FS_FILE_HEAER_SIZE;
import static com.agoda.compression.constants.CompressionUtilConstants.INDEX_FORMAT;
import static com.agoda.compression.constants.CompressionUtilConstants.SPLIT_FILE_ID;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Common Utility Class
 * 
 * @author Vinod Pandey
 *
 */
public class CommonUtils {

	/**
	 * Convert mega bytes into bytes
	 * 
	 * @param mb
	 *            mega bytes
	 * 
	 * @return bytes
	 */
	public static Long convertMbToBytes(Integer mb) {
		return new Long(mb * 1024 * 1024);
	}

	/**
	 * Create file, if not exists
	 * 
	 * @param filePath
	 *            file to be created
	 * 
	 * @throws IOException
	 */
	public static void createFile(Path filePath) throws IOException {

		if (!(Files.exists(filePath))) {
			Files.createFile(filePath);
		}
	}

	/**
	 * Create directories recursively, if not exists
	 * 
	 * @param dirPath
	 *            directory to be created
	 * 
	 * @throws IOException
	 */
	public static void createDir(Path dirPath) throws IOException {

		if (!(Files.exists(dirPath))) {
			Files.createDirectories(dirPath);
		}
	}

	/**
	 * Create ZIP file system for java's default compression and decompression
	 * process
	 * 
	 * @param createVal
	 *            environment parameter for mode of zip file system
	 * @param zipFile
	 *            path of zip file
	 * 
	 * @return created zip file system instance
	 * @throws IOException
	 */
	public static FileSystem createZipFileSystem(String createVal, String zipFile) throws IOException {
		Map<String, Object> env = new HashMap<>();
		env.put("create", createVal);
		/* useTempFile is used to avoid OutOfMemory error in case of large files */
		env.put("useTempFile", Boolean.TRUE);
		URI zipURI = URI.create(String.format("jar:file:/%s", zipFile.replaceAll("\\\\", "/")));
		return FileSystems.newFileSystem(zipURI, env);
	}

	/**
	 * Get compressed file size with the help of file attributes
	 * 
	 * @param path
	 *            file path for which compressed size is required
	 * 
	 * @return extracted compressed file size
	 * @throws IOException
	 */
	public static Long getCompressedSize(Path path) throws IOException {

		String result = Files.readAttributes(path, BasicFileAttributes.class).toString();

		/**
		 * extract the compressed file size from file attributes which does not contain
		 * getter method for compressedSize
		 */
		result = result.replaceAll("[\\n\\r\\t ]", "");

		int s = result.indexOf("compressedSize:") + 15;
		int l = result.indexOf("crc:");

		result = result.substring(s, l);

		return Long.valueOf(result);
	}

	/**
	 * Split input file into multiple files of specified chunk size and return list
	 * of all split files
	 * 
	 * @param inputFilePath
	 *            input file path which is required to be split
	 * 
	 * @param chunkSize
	 *            size of split chunk
	 * 
	 * @return list of file paths generated from split process
	 * 
	 * @throws IOException
	 */
	public static List<Path> splitFiles(Path inputFilePath, long chunkSize) throws IOException {

		List<Path> splitFileList = new LinkedList<>();

		/**
		 * start reading data from input file and write into new file. Once size of
		 * output file is almost same of chunk size then create new output file for
		 * writing.
		 */
		try (FileChannel in = FileChannel.open(inputFilePath, READ)) {

			int index = 1;
			long position = 0;

			while (position < in.size()) {

				Path destination = FileSystems.getDefault().getPath(
						inputFilePath.toString() + DEFAULT_COMP + SPLIT_FILE_ID + String.format(INDEX_FORMAT, index++));

				try (FileChannel out = FileChannel.open(destination, CREATE, WRITE)) {

					/** 10 KB size is retained for file header purpose used by file system */
					position += in.transferTo(position, (chunkSize - FS_FILE_HEAER_SIZE), out);

					splitFileList.add(destination);

					// Files.copy(destination, destination, REPLACE_EXISTING);
				}
			}
		}

		return splitFileList;
	}

	/**
	 * Merge byte data from all input files and create new merged file at same
	 * location
	 * 
	 * @param chunkFilePaths
	 *            list of input file paths
	 * 
	 * @throws IOException
	 */
	public static void mergeFiles(List<Path> chunkFilePaths) throws IOException {

		/**
		 * check and return in case of no file is passed in input list for merging
		 */
		if (chunkFilePaths == null || chunkFilePaths.isEmpty()) {
			return;
		}

		/**
		 * generated merged file name with the help of first chunk file name
		 */
		Path mergedFilePath = Paths
				.get(chunkFilePaths.get(0).toString().replace((DEFAULT_COMP + SPLIT_FILE_ID + "001"), ""));

		/**
		 * create file if its not present on file system
		 */
		createFile(mergedFilePath);

		/**
		 * read data from all input files and write into common merged file
		 */
		try (FileChannel out = FileChannel.open(mergedFilePath, CREATE, WRITE)) {

			for (Path inFile : chunkFilePaths) {
				try (FileChannel in = FileChannel.open(inFile, READ)) {
					for (long position = 0, length = in.size(); position < length;)
						position += in.transferTo(position, length - position, out);
				}

				/**
				 * delete chunk file once used for merging
				 */
				Files.delete(inFile);
			}
		}
	}

}
