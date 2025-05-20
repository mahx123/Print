package com.rookie.printonline.gp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.OutputStream;
import java.net.Socket;

public class NetworkPrinterApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button printButton = new Button("打印测试");
        printButton.setOnAction(e -> printViaNetwork());

        VBox root = new VBox(printButton);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("网络打印机控制");
        primaryStage.show();
    }

    private void printViaNetwork() {
        try (Socket socket = new Socket("127.0.0.1", 9100); // 打印机IP和端口
             OutputStream out = socket.getOutputStream()) {

            // ESC/POS指令
            byte[] commands = new byte[] {
                    0x1B, 0x40,             // 初始化
                    0x1B, 0x61, 0x01,       // 居中对齐
                    0x1B, 0x21, 0x08,       // 设置双倍高宽
                    'N', 'e', 't', 'w', 'o', 'r', 'k', ' ', 'P', 'r', 'i', 'n', 't', 'i', 'n', 'g', 0x0A,
                    0x1B, 0x21, 0x00,       // 恢复正常大小
                    0x1B, 0x61, 0x00,      // 左对齐
                    'T', 'e', 's', 't', ' ', 's', 'u', 'c', 'c', 'e', 's', 's', 'f', 'u', 'l', '!', 0x0A,
                    0x0A, 0x0A, 0x0A,       // 走纸3行
                    0x1D, 0x56, 0x00        // 全切
            };

            out.write(commands);
            out.flush();
            System.out.println("已发送到网络打印机");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}