package com.city.esdemo2.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/")
public class downloadfile {


        @GetMapping("download")
        public void downloadFile(HttpServletResponse response) {
                downloadFile("功能配置操作说明.docx",response);
        }
    /**
     * 下载文件
     *
     * @param fileName 文件名
     * @param response 响应
     */
    public void downloadFile(String fileName, HttpServletResponse response) {
        InputStream inputStream = null;
        try {

            // 设置响应内容类型和字符编码
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            // 对文件名进行URLEncoder编码，处理空格和特殊字符
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20");

            // 设置Content-Disposition头，确保文件名显示正确
            response.setHeader("Content-Disposition", "attachment; filename=" +fileName);

            // 获取文件输入流
            Path path = Paths.get("src/main/resources/files/" + fileName);
            File file = path.toFile();
            inputStream = new FileInputStream(file);
            // 将文件内容写入响应输出流
            IOUtils.copy(inputStream, response.getOutputStream());

        } catch (IOException e) {
            // 记录错误日志并设置响应状态为500内部服务器错误
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            // 确保资源正确关闭
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
