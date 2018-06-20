package com.usi.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author 廖大剑
 * @version V1.0
 * @Description: 文件工具
 * @Company: 广东全通教育股份有限公司
 * @date 2015/8/20
 */
public class FileUtil {

	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 读取文件内容 ，一次读取多个字节
	 *
	 * @return 返回文件内容
	 */
	public static String readerFileContent(String path) {
		return readerFileContent(path, "UTF-8");
	}

	/**
	 * 读取文件内容 ，一次读取多个字节
	 *
	 * @return 返回文件内容
	 */
	public static String readerFileContent(String path, String encoding) {
		String text = null;
		try {
			File file = new File(path);
			if(!file.exists()){
                throw new RuntimeException("上传文件为空");
            }else{
                text = FileUtils.readFileToString(file,StringUtils.isBlank(encoding) ? "UTF-8" : encoding);
            }
		} catch (Exception e) {
			logger.error("读取文件内容异常：{}，文件{}：",e,path);
		}
		return text;
	}

	/**
	 * 读取文件字节内容，并以base64加密返回
	 *
	 * @param path
	 * @return base64加密内容
	 * @throws IOException
	 */
	public static String readerFileBytes(String path) throws IOException {

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		//一个字节一个节点读
		FileInputStream is = new FileInputStream(path);
		int len = 0;
		while ((len = is.read()) != -1) {
			bytes.write(len);
		}
		byte[] by = bytes.toByteArray();
		is.close();
		return Base64.encodeBase64String(by);
	}

	/**
	 * 将文件字节数组转成原始文件存储在服务器本地
	 * @param toFile 保存文件全路径, 包含文件名
	 * @param str    Base64加密的文件字节数据
	 * @return
	 */

	public static String saveByteZipFile(String toFile, String str, String md5a) throws IOException {
		try {
			//存储路径
			String dir = toFile.substring(0, toFile.lastIndexOf(File.separator));
			if (!new File(dir).exists()) {
				FileUtils.forceMkdir(new File(dir));
			}

			//写zip文件
			byte[] bytes = Base64.decodeBase64(str);
			String bakFileName = dir + File.separator + DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS") + ".zip";
			File targetFile = new File(bakFileName);
			FileUtils.writeByteArrayToFile(targetFile, bytes);

			//写完后检查写文件是否有数据丢失
			String md5v = EncryptUtil.fileMd5(targetFile);


			if (!md5a.equalsIgnoreCase(md5v)) {

				throw new RuntimeException("接收的文件md5值不正确，可能丢失数据");
			}

			return bakFileName;
		} catch (IOException ex1) {
			ex1.printStackTrace();
			throw ex1;
		}
	}

	/**
	 * 将文件字节数组转成原始文件存储在服务器本地
	 *
	 * @param toFile 保存文件全路径, 包含文件名
	 * @param str    Base64加密的文件字节数据
	 * @return
	 */

	public static String saveByteFile(String toFile, String str, String md5a) throws IOException {
		try {
			//存储路径
			String dir = toFile.substring(0, toFile.lastIndexOf(File.separator));
			if (!new File(dir).exists()) {
				FileUtils.forceMkdir(new File(dir));
			}

			//写zip文件
			byte[] bytes = Base64.decodeBase64(str);
			// String bakFileName = dir + File.separator + DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS") + ".zip";
			File targetFile = new File(toFile);
			FileUtils.writeByteArrayToFile(targetFile, bytes);

			// 是否验证md5
			if (StringUtils.isNotBlank(md5a)) {
				//写完后检查写文件是否有数据丢失
				String md5v = EncryptUtil.fileMd5(targetFile);
				if (!md5a.equalsIgnoreCase(md5v)) {
					throw new RuntimeException("接收的文件md5值不正确，可能丢失数据");
				}
			}


			return toFile;
		} catch (IOException ex1) {
			ex1.printStackTrace();
			throw ex1;
		}
	}


	/**
	 * 保存文件
	 *
	 * @param toFile 文件地址
	 * @param str    文件内容
	 * @return
	 * @throws IOException
	 */
	public static String saveFile(String toFile, String str) throws IOException {
		try {
			//存储路径
			String dir = toFile.substring(0, toFile.lastIndexOf(File.separator));
			if (!new File(dir).exists()) {
				FileUtils.forceMkdir(new File(dir));
			}
			//写文件
			byte[] bytes = str.getBytes();
			File targetFile = new File(toFile);
			FileUtils.writeByteArrayToFile(targetFile, bytes);
			return toFile;
		} catch (IOException ex1) {
			ex1.printStackTrace();
			throw ex1;
		}
	}

