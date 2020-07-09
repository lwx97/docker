package com.maowudi.docker.remote;


import com.maowudi.docker.util.docker.DockerService;
import com.maowudi.docker.util.httpclient.HttpClientUtil;
import com.maowudi.docker.util.projectbuild.ProjectBuildUtil;
import com.maowudi.docker.util.targz.TargzUtil;
import com.maowudi.docker.vo.DockerCreateContainerResopnse;
import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class RemoteDeploy {

    private final static Logger log = LoggerFactory.getLogger(RemoteDeploy.class);

    public static void autoDeploy(String aliyunDockerTcp,String dockerfilePath, String tag, String containerName, int port,String pomPath) {
        File file = new File(dockerfilePath);
        String gzOutPath = file.getParent() + File.separator + "dockerfile.tar.gz";
        log.info("1.先构建本地项目");
        //1.先构建本地项目
        ProjectBuildUtil.packageLocalProjectSkipTest(pomPath);
        //2.获取项目target路径
        String localProjectTargetPath = ProjectBuildUtil.getLocalProjectTargetPath();
        log.info("获取项目target路径:{}", localProjectTargetPath);
        //3.获取 pom model 得到jar包名称,dockerfile 路径
        Model model = ProjectBuildUtil.getLocalProjectPomModel();
        String jarName = model.getArtifactId() + "-" + model.getVersion() + "." + model.getPackaging();
        log.info("jar包名称", jarName);
        log.info("dockerfile 路径", dockerfilePath);
        //4.打成tar.gz包
        log.info("开始压缩成tar.gz");
        List<String> filePaths = new ArrayList();
        filePaths.add(localProjectTargetPath + jarName);
        filePaths.add(dockerfilePath);
        TargzUtil.compressToTargz(filePaths, gzOutPath);
        log.info("压缩文件完成！");
        //5.上传文件到docker服务器
        log.info("开始上传文件到Docker服务器");
        log.info("开始构建镜像");
        HttpClientUtil.upload("http://39.105.134.3:8077/uploadDockerfileGz?tag=" + tag, new File(gzOutPath), "dockerfile.tar.gz");
        //6.通过镜像TAG 创建容器启动
        log.info("构建镜像成功！");
        log.info("镜像tag:{}", tag);
        DockerService dockerService = new DockerService(aliyunDockerTcp);
        dockerService.connectDocker();
        log.info("开始创建容器");
        DockerCreateContainerResopnse remotetest = dockerService.createContainer(tag, containerName, port);
        log.info("容器创建成功！容器ID：{}", remotetest.getContainerId());
        log.info("开始启动容器");
        dockerService.startContainer(remotetest.getContainerId());
        log.info("容器启动成功！暴露端口：{}", remotetest.getBindPort());
    }

    public static void autoDeploy(String aliyunDockerTcp,String dockerfilePath, int port) {
        //"tcp://39.105.134.3:2375"
        String tagx = UUID.randomUUID().toString().replace("-", "");
        String tag = "liwenx97/lwx:" + tagx.substring(0, 7);
        String containerName = "remote_" + tagx.substring(0, 7);
        autoDeploy(aliyunDockerTcp,dockerfilePath, tag, containerName, port,null);
    }



}
