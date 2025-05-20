package com.rookie.printonline.dto;

import java.util.List;
import java.util.ArrayList;

public class TemplateData {
    private String qrcode;
    private String sn;

    // 构造函数、getter和setter
    public TemplateData(String qrcode, String sn) {
        this.qrcode = qrcode;
        this.sn = sn;
    }

    public String getQrcode() {
        return qrcode;
    }

    public String getSn() {
        return sn;
    }
}
