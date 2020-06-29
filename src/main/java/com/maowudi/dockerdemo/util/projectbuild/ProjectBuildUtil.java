package com.maowudi.dockerdemo.util.projectbuild;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProjectBuildUtil {


    /**
     * 执行maven命令
     * @param mvnCmd 多个命令以空格隔开
     */
    public static void execMavenCmd(String mvnCmd){
        DefaultInvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File("pom.xml"));
        List<String> cmdList = Arrays.asList(mvnCmd.trim().split(" "));
        if(cmdList !=null || cmdList.size()==0) {
            if(mvnCmd.length()>0) {
                cmdList = Collections.singletonList(mvnCmd);
            }else {
                throw new RuntimeException("没有mvn命令");
            }
        }
        request.setGoals(cmdList);
        DefaultInvoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(getSystemEnv("MAVEN_HOME")));
        try {
            InvocationResult execute = invoker.execute(request);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建本地项目
     */
    public static void buildLocalProject(){
        execMavenCmd("clean compile");
    }

    /**
     * 本地项目打包
     */
    public static void packageLocalProject(){
        execMavenCmd("clean package");
    }

    /**
     * 将本地项目安装到本地仓库中
     */
    public static void installLocalProject(){
        execMavenCmd("clean install");
    }

    /**
     * 获取环境变量
     * @param envName
     * @return
     */
    public static String getSystemEnv(String envName){
        Map<String, String> map = System.getenv();
        return map.get(envName);
    }

    public static void main(String[] args) {
        ProjectBuildUtil.execMavenCmd("clean compile");
    }
}
