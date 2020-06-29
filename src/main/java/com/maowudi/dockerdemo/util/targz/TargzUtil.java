package com.maowudi.dockerdemo.util.targz;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
     * @param sourceFolder
     * @param targzPath
     */
    public static void compressToTargz(String sourceFolder, String targzPath){
        new TargzUtil().createTarFile(sourceFolder, targzPath);
    }


    /**
     * 创建TarFile
     * @param sourcePath
     * @param targzPath
     */
    private void createTarFile(String sourcePath, String targzPath){
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
            addDirectoryToTarGZ(sourcePath,"",aos);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加File到tar.gz中
     * @param sourcePath
     * @param parent
     * @param tarArchive
     * @throws IOException
     */
    private void addDirectoryToTarGZ(String sourcePath,String parent, TarArchiveOutputStream tarArchive) throws IOException {
        File file = new File(sourcePath);
        String entryName = parent + file.getName();
        // 添加 tar ArchiveEntry
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file,entryName);
        tarArchiveEntry.setSize(file.length());
        tarArchive.putArchiveEntry(tarArchiveEntry);
        if(file.isFile()){
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fis);
            // 写入文件
            IOUtils.copy(bufferedInputStream,tarArchive);
            tarArchive.closeArchiveEntry();
            bufferedInputStream.close();
            fis.close();
        } else if (file.isDirectory()) {
            // 因为是个文件夹，无需写入内容，关闭即可
            tarArchive.closeArchiveEntry();
            // 读取文件夹下所有文件
            for (File f : file.listFiles()) {
                // 递归
                if(Files.isSameFile(Paths.get(targzPath),Paths.get(f.toURI()))){
                    continue;
                }
                addDirectoryToTarGZ(f.getAbsolutePath(), entryName + File.separator, tarArchive);
            }
        }
    }

    public static void main(String[] args) {
        TargzUtil.compressToTargz("D:\\javawork\\IdeaProjects\\docker\\src\\main\\docker\\dockerdemo-0.0.1-SNAPSHOT.jar"
                ,"D:\\javawork\\IdeaProjects\\docker\\src\\main\\docker\\dockerfile.tar.gz");
    }

}
