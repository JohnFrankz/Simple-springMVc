package com.okmomak.test;

import com.okmomak.spring.xml.XMLParser;
import org.junit.Test;

public class MVCTest {

    @Test
    public void testXMLParser() {
        String basePath = XMLParser.getBasePath("springmvc.xml");
        System.out.println("basePath = " + basePath);
    }
}
