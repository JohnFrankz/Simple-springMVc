package com.okmomak.spring.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

public class XMLParser {
    public static String getBasePath(String file){
        SAXReader saxReader = new SAXReader();
        InputStream resourceAsStream = XMLParser.class.getClassLoader().getResourceAsStream(file);
        Document document = null;
        try {
            document = saxReader.read(resourceAsStream);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        Element root = document.getRootElement();
        Element element = root.element("component-scan");
        String packagePath = element.attribute("base-package").getText();

        return packagePath;
    }
}
