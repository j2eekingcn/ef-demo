package cn.br.common.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

/**
 * IO Kit
 *
 * @author biezhi
 * 2017/6/2
 */
@Slf4j
@UtilityClass
public class IOKit {

	/**
	 * 关闭资源
	 * @param closeable
	 */
	public static void closeQuietly(Closeable closeable) {
		try {
			if (null == closeable) {
				return;
			}
			closeable.close();
		} catch (Exception e) {
			log.error("Close closeable error", e);
		}
	}

	/**
	 * 读取文件文本内容
	 * @param file
	 * @return
	 */
	public static String readToString(String file) throws IOException {
		return readToString(Paths.get(file));
	}

	/**
	 * 读取文件文本内容
	 * @param bufferedReader
	 * @return
	 */
	public static String readToString(BufferedReader bufferedReader) {
		return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
	}

	/**
	 * 读取文件文本内容
	 * @param path
	 */
	public static String readToString(Path path) throws IOException {
		BufferedReader bufferedReader = Files.newBufferedReader(path);
		return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
	}

	/**
	 * 读取文件文本内容
	 * @param input
	 */
	public static String readToString(InputStream input) throws IOException {
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
			return buffer.lines().collect(Collectors.joining(System.lineSeparator()));
		}
	}

	/**
	 * 复制文件
	 * @param source 源文件
	 * @param dest 目标文件
	 */
	public static void copyFile(File source, File dest) throws IOException {
		try (FileChannel in = new FileInputStream(source).getChannel(); FileChannel out = new FileOutputStream(dest).getChannel()) {
			out.transferFrom(in, 0, in.size());
		}
	}

	/**
	 * GZIP压缩
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	public static void compressGZIP(File input, File output) throws IOException {
		try (GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(output))) {
			try (FileInputStream in = new FileInputStream(input)) {
				byte[] buffer = new byte[1024];
				int len;
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
			}
		}
	}

	/**
	 * GZIP 压缩成字节
	 * @param content
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static byte[] compressGZIPAsString(String content, Charset charset) throws IOException {
		if (content == null || content.length() == 0) {
			return null;
		}
		GZIPOutputStream gzip;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		gzip = new GZIPOutputStream(out);
		gzip.write(content.getBytes(charset));
		gzip.close();
		return out.toByteArray();
	}

}
