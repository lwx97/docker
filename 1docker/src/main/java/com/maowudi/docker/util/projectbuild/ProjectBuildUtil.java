package com.maowudi.docker.util.projectbuild;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ch.qos.logback.core.util.EnvUtil.isWindows;

public class ProjectBuildUtil {


    /**
     * 执行maven命令
     *
     * @param mvnCmd 多个命令以空格隔开
     */
    public static void execMavenCmd(String mvnCmd) {
        DefaultInvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File("pom.xml"));
        List<String> cmdList = Arrays.asList(mvnCmd.trim().split(" "));
        if (cmdList != null || cmdList.size() == 0) {
            if (mvnCmd.length() > 0) {
                cmdList = Collections.singletonList(mvnCmd);
            } else {
                throw new RuntimeException("没有mvn命令");
            }
        }
        request.setGoals(cmdList);
        DefaultInvoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(getSystemEnv("MAVEN_HOME")));
        try {
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建本地项目
     */
    public static void buildLocalProject() {
        execMavenCmd("clean compile");
    }

    /**
     * 本地项目打包
     */
    public static void packageLocalProject() {
        execMavenCmd("clean package");
    }

    /**
     * 本地项目打包跳过Test
     */
    public static void packageLocalProjectSkipTest() {
        execMavenCmd("clean package -Dmaven.test.skip=true");
    }

    /**
     * 将本地项目安装到本地仓库中
     */
    public static void installLocalProject() {
        execMavenCmd("clean install");
    }

    /**
     * 获取环境变量
     *
     * @param envName
     * @return
     */
    public static String getSystemEnv(String envName) {
        Map<String, String> map = System.getenv();
        return map.get(envName);
    }

    /**
     * 获取本地项目target目录
     *
     * @return
     */
    public static String getLocalProjectTargetPath() {
        String classPath = ProjectBuildUtil.class.getResource("/").getPath().replace("classes/", "");
        if (isWindows() && classPath.startsWith("/")) {
            classPath = classPath.substring(1);
        }
        System.out.println(classPath);
        return classPath;
    }

    /**
     * 获取本地项目pom model实体
     *
     * @return
     */
    public static Model getLocalProjectPomModel() {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        if (isWindows() && basePath.startsWith("/")) {
            basePath = basePath.substring(1);
        }
        if (basePath.indexOf("/target/") != -1) {
            basePath = basePath.substring(0, basePath.indexOf("/target/"));
        }
        Model model = null;
        try {
            model = reader.read(new FileReader(new File(basePath + "\\pom.xml")));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static void main(String[] args) {
        String localProjectTargetPath = ProjectBuildUtil.getLocalProjectTargetPath();
    }
}
