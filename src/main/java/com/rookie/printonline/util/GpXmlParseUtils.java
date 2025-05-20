package com.rookie.printonline.util;
import com.rookie.printonline.dto.TemplateData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.File;
import org.w3c.dom.*;

import java.util.HashMap;
import java.util.Map;

public class GpXmlParseUtils {
    private TemplateData data;
    private static final double DPI = 203; // 标准DPI
   private static final double MM_TO_INCH = 25.4;

    public GpXmlParseUtils(TemplateData data) {
        this.data = data;
    }
    private static double mmToPx(double mm) {
        return mm / MM_TO_INCH * DPI;
    }

    public  String convertToGPCommands(String xmlFilePath) throws Exception {
        StringBuilder gpCommands = new StringBuilder();

        // 1. 解析XML文件
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(xmlFilePath));

        // 2. 获取页面尺寸（单位：mm）
        Element page = document.getDocumentElement();
        String width = page.getAttribute("width");
        String height = page.getAttribute("height");



        // 4. 处理所有布局元素
        NodeList layouts = document.getElementsByTagName("layout");
        for (int i = 0; i < layouts.getLength(); i++) {
            Element layout = (Element) layouts.item(i);
            processLayoutElement(layout, gpCommands);
        }

        // 5. 处理线条元素
        NodeList lines = document.getElementsByTagName("line");
        for (int i = 0; i < lines.getLength(); i++) {
            Element line = (Element) lines.item(i);
           // processLineElement(line, gpCommands);
        }

        // 6. 打印标签
        gpCommands.append("PRINT 1\n");

        return gpCommands.toString();
    }
    private  void processLayoutElement(Element layout, StringBuilder gpCommands) {
        // 获取布局位置和尺寸
        String left = layout.getAttribute("left");
        String top = layout.getAttribute("top");
        String width = layout.getAttribute("width");
        String height = layout.getAttribute("height");

        // 转换为毫米到点(dot)的转换 (假设200 DPI: 1mm = 8 dots)
        int x = (int)(mmToPx(Double.parseDouble(left)));
        int y =(int)(mmToPx(Double.parseDouble(top)));
        int w =(int)(mmToPx(Double.parseDouble(width)));
        int h = (int)(mmToPx(Double.parseDouble(height)));

        // 检查布局中的元素类型
        NodeList children = layout.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;

                if (element.getTagName().equals("text")) {
                   // processTextElement(element, x, y, w, h, gpCommands);
                } else if (element.getTagName().equals("barcode")) {
                    processBarcodeElement(element, x, y, w, h, gpCommands);
                }
            }
        }
    }
    private  void processLineElement(Element lineElement, StringBuilder gpCommands) {
        // 获取线条属性
        String startX = lineElement.getAttribute("startX");
        String startY = lineElement.getAttribute("startY");
        String endX = lineElement.getAttribute("endX");
        String endY = lineElement.getAttribute("endY");

        // 转换为点(dot)
        int x1 = (int)(Double.parseDouble(startX) * 8);
        int y1 = (int)(Double.parseDouble(startY) * 8);
        int x2 = (int)(Double.parseDouble(endX) * 8);
        int y2 = (int)(Double.parseDouble(endY) * 8);

        // 线条粗细
        int thickness = 2; // 默认2点

        // 添加BOX指令绘制线条 (用很窄的矩形模拟线条)
        if (x1 == x2) { // 垂直线
            gpCommands.append(String.format("BAR %d,%d,%d,%d\n",
                    x1, y1, thickness, y2 - y1));
        } else if (y1 == y2) { // 水平线
            gpCommands.append(String.format("BAR %d,%d,%d,%d\n",
                    x1, y1, x2 - x1, thickness));
        } else { // 斜线 (用BOX指令模拟)
            gpCommands.append(String.format("BOX %d,%d,%d,%d,%d\n",
                    x1, y1, x2, y2, thickness));
        }
    }

    private  void processBarcodeElement(Element barcodeElement, int x, int y, int w, int h, StringBuilder gpCommands) {
        // 获取条码内容
        String content = barcodeElement.getTextContent().trim();

        // 替换模板变量
        if (content.contains("<%=_data.qrcode%>")) {
            content = content.replace("<%=_data.qrcode%>", data.getQrcode());
        }

        // 二维码参数
        String eccLevel = "L"; // 纠错等级
        int cellWidth = 4; // 单元宽度
        String mode = "A"; // 自动编码

        // 添加QRCODE指令
        gpCommands.append(String.format("QRCODE %d,%d,%s,%d,%s,0,\"%s\"\n",
                x, y, eccLevel, cellWidth, mode, content));
    }
    private  void processTextElement(Element textElement, int x, int y, int w, int h, StringBuilder gpCommands) {
        // 获取文本内容
        String content = textElement.getTextContent().trim();

        // 替换模板变量
        if (content.contains("<%=_data.qrcode%>")) {
            content = content.replace("<%=_data.qrcode%>", data.getQrcode());
        } else if (content.contains("<%=_data.sn%>")) {
            content = content.replace("<%=_data.sn%>", data.getSn());
        }

        // 处理多行文本
        content = content.replace("\n", "\"+\n\"");

        // 获取样式属性
        String style = textElement.getAttribute("style");
        Map<String, String> styleMap = parseStyle(style);

        // 设置字体大小和类型
        int fontSize = 10; // 默认
        if (styleMap.containsKey("fontSize")) {
            fontSize = Integer.parseInt(styleMap.get("fontSize"));
        }

        String fontType = "3"; // 16×24 dot 英数字体 (默认)
        if (styleMap.containsKey("fontFamily")) {
            String fontFamily = styleMap.get("fontFamily");
            if (fontFamily.contains("黑体") || fontFamily.contains("SimHei")) {
                fontType = "TSS24.BF2"; // 简体中文24×24
            }
        }

        // 旋转角度
        String rotation = "0";

        // 放大倍数 (根据字体大小调整)
        int xMul = 1;
        int yMul = 1;
        if (fontSize > 24) {
            xMul = 2;
            yMul = 2;
        } else if (fontSize > 16) {
            xMul = 1;
            yMul = 1;
        }

        // 添加TEXT指令
        gpCommands.append(String.format("TEXT %d,%d,\"%s\",%s,%d,%d,\"%s\"\n",
                x, y, fontType, rotation, xMul, yMul, content));
    }

    /**
     * 从 resources 目录解析 XML 文件
     * @param resourcePath resources 下的相对路径（如 "config.xml" 或 "folder/config.xml"）
     * @return 解析后的 Document 对象
     */
    public  Document parseXmlFromResources(String resourcePath) throws Exception {
        // 获取资源输入流
        InputStream inputStream = GpXmlParseUtils.class.getClassLoader()
                .getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IllegalArgumentException("文件未找到: " + resourcePath +
                    " (请确认文件是否在 resources 目录下)");
        }

        try {
            // 创建安全的 XML 解析器
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // 安全设置，防止 XXE 攻击
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            factory.setNamespaceAware(true); // 支持命名空间

            // 解析 XML
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(inputStream);

        } finally {
            // 确保输入流关闭
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        }
    }

    private  Map<String, String> parseStyle(String style) {
        Map<String, String> styleMap = new HashMap<>();
        if (style == null || style.isEmpty()) {
            return styleMap;
        }

        String[] parts = style.split(";");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                styleMap.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }

        return styleMap;
    }
}
