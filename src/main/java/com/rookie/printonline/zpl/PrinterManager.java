package com.rookie.printonline.zpl;

//import com.alibaba.fastjson.JSON;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import java.io.ByteArrayInputStream;
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

        // 转换模板为ZPL指令
        String zplContent = convertTemplateToZPL(template);

        // 准备打印作业
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc doc = new SimpleDoc(new ByteArrayInputStream(zplContent.getBytes(StandardCharsets.UTF_8)), flavor, null);

        // 设置打印属性
        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        attributes.add(new Copies(1));

        // 如果配置了媒体尺寸，添加到打印属性中
        if (config.getExtraParams().containsKey("mediaSize")) {
            MediaSizeName mediaSize = (MediaSizeName) config.getExtraParams().get("mediaSize");
            attributes.add(mediaSize);
        }

        // 创建并执行打印作业
        DocPrintJob job = printService.createPrintJob();
        try {
            job.print(doc, attributes);
            return createSuccessResult(zplContent);
        } catch (PrintException e) {
            return createErrorResult("打印失败: " + e.getMessage());
        }
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
     * 将模板转换为ZPL指令
     * @param template 打印模板
     * @return ZPL指令字符串
     */
    private String convertTemplateToZPL(CainiaoPrintTemplate template) {
        // 这里应该实现从菜鸟模板到ZPL的转换
        // 简化示例，实际实现需要根据模板内容生成对应的ZPL指令

        StringBuilder zpl = new StringBuilder();
        zpl.append("^XA^LH0,0^FO50,50^BQN,2,10^FDQA,");

        // 添加二维码数据
        String qrcodeData = (String) template.getTemplateData().get("qrcode");
        zpl.append(qrcodeData).append("^FS");

        // 添加文本
        zpl.append("^FO200,50^A0N,25,25^FD").append(qrcodeData).append("^FS");

        // 添加OCOC文本
        zpl.append("^FO300,50^A0N,30,30^FDOCOC^FS");

        // 添加序列号
        String sn = (String) template.getTemplateData().get("sn");
        zpl.append("^FO150,10^A0N,20,20^FD").append(sn).append("^FS");

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