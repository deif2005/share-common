package com.usi.util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 压缩包相关工具类
 * @date
 */
public class ZipUtil {

    private static Logger logger = LoggerFactory.getLogger(ZipUtil.class);

    /**
     * 将文件或文件夹压缩成zip压缩包
     * @param savePath zip压缩包保存地址
     * @param files 文件地址列表
     * @param directory 文件夹地址列表
     * @param password 密码
     */
    public static void compressZip(String savePath,String files[],String directory[],String password) {
        try {
            //判断压缩包是否存在，存在则删除重新创建，否则会向压缩包内添加文件
            File zipFile = new File(savePath);
            if(zipFile.exists()){
                zipFile.delete();
            }
            //生成压缩包的地址
            ZipFile zip = new ZipFile(savePath);
            //生成文件夹
            String saveDir = savePath.substring(0,savePath.lastIndexOf("/") + 1);
            if(!new File(saveDir).exists()){
                FileUtil.createDirOrFile(saveDir,false);
            }
            //封装成压缩工具类所需的fileList
            ArrayList<File> filesToAdd = new ArrayList<>();
            if(files != null && files.length > 0) {
                for (String fileDir : files) {
                    File file = new File(fileDir);
                    //判断文件是否存在
                    if (!file.exists()) {
                        throw new RuntimeException("压缩文件不存在：" + fileDir);
                    }
                    filesToAdd.add(file);
                }
            }
            //实例化压缩参数类
            ZipParameters parameters = new ZipParameters();
            //压缩方式
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            //压缩级别
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            //加密
            if(StringUtils.isNotBlank(password)){
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
                parameters.setPassword(password);
            }
            //添加要压缩的文件
            zip.addFiles(filesToAdd, parameters);
            //添加要压缩的文件夹
            if(directory != null && directory.length > 0) {
                for (String dir : directory) {
                    File dirFile = new File(dir);
                    //判断文件是否存在
                    if (!dirFile.exists()) {
                        throw new RuntimeException("压缩文件夹不存在：" + dir);
                    }
                    zip.addFolder(dir, parameters);
                }
            }
        }catch (Exception e) {
            logger.error("压缩文件及文件夹异常：",e);
        }
    }

    /**
     * 解压缩文件
     * @param zipPath zip包文件地址
     * @param password 解压密码(可选)
     * @param
     */
    public static List<String> unZip(String zipPath,String password,String rules){
        List<String> fileList = new ArrayList<>();
        try {
            ZipFile zipFile = new ZipFile(zipPath);
            zipFile.setFileNameCharset(getEncoding(zipPath));
            //判断是否存在密码
            if (zipFile.isEncrypted() && StringUtils.isNotBlank(password)) {
                zipFile.setPassword(password);
            }
            //解压缩目录，当前压缩包文件名为目录
            String dirPath = zipPath.substring(0, zipPath.lastIndexOf("."));
            zipFile.extractAll(dirPath);

            //获取解压缩的所有文件路径列表
            FileUtil.listDirectory(new File(dirPath),fileList);

            //判断是否需要验证文件格式
            if (StringUtils.isNotBlank(rules)) {
                //获取文件目录，验证解压缩文件格式
                for (String path : fileList) {
                    String suffix = path.substring(path.lastIndexOf(".") + 1);
                    //不包含文件格式
                    if (!rules.contains(suffix)) {
                        //抛出业务异常，需要删除压缩包及文件夹
                        throw new RuntimeException("上传文件压缩包内文件格式有误");
                    }
                }
            }
        } catch (Exception e) {
            if(e.getMessage().contains("zip file does not exist")){
                throw new RuntimeException("解压缩文件失败，文件不存在：" + zipPath);
            }else if(e.getMessage().contains("not a zip file")){
                throw new RuntimeException("解压缩文件不是zip格式！");
            }else if(e.getMessage().contains("password")){
                throw new RuntimeException("解压缩文件失败，密码错误！");
            }
        }
        return fileList;
    }

    private static String getEncoding(String path) throws Exception
    {
        String encoding = "UTF-8" ;
        ZipFile zipFile = new ZipFile(path) ;
        zipFile.setFileNameCharset(encoding) ;
        List<FileHeader> list = zipFile.getFileHeaders() ;
        for(int i=0;i<list.size();i++)
        {
            FileHeader fileHeader = list.get(i) ;
            String fileName = fileHeader.getFileName();
            //判断是不是乱码
            if (!isMessyCode(fileName)){
                return encoding;
            }else
                return "GBK";
        }
        return encoding ;
    }

    /**
     * 判断是否为乱码
     * @param strName
     * @return
     */
    private static boolean isMessyCode(String strName) {
        try {
            Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
            Matcher m = p.matcher(strName);
            String after = m.replaceAll("");
            String temp = after.replaceAll("\\p{P}", "");
            char[] ch = temp.trim().toCharArray();

            int length = (ch != null) ? ch.length : 0;
            for (int i = 0; i < length; i++) {
                char c = ch[i];
                if (!Character.isLetterOrDigit(c)) {
                    String str = "" + ch[i];
                    if (!str.matches("[\u4e00-\u9fa5]+")) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void main(String[] args) {
        String filePath = "D:\\home\\usi_resource\\subject\\jy_package\\初中数学基础数据 (1).zip";
        List<String> fileList = unZip(filePath,null,null);
        System.out.println(fileList.toString());
    }
}
