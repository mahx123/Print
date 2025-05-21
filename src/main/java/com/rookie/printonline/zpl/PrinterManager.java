package com.rookie.printonline.zpl;

//import com.alibaba.fastjson.JSON;

import com.rookie.printonline.gp.GpPrintExe;
import com.rookie.printonline.sdk.GbLibDll;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 打印机管理类，用于管理和操作打印机
 */
public class PrinterManager {
    private static PrinterManager instance;
    private List<PrinterConfig> printerConfigs;

    private PrinterManager() {
        this.printerConfigs = new ArrayList<>();
    }

    public static synchronized PrinterManager getInstance() {
        if (instance == null) {
            instance = new PrinterManager();
        }
        return instance;
    }

    /**
     * 添加打印机配置
     * @param config 打印机配置
     */
    public void addPrinterConfig(PrinterConfig config) {
        printerConfigs.add(config);
    }

    /**
     * 获取所有打印机配置
     * @return 打印机配置列表
     */
    public List<PrinterConfig> getAllPrinterConfigs() {
        return printerConfigs;
    }

    /**
     * 根据名称获取打印机配置
     * @param printerName 打印机名称
     * @return 打印机配置，如果未找到则返回null
     */
    public PrinterConfig getPrinterConfig(String printerName) {
        for (PrinterConfig config : printerConfigs) {
            if (config.getPrinterName().equals(printerName)) {
                return config;
            }
        }
        return null;
    }

    /**
     * 打印标签到本地打印机
     * @param printerName 打印机名称
     * @param template 打印模板
     * @return 打印结果
     * @throws Exception 打印异常
     */
    public PrintResult printLabel(String printerName, CainiaoPrintTemplate template) throws Exception {
        PrinterConfig config = getPrinterConfig(printerName);
        if (config == null) {
            throw new IllegalArgumentException("未找到打印机配置: " + printerName);
        }

        // 查找本地打印机
        PrintService printService = findPrintService(printerName);
        if (printService == null) {
            throw new PrintException("未找到本地打印机: " + printerName);
        }

        // 根据打印机类型生成相应的打印指令
        String printerType = config.getPrinterType().toLowerCase();
        String printContent;

        if (printerType.contains("tsc") || printerType.contains("佳博")) {
            printContent = convertTemplateToTSC(template, config);
        } else {
            // 默认使用ZPL
            printContent = convertTemplateToZPL(template, config);
        }

        // 准备打印作业
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc doc = new SimpleDoc(new ByteArrayInputStream(printContent.getBytes(StandardCharsets.UTF_8)), flavor, null);

        // 设置打印属性
        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        attributes.add(new Copies(1));

        // 创建并执行打印作业
        DocPrintJob job = printService.createPrintJob();
        try {
            job.print(doc, attributes);
            return createSuccessResult(printContent);
        } catch (PrintException e) {
            return createErrorResult("打印失败: " + e.getMessage());
        }
//        GbLibDll.INSTANCE.clearbuffer();
//        // 3. 转换XML为GP指令
//        try {
//            String gpCommands = printContent;
//
//            GpPrintExe.printByXmlTemplate(gpCommands);
//            GbLibDll.INSTANCE.closeport();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
       // return null;
    }

