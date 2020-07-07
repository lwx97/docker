package com.example.docker.util.targz;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class CompressFileGZIP {

    private static void doCompressFile(String inFileName) {
        try {
            System.out.println("Creating the GZIP output stream.");
            String outFileName = inFileName + ".tar.gz";
            GZIPOutputStream out = null;
            try {
                out = new GZIPOutputStream(new FileOutputStream(outFileName));
            } catch (FileNotFoundException e) {
                System.err.println("Could not create file: " + outFileName);
                System.exit(1);
            }

            System.out.println("Opening the input file.");
            FileInputStream in = null;
            try {
                in = new FileInputStream(inFileName);
            } catch (FileNotFoundException e) {
                System.err.println("File not found. " + inFileName);
                System.exit(1);
            }

            System.out.println("Transfering bytes from input file to GZIP Format.");
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();

            System.out.println("Completing the GZIP file");
            out.finish();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void doCompressFileList(String outFileName, List<File> files) {
        try {
            System.out.println("Creating the GZIP output stream.");
            outFileName += ".tar.gz";
            GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(outFileName));
            files.stream().forEach(f -> {
                FileInputStream in = null;
                try {
                    in = new FileInputStream(f);
                } catch (FileNotFoundException e) {
                    System.err.println("File not found. " + f);
                    System.exit(1);
                }

                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            });
            System.out.println("Completing the GZIP file");
            out.finish();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public static void doCompressDir(String folderPath) {

        File file = new File(folderPath);
        if (file.isFile()) {
            doCompressFile(folderPath);
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            List<File> fileList = Arrays.asList(files);
            doCompressFileList(folderPath, fileList);
        }
    }


    /**
     * Sole entry point to the class and application.
     *
     * @param args Array of String arguments.
     */
    public static void main(String[] args) {
        String str = "C:\\Users\\HD\\IdeaProjects\\docker\\src\\main\\docker\\dockerfile";
        doCompressFile(str);
    }
}
