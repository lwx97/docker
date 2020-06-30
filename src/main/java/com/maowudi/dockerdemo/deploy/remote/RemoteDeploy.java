package com.maowudi.dockerdemo.deploy.remote;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.maowudi.dockerdemo.util.docker.DockerService;
import com.maowudi.dockerdemo.util.httpclient.HttpClientUtil;
import com.maowudi.dockerdemo.util.projectbuild.ProjectBuildUtil;
import com.maowudi.dockerdemo.util.targz.TargzUtil;
import com.maowudi.dockerdemo.vo.DockerCreateContainerResopnse;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RemoteDeploy {

    private final static Logger log = LoggerFactory.getLogger(RemoteDeploy.class);

    public static void autoDeploy(String dockerfilePath,String gzOutPath,String tag){
        log.info("1.先构建本地项目");
        //1.先构建本地项目
        ProjectBuildUtil.packageLocalProject();
        //2.获取项目target路径
        String localProjectTargetPath = ProjectBuildUtil.getLocalProjectTargetPath();
        log.info("获取项目target路径:{}",localProjectTargetPath);
        //3.获取 pom model 得到jar包名称,dockerfile 路径
        Model model = ProjectBuildUtil.getLocalProjectPomModel();
        String jarName = model.getArtifactId() + "-" + model.getVersion() +"."+ model.getPackaging();
        log.info("jar包名称",jarName);
        log.info("dockerfile 路径",dockerfilePath);
        //4.打成tar.gz包
        log.info("开始压缩成tar.gz");
        List<String> filePaths = new ArrayList<>();
        filePaths.add(localProjectTargetPath + jarName);
        filePaths.add(dockerfilePath);
        TargzUtil.compressToTargz(filePaths,gzOutPath);
        log.info("压缩文件完成！");
        //5.上传文件到docker服务器
        log.info("开始上传文件到Docker服务器");
        log.info("开始构建镜像");
        HttpClientUtil.upload("http://39.105.134.3:80/uploadDockerfileGz?tag="+tag,new File(gzOutPath),"dockerfile.tar.gz");
        //6.通过镜像TAG 创建容器启动
        log.info("构建镜像成功！");
        DockerService dockerService = new DockerService();
        dockerService.connectDocker();
        log.info("开始创建容器");
        DockerCreateContainerResopnse remotetest = dockerService.createContainer(tag, "remotetest", 8077);
        log.info("开始启动容器");
        dockerService.startContainer(remotetest.getContainerId());
        log.info("容器启动成功！暴露端口：{}",remotetest.getBindPort());
    }

    public static void main(String[] args){
        autoDeploy("C:\\Users\\HD\\IdeaProjects\\docker\\src\\main\\docker\\dockerfile","C:\\Users\\HD\\IdeaProjects\\docker\\src\\main\\docker\\dockerfile.tar.gz","remotedeploytest");
    }

}
