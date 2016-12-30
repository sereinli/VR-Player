package me.rain.android.vrvideo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import me.rain.android.vrvideo.BuildConfig;

public class FileUtils {

	public static final int GLOBLE_BUFFER_SIZE = 512 * 1024;
	
	public static String printSDCardRoot() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	public static File getFileFromUri(Uri uri) {
		if (uri == null){
			return null;
		}
		if ((uri.getScheme().equals("file"))
				&& (!TextUtils.isEmpty(uri.getPath()))) {
			return new File(uri.getPath());
		}

		return null;
	}

	public static boolean closeStream(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
				return true;
			}
		} catch (IOException e) {
		}

		return false;
	}

	public static boolean hasSDCardMounted() {
		return Environment.getExternalStorageState().equals("mounted");
	}

	public static boolean doesExisted(File file) {
		return (file != null) && (file.exists());
	}

	public static InputStream makeInputBuffered(InputStream input_) {
		if (input_ == null){
			return null;
		}
		if ((input_ instanceof BufferedInputStream)) {
			return input_;
		}

		return new BufferedInputStream(input_, GLOBLE_BUFFER_SIZE);
	}

	public static FileInputStream getFileInputStream(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			Utils.loge(e);
		}

		return fis;
	}

	public static FileInputStream getFileInputStream(String path) {
		return getFileInputStream(new File(path));
	}

	// 检测文件是否存在
	public static boolean isFileExist(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}
		File file = new File(filePath);
		return file.isFile() && file.exists();
	}

	// 检测目录是否存在
	public static boolean isDirectoryExist(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}
		File file = new File(filePath);
		return file.isDirectory() && file.exists();
	}

	// 以字符的方式读取文件
	public static String readStringFromFile(String path) {
		if (!isFileExist(path)) {
			return "";
		}
		File file = new File(path);
		BufferedReader reader = null;
		StringBuilder content = new StringBuilder();
		try {
			reader = new BufferedReader(new FileReader(file));
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				content.append(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}

		}
		return content.toString();
	}

	// 如果文件大写超过maxSize，则不写入
	public static void writeFile(String filePath, String content,
								 boolean isAppend, long maxSize) {
		if (TextUtils.isEmpty(filePath)) {
			return;
		}
		File file = new File(filePath);
		if (file.exists() && file.length() >= maxSize) {
			return;
		}
		writeFile(filePath, content, isAppend);
	}

	public synchronized static void writeFile(String filePath, String content,
											  boolean isAppend) {
		if (filePath == null) {
			return;
		}
		if (content == null) {
			return;
		}
		File file = new File(filePath);
		FileWriter fileWriter = null;
		try {
			if (!file.exists()) {
				makesureFileExist(file);
				// file.createNewFile();
			}
			fileWriter = new FileWriter(file, isAppend);
			fileWriter.write(content);
			fileWriter.flush();
		} catch (IOException e) {
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Convert Uri to absolute path
	public static String getAbsolutePathFromUri(String uriString) {

		if (TextUtils.isEmpty(uriString)) {
			return null;
		}

		Uri uri = Uri.parse(uriString);

		if (uri == null) {
			return null;
		}

		return uri.getPath();
	}

	private static final int DEFAULT_DELETE_LINITE = 5;
	private static FileFilter fileOnlyFilter;
	private static FileFilter dirOnlyFilter;

	public static void copy(String pathIn_, String pathOut_) throws IOException {
		copy(new File(pathIn_), new File(pathOut_));
	}

	public static void copy(File in_, File out_) throws IOException {

		makesureParentExist(out_);
		copy(new FileInputStream(in_), new FileOutputStream(out_));
	}

	public static void makesureParentExist(File file_) {
		File parent = file_.getParentFile();
		if ((parent != null) && (!parent.exists()))
			mkdirs(parent);
	}

	public static void makesureParentExist(String filepath_) {
		makesureParentExist(new File(filepath_));
	}

	public static void makesureFileExist(File file_) {
		if (!file_.exists()) {
			makesureParentExist(file_);
			createNewFile(file_);
		}
	}

	public static void makesureFileExist(String filePath_) {
		makesureFileExist(new File(filePath_));
	}

	public static void copy(InputStream input_, OutputStream output_)
			throws IOException {
		try {
			byte[] buffer = new byte[GLOBLE_BUFFER_SIZE];
			int temp = -1;
			input_ = makeInputBuffered(input_);
			output_ = makeOutputBuffered(output_);
			while ((temp = input_.read(buffer)) != -1) {
				output_.write(buffer, 0, temp);
				output_.flush();
			}
		} catch (IOException e) {
			throw e;
		} finally {
			FileUtils.closeStream(input_);
			FileUtils.closeStream(output_);
		}
	}

	public static OutputStream makeOutputBuffered(OutputStream output_) {
		if ((output_ instanceof BufferedOutputStream)) {
			return output_;
		}

		return new BufferedOutputStream(output_, GLOBLE_BUFFER_SIZE);
	}

	public static void copyWithoutOutputClosing(InputStream input_,
			OutputStream output_) throws IOException {
		try {
			byte[] buffer = new byte[GLOBLE_BUFFER_SIZE];
			int temp = -1;
			input_ = makeInputBuffered(input_);
			output_ = makeOutputBuffered(output_);
			while ((temp = input_.read(buffer)) != -1) {
				output_.write(buffer, 0, temp);
				output_.flush();
			}
			output_.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			FileUtils.closeStream(input_);
		}
	}

	public static byte[] readInputStream(InputStream is_) {
		if (is_ != null) {
			ByteArrayOutputStream baos = null;
			try {
				baos = new ByteArrayOutputStream(GLOBLE_BUFFER_SIZE);
				copyWithoutOutputClosing(is_, baos);
				byte[] res = baos.toByteArray();
				byte[] arrayOfByte1 = res;
				return arrayOfByte1;
			} catch (IOException e) {
				Utils.loge(e);
			} finally {
				FileUtils.closeStream(baos);
			}
		}

		return null;
	}

	public static byte[] readFile(File file_) {
		return readInputStream(getFileInputStream(file_));
	}

	public static byte[] readFile(String path_) throws FileNotFoundException {
		File f = new File(path_);
		if ((doesExisted(f)) && (f.isFile()))
			return readFile(f);
		throw new FileNotFoundException(f.getAbsolutePath());
	}

	public static String readFileForString(File file_, String charset_)
			throws UnsupportedEncodingException {
		return new String(readFile(file_), charset_);
	}

	public static String readFileForString(String path_, String charset_)
			throws UnsupportedEncodingException, FileNotFoundException {
		return new String(readFile(path_), charset_);
	}

	public static String readFileForString(File file_) {
		return new String(readFile(file_));
	}

	public static String readFileForString(String path_)
			throws FileNotFoundException {
		return new String(readFile(path_));
	}

	public static Object loadObject(String filepath_) throws IOException,
			ClassNotFoundException {
		if (doesExisted(new File(filepath_)))
			return loadObject(new FileInputStream(filepath_));
		return null;
	}

	public static Object loadObject(InputStream input_) throws IOException,
			ClassNotFoundException {
		ObjectInputStream ois = null;
		try {
			Object obj = null;
			ois = new ObjectInputStream(input_ = makeInputBuffered(input_));
			obj = ois.readObject();
			Object localObject2 = obj;
			return localObject2;
		} catch (IOException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			throw e;
		} finally {
			FileUtils.closeStream(ois);
		}
	}

	public static void saveObject(Object obj_, String filepath_)
			throws IOException {

		saveObject(obj_, new FileOutputStream(filepath_));
	}

	public static void saveObject(Object obj_, OutputStream output_)
			throws IOException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(makeOutputBuffered(output_));
			oos.writeObject(obj_);
		} catch (IOException e) {
			throw e;
		} finally {
			FileUtils.closeStream(oos);
		}
	}

	public static FileOutputStream getEmptyFileOutputStream(String path_)
			throws FileNotFoundException {

		return getEmptyFileOutputStream(new File(path_));
	}

	public static FileOutputStream getEmptyFileOutputStream(File file_)
			throws FileNotFoundException {

		if (!file_.exists()) {
			makesureParentExist(file_);
		} else {
			delete(file_);
		}
		createNewFile(file_);
		return new FileOutputStream(file_);
	}

	public static void deleteFiles(File file_) {

		if ((file_.exists()) && (file_.isDirectory())) {
			File[] childrenFile = file_.listFiles();
			if (childrenFile != null) {
				for (File f : childrenFile) {
					if (f.isFile()) {
						delete(f);
					} else if (f.isDirectory()) {
						deleteFiles(f);
					}
				}
			}
			delete(file_);
		} else if ((file_.exists()) && (file_.isFile())) {
			delete(file_);
		}
	}

	public static void deleteFiles(String path_) {
		deleteFiles(new File(path_));
	}

	public static long getFileSize(File file_) {

		int totalsize = 0;
		if (!file_.exists()) {
			Utils.logd(file_.getAbsolutePath() + " dones't exist.");
		} else if (file_.isFile()) {
			totalsize = (int) (totalsize + file_.length());
		} else {
			File[] childrenFile = file_.listFiles();
			if (childrenFile != null) {
				for (File f : childrenFile) {
					totalsize = (int) (totalsize + getFileSize(f));
				}
			}

		}

		return totalsize;
	}

	public static long getFileSize(String filepath_) {
		return getFileSize(new File(filepath_));
	}

	public static boolean doesExisted(String filepath) {
		if (TextUtils.isEmpty(filepath))
			return false;
		return doesExisted(new File(filepath));
	}

	public static void writeToRandomAccessFile(RandomAccessFile raf_,
			InputStream input_) {
		try {
			byte[] temp = new byte[GLOBLE_BUFFER_SIZE];
			int mark = -1;
			input_ = makeInputBuffered(input_);
			while ((mark = input_.read(temp)) != -1)
				raf_.write(temp, 0, mark);
		} catch (IOException e) {
			Utils.loge(e);
		} finally {
			FileUtils.closeStream(input_);
		}
	}

	public static void writeToRandomAccessFile(RandomAccessFile raf_,
			byte[] bytes_) {
		writeToRandomAccessFile(raf_, new ByteArrayInputStream(bytes_));
	}

	public static void writeUTF8ToRandomAccessFile(RandomAccessFile raf_,
			String string_) {
		try {
			if (!TextUtils.isEmpty(string_))
				writeToRandomAccessFile(raf_, string_.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			Utils.loge(e);
		}
	}

	public static synchronized FileFilter getFileOnlyFilter() {
		if (fileOnlyFilter == null) {
			fileOnlyFilter = new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.isFile();
				}
			};
		}

		return fileOnlyFilter;
	}

	public static synchronized FileFilter getDirOnlyFilter() {
		if (dirOnlyFilter == null) {
			dirOnlyFilter = new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			};
		}

		return dirOnlyFilter;
	}

	public static void renameLowercase(File file_) {
		if ((doesExisted(file_)) && (file_.isFile())) {
			String parent = file_.getParent();
			parent = parent == null ? "" : parent;
			String newPath = parent + "/" + file_.getName().toLowerCase();
			if (!newPath.equals(file_.getAbsolutePath()))
				renameTo(file_, new File(newPath));
		}
	}

	public static void renameLowercase(String filePath) {
		renameLowercase(new File(filePath));
	}

	public static void renameUpperCase(File file_) {
		if ((doesExisted(file_)) && (file_.isFile())) {
			String parent = file_.getParent();
			parent = parent == null ? "" : parent;
			String newPath = parent + "/" + file_.getName().toUpperCase();
			if (!newPath.equals(file_.getAbsolutePath()))
				renameTo(file_, new File(newPath));
		}
	}

	public static void renameUpperCase(String filePath) {
		renameUpperCase(new File(filePath));
	}

	public static void delete(File f) {
		if ((f != null) && (f.exists()) && (!f.delete()) && BuildConfig.DEBUG_MODE)
			throw new RuntimeException(f.getAbsolutePath()
					+ " doesn't be deleted!");
	}

	public static void renameTo(File src, File dest) {
		if ((src != null) && (dest != null) && (src.exists())
				&& (!src.renameTo(dest)) && BuildConfig.DEBUG_MODE)
			throw new RuntimeException(src.getAbsolutePath()
					+ " doesn't be rename to " + dest.getAbsolutePath());
	}

	public static void createNewFile(File file_) {
		if ((!__createNewFile(file_)) && BuildConfig.DEBUG_MODE)
			throw new RuntimeException(file_.getAbsolutePath()
					+ " doesn't be created!");
	}

	private static boolean __createNewFile(File file_) {
		makesureParentExist(file_);
		if (file_.exists())
			delete(file_);
		try {
			return file_.createNewFile();
		} catch (IOException e) {
			Utils.loge(e);
		}

		return false;
	}

	public static void mkdirs(File dir_) {
		if ((!dir_.exists()) && (!dir_.mkdirs()) && BuildConfig.DEBUG_MODE)
			throw new RuntimeException("fail to make " + dir_.getAbsolutePath());
	}

	public static FileOutputStream getFileOutputStream(File file) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			Utils.loge(e);
		}
		return fos;
	}

	public static FileOutputStream getFileOutputStream(String path) {
		return getFileOutputStream(new File(path));
	}

	public static String getJarPath(Class<?> clazz) {
		try {
			String path = clazz.getProtectionDomain().getCodeSource()
					.getLocation().toString();
			if (path.startsWith("file:/"))
				return path.substring("file:/".length());
		} catch (Exception localException) {
		}
		return "";
	}

	public static boolean isSame(InputStream input1, InputStream input2) {

		input1 = makeInputBuffered(input1);
		input2 = makeInputBuffered(input2);
		int temp = -2;
		try {
			while ((temp = input1.read()) == input2.read())
				if (temp == -1)
					return true;
		} catch (Exception e) {
			Utils.loge(e);
		} finally {
			FileUtils.closeStream(input1);
			FileUtils.closeStream(input2);
		}
		return false;
	}

	public static boolean isSame(File file1, File file2) {

		if (file1.length() != file2.length())
			return false;
		try {
			return isSame(new FileInputStream(file1),
					new FileInputStream(file2));
		} catch (FileNotFoundException e) {
			Utils.loge(e);
		}
		return false;
	}

	public static boolean isSame(String path1, String path2) {
		return isSame(new File(path1), new File(path2));
	}
	
	/**
	 * 如果doesExisted(filepath_)为true且是文件类型则删除文件，并返回true； 否则什么也不做并且返回false。
	 * 如果删除失败，不会重试。
	 * 
	 * @param file
	 * @return
	 */
	public static boolean deleteDependon(File file) {
		return FileUtils.deleteDependon(file, 0);
	}

	/**
	 * 如果doesExisted(file_)为true则删除文件，并返回true； 否则什么也不做并且返回false。
	 * 其中，删除文件的时候如果删除失败会尝试最多删除 maxRetryCount 次.
	 * 
	 * @param file
	 * @return
	 */
	public static boolean deleteDependon(File file, int maxRetryCount) {
		int retryCount = 1;
		maxRetryCount = maxRetryCount < 1 ? FileUtils.DEFAULT_DELETE_LINITE
				: maxRetryCount;
		boolean isDeleted = false;

		if (file != null) {
			while (!isDeleted && (retryCount <= maxRetryCount)
					&& doesExisted(file)) {
				if (!(isDeleted = file.delete())) {
					Utils.logd(file.getAbsolutePath() + "删除失败，失败次数为:"
							+ retryCount);
					retryCount++;
				}
			}

		}

		return isDeleted;
	}

	/**
	 * 如果doesExisted(filepath_)为true且是文件类型则删除文件，并返回true； 否则什么也不做并且返回false。
	 * 如果删除失败，不会重试。
	 * 
	 * @param filepath
	 * @return
	 */
	public static boolean deleteDependon(String filepath) {
		return FileUtils.deleteDependon(filepath, 0);
	}

	/**
	 * 如果doesExisted(file_)为true且是文件类型则删除文件，并返回true； 否则什么也不做并且返回false。
	 * 其中，删除文件的时候如果删除失败会尝试最多删除 maxRetryCount 次.
	 * 
	 * @param filepath
	 * @param maxRetryCount
	 * @return
	 */
	public static boolean deleteDependon(String filepath, int maxRetryCount) {
		if (TextUtils.isEmpty(filepath)) {
			return false;
		}
		return FileUtils.deleteDependon(new File(filepath), maxRetryCount);
	}

    /**
     * 依靠系统打开文件，类型依靠filepath的后缀名获取
     * 
     * @param context
     * @param filepath
     */
    public static void openFileByOS(Context context, String filepath) {
        if (TextUtils.isEmpty(filepath) || !filepath.contains(".")) {
            return;
        }
        File file = new File(filepath);
        if (file == null || !file.exists()) {
            return;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            // no activity found
        }
    }

    /**
     * 根据后缀名获取文件的MIME
     * 
     * @param file
     * @return
     */
    private static String getMIMEType(File file) {
        String type = "*/*";
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        String end = fileName.substring(dotIndex, fileName.length());
        if (TextUtils.isEmpty(end) && end.length() < 2) {
            return type;
        }
        end = end.substring(1, end.length()).toLowerCase();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        type = mimeTypeMap.getMimeTypeFromExtension(end);
        return type;
    }
}
