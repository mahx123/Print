package com.rookie.printonline.common;


import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class LabelPrinter {

    // 标签尺寸(毫米)
    private static final double LABEL_WIDTH_MM = 100;
    private static final double LABEL_HEIGHT_MM = 32;

    // 毫米转点(1英寸=25.4毫米, 1英寸=72点)
    private static double mmToPoint(double mm) {
        return mm / 25.4 * 72;
    }

    public static void main(String[] args) throws Exception {
        // 1. 生成二维码图片
        BufferedImage qrCodeImage = generateQRCode("PROD-12345", 150, 150);

        // 2. 创建自定义页面格式
        Paper paper = new Paper();
        double width = mmToPoint(LABEL_WIDTH_MM);
        double height = mmToPoint(LABEL_HEIGHT_MM);
        paper.setSize(width, height);
        paper.setImageableArea(0, 0, width, height); // 全页面可打印

        PageFormat pageFormat = new PageFormat();
        pageFormat.setPaper(paper);
        pageFormat.setOrientation(PageFormat.PORTRAIT);

        // 3. 创建打印内容
        Printable printable = (graphics, pf, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) graphics;

            // 设置高质量渲染
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            // 绘制二维码(位置调整为小标签尺寸)
            int qrSize = 120; // 二维码大小(点)
            int qrX = (int)(width/2 - qrSize/2); // 水平居中
            int qrY = 10;
            g2d.drawImage(qrCodeImage, qrX, qrY, qrSize, qrSize, null);

            // 绘制文本
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            String text = "产品编号: PROD-12345";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textX = (int)(width/2 - textWidth/2); // 水平居中
            int textY = qrY + qrSize + 15;
            g2d.drawString(text, textX, textY);

            return Printable.PAGE_EXISTS;
        };

        // 4. 执行打印
        print(printable, pageFormat);
    }

    private static BufferedImage generateQRCode(String content, int width, int height) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1); // 设置二维码边距
        BitMatrix matrix = new MultiFormatWriter().encode(
                content, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    private static void print(Printable printable, PageFormat pageFormat) throws Exception {
        // 获取打印服务
        PrintService[] services = PrinterJob.lookupPrintServices();
        if (services.length == 0) {
            throw new RuntimeException("没有找到可用的打印机");
        }

        // 选择打印机(这里选择默认打印机)
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        System.out.println("使用打印机: " + printService.getName());

        // 创建打印作业
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(printable, pageFormat);

        try {
            // 显示打印对话框(可选)
            if (job.printDialog()) {
                job.print();
                System.out.println("标签打印已提交");
            } else {
                System.out.println("用户取消了打印");
            }
        } catch (PrinterException e) {
            System.err.println("打印失败: " + e.getMessage());
            // 尝试使用另一种打印方式
            tryAlternativePrint(printable, printService);
        }
    }

    // 备选打印方法
    private static void tryAlternativePrint(Printable printable, PrintService printService) {
        try {
            // 创建打印属性
            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(new MediaPrintableArea(
                    0, 0,
                    (float)LABEL_WIDTH_MM, (float)LABEL_HEIGHT_MM,
                    MediaPrintableArea.MM
            ));
            attributes.add(new Copies(1));

            // 创建打印文档
            Doc doc = new SimpleDoc(printable, DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);

            // 提交打印作业
            DocPrintJob job = printService.createPrintJob();
            job.print(doc, attributes);
            System.out.println("已使用备选方法提交打印");
        } catch (Exception e) {
            System.err.println("备选打印方法也失败: " + e.getMessage());
        }
    }
}