	/**
	 * 创建目录或文件
	 * @param fileOrDirPath 文件绝对路径
	 * @param overlay 存在是否覆盖
	 * @return 0正常创建文件，1文件已存在
	 */
	public static boolean createDirOrFile(String fileOrDirPath,boolean overlay) throws IOException {
		File file = new File(fileOrDirPath);
		//存在覆盖
		if (file.exists() && overlay) {
			file.delete();
		}
		//存在不覆盖，返回false
		else if(file.exists() && !overlay) {
			return false;
		}
		try {
			//创建文件或目录
			String dir = fileOrDirPath.substring(0, fileOrDirPath.lastIndexOf("/"));
			File dirs = new File(dir);
			if (!dirs.isDirectory()) {
				//目录不存在，需创建
				dirs.mkdirs();
			}
			//创建文件
			file.createNewFile();
		} catch (Exception e) {
			logger.error("创建文件或目录失败：{}，文件：{}", e,fileOrDirPath);
			throw e;
		}
		return true;
	}

	/**
	 * 删除单个文件
	 *
	 * @param sPath 被删除文件的路径+文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String sPath) {
		Boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 *
	 * @param sPath 被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		Boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 *
	 * 是否存在目标文件夹
	 *
	 * @param filePath
	 * @return
	 */
	public static boolean isExistFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists() && !file.isDirectory()) {
			return false;
		}
		return true;
	}

		/**
		 * 复制单个文件
		 * @param srcFileName  待复制的文件名
		 * @param destFileName 目标文件名
		 * @param overlay      如果目标文件存在，是否覆盖
		 * @return 如果复制成功返回true，否则返回false
		 */
		public static boolean copyFile(String srcFileName, String destFileName,boolean overlay) {
			File srcFile = new File(srcFileName);

			// 判断源文件是否存在
			if (!srcFile.exists()) {
				throw new RuntimeException("源文件：" + srcFileName + "不存在！");
			} else if (!srcFile.isFile()) {
				throw new RuntimeException("复制文件失败，源文件：" + srcFileName + "不是一个文件！");
			}

			// 判断目标文件是否存在
			File destFile = new File(destFileName);
			if (destFile.exists()) {
				// 如果目标文件存在并允许覆盖
				if (overlay) {
					// 删除已经存在的目标文件，无论目标文件是目录还是单个文件
					new File(destFileName).delete();
				}
			} else {
				// 如果目标文件所在目录不存在，则创建目录
				if (!destFile.getParentFile().exists()) {
					// 目标文件所在目录不存在
					if (!destFile.getParentFile().mkdirs()) {
						// 复制文件失败：创建目标文件所在目录失败
						return false;
					}
				}
			}

			// 复制文件
			int byteread = 0; // 读取的字节数
			InputStream in = null;
			OutputStream out = null;

			try {
				in = new FileInputStream(srcFile);
				out = new FileOutputStream(destFile);
				byte[] buffer = new byte[1024];

				while ((byteread = in.read(buffer)) != -1) {
					out.write(buffer, 0, byteread);
				}
				return true;
			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				return false;
			} finally {
				try {
					if (out != null)
						out.close();
					if (in != null)
						in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * 复制整个目录的内容
		 *
		 * @param srcDirName  待复制目录的目录名
		 * @param destDirName 目标目录名
		 * @param overlay     如果目标目录存在，是否覆盖
		 * @return 如果复制成功返回true，否则返回false
		 */
		public static boolean copyDirectory(String srcDirName, String destDirName,boolean overlay) {
			// 判断源目录是否存在
			File srcDir = new File(srcDirName);
			if (!srcDir.exists()) {
				throw new RuntimeException("复制目录失败：源目录" + srcDirName + "不存在！");
			} else if (!srcDir.isDirectory()) {
				throw new RuntimeException("复制目录失败：" + srcDirName + "不是目录！");
			}

			// 如果目标目录名不是以文件分隔符结尾，则加上文件分隔符
			if (!destDirName.endsWith(File.separator)) {
				destDirName = destDirName + File.separator;
			}
			File destDir = new File(destDirName);
			// 如果目标文件夹存在
			if (destDir.exists()) {
				// 如果允许覆盖则删除已存在的目标目录
				if (overlay) {
					new File(destDirName).delete();
				} else {
					throw new RuntimeException("复制目录失败：目的目录" + destDirName + "已存在！");
				}
			} else {
				// 创建目的目录
				if (!destDir.mkdirs()) {
					System.out.println("复制目录失败：创建目的目录失败！");
					return false;
				}
			}

			boolean flag = true;
			File[] files = srcDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				// 复制文件
				if (files[i].isFile()) {
					flag = copyFile(files[i].getAbsolutePath(),
							destDirName + files[i].getName(), overlay);
					if (!flag)
						break;
				} else if (files[i].isDirectory()) {
					flag = copyDirectory(files[i].getAbsolutePath(),
							destDirName + files[i].getName(), overlay);
					if (!flag)
						break;
				}
			}
			if (!flag) {
				throw new RuntimeException("复制目录" + srcDirName + "至" + destDirName + "失败！");
			} else {
				return true;
			}
		}

	/**
	 * 获取文件大小
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String getFileSize(String filePath) throws IOException {
		String fileSize;
		FileInputStream fis= null;
		try{
			File f = new File(filePath);
			if(!f.exists()){
				logger.error("获取文件大小异常，文件不存在：" + filePath);
				throw new RuntimeException("获取文件大小异常，文件不存在：" + filePath);
			}
			fis= new FileInputStream(f);
			fileSize = formetFileSize(fis.available());
		}catch(Exception e){
			throw e;
		} finally{
			if (null!=fis){
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("获取文件大小异常：",e);
				}
			}
		}
		return fileSize;
	}

	/**
	 * 文件大小转换
	 * @param fileS
	 * @return
	 */
	private static String formetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString;
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) +"G";
		}
		return fileSizeString;
	}

	/**
	 * 获取文件md5值
	 * @param filePath 文件路径
	 * @return
	 */
	public static String getMd5ByFile(String filePath) throws FileNotFoundException {
		String value = null;
		File file = new File(filePath);
		if(!file.exists()){
			logger.error("文件大小转换，文件不存在：" + filePath);
			throw new RuntimeException("文件大小转换，文件不存在：" + filePath);
		}
		FileInputStream in = new FileInputStream(file);
		try {
			MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}

	/**
	 * 获取网络文件并保存到本机指定目录(文件名加－时间戳yyyyMMddkkmmss)
	 * @param url 网络文件路径，文件名为中文需要调用此方法前进行urlEncode
	 * @param fileName 文件名称(带后缀)
	 * @param saveDir 保存地址，不加文件名
	 * @return
	 */
	public static void downloadFromUrl(String url,String fileName,String saveDir) {
		try {
			URL httpUrl = new URL(url);
			File f = new File(saveDir + URLDecoder.decode(fileName, "UTF-8"));
			FileUtils.copyURLToFile(httpUrl, f);
		}catch (FileNotFoundException nfe){
			logger.error("获取网络文件异常，文件不存在：",nfe);
			throw new RuntimeException("获取网络文件异常，文件不存在：",nfe);
		}
		catch (Exception e) {
			logger.error("获取网络文件异常：",e);
			throw new RuntimeException("获取网络文件异常：",e);
		}
	}

	/**
	 * 遍历目录及其子目录下的所有文件并保存
	 * @param dir 目录全路径
	 * @param myfile 列表：保存文件路径
	 */
	public static void listDirectory(File dir, List<String> myfile){
		if (!dir.exists()){
			System.out.println("文件名称不存在!");
		}
		else
		{
			if (dir.isFile()){
				myfile.add(dir.getPath());
			} else{
				File[] files = dir.listFiles();
				for (int i = 0; i < files.length; i++  ){
					listDirectory(files[i], myfile);
				}
			}
		}
	}

	// 缓存文件头信息-文件头信息
	public static final HashMap<String, String> mFileTypes = new HashMap<String, String>();
	static {
		mFileTypes.put("ffd8ffe000104a464946", "jpg"); //JPEG (jpg)
		mFileTypes.put("89504e470d0a1a0a0000", "png"); //PNG (png)
		mFileTypes.put("47494638396126026f01", "gif"); //GIF (gif)
		mFileTypes.put("49492a00227105008037", "tif"); //TIFF (tif)
		mFileTypes.put("424d228c010000000000", "bmp"); //16色位图(bmp)
		mFileTypes.put("424d8240090000000000", "bmp"); //24位位图(bmp)
		mFileTypes.put("424d8e1b030000000000", "bmp"); //256色位图(bmp)
		mFileTypes.put("d0cf11e0a1b11ae10000", "doc,xls"); //MS Excel 注意：word、msi 和 excel的文件头一样
		mFileTypes.put("504b0304140006000800", "docx,xlsx");//docx文件
		mFileTypes.put("00000020667479706d70", "mp4");
		mFileTypes.put("49443303000000002176", "mp3");
		mFileTypes.put("000001ba210001000180", "mpg"); //
		mFileTypes.put("3026b2758e66cf11a6d9", "wmv"); //wmv与asf相同
		mFileTypes.put("52494646e27807005741", "wav"); //Wave (wav)
		mFileTypes.put("52494646d07d60074156", "avi");
		mFileTypes.put("504b0304140000000800", "zip");
		mFileTypes.put("526172211a0700cf9073", "rar");
	}

	/**
	 * @author guoxk
	 *
	 * 方法描述：根据文件路径获取文件头信息
	 * @param filePath 文件路径
	 * @return 文件头信息
	 */
	public static String getFileHeader(String filePath) {
		FileInputStream is = null;
		String value = null;
		try {
			is = new FileInputStream(filePath);
			byte[] b = new byte[4];
			/*
			 * int read() 从此输入流中读取一个数据字节。int read(byte[] b) 从此输入流中将最多 b.length
			 * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
			 * 从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
			 */
			is.read(b, 0, b.length);
			value = bytesToHexString(b);
		} catch (Exception e) {
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return value;
	}

	public static String getFileHeader(FileInputStream fileInputStream) {
		FileInputStream is = fileInputStream;
		String value = null;
		try {
//			is = new FileInputStream(filePath);
			byte[] b = new byte[4];
			/*
			 * int read() 从此输入流中读取一个数据字节。int read(byte[] b) 从此输入流中将最多 b.length
			 * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
			 * 从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
			 */
			is.read(b, 0, b.length);
			value = bytesToHexString(b);
		} catch (Exception e) {
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return value;
	}

	/**
	 * 获取文件类型
	 * @param filePath
	 * @return
	 */
	public static String getFileType(String filePath){
		return mFileTypes.get(getFileHeader(filePath));
	}

	/**
	 *
	 * 方法描述：将要读取文件头信息的文件的byte数组转换成string类型表示
	 * @param src 要读取文件头信息的文件的byte数组
	 * @return   文件头信息
	 */
	private static String bytesToHexString(byte[] src) {
		StringBuilder builder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		String hv;
		for (int i = 0; i < src.length; i++) {
			// 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
			hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
			if (hv.length() < 2) {
				builder.append(0);
			}
			builder.append(hv);
		}
//      System.out.println(builder.toString());
		return builder.toString();
	}

	public static void main(String[] arg) throws Exception {
//		List<String> file = new ArrayList<>();
//		listDirectory(new File("D:\\zipts\\test"),file);
//		System.out.println(file.toString());

		System.out.println(getFileType("F:\\帐号密码管理.xlsx"));

		//readerFileContent("D:/Dev/apache-tomcat-7.0.67/webap123ps/microschoolappsvr-static/download/paper/20160803145918/导出测试-20160803145918/db6badb3-02cb-4c27-afd7-1548cdb69417/paper.xml", "UTF-8");
		/*Map<String, Class> classMap = new HashMap<>();
		classMap.put("topicList", TopicXml.class);
		classMap.put("sectionList", SectionXml.class);
		classMap.put("questionList", QuestionXml.class);
		classMap.put("resourceList", ResourceXml.class);
		PaperXml xml = (PaperXml) XmlUtil.xmlToBean(readerFileContent("D:/Dev/apache-tomcat-7.0.67/webapps/microschoolappsvr-static/download/paper/20160803145918/导出测试-20160803145918/db6badb3-02cb-4c27-afd7-1548cdb69417/paper.xml", "UTF-8"), PaperXml.class, classMap);
		System.out.println("123123");*/
		//System.out.println(getMd5ByFile("h:/ideaiu14.rar"));
		//createDirOrFile("d:\\download\\paper\\20160718191026\\44444");

		//copyFile("d:/制卷系统.rar","d:/upload/制卷系统.rar",true);
		//String len = FileUtil.readerFileContent("D:\\wrok\\yj-core\\doc\\全流程测试A3C4SX多选题.xml");
//        List<String> list = FileUtil.unZip("C:\\Users\\john\\Downloads\\test.zip");
//        for(String path : list) {
//            System.out.println(path);
//        }

		// System.out.println(FileUtil.renameFile("C:/zyj_file_dir/paper/229/e9629f96-543d-11e5-9410-525400a25a33/090637/StudentZip.zip","1362918EDB6FEC3FFD6F31B2C48FE3FDF"));
	}
}
