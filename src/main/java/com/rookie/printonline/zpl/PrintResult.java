package com.rookie.printonline.zpl;

/**
 * 打印结果类，封装API返回结果
 */
public class PrintResult {
    private boolean success;
    private String errorCode;
    private String errorMsg;
    private String printData;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getPrintData() {
        return printData;
    }

    public void setPrintData(String printData) {
        this.printData = printData;
    }

    @Override
    public String toString() {
        return "PrintResult{" +
                "success=" + success +
                ", errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", printData='" + printData + '\'' +
                '}';
    }
}