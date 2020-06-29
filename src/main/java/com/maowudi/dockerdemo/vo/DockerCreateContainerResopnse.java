package com.maowudi.dockerdemo.vo;

import com.github.dockerjava.api.command.CreateContainerResponse;
import lombok.Data;

@Data
public class DockerCreateContainerResopnse {

    private int port;
    private String url;
    private String containerId;
    private int bindPort;
    private CreateContainerResponse createContainerResponse;
}
