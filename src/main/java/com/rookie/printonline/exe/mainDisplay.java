package com.rookie.printonline.exe;

import java.io.IOException;
import javax.print.PrintException;

import javax.xml.parsers.ParserConfigurationException;


import org.xml.sax.SAXException;


public class mainDisplay {
	public static String filename;
  
	public static void main(String args[])throws ParserConfigurationException, SAXException, IOException, PrintException{
		
		/************��ȡ�ļ���Դ***********/
		filename="D:\\vx_storage\\xwechat_files\\mahuaixiao_5123\\msg\\file\\2025-05\\QR_Print_Template_100_32_2.0.xml";
		/*****���ô�ӡ******************/
		xmlPrint xml = new xmlPrint();
		xml.printAction();
	   }
}

