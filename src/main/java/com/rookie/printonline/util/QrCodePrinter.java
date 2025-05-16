package com.rookie.printonline.util;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class QrCodePrinter {

    // 生成二维码图片
    public static BufferedImage generateQRCodeImage(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    // 使用FreeMarker渲染模板
    public static String renderTemplate(String templateContent, Map<String, String> data) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDefaultEncoding("UTF-8");

        // 准备模板数据
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("_data", data);

        // 从字符串加载模板
        Template template = new Template("labelTemplate", new StringReader(templateContent), cfg);

        // 生成输出
        StringWriter out = new StringWriter();
        template.process(templateData, out);
        return out.toString();
    }

    // 打印二维码图片
    public static void printImage(BufferedImage image) throws PrintException {
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();

        if (printService == null) {
            throw new IllegalStateException("没有找到默认打印机");
        }

        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        attributes.add(new Copies(1));
        attributes.add(MediaSizeName.ISO_A4);
        attributes.add(OrientationRequested.PORTRAIT);

        DocPrintJob job = printService.createPrintJob();
        Doc doc = new SimpleDoc(image, DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);

        job.print(doc, attributes);
    }

    // 主流程
    public static void generateAndPrintQrCode(String xmlTemplate, Map<String, String> data) {
        try {
            // 1. 生成二维码图片
            String qrContent = data.get("qrcode");
            if (qrContent == null || qrContent.isEmpty()) {
                throw new IllegalArgumentException("二维码内容不能为空");
            }

            BufferedImage qrImage = generateQRCodeImage(qrContent, 200, 200);

            // 2. 保存二维码图片供参考（可选）
            File outputFile = new File("qrcode.png");
            ImageIO.write(qrImage, "png", outputFile);
            System.out.println("二维码已保存到: " + outputFile.getAbsolutePath());

            // 3. 渲染模板
            String renderedTemplate = renderTemplate(xmlTemplate, data);
            System.out.println("渲染后的模板内容:\n" + renderedTemplate);

            // 4. 打印二维码
            System.out.println("正在打印...");
            printImage(qrImage);
            System.out.println("打印任务已提交");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            // 准备数据
            Map<String, String> data = new HashMap<>();
            data.put("qrcode", "https://example.com/product/12345");
            data.put("sn", "SN123456");
            String path="D:\\vx_storage\\xwechat_files\\mahuaixiao_5123\\msg\\file\\2025-05\\QR_Print_Template_100_32_2.0.xml";
            // 您的XML模板内容
            String xmlTemplate = FileUtils.readFileToString(new File(path),"utf-8"); // 替换为您的完整XML内容

            // 生成并打印
            generateAndPrintQrCode(xmlTemplate, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}