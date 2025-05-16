package com.rookie.printonline.exe;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;    
 import javax.xml.parsers.DocumentBuilderFactory;    
import javax.xml.parsers.ParserConfigurationException;
 
 import org.w3c.dom.Document;    
 import org.w3c.dom.Element;    
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
public class xmlToDoc {
	/**
	 * ��������������
	 * @param filename
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
    String s;
	public String[] getDoc(String filename){    


	
			/**
			 * ������������
			 */
            DocumentBuilderFactory factory = DocumentBuilderFactory    
                    .newInstance();    
            DocumentBuilder builder = null;
			try {
				builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
            System.out.println(filename);
            Document doc = null;
			try {
				doc = builder.parse(filename);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
           doc.normalize();   
            NodeList links = doc.getElementsByTagName("BkDetail1"); 
           int length=links.getLength();
            String data[]= new String[length];
            for (int i = 0; i < length; i++) { 
               Element link = (Element) links.item(i); 
               if(link.hasChildNodes()){
               String s=((NodeList) link).item(0).getNodeValue();
               data[i]=s;
               System.out.println(data[i]);
               }
            }
        return data;
    }    

}
