package com.rookie.printonline.gp;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GPrinterUSBExample {

    public static void main(String[] args) {
        String printerName = "Gprinter GP-1324D"; // 替换为你的打印机名称

        try {
            printViaUSB(printerName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printViaUSB(String printerName) throws IOException {
        // 1. 查找打印机
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService myPrinter = null;

        for (PrintService service : services) {
            if (service.getName().contains(printerName)) {
                myPrinter = service;
                break;
            }
        }

        if (myPrinter == null) {
            throw new IOException("找不到指定的打印机: " + printerName);
        }

        // 2. 准备打印数据 (ESC/POS指令)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 初始化打印机
        outputStream.write(0x1B);
        outputStream.write(0x40);

        // 设置中文编码 (GBK)
        outputStream.write(0x1C);
        outputStream.write(0x26);

        // 打印文本
        String text = "佳博打印机测试\nUSB连接示例\n";
        outputStream.write(text.getBytes("GBK"));

        // 换行
        // 只发送初始化+换行+文本测试
        outputStream.write(new byte[]{0x1B, 0x40}); // 初始化
        outputStream.write("TEST ENGLISH\n".getBytes()); // 英文测试
        outputStream.write("\n\n\n".getBytes());

        // 3. 创建打印任务
        DocPrintJob job = myPrinter.createPrintJob();
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(outputStream.toByteArray(), flavor, null);

        // 设置打印份数
        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        attributes.add(new Copies(1));

        // 4. 发送打印任务
        try {
            job.print(doc, attributes);
            System.out.println("打印任务已发送");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}