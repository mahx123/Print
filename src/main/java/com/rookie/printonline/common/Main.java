package com.rookie.printonline.common;
import com.rookie.printonline.result.vo.QrPrintTemplate;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        try {
            String xmlContent = FileUtils.readFileToString(new File("D:\\vx_storage\\xwechat_files\\mahuaixiao_5123\\msg\\file\\2025-05\\QR_Print_Template_100_32_2.0.xml"), "UTF-8");
            // 你的XML内容
          //  String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>..."; // 你的完整XML内容

            // 首先提取模板中需要的参数
            Map<String, String> templateParams = QrTemplateParser.extractParametersFromXml(xmlContent);
            System.out.println("模板需要的参数: " + templateParams.keySet());

            // 准备数据
            Map<String, String> data = new HashMap<>();
            data.put("qrcode", "123456789012345678901234");
            data.put("batchCode", "BATCH001");
            data.put("skuCode", "SKU12345");
            data.put("produceDateCode", "2023-11-15");
            data.put("skuName", "测试产品");
            data.put("sn", "SN123456");

            // 解析并填充对象
            QrPrintTemplate template = QrTemplateParser.parseTemplate(xmlContent, data);
            System.out.println("解析结果: " + template);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
