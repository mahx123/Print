package com.rookie.printonline.zpl;


import java.util.HashMap;
import java.util.Map;

/**
 * 菜鸟云打印模板类，用于管理模板信息和数据
 */
public class CainiaoPrintTemplate {
    private String templateId;
    private String templateContent;
    private Map<String, Object> templateData;

    public CainiaoPrintTemplate(String templateId, String templateContent) {
        this.templateId = templateId;
        this.templateContent = templateContent;
        this.templateData = new HashMap<>();
    }

    /**
     * 添加模板数据
     * @param key 数据键
     * @param value 数据值
     */
    public void addData(String key, Object value) {
        this.templateData.put(key, value);
    }

    /**
     * 获取模板ID
     * @return 模板ID
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * 获取模板内容
     * @return 模板内容
     */
    public String getTemplateContent() {
        return templateContent;
    }

    /**
     * 获取模板数据
     * @return 模板数据映射
     */
    public Map<String, Object> getTemplateData() {
        return templateData;
    }
}