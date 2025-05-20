package com.rookie.printonline.zpl;


import com.rookie.printonline.common.JsonUtil;

import java.util.List;

/**
 * 打印示例应用，展示如何配置打印机并打印标签
 */
public class SamplePrintApp {
    public static void main(String[] args) {
        try {

            // 获取所有可用的打印机名称
            PrinterManager printerManager2 = PrinterManager.getInstance();
            List<PrinterConfig> allPrinterConfigs = printerManager2.getAllPrinterConfigs();
            System.out.println("可用打印机列表：");
            for (PrinterConfig name : allPrinterConfigs) {
                System.out.println(JsonUtil.objectToJson(name));
            }
            // 1. 配置打印机
            PrinterConfig printerConfig = new PrinterConfig("Gprinter GP-1324D", "热敏打印机");
            printerConfig.setDpi(300); // 设置DPI为300
            printerConfig.addExtraParam("printSpeed", 4); // 设置打印速度(英寸/秒)
            printerConfig.addExtraParam("darkness", 15); // 设置打印浓度(0-30)

            // 2. 添加打印机配置到管理器
            PrinterManager printerManager = PrinterManager.getInstance();
            printerManager.addPrinterConfig(printerConfig);

            // 3. 创建打印模板
            CainiaoPrintTemplate template = OCocTemplateBuilder.createTemplate(
                    "ococ_label_template",    // 模板ID
                    "QR1234567890",          // 二维码内容
                    "SN20250519"             // 序列号
            );

            // 4. 执行打印
            PrintResult result = printerManager.printLabel("Gprinter GP-1324D", template);

            // 5. 处理打印结果
            if (result.isSuccess()) {
                System.out.println("打印成功！");
                System.out.println("打印数据：" + result.getPrintData());
            } else {
                System.out.println("打印失败！错误码：" + result.getErrorCode());
                System.out.println("错误信息：" + result.getErrorMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}