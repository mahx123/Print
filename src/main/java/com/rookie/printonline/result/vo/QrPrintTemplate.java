package com.rookie.printonline.result.vo;
/**
 *@Title: 打印模板对象
 *@Package: com.rookie.printonline.result.vo
 *@Description: TODO
 *@Author: mahx 马怀啸
 *@Email: 616107968@qq.com
 *@Date: 2025/5/16 16:20
 *@Version: V1.0.0
 *@Copyright: 菜鸟
 */
public class QrPrintTemplate {
    private String qrcode;
    private String batchCode;
    private String skuCode;
    private String produceDateCode;
    private String skuName;
    private String sn;
    private String lastFourChars;

    // Getters and Setters
    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getProduceDateCode() {
        return produceDateCode;
    }

    public void setProduceDateCode(String produceDateCode) {
        this.produceDateCode = produceDateCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getLastFourChars() {
        return lastFourChars;
    }

    public void setLastFourChars(String lastFourChars) {
        this.lastFourChars = lastFourChars;
    }

    @Override
    public String toString() {
        return "QrPrintTemplate{" +
                "qrcode='" + qrcode + '\'' +
                ", batchCode='" + batchCode + '\'' +
                ", skuCode='" + skuCode + '\'' +
                ", produceDateCode='" + produceDateCode + '\'' +
                ", skuName='" + skuName + '\'' +
                ", sn='" + sn + '\'' +
                ", lastFourChars='" + lastFourChars + '\'' +
                '}';
    }
}
