package com.harrison.commonutils.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * @Description: 文件处理工具类
 * @Author: HanYu
 * @Date: 2021/2/2
 **/
@Slf4j
public class FileUtils {

    /**上传地址*/
    private String uploadPath="";
    
    /**下载地址*/
    private String downloadPath="";
    
    
    /**
     * 文件上传
     * @param file 上传的文件
     * @return Boolean 上传结果
     */
    public  Boolean fileUpload(MultipartFile file){

        if(file==null || file.isEmpty()){
            log.info("文件为空，请选择正确的文件上传");
            return  false;
        }
        //获取文件名
        String fileName=file.getOriginalFilename();
        log.info("上传文件名为:"+fileName);
        //上传地址
        String filePath=uploadPath;
        File reportFile=new File(filePath,fileName);
        if(!reportFile.getParentFile().exists()){
            reportFile.getParentFile().mkdirs();
        }
        try {
            file.transferTo(reportFile);
            log.info("文件上传成功");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.info("文件上传失败");
        }
        return false;
    }

    /**
     * 文件下载
     * @param response 返回响应
     * @param fileName 需要下载的文件名称
     */
    public  void fileDownload(HttpServletResponse response, String fileName){

        if(!StringUtils.isEmpty(fileName)){
            // 获取需要下载的文件
            File downloadFile=new File(downloadPath,fileName);
            if(downloadFile.exists()){
                response.setContentType("application/force-download");
                try {
                    response.addHeader("Content-Disposition","attachment;fileName=" +  URLEncoder.encode(fileName, "UTF-8"));// 设置文件名
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                byte[] buffer =new byte[1024];
                FileInputStream fileInputStream=null;
                BufferedInputStream bufferedInputStream=null;

                try {
                    fileInputStream=new FileInputStream(downloadFile);
                    bufferedInputStream=new BufferedInputStream(fileInputStream);

                    OutputStream outputStream = response.getOutputStream();
                    int i=bufferedInputStream.read(buffer);
                    while (i!=-1){
                        outputStream.write(buffer,0,i);
                        i=bufferedInputStream.read(buffer);
                    }
                    log.info(fileName+" 下载完成");
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info(fileName+" 下载失败");
                }finally {
                    if(bufferedInputStream!=null){
                        try {
                            bufferedInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(fileInputStream!=null){
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    /**
     * 项目静态资源目录文件下载
     * @param response 返回响应
     * @param fileName 需要下载的文件名称
     * @throws IOException IO异常
     */
    public  void staticFileDownload(HttpServletResponse response,String fileName) throws IOException {

        if(!StringUtils.isEmpty(fileName)){

            //获取需要下载的资源
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "static/"+fileName);
            InputStream inputStream = resources[0].getInputStream();
            //设置返回头Header Content-Type :  application/force-download
            response.setContentType("application/force-download");
            try {
                // 设置下载文件的文件名
                response.addHeader("Content-Disposition","attachment;fileName=" +  URLEncoder.encode(fileName, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] buffer =new byte[1024];
            BufferedInputStream bufferedInputStream=null;

            try {
                bufferedInputStream=new BufferedInputStream(inputStream);
                OutputStream outputStream = response.getOutputStream();
                int i=bufferedInputStream.read(buffer);
                while (i!=-1){
                    outputStream.write(buffer,0,i);
                    i=bufferedInputStream.read(buffer);
                }
                log.info(fileName+" 下载完成");
            } catch (Exception e) {
                e.printStackTrace();
                log.info(fileName+" 下载失败");
            }finally {
                if(bufferedInputStream!=null){
                    try {
                        bufferedInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(inputStream!=null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /**
     * 多文件上传
     * @param request http 请求参数
     * @return 上传状态
     */
    public  boolean multiFileUpload(HttpServletRequest request) {

        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
            String filePath = uploadPath;
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    stream = new BufferedOutputStream(new FileOutputStream(new File(filePath + file.getOriginalFilename())));//设置文件路径及名字
                    stream.write(bytes);// 写入
                    stream.close();
                } catch (Exception e) {
                    stream = null;
                    log.info("第 " + i + " 个文件上传失败 ==> " + e.getMessage());
                    return false;
                }
            } else {
                log.info("第 " + i + " 个文件上传失败因为文件为空");
                return false;
            }
        }
        return true;
    }
    
    
    
}
