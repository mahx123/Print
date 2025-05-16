package com.rookie.printonline.result.vo;

public class PrintData {
    private String qrcode;  // 二维码内容
    private String sn;      // 序列号（右边的数字）

    // 构造方法
    public PrintData(String qrcode, String sn) {
        this.qrcode = qrcode;
        this.sn = sn;
    }

    public PrintData() {

    }

    // Getter和Setter
    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
