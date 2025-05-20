package com.rookie.printonline.zpl;


import java.util.HashMap;
import java.util.Map;

/**
 * 打印机配置类，用于设置打印机参数
 */
public class PrinterConfig {
    private String printerName;
    private String printerType;
    private int dpi;
    private Map<String, Object> extraParams;

    public PrinterConfig(String printerName, String printerType) {
        this.printerName = printerName;
        this.printerType = printerType;
        this.dpi = 203; // 默认203 DPI
        this.extraParams = new HashMap<>();
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String getPrinterType() {
        return printerType;
    }

    public void setPrinterType(String printerType) {
        this.printerType = printerType;
    }

    public int getDpi() {
        return dpi;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    public Map<String, Object> getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(Map<String, Object> extraParams) {
        this.extraParams = extraParams;
    }

    public void addExtraParam(String key, Object value) {
        this.extraParams.put(key, value);
    }

    @Override
    public String toString() {
        return "PrinterConfig{" +
                "printerName='" + printerName + '\'' +
                ", printerType='" + printerType + '\'' +
                ", dpi=" + dpi +
                ", extraParams=" + extraParams +
                '}';
    }
}