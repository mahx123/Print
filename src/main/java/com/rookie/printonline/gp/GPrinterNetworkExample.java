package com.rookie.printonline.gp;

import java.io.OutputStream;
import java.net.Socket;

public class GPrinterNetworkExample {

    public static void main(String[] args) {
        String printerIp = "127.0.0.1"; // 打印机IP地址
        int printerPort = 9100; // 默认端口

        try {
            sendToNetworkGPrinter(printerIp, printerPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendToNetworkGPrinter(String ip, int port) throws Exception {
        Socket socket = new Socket(ip, port);
        OutputStream out = socket.getOutputStream();

        // 初始化打印机
        byte[] initCmd = new byte[]{0x1B, 0x40};
        out.write(initCmd);

        // 设置居中打印
        byte[] centerCmd = new byte[]{0x1B, 0x61, 0x01};
        out.write(centerCmd);

        // 打印文本
        String text = "网络打印测试\n";
        out.write(text.getBytes("GBK"));

        // 切纸
        byte[] cutCmd = new byte[]{0x1D, 0x56, 0x41, 0x00};
        out.write(cutCmd);

        out.flush();
        out.close();
        socket.close();

        System.out.println("Network print command sent successfully");
    }
}
