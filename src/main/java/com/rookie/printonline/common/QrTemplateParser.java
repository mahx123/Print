package com.rookie.printonline.common;
import com.rookie.printonline.result.vo.QrPrintTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *@Title: 模板解析类
 *@Package: com.rookie.printonline.common
 *@Description: TODO
 *@Author: mahx 马怀啸
 *@Email: 616107968@qq.com
 *@Date: 2025/5/16 15:47
 *@Version: V1.0.0
 *@Copyright: 菜鸟
 */
public class QrTemplateParser {


    public static QrPrintTemplate parseTemplate(String xmlContent, Map<String, String> data) throws Exception {
        QrPrintTemplate template = new QrPrintTemplate();

        // 从data中填充基本属性
        template.setQrcode(data.get("qrcode"));
        template.setBatchCode(data.get("batchCode"));
        template.setSkuCode(data.get("skuCode"));
        template.setProduceDateCode(data.get("produceDateCode"));
        template.setSkuName(data.get("skuName"));
        template.setSn(data.get("sn"));

        // 处理lastFourChars
        String qrcode = data.get("qrcode");
        if (qrcode != null && qrcode.length() >= 24) {
            template.setLastFourChars(qrcode.substring(20, 24));
        } else {
            template.setLastFourChars("");
        }

        // 解析XML模板中的其他信息（如果需要）
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));

        // 这里可以添加对XML结构的进一步解析逻辑
        // 例如获取所有layout元素等

        return template;
    }

    public static Map<String, String> extractParametersFromXml(String xmlContent) throws Exception {
        Map<String, String> params = new HashMap<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));

        // 查找所有包含<%=...%>的文本节点
        NodeList textNodes = doc.getElementsByTagName("text");
        for (int i = 0; i < textNodes.getLength(); i++) {
            Node textNode = textNodes.item(i);
            String content = textNode.getTextContent();

            // 提取参数名
            if (content.contains("<%=")) {
                int start = content.indexOf("<%=") + 3;
                int end = content.indexOf("%>", start);
                if (end > start) {
                    String param = content.substring(start, end).trim();
                    // 去掉可能的_data.前缀
                    if (param.startsWith("_data.")) {
                        param = param.substring(6);
                    }
                    params.put(param, "");
                }
            }
        }

        return params;
    }
}
