package com.rookie.printonline.gp;

import gnu.io.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.OutputStream;
import java.util.Enumeration;

public class SerialPrinterApp extends Application {

    private SerialPort serialPort;
    private OutputStream outputStream;

    @Override
    public void start(Stage primaryStage) {
        Button connectButton = new Button("连接打印机");
        Button printButton = new Button("打印测试");

        connectButton.setOnAction(e -> connectToPrinter());
        printButton.setOnAction(e -> sendEscPosCommands());

        VBox root = new VBox(10, connectButton, printButton);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("串口打印机控制");
        primaryStage.show();
    }

    private void connectToPrinter() {
        try {
            Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
            while (portEnum.hasMoreElements()) {
                CommPortIdentifier portIdentifier = portEnum.nextElement();
                if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    System.out.println("找到串口: " + portIdentifier.getName());
                    // 根据实际情况修改端口名
                    if (portIdentifier.getName().equals("USB001")) {
                        serialPort = (SerialPort) portIdentifier.open("JavaFX Printer App", 2000);
                        serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                        outputStream = serialPort.getOutputStream();
                        System.out.println("已连接到打印机");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEscPosCommands() {
        if (outputStream == null) {
            System.out.println("请先连接打印机");
            return;
        }

        try {
            // ESC/POS指令
            byte[] commands = new byte[] {
                    0x1B, 0x40,             // 初始化
                    0x1B, 0x21, 0x08,       // 设置双倍高宽
                    'H', 'e', 'l', 'l', 'o', ' ', 'G', 'P', 'r', 'i', 'n', 't', 'e', 'r', 0x0A,
                    0x1B, 0x21, 0x00,       // 恢复正常大小
                    'N', 'o', 'r', 'm', 'a', 'l', ' ', 't', 'e', 'x', 't', 0x0A,
                    0x0A, 0x0A, 0x0A,      // 走纸3行
                    0x1D, 0x56, 0x00        // 全切
            };

            outputStream.write(commands);
            outputStream.flush();
            System.out.println("指令已发送");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (serialPort != null) {
            serialPort.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}