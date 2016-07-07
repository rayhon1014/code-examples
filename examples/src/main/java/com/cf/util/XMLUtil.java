package com.cf.util;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
/**
 * Created by IntelliJ IDEA.
 * User: rayhon
 * Date: 12/19/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class XMLUtil {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(XMLUtil.class);

    public static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }

    public static String toXMLString(Document docment)
    {
        StringWriter sw = new StringWriter();
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(docment), new StreamResult(writer));
            return writer.getBuffer().toString().replaceAll("\n|\r", "");
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();

    }

    public static NodeList getNodeListByXPath(Node node, String xPathKey){
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = null;
        NodeList nodeList = null;
        try {
            expression = xPath.compile(xPathKey);
            nodeList = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            LOGGER.info(e);
        }
        LOGGER.info("xPathKey :: " + xPathKey + " :: Value :: " + ((null != nodeList && null != nodeList.item(0)) ? nodeList.item(0).getNodeValue() : ""));
        return nodeList;
    }

    /**
     * @param doc
     * @param xPathKey
     * @return
     */
    public static String getElementsByXpath(Document doc, String xPathKey) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = null;
        NodeList nodeList = null;
        String elements = null;
        try {
            expression = xPath.compile(xPathKey);
            nodeList = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
            for (int j = 0; j < nodeList.getLength(); j++) {
                elements += nodeList.item(j).getNodeValue() + "|";
            }
        } catch (XPathExpressionException e) {
            LOGGER.info(e);
        }
        LOGGER.info("xPathKey :: " + xPathKey + " :: Value :: " + elements);
        return null != elements ? (elements.substring(0, elements.lastIndexOf("|") > 0 ? elements.lastIndexOf("|") - 1 : 0)) : "";
    }

    public static NamedNodeMap getElementAttributes(String url, String xPathKey) {
        Document document = getResponse(url);
        NamedNodeMap productAttributes = null;
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = null;
        NodeList nodeList = null;
        try {
            expression = xPath.compile(xPathKey);
            nodeList = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            LOGGER.info(e);
        }
        //As of now returning Products Attributes.
        productAttributes = null != nodeList ? getProductAttributes(nodeList) : null;
        return productAttributes;
    }

    public static NodeList getElementNodeList(String url, String xPathKey) {
        Document document = getResponse(url);
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = null;
        NodeList nodeList = null;
        try {
            expression = xPath.compile(xPathKey);
            nodeList = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            LOGGER.info(e);
        }

        return null != nodeList ? nodeList : null;
    }

	public static String getElementValue(Node node, String xPath)
    {
        XPath path = XPathFactory.newInstance().newXPath();
        XPathExpression expression = null;
        String elementValue = null;
        NodeList nodeList = null;
        try {
            expression = path.compile(xPath);
            nodeList = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            LOGGER.info(e);
        }
        elementValue = (null != nodeList && null != nodeList.item(0)) ? nodeList.item(0).getNodeValue() : "";
        LOGGER.info("xPath :: " + xPath + ", value :: " + elementValue);
        return elementValue;
    }

	public static List<String> getElementValues(Node node, String xPath){
		NodeList nodeList = getNodeListByXPath(node, xPath);
		if(nodeList == null){
			return null;
		}

		List<String> values = new ArrayList<String>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			values.add(nodeList.item(i).getNodeValue());
		}
		return values;
	}


    public static Document getResponse(String url) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
        Document doc = null;
        boolean parsed = false;
        int parseTrycounter = 0;
        while (!parsed) {
            try {
                doc = db.parse(url);
                parsed = true;
            } catch (Exception e) {
                parseTrycounter++;
                if (parseTrycounter >= 5) {
                    throw new RuntimeException("503");
                }
            }
        }
        return doc;
    }

    public static Document getResponse(InputStream inputStream) throws Exception {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(inputStream);
        } catch (Exception ex) {
            throw new Exception("Parse xml failed. Error message - " + ex.getMessage());
        }

        return doc;
    }

    public static NamedNodeMap getProductAttributes(NodeList nodeList) {
        NamedNodeMap productAttributes = (null != nodeList && null != nodeList.item(0)) ? nodeList.item(0).getAttributes() : null;
        return productAttributes;
    }


    public static String getNamedAttributeValue(NamedNodeMap namedNodeMap, String attribute) {
        String nodeValue = "";
        nodeValue = (null != namedNodeMap && null != namedNodeMap.getNamedItem(attribute)) ? namedNodeMap.getNamedItem(attribute).getTextContent() : "";
        return nodeValue;
    }

    public static List<NamedNodeMap> getNamedNodeMap(NodeList nodeList) {
        List<NamedNodeMap> namedNodeMapList = new ArrayList<NamedNodeMap>();
        if (null != nodeList && nodeList.getLength() > 0) {
            int nodeCount = nodeList.getLength();
            for (int i = 0; i < nodeCount; i++) {
                namedNodeMapList.add(nodeList.item(i).getAttributes());
            }
        }
        return namedNodeMapList;
    }

    /**
     * check and see if the xml is well-formed
     * @param xml
     * @return
     */
    public static boolean validate(String xml) {

        try
        {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(new DefaultHandler());
            InputSource source = new InputSource(new ByteArrayInputStream(xml.getBytes()));
            parser.parse(source);
        }
        catch (SAXException e)
        {
            return false;
        }
        catch(IOException ioe)
        {
            throw new RuntimeException("Cannot parse input string. Message:" + ioe.getMessage(), ioe);
        }

        return true;
    }

    public static String formatHtml(String html)
    {
        TagNode tagNode = new HtmlCleaner().clean(html);
        String cleanHtml =  new PrettyXmlSerializer(new CleanerProperties()).getAsString(tagNode);
        return cleanHtml;
    }


}
