package com.rookie.printonline.util;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Sides;
import java.io.FileInputStream;

public class ImagePrinter {
    public static void printImage(String filePath) {
        try {
            // 根据文件后缀确定DocFlavor类型
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            if (filePath.endsWith(".gif"))  {
                flavor = DocFlavor.INPUT_STREAM.GIF;
            } else if (filePath.endsWith(".jpg"))  {
                flavor = DocFlavor.INPUT_STREAM.JPEG;
            } else if (filePath.endsWith(".png"))  {
                flavor = DocFlavor.INPUT_STREAM.PNG;
            }

            // 创建打印属性（份数、纸张方向等）
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            pras.add(new Copies(1));     // 打印份数
            pras.add(Sides.ONE_SIDED);    // 单面打印

            // 获取打印服务
            PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor,  pras);
            if (services.length  == 0) {
                System.out.println(" 未找到可用打印机");
                return;
            }
            PrintService printer = services[0];

            // 执行打印任务
            DocPrintJob job = printer.createPrintJob();
            FileInputStream fis = new FileInputStream(filePath);
            Doc doc = new SimpleDoc(fis, flavor, null);
            job.print(doc,  pras);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        printImage("D://img.png");
    }
}