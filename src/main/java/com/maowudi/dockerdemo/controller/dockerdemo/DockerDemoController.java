package com.maowudi.dockerdemo.controller.dockerdemo;

import com.maowudi.dockerdemo.controller.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

@RestController
public class DockerDemoController extends BaseController {

    private Logger log = LoggerFactory.getLogger(DockerDemoController.class);

    private String linuxPath = "/root/dockerfiledome/";

    @RequestMapping("/hello")
    public String hello(@RequestParam(required = false) String name) {
        if (name != null) {
            return "hello " + name;
        }
        return "hello world";
    }

    @RequestMapping("/uploadDockerfileGz")
    public String uploadDockerfileGz(@RequestParam("file")MultipartFile file) {
        try {
            if(file.isEmpty()){
                throw new RuntimeException("文件为空！");
            }
            String filename = file.getOriginalFilename();
            String suffx = filename.substring(filename.lastIndexOf("."));
            log.info("上传文件：{}",filename);
            //每次都是新的文件目录
            String time = new Date().getTime() + "";
            String path = linuxPath + time + filename;
            File newFile = new File(path);
            if(newFile.getParentFile().exists()){
                newFile.getParentFile().mkdirs();
            }
            //写入文件
            file.transferTo(newFile);
            return buildImages(path,filename,"javaTest");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error!!!!!";
    }

    @RequestMapping("/dockertest")
    public String dockertest(){
       return buildImages("/root/dockerfiledome/","dockerfile.tar.gz","ttttt");
    }

    public String buildImages(String path,String filename,String tag){
        //curl POST -H "Content-Type:application/tar" --data-binary '@dockerfile.tar.gz' http://localhost:2375/build?t=samplerepo
        String[] cdCmd = {"cd",path};
        String s1 = execCurl(cdCmd);
        log.info("-----------------------------------");
        System.out.println(s1);
        String[] curlCmd = {"curl", "-X", "POST", "-H", "Content-Type:application/tar", "--data-binary", "@" + filename, "http://localhost:2375/build?t=" + tag};
        String s = execCurl(curlCmd);
        log.info("-----------------------------------");
        System.out.println(s);
        return s;
    }

    public static String execCurl(String[] cmds) {
        ProcessBuilder process = new ProcessBuilder(cmds);
        Process p;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            return builder.toString();
        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        }
        return null;
    }

}
