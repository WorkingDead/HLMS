package utils.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

/*
 * reference: http://nothing.tw/jdk_api_1_6/javax/xml/bind/Marshaller.html
static String 	JAXB_ENCODING
          用來指定已編組 XML 資料中輸出編碼的屬性名稱。
static String 	JAXB_FORMATTED_OUTPUT
          用來指定是否使用換行和縮排對已編組 XML 資料進行格式化的屬性名稱。
static String 	JAXB_FRAGMENT
          用來指定 marshaller 是否將產生文檔級事件（即調用 startDocument 或 endDocument）的屬性名稱。
static String 	JAXB_NO_NAMESPACE_SCHEMA_LOCATION
          用來指定將放置在已編組 XML 輸出中的 xsi:noNamespaceSchemaLocation 屬性值的屬性名稱。
static String 	JAXB_SCHEMA_LOCATION
          用來指定將放置在已編組 XML 輸出中的 xsi:schemaLocation 屬性值的屬性名稱。
 */

public class JaxbXmlFactory {
	
	public static Marshaller prepareMarshaller(Class clz) throws JAXBException
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(clz);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//若設為 true 則 marshal 出來的 xml 會自動縮排
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        return marshaller;
    }
	
	public static <clz> clz unmarshaller(Class clz, InputStream is) throws JAXBException, XMLStreamException, FactoryConfigurationError, FileNotFoundException
    {
		JAXBContext context = JAXBContext.newInstance(clz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (clz) unmarshaller.unmarshal(is);
    }
	
	public static <clz> clz unmarshaller(Class clz, StringBuffer sb) throws JAXBException, XMLStreamException, FactoryConfigurationError, FileNotFoundException
    {
		JAXBContext context = JAXBContext.newInstance(clz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (clz) unmarshaller.unmarshal(new ByteArrayInputStream(sb.toString().getBytes()));
    }
	
	public static <clz> clz unmarshaller(Class clz, File file) throws JAXBException, XMLStreamException, FactoryConfigurationError, FileNotFoundException
    {
		JAXBContext context = JAXBContext.newInstance(clz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStreamReader in = new InputStreamReader(new FileInputStream(file));
        XMLEventReader xer = XMLInputFactory.newInstance()
                .createXMLEventReader(in);
        return (clz) unmarshaller.unmarshal(xer);
    }
	
	public static <clz> clz unmarshaller(Class clz, URL url) throws JAXBException, XMLStreamException, FactoryConfigurationError, IOException
    {
        return (clz) unmarshaller(clz, url, null);
    }
	
	public static <clz> clz unmarshaller(Class clz, URL url, Proxy proxy) throws JAXBException, XMLStreamException, FactoryConfigurationError, IOException
    {
		JAXBContext context = JAXBContext.newInstance(clz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStreamReader in = new InputStreamReader(url.openStream());
        XMLEventReader xer = XMLInputFactory.newInstance()
                .createXMLEventReader(in);
        return (clz) unmarshaller.unmarshal(xer);
    }
}
