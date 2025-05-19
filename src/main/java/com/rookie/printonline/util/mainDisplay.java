package com.rookie.printonline.util;

import java.io.IOException;
import javax.print.PrintException;

import javax.xml.parsers.ParserConfigurationException;


import org.xml.sax.SAXException;


public class mainDisplay {
	public static String filename;
  
	public static void main(String args[])throws ParserConfigurationException, SAXException, IOException, PrintException{
		
		/************��ȡ�ļ���Դ***********/
		filename="D:\\xml//QR_Print_Template_02.xml";
		/*****���ô�ӡ******************/
		xmlPrint xml = new xmlPrint();
		xml.printAction();
	   }
}

