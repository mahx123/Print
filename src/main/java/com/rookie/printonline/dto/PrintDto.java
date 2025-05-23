package com.rookie.printonline.dto;

import java.util.List;

public class PrintDto {

    private String printerName;

    private List<String> barCodeList;

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public void setBarCodeList(List<String> barCodeList) {
        this.barCodeList = barCodeList;
    }

    public String getPrinterName() {
        return printerName;
    }

    public List<String> getBarCodeList() {
        return barCodeList;
    }

    @Override
    public String toString() {
        return "PrintDto{" +
                "printerName='" + printerName + '\'' +
                ", barCodeList=" + barCodeList +
                '}';
    }

    public PrintDto() {


    }
}