    /**
     * 查找本地打印机
     * @param printerName 打印机名称
     * @return 打印服务对象，如果未找到则返回null
     */
    private PrintService findPrintService(String printerName) {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }
        return null;
    }

    /**
     * 将模板转换为TSC指令 (适用于佳博打印机)
     * @param template 打印模板
     * @param config 打印机配置
     * @return TSC指令字符串
     */
    private String convertTemplateToTSC(CainiaoPrintTemplate template, PrinterConfig config) {
        StringBuilder tsc = new StringBuilder();

        // 获取打印机配置参数
        int width = (int) config.getExtraParams().getOrDefault("labelWidth", 100);  // 标签宽度，单位mm
        int height = (int) config.getExtraParams().getOrDefault("labelHeight", 32);  // 标签高度，单位mm
        int dpi = config.getDpi();

        // 初始化打印机
        tsc.append("SIZE ").append(width).append(" mm,").append(height).append(" mm\r\n");
        tsc.append("GAP 2 mm, 0 mm\r\n");
        tsc.append("DIRECTION 1\r\n");
        tsc.append("REFERENCE 0,0\r\n");
        tsc.append("CLS\r\n");

        // 添加二维码
        String qrcodeData = (String) template.getTemplateData().get("qrcode");
        int qrSize = (int) config.getExtraParams().getOrDefault("qrSize", 5);  // 二维码大小

        // 左侧二维码
        tsc.append("QRCODE 20,20,L,").append(qrSize).append(",A,0,M2,S7,\"").append(qrcodeData).append("\"\r\n");

        // 右侧二维码
        tsc.append("QRCODE ").append(width - 40).append(",20,L,").append(qrSize).append(",A,0,M2,S7,\"").append(qrcodeData).append("\"\r\n");

        // 添加文本
        int fontSize = (int) config.getExtraParams().getOrDefault("fontSize", 4);  // 字体大小

        // 左侧文本
        tsc.append("TEXT 60,30,\"TSS24.BF2\",0,1,1,\"").append(qrcodeData).append("\"\r\n");

        // 右侧文本
        tsc.append("TEXT ").append(width - 75).append(",30,\"TSS24.BF2\",0,1,1,\"").append(qrcodeData).append("\"\r\n");

        // 添加OCOC文本
        tsc.append("TEXT ").append(width/2 - 30).append(",20,\"TSS24.BF2\",0,2,2,\"OCOC\"\r\n");

        // 添加序列号
        String sn = (String) template.getTemplateData().get("sn");
        tsc.append("TEXT ").append(width/2 - 20).append(",5,\"TSS24.BF2\",0,1,1,\"").append(sn).append("\"\r\n");

        // 添加中间分隔线
        tsc.append("BAR ").append(width/2).append(",15,").append(width/2).append(",").append(height - 15).append(",2\r\n");

        // 打印
        tsc.append("PRINT 1\r\n");

        return tsc.toString();
    }

    /**
     * 将模板转换为ZPL指令
     * @param template 打印模板
     * @param config 打印机配置
     * @return ZPL指令字符串
     */
    private String convertTemplateToZPL(CainiaoPrintTemplate template, PrinterConfig config) {
        // 保留原有的ZPL转换逻辑，用于非佳博打印机
        StringBuilder zpl = new StringBuilder();

        // 获取打印机配置参数
        int width = (int) config.getExtraParams().getOrDefault("labelWidth", 4);  // 标签宽度，单位英寸
        int height = (int) config.getExtraParams().getOrDefault("labelHeight", 1.26);  // 标签高度，单位英寸

        // 初始化打印机
        zpl.append("^XA^LH0,0");

        // 添加二维码
        String qrcodeData = (String) template.getTemplateData().get("qrcode");

        // 左侧二维码
        zpl.append("^FO50,20^BQN,2,10^FDQA,").append(qrcodeData).append("^FS");

        // 右侧二维码
        zpl.append("^FO").append(width * 200 - 100).append(",20^BQN,2,10^FDQA,").append(qrcodeData).append("^FS");

        // 添加文本
        // 左侧文本
        zpl.append("^FO150,30^A0N,25,25^FD").append(qrcodeData).append("^FS");

        // 右侧文本
        zpl.append("^FO").append(width * 200 - 200).append(",30^A0N,25,25^FD").append(qrcodeData).append("^FS");

        // 添加OCOC文本
        zpl.append("^FO").append(width * 100 - 50).append(",20^A0N,30,30^FDOCOC^FS");

        // 添加序列号
        String sn = (String) template.getTemplateData().get("sn");
        zpl.append("^FO").append(width * 100 - 40).append(",5^A0N,20,20^FD").append(sn).append("^FS");

        // 添加中间分隔线
        zpl.append("^FO").append(width * 100).append(",15^GB1,70,3^FS");

        // 结束
        zpl.append("^XZ");

        return zpl.toString();
    }

    /**
     * 创建成功的打印结果
     * @param printData 打印数据
     * @return 打印结果对象
     */
    private PrintResult createSuccessResult(String printData) {
        PrintResult result = new PrintResult();
        result.setSuccess(true);
        result.setPrintData(printData);
        return result;
    }

    /**
     * 创建错误的打印结果
     * @param errorMsg 错误信息
     * @return 打印结果对象
     */
    private PrintResult createErrorResult(String errorMsg) {
        PrintResult result = new PrintResult();
        result.setSuccess(false);
        result.setErrorCode("PRINT_ERROR");
        result.setErrorMsg(errorMsg);
        return result;
    }
}