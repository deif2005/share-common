package com.wd.util;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author miou
 * @date 2019-02-22
 */
public class RarUtil {
    /**
     * 解压rar格式压缩包。
     * 对应的是java-unrar-0.3.jar，但是java-unrar-0.3.jar又会用到commons-logging-1.1.1.jar
     */
    private static void unrar(String sourceRar,String destDir) throws Exception{
        Archive a = null;
        FileOutputStream fos = null;
        try{
            a = new Archive(new File(sourceRar));
            FileHeader fh = a.nextFileHeader();
            while(fh!=null){
                if(!fh.isDirectory()){
                    //1 根据不同的操作系统拿到相应的 destDirName 和 destFileName
                    String compressFileName = fh.getFileNameString().trim();
                    String destFileName = "";
                    String destDirName = "";
                    //非windows系统
                    if(File.separator.equals("/")){
                        destFileName = destDir + compressFileName.replaceAll("\\\\", "/");
                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("/"));
                        //windows系统
                    }else{
                        destFileName = destDir + compressFileName.replaceAll("/", "\\\\");
                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("\\"));
                    }
                    //2创建文件夹
                    File dir = new File(destDirName);
                    if(!dir.exists()||!dir.isDirectory()){
                        dir.mkdirs();
                    }
                    //3解压缩文件
                    fos = new FileOutputStream(new File(destFileName));
                    a.extractFile(fh, fos);
                    fos.close();
                    fos = null;
                }
                fh = a.nextFileHeader();
            }
            a.close();
            a = null;
        }catch(Exception e){
            throw e;
        }finally{
            if(fos!=null){
                try{fos.close();fos=null;}catch(Exception e){e.printStackTrace();}
            }
            if(a!=null){
                try{a.close();a=null;}catch(Exception e){e.printStackTrace();}
            }
        }
    }

    public static void main(String[] args) {
        try{
            unrar("E:\\word_question_resource\\新建文本文档.rar","E:\\word_question_resource");
        }catch(Exception e){
            System.out.println("解压出错");
        }
    }
}
