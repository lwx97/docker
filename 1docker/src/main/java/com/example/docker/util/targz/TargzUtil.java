package com.example.docker.util.targz;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * @ProjectName: dockerdemo
 * @Package: com.maowudi.dockerdemo.util.targz
 * @ClassName: TargzUtil
 * @Author: 李文祥
 * @Description: tar.gz 压缩工具类
 * @Date: 2020/6/29 21:15
 * @Version: 1.0
 */
public class TargzUtil {

    private String targzPath;

    /**
     * 压缩文件成gz
     *
     * @param sourceFolder
     * @param targzPath    例如：D:/HD/dockerfile.tar.gz
     */
    public static void compressToTargz(String sourceFolder, String targzPath) {
        new TargzUtil().createTarFile(sourceFolder, targzPath);
    }

    /**
     * 压缩文件/目录内的文件成tar.gz
     *
     * @param sourceFolder
     */
    public static void compressToTargz(String sourceFolder) {
        int i = sourceFolder.lastIndexOf(".");
        String targzPath = "";
        if (i == -1) {
            targzPath = sourceFolder + ".tar.gz";
        } else {
            targzPath = sourceFolder.substring(0, i) + ".tar.gz";
        }
        new TargzUtil().createTarFile(sourceFolder, targzPath);
    }

    /**
     * 压缩多个不同目录的文件成tar.gz
     *
     * @param filePaths
     * @param targzPath
     */
    public static void compressToTargz(List<String> filePaths, String targzPath) {
        new TargzUtil().createTarFile(filePaths, targzPath);
    }


    private void createTarFile(List<String> filePaths, String targzPath) {
        this.targzPath = targzPath;
        TarArchiveOutputStream aos = null;
        try {
            // 创建一个 FileOutputStream 到输出文件（.tar.gz）
            FileOutputStream fileOutputStream = new FileOutputStream(targzPath);
            // 创建一个 GZIPOutputStream，用来包装 FileOutputStream 对象
            GZIPOutputStream gos = new GZIPOutputStream(fileOutputStream);
            // 创建一个 TarArchiveOutputStream，用来包装 GZIPOutputStream 对象
            aos = new TarArchiveOutputStream(gos);
            // 若不设置此模式，当文件名超过 100 个字节时会抛出异常，异常大致如下：
            // is too long ( > 100 bytes)
            // 具体可参考官方文档：http://commons.apache.org/proper/commons-compress/tar.html#Long_File_Names
            aos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            for (String path : filePaths) {
                if (Files.isSameFile(Paths.get(targzPath), Paths.get(path))) {
                    continue;
                }
                addDirectoryToTarGZ(path, "", aos);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                aos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 创建TarFile
     *
     * @param sourcePath
     * @param targzPath
     */
    private void createTarFile(String sourcePath, String targzPath) {
        this.targzPath = targzPath;
        TarArchiveOutputStream aos = null;
        try {
            // 创建一个 FileOutputStream 到输出文件（.tar.gz）
            FileOutputStream fileOutputStream = new FileOutputStream(targzPath);
            // 创建一个 GZIPOutputStream，用来包装 FileOutputStream 对象
            GZIPOutputStream gos = new GZIPOutputStream(fileOutputStream);
            // 创建一个 TarArchiveOutputStream，用来包装 GZIPOutputStream 对象
            aos = new TarArchiveOutputStream(gos);
            // 若不设置此模式，当文件名超过 100 个字节时会抛出异常，异常大致如下：
            // is too long ( > 100 bytes)
            // 具体可参考官方文档：http://commons.apache.org/proper/commons-compress/tar.html#Long_File_Names
            aos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            File file = new File(sourcePath);
            if (file.isFile()) {
                addDirectoryToTarGZ(sourcePath, "", aos);
            } else if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    if (Files.isSameFile(Paths.get(targzPath), Paths.get(f.toURI()))) {
                        continue;
                    }
                    addDirectoryToTarGZ(f.getPath(), "", aos);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                aos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 添加File到tar.gz中
     *
     * @param sourcePath
     * @param parent
     * @param tarArchive
     * @throws IOException
     */
    private void addDirectoryToTarGZ(String sourcePath, String parent, TarArchiveOutputStream tarArchive) throws IOException {
        File file = new File(sourcePath);
        String entryName = parent + file.getName();
        // 添加 tar ArchiveEntry
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file, entryName);
        tarArchiveEntry.setSize(file.length());
        tarArchive.putArchiveEntry(tarArchiveEntry);
        if (file.isFile()) {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fis);
            // 写入文件
            IOUtils.copy(bufferedInputStream, tarArchive);
            tarArchive.closeArchiveEntry();
        } else if (file.isDirectory()) {
            // 因为是个文件夹，无需写入内容，关闭即可
            tarArchive.closeArchiveEntry();
            // 读取文件夹下所有文件
            for (File f : file.listFiles()) {
                // 递归
                if (Files.isSameFile(Paths.get(targzPath), Paths.get(f.toURI()))) {
                    continue;
                }
                addDirectoryToTarGZ(f.getPath(), "", tarArchive);
            }
        }
    }

    public static void main(String[] args) {
//        TargzUtil.compressToTargz("C:\\Users\\HD\\IdeaProjects\\docker\\src\\main\\docker","C:\\Users\\HD\\IdeaProjects\\docker\\src\\main\\docker\\docker.tar.gz");
        TargzUtil.compressToTargz("C:\\Users\\HD\\IdeaProjects\\docker\\src\\main\\docker");
    }

}
