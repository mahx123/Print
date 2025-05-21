package com.rookie.printonline.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
public class XmlUtils {

    /**
     * 从 resources 目录解析 XML 文件
     * @param resourcePath resources 下的相对路径（如 "config.xml" 或 "folder/config.xml"）
     * @return 解析后的 Document 对象
     */
    public static Document parseXmlFromResources(String resourcePath) throws Exception {
        // 获取资源输入流
        InputStream inputStream = XmlUtils.class.getClassLoader()
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
}
