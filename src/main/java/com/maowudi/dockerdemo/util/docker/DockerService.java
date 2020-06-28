package com.maowudi.dockerdemo.util.docker;

import com.alibaba.fastjson.JSONObject;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DockerClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DockerService {

    private Logger log = LoggerFactory.getLogger(DockerService.class);
    private DockerClient dockerClient;
    /**
     * 连接默认的docker 得到docker客户端
     * @return
     */
    public DockerClient connectDocker() {
        if (dockerClient != null) {
            return dockerClient;
        }
        dockerClient = DockerClientBuilder.getInstance("tcp://39.105.134.3:2375").build();
        Info exec = dockerClient.infoCmd().exec();
        String info = JSONObject.toJSONString(exec);
        log.info("当前登录docker信息：");
        log.info("info:{}",info);
        return dockerClient;
    }

    /**
     * 连接指定的docker 得到docker客户端
     * @return
     */
    public DockerClient connectDocker(String tcp) {
        dockerClient = DockerClientBuilder.getInstance(tcp).build();
        Info exec = dockerClient.infoCmd().exec();
        String info = JSONObject.toJSONString(exec);
        log.info("当前登录docker信息：");
        log.info("info:{}",info);
        return dockerClient;
    }

    /**
     * 启动指定的容器
     * @param dockerClient
     * @param containerId
     */
    public static void startContainer(DockerClient dockerClient,String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }

    /**
     * 启动指定的容器
     * @param containerId
     */
    public  void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }


    public static void main(String[] args) {
        DockerService dockerService = new DockerService();
        dockerService.connectDocker();
        dockerService.startContainer("24f24e7ddb10");
    }
}
