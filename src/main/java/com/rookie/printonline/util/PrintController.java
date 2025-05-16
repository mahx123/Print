package com.rookie.printonline.util;
import com.rookie.printonline.result.vo.PrintData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
public class PrintController {
    public void generateAndPrintLabel(PrintData data) {
        try {
            // 1. 处理模板
            String templatePath = "D:\\vx_storage\\xwechat_files\\mahuaixiao_5123\\msg\\file\\2025-05\\QR_Print_Template_100_32_2.0.xml";
            String processedTemplatePath = "processed_template.xml";
            QrTemplateProcessor.processTemplate(templatePath, processedTemplatePath, data);

            // 2. 生成二维码图片
            BufferedImage qrImage = QrTemplateProcessor.generateQRCodeImage(
                    data.getQrcode(), 200, 200);
            String qrImagePath = "qrcode.png";
            ImageIO.write(qrImage, "png", new File(qrImagePath));

            // 3. 打印
            // 方法1：使用自定义打印类（推荐）
            LabelPrinter printer = new LabelPrinter(qrImagePath, data.getSn());
            printer.print();

            // 方法2：直接打印XML模板
            // LabelPrinter.printXmlTemplate(processedTemplatePath);

            System.out.println("打印任务已发送");

        } catch (Exception e) {
            System.err.println("打印出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
