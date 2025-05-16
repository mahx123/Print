package com.rookie.printonline.util;

import java.awt.*;
import java.awt.print.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.print.*;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class LabelPrinter implements Printable {
    private final BufferedImage qrImage;
    private final String rightText;

    public LabelPrinter(BufferedImage qrImage, String rightText) {
        this.qrImage = qrImage;
        this.rightText = rightText;
    }

    public void print() throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);

        if (job.printDialog()) {
            job.print();
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g2d = (Graphics2D)graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        // 绘制二维码（左侧，大小100x100）
        g2d.drawImage(qrImage, 10, 10, 100, 100, null);

        // 绘制右侧文本
        g2d.setFont(new Font("SimHei", Font.PLAIN, 12));
        String[] lines = rightText.split("\n");
        int yPos = 15;
        for (String line : lines) {
            g2d.drawString(line, 120, yPos);
            yPos += 15;
        }

        return PAGE_EXISTS;
    }

    // 生成二维码图片（带白边）
    public static BufferedImage generateQRCode(String content, int size) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 2); // 设置白边
        BitMatrix matrix = new MultiFormatWriter().encode(
                content, BarcodeFormat.QR_CODE, size, size, hints);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }
}