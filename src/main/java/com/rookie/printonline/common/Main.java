package com.rookie.printonline.common;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.rookie.printonline.result.vo.PrintData;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // 1. 准备数据
        String continuousNumber = "0002300025664897590010"; // 扫描二维码得到的内容
        String[] displayNumbers = {
                "0000",
                "2320",
                "0025",
                "6648",
                "9759",
                "0010"
        };

        // 2. 生成二维码图片
        BufferedImage qrImage = generateQRCode(continuousNumber, 200);

        // 3. 创建带右侧数字的完整图片
        BufferedImage combinedImage = createCombinedImage(qrImage, displayNumbers);

        // 4. 显示结果（实际使用时替换为打印代码）
        displayImage(combinedImage);

        // 5. 保存图片（可选）
        saveImage(combinedImage, "qrcode_with_numbers.png");
    }

    // 生成二维码

    // 生成二维码
    private static BufferedImage generateQRCode(String content, int size) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = new MultiFormatWriter().encode(
                    content, BarcodeFormat.QR_CODE, size, size, hints);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (Exception e) {
            throw new RuntimeException("生成二维码失败", e);
        }
    }

    // 创建组合图片（左侧二维码+右侧数字）
    private static BufferedImage createCombinedImage(BufferedImage qrImage, String[] numbers) {
        int qrWidth = qrImage.getWidth();
        int qrHeight = qrImage.getHeight();
        int textAreaWidth = 150; // 右侧文本区域宽度
        int padding = 20; // 内边距

        // 创建新图片
        BufferedImage combined = new BufferedImage(
                qrWidth + textAreaWidth,
                qrHeight,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g = combined.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, combined.getWidth(), combined.getHeight());

        // 绘制二维码（左侧）
        g.drawImage(qrImage, padding, padding, null);

        // 绘制数字（右侧）
        g.setColor(Color.BLACK);
        g.setFont(new Font("SimHei", Font.BOLD, 16));

        int xPos = qrWidth + padding;
        int yPos = padding + 20;
        int lineHeight = 25;

        for (String number : numbers) {
            g.drawString(number, xPos, yPos);
            yPos += lineHeight;
        }

        g.dispose();
        return combined;
    }

    // 显示图片（测试用）
    private static void displayImage(BufferedImage image) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
    }

    // 保存图片到文件
    private static void saveImage(BufferedImage image, String filename) {
        try {
            javax.imageio.ImageIO.write(image, "PNG", new File(filename));
            System.out.println("图片已保存为: " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}