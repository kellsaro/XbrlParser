/*
 * created by github.com/marcioAlexandre
 * Jun01, 2018
 * 
 */
 
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
import org.json.*;

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
	
	@Test
	public void buildJsonString() throws ParserConfigurationException, SAXException, IOException {
		xfilebusiness = new XbrlFileBusiness();
		//String xbrlUri = "https://www.sec.gov/Archives/edgar/data/1169567/000116956714000019/oxfo-20140930.xml";
		xfilebusiness.setFileAs(xbrlUri);
		
		InstanceBusiness ib = new InstanceBusiness();
		ib.setRootNodeFrom(xfilebusiness.getFileAsDocument());
		ib.build();
		String docType = "\"documentType\" : \"http://www.xbrl.org/CR/2017-05-02/xbrl-json\",\n";
		String prefixes = xfilebusiness.getJustPrefixes(ib.getInstance());
		String dts = xfilebusiness.getJustDts(ib.getInstance());
		String facts = xfilebusiness.getJustFacts(ib.getInstance());
		String f = "{\n" 
				+"  \"report\" : {\n"
				+"    "+docType
				+"    \"prefix\" : "+prefixes
				+",\n    \"dts\": "+dts
				+",\n    \"fact\" : "+facts
				+"  }\n"
				+"}";
		
		//xfilebusiness.saveStringInFile(f);
		
		assertTrue(this.isJSONValid(prefixes));
		assertTrue(this.isJSONValid(dts));
		assertTrue(this.isJSONValid(facts));
		assertTrue(this.isJSONValid(f));
		
	}
	
	private boolean isJSONValid(String test) {
	    try {
	        new JSONObject(test);
	    } catch (JSONException ex) {
	        try {
	            new JSONArray(test);
	        } catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}
	
	@Test
	public void setFileAs_validURI() throws ParserConfigurationException, SAXException, IOException {
		assertNotNull(xfilebusiness.getFileAsDocument());
		assertTrue(xfilebusiness.getFileAsDocument().getDocumentElement()
				.getTagName().toLowerCase().contains("xbrl"));
	}
	

	@Test
	public void isXbrlDoc_ifTrue() throws SAXException, IOException {
		assertTrue(xfilebusiness
				.isXbrlDoc(xfilebusiness.getFileAsDocument()));
	}

	@Test
	public void isXbrlDoc_ifEmpty() throws ParserConfigurationException {
		assertFalse(xfilebusiness.isXbrlDoc(null));
	}

	@Test
	public void isXbrlDoc_ifInvalidXMLDoc() throws ParserConfigurationException, SAXException, IOException {
		assertFalse(xfilebusiness.isXbrlDoc(invalidFile));
	}
	
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

	@Test 
	public void setFileAs_MultipartFile() {


	}
	
}
