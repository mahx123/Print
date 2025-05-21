package com.rookie.printonline.zpl;


/**
 * 打印示例应用，展示如何配置打印机并打印标签
 */
public class SamplePrintApp {
    public static void main(String[] args) {
        try {
            // 1. 配置打印机
//            PrinterConfig printerConfig = new PrinterConfig("TSC TTP-244 Pro", "热敏打印机");
//            printerConfig.setDpi(300); // 设置DPI为300
//            printerConfig.addExtraParam("printSpeed", 4); // 设置打印速度(英寸/秒)
//            printerConfig.addExtraParam("darkness", 15); // 设置打印浓度(0-30)

//            // 1. 配置佳博打印机
            PrinterConfig printerConfig = new PrinterConfig("ZDesigner GT800-300dpi EPL", "ZPL");
            printerConfig.setDpi(203); // 设置DPI
            printerConfig.addExtraParam("labelWidth", 100); // 标签宽度(mm)
            printerConfig.addExtraParam("labelHeight", 32); // 标签高度(mm)
            printerConfig.addExtraParam("qrSize", 10); // 二维码大小
            printerConfig.addExtraParam("fontSize", 5); // 字体大小

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
         //   PrintResult result = printerManager.printLabel("TSC TTP-244 Pro", template);
            PrintResult result = printerManager.printLabel("ZDesigner GT800-300dpi EPL", template);

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