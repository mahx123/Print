package com.rookie.printonline.common;


/**
 * OCOC标签模板构建器，用于创建特定格式的双OCOC标签模板
 */
public class OCocTemplateBuilder {

    // 模板内容常量
    private static final String TEMPLATE_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<page\n" +
                    "        xmlns=\"http://cloudprint.cainiao.com/print\"\n" +
                    "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "        xsi:schemaLocation=\"http://cloudprint.cainiao.com/print http://cloudprint-docs-resource.oss-cn-shanghai.aliyuncs.com/lpml_schema.xsd\"\n" +
                    "        xmlns:editor=\"http://cloudprint.cainiao.com/schema/editor\"\n" +
                    "        width=\"100\" height=\"32\"  splitable=\"false\" >\n" +
                    "        <header height=\"0\" >\n" +
                    "        </header>\n" +
                    "        <layout \n" +
                    "            editor:_for_=\"169874052945135\"\n" +
                    "            id=\"1698740529451118\" width=\"5.1\" height=\"24\" left=\"39.8\" top=\"4\"  style=\"zIndex:3;\">\n" +
                    "            <text  \n" +
                    "                style=\"fontFamily:黑体;fontWeight:bold;fontSize:15;align:center;valign:middle;\">\n" +
                    "                <![CDATA[O\n" +
                    "C\n" +
                    "O\n" +
                    "C]]>\n" +
                    "            </text>\n" +
                    "        </layout>\n" +
                    "        <layout \n" +
                    "            editor:_for_=\"169874060404498\"\n" +
                    "            id=\"1698740604057157\" width=\"25\" height=\"25\" left=\"3\" top=\"3.5\"  style=\"zIndex:4;\"><barcode type=\"qrcode\" ratioMode=\"keepRatio\"       ><![CDATA[<%=_data.qrcode%>]]></barcode>\n" +
                    "        </layout>\n" +
                    "        <layout \n" +
                    "            editor:_for_=\"1698740643252641\"\n" +
                    "            id=\"1698740643252790\" width=\"7.5\" height=\"20\" left=\"30\" top=\"4\"  style=\"zIndex:5;\">\n" +
                    "            <text editor:_printName_=\"标签内容\" \n" +
                    "                style=\"fontFamily:SimHei;fontWeight:bold;fontSize:10;\">\n" +
                    "                <![CDATA[<%=_data.qrcode%>]]>\n" +
                    "            </text>\n" +
                    "        </layout>\n" +
                    "        <layout \n" +
                    "            editor:_for_=\"1698740770832549\"\n" +
                    "            id=\"1698740770832910\" width=\"25\" height=\"25\" left=\"72\" top=\"3.5\"  style=\"zIndex:7;\"><barcode type=\"qrcode\" ratioMode=\"keepRatio\"       ><![CDATA[<%=_data.qrcode%>]]></barcode>\n" +
                    "        </layout>\n" +
                    "        <line\n" +
                    "                    style=\"lineType:dashed;lineColor:#000;\"\n" +
                    "                    startX=\"50\" \n" +
                    "                    startY=\"7\" \n" +
                    "                    endX=\"50\"\n" +
                    "                    endY=\"27\"\n" +
                    "                    editor:_deg_=\"90\">\n" +
                    "                </line>\n" +
                    "        <layout \n" +
                    "            editor:_for_=\"1698744028509846\"\n" +
                    "            id=\"169874402850823\" width=\"7.5\" height=\"20\" left=\"62.5\" top=\"4\"  style=\"zIndex:8;\">\n" +
                    "            <text editor:_printName_=\"标签内容\" \n" +
                    "                style=\"fontFamily:SimHei;fontWeight:bold;fontSize:10;\">\n" +
                    "                <![CDATA[<%=_data.qrcode%>]]>\n" +
                    "            </text>\n" +
                    "        </layout>\n" +
                    "        <layout \n" +
                    "            editor:_for_=\"169874440321076\"\n" +
                    "            id=\"1698744403210405\" width=\"5.1\" height=\"24\" left=\"56\" top=\"4\"  style=\"zIndex:9;\">\n" +
                    "            <text  \n" +
                    "                style=\"fontFamily:SimHei;fontWeight:bold;fontSize:15;align:center;valign:middle;\">\n" +
                    "                <![CDATA[O\n" +
                    "C\n" +
                    "O\n" +
                    "C]]>\n" +
                    "            </text>\n" +
                    "        </layout>\n" +
                    "        <footer height=\"0\" >\n" +
                    "        </footer>\n" +
                    "        <layout \n" +
                    "            editor:_for_=\"1699926321631581\"\n" +
                    "            id=\"169992632163181\" width=\"10\" height=\"16\" left=\"45\" top=\"8.8\"  style=\"zIndex:10;\">\n" +
                    "            <text  \n" +
                    "                style=\"fontFamily:SimHei;fontSize:15;align:center;valign:middle;\">\n" +
                    "                <![CDATA[折线]]>\n" +
                    "            </text>\n" +
                    "        </layout>\n" +
                    "        <layout \n" +
                    "            editor:_for_=\"170849940761519\"\n" +
                    "            id=\"1708499407615992\" width=\"15\" height=\"5\" left=\"42\" top=\"3\"  style=\"zIndex:11;\">\n" +
                    "            <text  \n" +
                    "                style=\"fontFamily:SimHei;align:center;valign:middle;\">\n" +
                    "                <![CDATA[<%=_data.sn%>]]>\n" +
                    "            </text>\n" +
                    "        </layout>\n" +
                    "</page>";

    /**
     * 创建OCOC标签模板
     * @param templateId 模板ID
     * @return 菜鸟云打印模板对象
     */
    public static CainiaoPrintTemplate createTemplate(String templateId) {
        return new CainiaoPrintTemplate(templateId, TEMPLATE_XML);
    }

    /**
     * 创建OCOC标签模板并设置数据
     * @param templateId 模板ID
     * @param qrcode 二维码内容
     * @param sn 序列号
     * @return 菜鸟云打印模板对象
     */
    public static CainiaoPrintTemplate createTemplate(String templateId, String qrcode, String sn) {
        CainiaoPrintTemplate template = createTemplate(templateId);
        template.addData("qrcode", qrcode);
        template.addData("sn", sn);
        return template;
    }
}