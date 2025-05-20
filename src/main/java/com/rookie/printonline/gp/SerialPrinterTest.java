package com.rookie.printonline.gp;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.OutputStream;
public class SerialPrinterTest {
    public static void main(String[] args) {
        // 获取系统中所有可用的串口
        SerialPort[] ports = SerialPort.getCommPorts();

        if (ports.length == 0) {
            System.out.println("未发现可用的串口");
            return;
        }

        // 打印每个串口的信息
        System.out.println("发现 " + ports.length + " 个可用的串口:");
        for (SerialPort port : ports) {
            System.out.println("-------------------------------");
            System.out.println("端口名称: " + port.getSystemPortName());
            System.out.println("端口描述: " + port.getPortDescription());
            System.out.println("是否已打开: " + (port.isOpen() ? "是" : "否"));
        }


        // 替换为实际的串口名称，如 COM3
        SerialPort serialPort = SerialPort.getCommPort("COM3");
        // 设置串口参数
        serialPort.setBaudRate(9600);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);

        if (serialPort.openPort()) {
            try (OutputStream outputStream = serialPort.getOutputStream()) {
                // 示例 GP 指令，设置标签尺寸、清除缓冲区、打印文本并执行打印
                String gpCommand = "SIZE 40 mm, 30 mm\n" +
                        "GAP 2 mm, 0 mm\n" +
                        "CLS\n" +
                        "TEXT 100,100,\"TSS24.BF2\",\"0\",90,\"Hello, GP Printer!\"\n" +
                        "PRINT 1\n";
                byte[] commandBytes = gpCommand.getBytes();
                outputStream.write(commandBytes);
                outputStream.flush();
                System.out.println("GP 指令发送成功");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                serialPort.closePort();
            }
        } else {
            System.out.println("无法打开串口");
        }
    }
}
