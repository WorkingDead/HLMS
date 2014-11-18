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
          �Ψӫ��w�w�s�� XML ��Ƥ���X�s�X���ݩʦW�١C
static String 	JAXB_FORMATTED_OUTPUT
          �Ψӫ��w�O�_�ϥδ���M�Y�ƹ�w�s�� XML ��ƶi��榡�ƪ��ݩʦW�١C
static String 	JAXB_FRAGMENT
          �Ψӫ��w marshaller �O�_�N���ͤ��ɯŨƥ�]�Y�ե� startDocument �� endDocument�^���ݩʦW�١C
static String 	JAXB_NO_NAMESPACE_SCHEMA_LOCATION
          �Ψӫ��w�N��m�b�w�s�� XML ��X���� xsi:noNamespaceSchemaLocation �ݩʭȪ��ݩʦW�١C
static String 	JAXB_SCHEMA_LOCATION
          �Ψӫ��w�N��m�b�w�s�� XML ��X���� xsi:schemaLocation �ݩʭȪ��ݩʦW�١C
 */

public class JaxbXmlFactory {
	
	public static Marshaller prepareMarshaller(Class clz) throws JAXBException
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(clz);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//�Y�]�� true �h marshal �X�Ӫ� xml �|�۰��Y��
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
