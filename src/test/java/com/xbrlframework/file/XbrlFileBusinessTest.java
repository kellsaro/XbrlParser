package com.xbrlframework.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.xbrlframework.file.XbrlFileBusiness;
import com.xbrlframework.instance.InstanceBusiness;

public class XbrlFileBusinessTest {

	static DocumentBuilder documentBuilder;
	static XbrlFileBusiness xfilebusiness;
	static String notXbrlUri = "https://www.sec.gov/Archives/edgar/data/320193/000032019318000070/aapl-20180331.xsd";
	static String xbrlUri = "https://www.sec.gov/Archives/edgar/data/320193/000032019318000070/aapl-20180331.xml";
	static Document invalidFile;

	@BeforeClass
	public static void getFileFromInternet() throws SAXException, IOException, ParserConfigurationException {
		documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		xfilebusiness = new XbrlFileBusiness();
		xfilebusiness.setFileAs(xbrlUri);
		invalidFile = documentBuilder.parse(notXbrlUri);	
	}
	

	/*
	 * testing toJson
	 */
	@Test
	public void toJson_AppleFile() throws SAXException, IOException {
		InstanceBusiness ib = new InstanceBusiness();
		ib.setRootNodeFrom(documentBuilder.parse(xbrlUri));
		ib.build();
		assertNotNull(xfilebusiness.parseToJson(ib.getInstance()));
	}

	
	@Test
	public void toJson_MsftFile() throws SAXException, IOException {
		final String xbrlUri = "https://sec.gov/Archives/edgar/data/789019/000156459018009307/msft-20180331.xml";
		InstanceBusiness ib = new InstanceBusiness();
		ib.setRootNodeFrom(documentBuilder.parse(xbrlUri));
		ib.build();
		assertNotNull(xfilebusiness.parseToJson(ib.getInstance()));		
	}
	
	
	/*
	 * testing setFileAs(string)
	 */
	@Test
	public void setFileAs_validURI() throws ParserConfigurationException, SAXException, IOException {
		assertNotNull(xfilebusiness.getFileAsDocument());
		assertTrue(xfilebusiness.getFileAsDocument().getDocumentElement()
				.getTagName().toLowerCase().contains("xbrl"));
	}
	

	/*
	 * testing isXbrlDoc()
	 */

	@Test
	public void isXbrlDoc_ifTrue() throws SAXException, IOException {
		// test
		assertTrue(xfilebusiness
				.isXbrlDoc(xfilebusiness.getFileAsDocument()));
	}

	@Test
	public void isXbrlDoc_ifEmpty() throws ParserConfigurationException {
		// test
		assertFalse(xfilebusiness.isXbrlDoc(null));
	}

	@Test
	public void isXbrlDoc_ifInvalidXMLDoc() throws ParserConfigurationException, SAXException, IOException {
		// test
		assertFalse(xfilebusiness.isXbrlDoc(invalidFile));
	}
	
	/*
	 * testing setFileAs =============================================
	 */
	@Test
	public void setFileAs_ifXbrlDoc() throws SAXException, IOException {
		assertNotNull(xfilebusiness.getFileAsDocument());
	}

	@Test
	public void setFileAs_ifNotXbrl() throws SAXException, IOException, ParserConfigurationException {
		XbrlFileBusiness tempFileBusiness = new XbrlFileBusiness();
		tempFileBusiness.setFileAs(notXbrlUri);
		assertNull(tempFileBusiness.getFileAsDocument());
	}
	
	
	/*
	 * testing setMultipartFile()
	 */

	@Test 
	public void setFileAs_MultipartFile() {


	}
	
}
