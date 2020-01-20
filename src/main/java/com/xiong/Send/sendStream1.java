package com.xiong.Send;


import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Iterator;


public class sendStream1 {
    public boolean sendS2F24() {
        //创建一个读取xml配置文件的对象 用来指向XML文件的输入流
        SAXReader saxReader = new SAXReader();
        try {
            InputStream is = new FileInputStream("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static InputStream getXmlInputStream(String xmlPath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(xmlPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    private static Element getRootElementFromIs(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        if (inputStream == null)
            return null;
        return null;
    }


    public boolean sendS6F1() {
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new File(""));
            Element root = document.getRootElement();
            Iterator it = root.elementIterator();
            while (it.hasNext()) {
                Element elememt = (Element) it.next();
                System.out.println("id:" + root.attributeValue("id"));
                System.out.println("title:" + root.attributeValue("title"));
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean sendF6F2() {
        Document document = DocumentHelper.createDocument();
        Element books = document.addElement("books");
        Element book1 = document.addElement("books");
        return false;
    }
}
