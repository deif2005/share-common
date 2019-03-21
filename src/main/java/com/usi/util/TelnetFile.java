package com.wd.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by defi on 2016/7/23.
 * 网络文件下载工具类
 */

public class TelnetFile {
    /**
     * http小文件下载
     * @param httpUrl  下载路径
     * @param saveFile 保存路径
     */
    public static boolean httpDownload(String httpUrl, String saveFile) {

        int byteSum = 0;
        int byteRead = 0;

        URL url = null;
        try {
            url = new URL(httpUrl);
        }catch (MalformedURLException el){
            el.printStackTrace();
        }
        try{
            File localFile = new File(saveFile);
            File parent = localFile.getParentFile();
            if (!parent.exists()){
                parent.mkdirs();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(saveFile);

            byte[] buffer = new byte[1204];
            while ((byteRead = inStream.read(buffer)) != -1) {
                byteSum += byteRead;
                System.out.println(byteSum);
                fs.write(buffer, 0, byteRead);
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * http上传文件
     * @param url
     * @param file
     * @return
     */
    public static String fileLoad(String url, File file){
        String body = "{}";
        if (url == null || "".equals(url)){
            throw new RuntimeException("参数不合法");
        }
        if (!file.exists()){
            throw new RuntimeException("上传指定的文件不存在!");
        }
        PostMethod postMethod = new PostMethod(url);
        try {
            // FilePart：用来上传文件的类,file即要上传的文件
            FilePart filePart = new FilePart("uploadFile",file);
            //FilePart filePart1 = new FilePart("uploadFile",new File("D:\\exam\\data\\remark1.txt"));
            Part[] parts = {filePart};
            // 对于MIME类型的请求，httpclient建议全用MulitPartRequestEntity进行包装
            MultipartRequestEntity mre = new MultipartRequestEntity(parts,postMethod.getParams());
            postMethod.setRequestEntity(mre);

            HttpClient client = new HttpClient();
            // 由于要上传的文件可能比较大 , 因此在此设置最大的连接超时时间
            client.getHttpConnectionManager().getParams().setConnectionTimeout(50000);
            int status = client.executeMethod(postMethod);
            if(status == HttpStatus.SC_OK){
                InputStream inputStream = postMethod.getResponseBodyAsStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String str;
                while ((str = bufferedReader.readLine()) != null){
                    stringBuffer.append(str);
                }
                body = stringBuffer.toString();
            }else {
                body = "Upload file failed";
            }
        } catch (FileNotFoundException e){
            throw new RuntimeException("上传的文件不存在");
        } catch (IOException e){
            throw new RuntimeException("文件上传异常");
        } finally{
//            释放链接
            postMethod.releaseConnection();
        }
        return body;
    }

    public static void main(String[] args){
        String filePath = "D:\\exam\\data\\remark.txt";
        String url = "http://192.168.10.61:8088/microschoolappsvr-static/upload.jsp";
//        upLoadFile1(filePath,url);
        try{
            //uploadFile();
            fileLoad("http://192.168.10.61:8080/fileupload/upload",new File("D:\\exam\\data\\remark.txt"));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }
}
