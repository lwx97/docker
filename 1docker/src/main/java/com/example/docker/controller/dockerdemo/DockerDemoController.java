package com.example.docker.controller.dockerdemo;

import com.maowudi.dockerdemo.controller.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Objects;

@RestController
public class DockerDemoController extends BaseController {

    private Logger log = LoggerFactory.getLogger(DockerDemoController.class);

    @Value("${dockertest.linuxPath}")
    private String linuxPath;

    /**
     * 是否删除上传的包
     */
    @Value("${dockertest.delTargz}")
    private boolean delTargz;


    @RequestMapping("/hello")
    public String hello(@RequestParam(required = false) String name) {
        if (name != null) {
            return successRespMsg("hello " + name);
        }
        return successRespMsg("hello world!");
    }

    @RequestMapping("/uploadDockerfileGz")
    public String uploadDockerfileGz(@RequestParam("file") MultipartFile file, @RequestParam(value = "tag", required = false) String tag) {
        if (tag == null) {
            tag = "test-java";
        }
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("文件为空！");
            }
            String filename = file.getOriginalFilename();
            String suffx = filename.substring(filename.lastIndexOf("."));
            log.info("上传文件：{}", filename);
            //每次都是新的文件目录
            String time = new Date().getTime() + "/";
            String path = linuxPath + time + filename;
            File newFile = new File(path);
            if (!newFile.getParentFile().exists()) {
                newFile.getParentFile().mkdirs();
            }
            //写入文件
            file.transferTo(newFile);
            return successRespMsg(buildImages(linuxPath + time, filename, tag));
        } catch (IOException e) {
            e.printStackTrace();
            return errorRespMsg("上传文件出错！");
        }
    }

    @RequestMapping("/dockertest/{info}")
    public String dockertest(@PathVariable(required = false) String info) {
        if (info == null || Objects.equals("lwx", info)) {
            return "请补充info信息";
        }
        return successRespMsg(buildImages(linuxPath, "dockerfile.tar.gz", "java-test"));
    }

    /**
     * 构建镜像
     *
     * @param path
     * @param filename
     * @param tag
     * @return
     */
    public String buildImages(String path, String filename, String tag) {
        //curl POST -H "Content-Type:application/tar" --data-binary '@dockerfile.tar.gz' http://localhost:2375/build?t=samplerepo
        log.info("---------------开始构建镜像--------------------");
        long startime = new Date().getTime();
        String[] curlCmd = {"curl", "-X", "POST", "-H", "Content-Type:application/tar", "--data-binary", "@" + path + filename, "http://localhost:2375/build?t=" + tag};
        String s = execCurl(curlCmd);
        long endtime = new Date().getTime();
        log.info(s);
        log.info("---------------构建镜像结束,用时：{}--------------------", endtime - startime);
        if (s.indexOf("Successfully built") != -1) {
            if (delTargz && path.length() > 10) {
                delTargz(path);
            }
            return tag;
        }
        return "构建镜像失败！";
    }

    /**
     * 执行linux命令
     *
     * @param cmds
     * @return
     */
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

    public void delTargz(String path) {
        String[] curlCmd = {"rm", "-rf", path};
        String s = execCurl(curlCmd);
        log.info("删除path:{}", path);
        log.info(s);
    }

}
