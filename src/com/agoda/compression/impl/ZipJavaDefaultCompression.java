/**
 * THIS PROGRAM IS GENERATED FOR FREE USE.
 * 
 * @createdAt 05-OCT-2019
 *
 */
package com.agoda.compression.impl;

import static com.agoda.compression.constants.CompressionUtilConstants.COMPRESS;
import static com.agoda.compression.constants.CompressionUtilConstants.DEFAULT_COMP;
import static com.agoda.compression.constants.CompressionUtilConstants.INDEX_FORMAT;
import static com.agoda.compression.constants.CompressionUtilConstants.PART_POSTFIX;
import static com.agoda.compression.constants.CompressionUtilConstants.SPLIT_FILE_ID;
import static com.agoda.compression.constants.CompressionUtilConstants.ZIP_EXTN;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.agoda.compression.Compression;
import com.agoda.compression.util.CommonUtils;

/**
 * Java's default compression implementation class. Compression and
 * decompression are achieved with the help of ZipFileSystem feature
 * 
 * @author Vinod Pandey
 *
 */
public class ZipJavaDefaultCompression extends Compression {

	private static final Logger LOG = Logger.getLogger(ZipJavaDefaultCompression.class.getName());

	@Override
	public void run() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("start - java's default implementation specific thread");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("invoked operation: " + getOperation());
		}

		String opInput = null;
		StringBuilder statusMsg = new StringBuilder(getOperation());

		try {

			/**
			 * call respective method based on operation used by user
			 */
			if (COMPRESS.equals(getOperation())) {
				opInput = getCompInput().getInputDirPath();
				compress();
			} else {
				opInput = getDecompInput().getInputDirPath();
				decompress();
			}

			statusMsg.append(" operation successful for input directory: ").append(opInput)
					.append(" which started with request-id: ").append(Thread.currentThread().getId());

			LOG.info(statusMsg);
			System.out.println("\n" + statusMsg + "\n");

		} catch (Exception e) {
			statusMsg.append(" operation failed for input directory: ").append(opInput)
					.append(" which started with request-id: ").append(Thread.currentThread().getId());
			LOG.error(statusMsg, e);
			System.err.println("\n" + statusMsg + "\n");
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("complete - java's default implementation specific thread");
		}
	}

	@Override
	public void compress() throws Exception {

		if (LOG.isTraceEnabled()) {
			LOG.trace("start - java's default zip compression process");
		}

		String inputDir = getCompInput().getInputDirPath();
		String outputDir = getCompInput().getOutputDirPath();

		if (LOG.isDebugEnabled()) {
			LOG.debug("compression input directory: " + inputDir);
			LOG.debug("compression output directory: " + outputDir);
		}

		/**
		 * create output directory, if not exists
		 */
		CommonUtils.createDir(Paths.get(outputDir));

		final Path sourceRoot = Paths.get(inputDir);

		final String baseFileName = outputDir + File.separator + sourceRoot.toFile().getName();

		/**
		 * walk over all files and directories present inside input directory and
		 * compress them (copy into zip file system) one by one
		 */
		Files.walkFileTree(sourceRoot, new SimpleFileVisitor<Path>() {

			long maxFileSize = CommonUtils.convertMbToBytes(getCompInput().getMaxCompressedFileSize());
			long currentSize = 0;
			int zipCount = 1;

			/**
			 * create zip file system for first target compressed zip file
			 */
			FileSystem destFs = CommonUtils.createZipFileSystem("true", getPartName(baseFileName, zipCount++));
			Path targetPath = destFs.getRootDirectories().iterator().next();

			private Path sourcePath;

			@Override
			public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
					throws IOException {
				if (sourcePath == null) {
					sourcePath = dir;
				} else {
					/**
					 * create directories in zip file system
					 */
					Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir).toString()));
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

				if (LOG.isDebugEnabled()) {
					LOG.debug("start compression for file: " + file.toString());
				}

				/**
				 * copy file from default file system to zip file system and check compressed
				 * file size
				 */
				Path targetFile = copyFileToZipFs(file);

				long currentFileSize = CommonUtils.getCompressedSize(targetFile);
				currentSize += currentFileSize;

				/**
				 * if single compressed file size is more than maximum allowed compressed size
				 * then single file is required to split for compression into multiple chunks
				 */
				if (currentFileSize > maxFileSize) {

					currentSize -= currentFileSize;

					List<Path> splitFiles = CommonUtils.splitFiles(file,
							getEstimatedChunkSize(targetFile, currentFileSize, maxFileSize));

					/**
					 * delete single file to avoid maximum allowed size and use split chunks for
					 * compression
					 */
					Files.delete(targetFile);
					CopyFilesToZipFs(splitFiles);

				}
				/**
				 * if single file's size is not more than allowed maximum file size but more
				 * than one files combined crossing compressed file limit
				 */
				else if (currentSize >= maxFileSize) {

					if (LOG.isDebugEnabled()) {
						LOG.debug("file size exceeded maximum allowed size after file: " + file.toString());
					}

					/**
					 * in case size of current zip file is exceeded maximum allowed compressed file
					 * size then delete current file from current zip file system and copy same in
					 * newly created zip file system
					 */
					Files.delete(targetFile);

					if (LOG.isDebugEnabled()) {
						LOG.debug("file: " + file.toString() + " deleted from current zip file");
					}

					destFs.close();
					destFs = CommonUtils.createZipFileSystem("true", getPartName(baseFileName, zipCount++));
					targetPath = destFs.getRootDirectories().iterator().next();
					currentSize = 0;

					targetFile = copyFileToZipFs(file);

					if (LOG.isDebugEnabled()) {
						LOG.debug("new zip file created and copied file: " + file.toString());
					}

				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("complete compression for file: " + file.toString());
				}

				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

				/**
				 * if all files and folders are compressed then close last open zip file system
				 */
				if (sourceRoot.equals(dir)) {
					destFs.close();
				}

				return FileVisitResult.CONTINUE;
			}

			/**
			 * Copy file from default file system to zip file system
			 * 
			 * @param file
			 *            file from default file system
			 * 
			 * @return path of file from zip file system
			 * @throws IOException
			 */
			private Path copyFileToZipFs(Path file) throws IOException {

				Files.createDirectories(targetPath.resolve(sourcePath.relativize(file.getParent()).toString()));

				/**
				 * copy file from default file system to zip file system
				 */
				Path targetFile = targetPath.resolve(sourcePath.relativize(file).toString());
				Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);

				return targetFile;
			}

			/**
			 * 
			 * @param splitFiles
			 * @throws IOException
			 */
			private void CopyFilesToZipFs(List<Path> splitFiles) throws IOException {

				long currentFileSize = 0;

				List<Path> remainingFiles = new LinkedList<>();

				/**
				 * check every chunk size and copy them in current zip file system keeping
				 * condition full-filled for maximum allowed compressed file size
				 */
				for (Path splitFile : splitFiles) {

					currentFileSize = Files.size(splitFile);

					if (currentSize + currentFileSize <= maxFileSize) {
						copyFileToZipFs(splitFile);
						Files.delete(splitFile);
						currentSize += currentFileSize;
					} else {
						remainingFiles.add(splitFile);
					}
				}

				/**
				 * chunks which are not added current zip file system will be added new zip file
				 * system recursively till all chunks are copies in zip file system with maximum
				 * allowed compressed file size
				 */
				if (!remainingFiles.isEmpty()) {
					destFs.close();
					destFs = CommonUtils.createZipFileSystem("true", getPartName(baseFileName, zipCount++));
					targetPath = destFs.getRootDirectories().iterator().next();
					currentSize = 0;
					CopyFilesToZipFs(remainingFiles);
				}
			}
		});

		if (LOG.isTraceEnabled()) {
			LOG.trace("complete - java's default zip compression process");
		}

	}

	@Override
	public void decompress() throws Exception {

		if (LOG.isTraceEnabled()) {
			LOG.trace("start - java's default zip decompression process");
		}

		String inputDir = getDecompInput().getInputDirPath();
		String outputDir = getDecompInput().getOutputDirPath();

		if (LOG.isDebugEnabled()) {
			LOG.debug("decompression input directory: " + inputDir);
			LOG.debug("decompression output directory: " + outputDir);
		}

		/**
		 * create output directory, if not exists
		 */
		CommonUtils.createDir(Paths.get(outputDir));

		Path sourceRoot = Paths.get(inputDir);

		/**
		 * walk over all zip files present inside input directory and decompress (copy
		 * into default file system) one by one
		 */
		Files.walk(sourceRoot).filter(path -> !Files.isDirectory(path)).forEach(path -> {

			try {

				Path sourcePath = Paths.get(path.toFile().getAbsolutePath());

				String temp = sourcePath.getFileName().toString();

				if (LOG.isDebugEnabled()) {
					LOG.debug("extraction start for zip input: " + sourcePath.toString());
				}

				/**
				 * create zip file system for currently traversed zip file
				 */
				FileSystem zipFs = CommonUtils.createZipFileSystem("false", sourcePath.toString());

				/**
				 * start extraction from root of currently traversed zip file inside output
				 * directory
				 */
				extract(zipFs, "/", outputDir + File.separator + temp.substring(0, temp.indexOf(".")));

				zipFs.close();

			} catch (IOException e) {
				LOG.error("error occurred in extraction process");
				throw new IllegalStateException(e);
			}
		});

		/**
		 * files which are split into multiple chunks in compression will be merged
		 * again while decompression
		 */
		List<Path> filesToMerge = new LinkedList<>();
		Files.walk(Paths.get(outputDir)).filter(path -> Files.isDirectory(path)).forEach(path -> {
			try {
				Files.walk(path).filter(
						file -> !Files.isDirectory(file) && file.toString().contains(DEFAULT_COMP + SPLIT_FILE_ID))
						.sorted().forEach(file -> filesToMerge.add(file));

				if (!filesToMerge.isEmpty()) {
					CommonUtils.mergeFiles(filesToMerge);
					filesToMerge.clear();
				}
			} catch (IOException e) {
				LOG.error("error occurred while merging of chunk files in decompression operation");
				throw new IllegalStateException(e);
			}

		});

		if (LOG.isTraceEnabled()) {
			LOG.trace("complete - java's default zip decompression process");
		}

	}

	/**
	 * Extracts all files and directories recursively from zip file system to
	 * default file system
	 * 
	 * @param fs
	 *            zip file system for source zip file
	 * @param path
	 *            path in zip file system
	 * @param destDir
	 *            path on default file system
	 * 
	 * @throws IOException
	 */
	private void extract(FileSystem fs, String path, String destDir) throws IOException {

		if (LOG.isTraceEnabled()) {
			LOG.trace("start - extraction process");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("extraction input: " + path.toString());
		}

		Path source = fs.getPath(path);

		if (Files.isDirectory(source)) {
			/**
			 * if path represents a directory then extract all nested files an directories
			 * recursively
			 */
			try (DirectoryStream<Path> ds = Files.newDirectoryStream(source)) {
				for (Path child : ds) {
					extract(fs, child.toString(), destDir);
				}
			}
		} else {

			/**
			 * if path represents a file then copy file from zip file system to default file
			 * system. Before copy operation, create parent directory if not present on
			 * default file system
			 */
			if (path.startsWith("/")) {
				path = path.substring(1);
			}

			Path destination = FileSystems.getDefault().getPath(destDir + File.separator + path);
			Path parent = destination.getParent();
			if (Files.notExists(parent)) {
				CommonUtils.createDir(parent);
			}

			Files.copy(source, destination, REPLACE_EXISTING);
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("complete - extraction process");
		}

	}

	/**
	 * Generate file part name based on base file name and current path index
	 * 
	 * @param name
	 *            base file name of zip file
	 * @param partIndex
	 *            currently used path index
	 * 
	 * @return generated zip file part name
	 */
	public String getPartName(String name, int partIndex) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("start - file part generation");
		}

		StringBuilder partNameBuilder = new StringBuilder(name);
		partNameBuilder.append(PART_POSTFIX);
		partNameBuilder.append(String.format(INDEX_FORMAT, partIndex));
		partNameBuilder.append(ZIP_EXTN);

		String result = partNameBuilder.toString();

		if (LOG.isDebugEnabled()) {
			LOG.debug("file part name: " + result);
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("complete - file part generation");
		}

		return result;

	}
}
