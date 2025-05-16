package com.rookie.printonline.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.rookie.printonline.result.vo.PrintData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class QrTemplateProcessor {

    // 生成二维码图片
    public static BufferedImage generateQRCodeImage(String text, int width, int height) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = new MultiFormatWriter().encode(text,
                BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    // 处理XML模板
    public static void processTemplate(String templatePath, String outputPath, PrintData data) throws Exception {
        // 1. 读取XML模板
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(templatePath));

        // 2. 替换模板中的变量
        replaceTemplateVariables(doc, data);

        // 3. 保存处理后的XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(outputPath));
        transformer.transform(source, result);
    }

    // 替换模板变量
    private static void replaceTemplateVariables(Document doc, PrintData data) throws Exception {
        // 替换二维码内容
        NodeList barcodeNodes = doc.getElementsByTagName("barcode");
        for (int i = 0; i < barcodeNodes.getLength(); i++) {
            Element barcode = (Element) barcodeNodes.item(i);
            if (barcode.getTextContent().contains("<%=_data.qrcode%>")) {
                barcode.setTextContent(data.getRightSideText());

                // 生成二维码图片并保存
                BufferedImage qrImage = generateQRCodeImage(data.getRightSideText(), 200, 200);
                File outputfile = new File("qrcode.png");
                ImageIO.write(qrImage, "png", outputfile);
            }
        }

        // 替换序列号
        NodeList textNodes = doc.getElementsByTagName("text");
        for (int i = 0; i < textNodes.getLength(); i++) {
            Element text = (Element) textNodes.item(i);
            if (text.getTextContent().contains("<%=_data.sn%>")) {
                text.setTextContent(data.getQrcodeContent());
            }
        }
    }
}