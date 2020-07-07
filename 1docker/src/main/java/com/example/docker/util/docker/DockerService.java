package com.example.docker.util.docker;

import com.alibaba.fastjson.JSONObject;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DockerClientBuilder;
import com.maowudi.dockerdemo.vo.DockerCreateContainerResopnse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class DockerService {

    private Logger log = LoggerFactory.getLogger(DockerService.class);
    private DockerClient dockerClient;

    private String aliyunDockerTcp = "tcp://39.105.134.3:2375";

    /**
     * 连接默认的docker 得到docker客户端
     *
     * @return
     */
    public DockerClient connectDocker() {
        return connectDocker(aliyunDockerTcp);
    }


    /**
     * 连接指定的docker 得到docker客户端
     *
     * @return
     */
    public DockerClient connectDocker(String tcp) {
        dockerClient = DockerClientBuilder.getInstance(tcp).build();
        Info exec = dockerClient.infoCmd().exec();
        String info = JSONObject.toJSONString(exec);
        log.info("当前登录docker信息：");
        log.info("info:{}", info);
        return dockerClient;
    }

    /**
     * 关闭连接
     *
     * @throws IOException
     */
    public void closeDockerClient() throws IOException {
        dockerClient.close();
    }

    /**
     * 创建容器
     *
     * @param imageTag
     */
    public String createContainer(String imageTag, String containerName, int port, int bindPort) {
        Ports portBindings = new Ports();
        //映射8080端口到主机8077
        portBindings.bind(ExposedPort.tcp(port), Ports.Binding.bindPort(bindPort));
        CreateContainerResponse exec = dockerClient.createContainerCmd(imageTag)
                .withHostConfig(new HostConfig().withPortBindings(portBindings))
                .withName(containerName)
                .withExposedPorts(new ExposedPort(bindPort))
                .exec();
        return exec.getId();

    }

    /**
     * 创建容器,随机生产端口
     *
     * @param imageTag
     */
    public DockerCreateContainerResopnse createContainer(String imageTag, String containerName, int port) {
        Ports portBindings = new Ports();
        //映射8080端口到主机随机端口
        int bindPort = 0;
        try {
            bindPort = new DatagramSocket(0).getLocalPort();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        portBindings.bind(ExposedPort.tcp(port), Ports.Binding.bindPort(bindPort));
        CreateContainerResponse exec = dockerClient.createContainerCmd(imageTag)
                .withHostConfig(new HostConfig().withPortBindings(portBindings))
                .withName(containerName)
                .withExposedPorts(new ExposedPort(bindPort))
                .exec();
        DockerCreateContainerResopnse resopnse = new DockerCreateContainerResopnse();
        resopnse.setBindPort(bindPort);
        resopnse.setPort(port);
        resopnse.setContainerId(exec.getId());
        resopnse.setCreateContainerResponse(exec);
        return resopnse;

    }

    public static void main(String[] args) {
        DockerService dockerService = new DockerService();
        dockerService.connectDocker();
        DockerCreateContainerResopnse test1111 = dockerService.createContainer("liwenx97/lwx:537abe2", "test", 8080);
        System.out.println(test1111);
    }

    /**
     * 启动指定的容器
     *
     * @param dockerClient
     * @param containerId
     */
    public static void startContainer(DockerClient dockerClient, String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }

    /**
     * 启动指定的容器
     *
     * @param containerId
     */
    public void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }


    /**
     * 关闭容器
     *
     * @param containerId
     */
    public void stopContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
    }

    /**
     * 删除指定容器
     *
     * @param containerId
     */
    public void deleteContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
    }

    /**
     * 删除指定镜像
     *
     * @param imageTag
     */
    public void deleteImages(String imageTag) {
        dockerClient.removeImageCmd(imageTag).exec();
    }

}
