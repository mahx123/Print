package com.rookie.printonline.common;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

/**
 * @Title: XML转图片
 * @Package: com.rookie.printonline.common
 * @Description: TODO
 * @Author: mahx 马怀啸
 * @Email: 616107968@qq.com
 * @Date: 2025/5/16 21:00
 * @Version: V1.0.0
 * @Copyright: 南京奥印智能装备科技有限公司
 */
public class Main_02 {

    public static void main(String[] args) throws Exception {
        try {
            // 1. 解析XML模板
            File xmlFile = new File("E://xml/QR_Print_Template_100_32_2.0.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder().parse(xmlFile);
            System.out.println("根元素: " + doc.getDocumentElement().getNodeName());

            // 2. 获取页面尺寸（毫米）
            Element page = doc.getDocumentElement();
            double widthMM = Double.parseDouble(page.getAttribute("width"));
            double heightMM = Double.parseDouble(page.getAttribute("height"));

            // 3. 创建BufferedImage（画布）
            int widthPx = (int) mmToPx(widthMM);
            int heightPx = (int) mmToPx(heightMM);
            BufferedImage image = new BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.BLACK);

            // 4. 处理所有布局元素
            NodeList layouts = doc.getElementsByTagName("layout");
            for (int i = 0; i < layouts.getLength(); i++) {
                Element layout = (Element) layouts.item(i);
                processLayoutElement(layout, g2d, widthPx, heightPx);
            }

            // 5. 处理线条元素
            NodeList lines = doc.getElementsByTagName("line");
            for (int i = 0; i < lines.getLength(); i++) {
                Element line = (Element) lines.item(i);
                processLineElement(line, g2d);
            }

            // 6. 保存为图片
            javax.imageio.ImageIO.write(image, "PNG", new File("output.png"));
            System.out.println("图片已生成: output.png");

            g2d.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processLayoutElement(Element layout, Graphics2D g2d, int widthPx, int heightPx) throws Exception {
        double left = Double.parseDouble(layout.getAttribute("left"));
        double top = Double.parseDouble(layout.getAttribute("top"));
        double width = Double.parseDouble(layout.getAttribute("width"));
        double height = Double.parseDouble(layout.getAttribute("height"));

        // 处理二维码
        NodeList barcodes = layout.getElementsByTagName("barcode");
        if (barcodes.getLength() > 0) {
            Element barcode = (Element) barcodes.item(0);
            if ("qrcode".equals(barcode.getAttribute("type"))) {
                String content = barcode.getTextContent().replace("<%=_data.qrcode%>", DATA.get("qrcode"));
                BufferedImage qrImage = generateQrCode(content, (int) mmToPx(width), (int) mmToPx(height));
                g2d.drawImage(qrImage, (int) mmToPx(left), (int) mmToPx(top), null);
            }
            return;
        }

        // 处理文本
        NodeList texts = layout.getElementsByTagName("text");
        if (texts.getLength() > 0) {
            Element text = (Element) texts.item(0);
            String content = text.getTextContent()
                    .replace("<%=_data.qrcode%>", DATA.get("qrcode"))
                    .replace("<%=_data.sn%>", DATA.get("sn"));

            // 设置字体样式
            Font font = parseFont(text.getAttribute("style"));
            g2d.setFont(font);

            // 绘制文本
            int x = (int) mmToPx(left);
            int y = (int) mmToPx(top) + font.getSize(); // 调整垂直位置
            g2d.drawString(content, x, y);
        }
    }

    private static void processLineElement(Element line, Graphics2D g2d) {
        int startX = (int) mmToPx(Double.parseDouble(line.getAttribute("startX")));
        int startY = (int) mmToPx(Double.parseDouble(line.getAttribute("startY")));
        int endX = (int) mmToPx(Double.parseDouble(line.getAttribute("endX")));
        int endY = (int) mmToPx(Double.parseDouble(line.getAttribute("endY")));

        // 设置线条样式
        String style = line.getAttribute("style");
        if (style.contains("dashed")) {
            float[] dashPattern = {5, 5};
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
        }
        if (style.contains("lineColor:#")) {
            String colorHex = style.split("lineColor:#")[1].split(";")[0];
            g2d.setColor(Color.decode("#" + colorHex));
        }

        // 绘制线条
        g2d.drawLine(startX, startY, endX, endY);
        g2d.setStroke(new BasicStroke()); // 重置为默认样式
    }

    private static BufferedImage generateQrCode(String content, int width, int height) throws Exception {
        BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height);
        BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                qrImage.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return qrImage;
    }

    private static Font parseFont(String style) {
        String fontFamily = "Arial";
        int fontSize = 10;
        int fontStyle = Font.PLAIN;

        String[] styles = style.split(";");
        for (String s : styles) {
            String[] kv = s.split(":");
            if (kv.length != 2) continue;

            switch (kv[0]) {
                case "fontFamily":
                    fontFamily = kv[1];
                    break;
                case "fontSize":
                    fontSize = (int) Double.parseDouble(kv[1]);
                    break;
                case "fontWeight":
                    if ("bold".equals(kv[1])) {
                        fontStyle = Font.BOLD;
                    }
                    break;
            }
        }
        return new Font(fontFamily, fontStyle, fontSize);
    }

    private static final Map<String, String> DATA = Map.of(
            "qrcode", "PROD-12345",
            "sn", "SN-2023-001"
    );

    private static final double DPI = 72; // 标准DPI
    private static final double MM_TO_INCH = 25.4;

    private static double mmToPx(double mm) {
        return mm / MM_TO_INCH * DPI;
    }
}
