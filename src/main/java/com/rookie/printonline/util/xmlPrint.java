package com.rookie.printonline.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
public class xmlPrint implements Printable { 
	private static  int PAGES;
	private String getdata[] = null;
	public void printAction() throws PrintException{
		/**
		 * ��ȡxml��ӡ��Ϣ
		 */
		xmlToDoc analysis =new xmlToDoc();
		getdata = analysis.getDoc(mainDisplay.filename);
		for(int i=0;i<getdata.length;i++)
		{
			if(getdata[i]==null)
			 getdata[i]=" "; // Ϊnull����ֵ
		 System.out.println(getdata[i]);
		}
		/**
		 * ��ô�ӡҳ��
		 */
		PAGES=(getdata.length/48)+1;


		 //ָ����ӡ�����ʽ 
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE; 
        //��λĬ�ϵĴ�ӡ���� 
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService(); 
        //������ӡ��ҵ 
        DocPrintJob job = printService.createPrintJob(); 
        //���ô�ӡ���� 
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet(); 
        DocAttributeSet das = new HashDocAttributeSet(); 
        //ָ����ӡ���� 
        Doc doc = new SimpleDoc(this, flavor, das); 
        //����ʾ��ӡ�Ի���ֱ�ӽ��д�ӡ���� 
        try{
	     job.print(doc, pras);
        }catch(PrintException e){e.printStackTrace(); };
	}
	public int print(Graphics graphics, PageFormat pf, int page)throws PrinterException {
		
		 Graphics2D g2 = (Graphics2D) graphics;
		g2.setPaint(Color.black); //���ô�ӡ��ɫΪ��ɫ 
		if (page >= PAGES) //����ӡҳ�Ŵ�����Ҫ��ӡ����ҳ��ʱ����ӡ�������� 
			return Printable.NO_SUCH_PAGE; 
		g2.translate(pf.getImageableX(), pf.getImageableY());//ת�����꣬ȷ����ӡ�߽� 
		drawCurrentPageText(g2, pf, page); //��ӡ��ǰҳ�ı� 
		return Printable.PAGE_EXISTS; //���ڴ�ӡҳʱ��������ӡ���� 
	}

	private void drawCurrentPageText(Graphics2D g2, PageFormat pf, int page) {
		
	        //��ȡĬ�����弰��Ӧ�ĳߴ� 
			FontRenderContext context = g2.getFontRenderContext(); 
			Font font = new Font("������", Font.PLAIN, 8);
	           g2.setFont(font);//�������� 
	        float ascent = 16;     //�����ַ����� 
	        int j=font.getSize(),u=j+7;    //����������֮�����
	        System.out.println(j);
	        switch(page){
	        case 0:
	        	for(int i=0;i<48;i++){
	        		if(i>getdata.length-1)
	        			break;
		        	 g2.drawString(getdata[i], -2, ascent); //�����ӡÿһ���ı���ͬʱ��ֽ��λ 
		        	 ascent += u;  
		        }
	        case 1:
	        	for(int i=48;i<48*2;i++){
	        		if(i>getdata.length-1)
	        			break;
		        	 g2.drawString(getdata[i], -2, ascent); //�����ӡÿһ���ı���ͬʱ��ֽ��λ 
		        	 ascent += u;  
		        }
	        case 2:
	        	for(int i=48*2;i<48*3;i++){
	        		if(i>getdata.length-1)
	        			break;
		        	 g2.drawString(getdata[i], -2, ascent); //�����ӡÿһ���ı���ͬʱ��ֽ��λ 
		        	 ascent += u;  
		        }
	        }


	    } 

}
