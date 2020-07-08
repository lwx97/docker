package com.example.demo.test;

import com.maowudi.docker.util.projectbuild.ProjectBuildUtil;

public class DockerTest {

    public static void main(String[] args) {
        ProjectBuildUtil.execMavenCmd("clean","C:/Users/HD/IdeaProjects/docker/demo/pom.xml");
        String localProjectTargetPath = ProjectBuildUtil.getLocalProjectTargetPath();
        System.out.println(localProjectTargetPath);
    }


}
