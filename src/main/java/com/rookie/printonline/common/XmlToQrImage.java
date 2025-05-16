package com.rookie.printonline.common;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @Title: XmlToQrImage
 * @Package: com.rookie.printonline.common
 * @Description: TODO
 * @Author: mahx 马怀啸
 * @Email: 616107968@qq.com
 * @Date: 2025/5/16 20:59
 * @Version: V1.0.0
 * @Copyright: 南京奥印智能装备科技有限公司
 */
public class XmlToQrImage {

    public static void main(String[] args) throws Exception {
        // 1. 解析XML
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new File("E:\\xml\\QR_Print_Template_100_32_2.0.xml"));

        // 2. 创建图片画布（100mm x 32mm，按300 DPI计算）
        int widthPx = mmToPx(100, 300);
        int heightPx = mmToPx(32, 300);
        BufferedImage image = new BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, widthPx, heightPx);

        // 3. 处理所有<barcode>标签
        NodeList barcodes = doc.getElementsByTagName("barcode");
        for (int i = 0; i < barcodes.getLength(); i++) {
            Element barcode = (Element) barcodes.item(i);
            if ("qrcode".equals(barcode.getAttribute("type"))) {
                String content = barcode.getTextContent().replace("<%=_data.qrcode%>", "PROD-12345");
                int qrWidth = mmToPx(Double.parseDouble(barcode.getParentNode().getAttributes().getNamedItem("width").getNodeValue()), 300);
                int qrHeight = mmToPx(Double.parseDouble(barcode.getParentNode().getAttributes().getNamedItem("height").getNodeValue()), 300);
                int left = mmToPx(Double.parseDouble(barcode.getParentNode().getAttributes().getNamedItem("left").getNodeValue()), 300);
                int top = mmToPx(Double.parseDouble(barcode.getParentNode().getAttributes().getNamedItem("top").getNodeValue()), 300);

                // 生成二维码并绘制到画布
                BufferedImage qrImage = generateQrCode(content, qrWidth, qrHeight);
                g2d.drawImage(qrImage, left, top, null);
            }
        }

        // 4. 保存图片
        javax.imageio.ImageIO.write(image, "PNG", new File("output_qrcode.png"));
        System.out.println("二维码图片已生成: output_qrcode.png");
    }

    private static BufferedImage generateQrCode(String content, int width, int height) throws Exception {
        BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height);
        BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                qrImage.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return qrImage;
    }

    private static int mmToPx(double mm, int dpi) {
        return (int) (mm / 25.4 * dpi);
    }
}
