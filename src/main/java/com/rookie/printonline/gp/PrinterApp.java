package com.rookie.printonline.gp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PrinterApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button printButton = new Button("打印测试");
        printButton.setOnAction(e -> printToGprinter());

        VBox root = new VBox(printButton);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("佳博打印机控制");
        primaryStage.show();
    }

    private void printToGprinter() {
        try {
            // 1. 查找打印机服务
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService gprinter = null;

            for (PrintService service : services) {
                if (service.getName().contains("GP") || service.getName().contains("佳博")) {
                    gprinter = service;
                    break;
                }
            }

            if (gprinter == null) {
                System.out.println("未找到佳博打印机");
                return;
            }

            // 2. 创建打印作业
            DocPrintJob job = gprinter.createPrintJob();
            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(new Copies(1));

            // 3. 准备ESC/POS指令
            String escPosCommands =
                    "\u001B@\u001B!u08" +    // 初始化+设置双倍高宽
                            "佳博打印机测试\n" +
                            "\u001B!u00" +           // 恢复正常大小
                            "这是正常大小的文本\n" +
                            "\n\n\n\u001dV\u0000";    // 走纸并切纸

            // 4. 转换为字节流
            InputStream is = new ByteArrayInputStream(escPosCommands.getBytes(StandardCharsets.ISO_8859_1));
            Doc doc = new SimpleDoc(is, DocFlavor.INPUT_STREAM.AUTOSENSE, null);

            // 5. 打印
            job.print(doc, attributes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}