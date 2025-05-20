package com.rookie.printonline.gp;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

public class NativeLibraryLoader {
    public static void loadLibraryFromJar(String path) throws IOException {


        String[] parts = path.split("/");
        String filename = (parts.length > 0) ? parts[parts.length - 1] : null;

        if (filename == null || filename.isEmpty()) {
            throw new IOException("无法从路径中提取文件名: " + path);
        }

        // 创建临时文件
        File temp = File.createTempFile("lib", filename);
        temp.deleteOnExit();

        try (InputStream is = NativeLibraryLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IOException("无法从 JAR 中找到资源: " + path);
            }
            Files.copy(is, temp.toPath());
        } catch (IOException e) {
            temp.delete();
            throw e;
        }

        // 加载临时文件
        System.load(temp.getAbsolutePath());
    }


    // 在主类中调用
    static {
        try {
            NativeLibraryLoader.loadLibraryFromJar("C:\\Windows\\System32/libusb4java.dll");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        System.out.println("============");
    }
}
