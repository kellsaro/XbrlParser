/*
 * created by github.com/marcioAlexandre
 * Jun01, 2018
 * 
 */
 
package com.xbrlframework.instance;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.xbrlframework.file.XbrlFileBusiness;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@SpringBootTest
public class InstanceBusinessTest {
	
	static InstanceBusiness ib = new InstanceBusiness();
	static DocumentBuilder documentBuilder;
	static XbrlFileBusiness xfilebusiness = new XbrlFileBusiness();
	static Document validFile;
	static Document invalidFile;
	
	static String notXbrlUri = "https://www.sec.gov/Archives/edgar/data/"
			+ "320193/000032019318000070/aapl-20180331.xsd";
	
	static String xbrlUri = "https://www.sec.gov/Archives/edgar/data/"
			+ "320193/000032019318000070/aapl-20180331.xml";
	
	
	@BeforeClass
	public static void getFileFromInternet() throws SAXException, IOException, ParserConfigurationException {
		documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		xfilebusiness.setFileAs(xbrlUri);
		invalidFile = documentBuilder.parse(notXbrlUri);
	}
	
	@Before
	public void instacianting() throws ParserConfigurationException {
		ib = new InstanceBusiness();
		ib.setRootNodeFrom(xfilebusiness.getFileAsDocument());
	}
	
	@Test
	public void putDtsList_AppleFile() {
		ib.putDtsList();
		assertNotNull(ib.getInstance().getDtsList());
		assertTrue(ib.getInstance().getDtsList().size() > 0);
	}
	
	@Test
	public void putDtsList_NullFile() {
		InstanceBusiness ib = new InstanceBusiness();
		ib.setRootNodeFrom(null);
		ib.putDtsList();
		assertNull(ib.getInstance().getDtsList());
	}
	
	@Test
	public void putDtsList_MicrosoftFile() throws SAXException, IOException {
		String xbrlUri = "https://sec.gov/Archives/edgar/data/789019/000156459018009307/msft-20180331.xml";
		ib.setRootNodeFrom(documentBuilder.parse(xbrlUri));
		ib.putDtsList();
		assertNotNull(ib.getInstance().getDtsList());
		assertTrue(ib.getInstance().getDtsList().size() > 0);
		assertTrue(ib.getInstance().getDtsList().get(0).getName().equals("schema"));
		assertTrue(ib.getInstance().getDtsList().get(0).getHref().equals("msft-20180331.xsd"));
	}

	public void getFacts_AppleFile() {
		assertTrue(ib.getFacts().size() > 1000);
	}
	
	@Test
	public void getUnits_AppleFile() {
		ib.putUnits();
		assertTrue(ib.getInstance().getUnitMap().size() > 0);
		assertEquals("unitNumerator:iso4217:USD/unitDenominator:xbrli:shares",
				ib.getInstance().getUnitMap().get("usdPerShare").getValue()	);
	}
	
	@Test
	public void getUnits_MicrosoftFile() throws SAXException, IOException {
		String xbrlUri = "https://www.sec.gov/Archives/edgar/data/789019/000156459018009307/msft-20180331.xml";
		InstanceBusiness ib = new InstanceBusiness();
		ib.setRootNodeFrom(documentBuilder.parse(xbrlUri));
		ib.putUnits();
		assertTrue(ib.getInstance().getUnitMap().size() > 0);
		assertEquals("iso4217:USD",
				ib.getInstance().getUnitMap().get("U_iso4217USD").getValue() );
		
		assertEquals("unitNumerator:iso4217:USD/unitDenominator:shares",
				ib.getInstance().getUnitMap().get("U_iso4217USD_shares").getValue()	);
	}
	
	
	@Test
	public void getUnits_FacebookFile() throws SAXException, IOException {
		String xbrlUri = "https://www.sec.gov/Archives/edgar/data/" 
					+ "1326801/000132680118000032/fb-20180331.xml";
		Document file = documentBuilder.parse(xbrlUri);
		ib.setRootNodeFrom(file);
		ib.putUnits();
		assertTrue(ib.getInstance().getUnitMap().size() > 0);
		assertEquals("fb:plan", 
				ib.getInstance().getUnitMap().get("plan").getValue());

		assertEquals("unitNumerator:iso4217:USD/unitDenominator:xbrli:shares",
				ib.getInstance().getUnitMap().get("usdPerShare").getValue());
	}

	@Test
	public void putContexts_AppleFile() throws Exception {
		ib.putContexts();
		assertTrue(ib.getInstance().getContextMap().size() > 0);
		
		
		assertFalse(ib.getInstance().getContextMap()
				.get("FD2017Q2QTD_us-gaap_IncomeStatementLocationAxis_us-gaap_ResearchAndDevelopmentExpenseMember")
				.getPeriod() instanceof PeriodInstant);
		
		assertTrue(ib.getInstance().getContextMap()
				.get("FD2017Q2QTD_us-gaap_IncomeStatementLocationAxis_us-gaap_ResearchAndDevelopmentExpenseMember")
				.getPeriod() instanceof PeriodStartEnd);
		
		assertEquals("2017-04-01",
				((PeriodStartEnd) ib.getInstance().getContextMap()
				.get("FD2017Q2QTD_us-gaap_IncomeStatementLocationAxis_us-gaap_ResearchAndDevelopmentExpenseMember")
				.getPeriod()).getEndValue()
			);
		
		assertTrue(
				ib.getInstance().getContextMap()
				.get("FI2017Q4_us-gaap_BalanceSheetLocationAxis_us-gaap_OtherAssetsMember_us-gaap_DerivativeInstrumentRiskAxis_us-gaap_ForeignExchangeContractMember_us-gaap_FairValueByFairValueHierarchyLevelAxis_us-gaap_FairValueInputsLevel2Member_us-gaap_HedgingDesignationAxis_us-gaap_NondesignatedMember")
				.getEntity().getCid().contains("0000320193")
			);
			
	}

	@Test
	public void getContexts_MicrosoftFile() throws Exception {
		String xbrlUri = "https://www.sec.gov/Archives/edgar/data/789019/000156459018009307/msft-20180331.xml";
		ib.setRootNodeFrom(documentBuilder.parse(xbrlUri));
		ib.putContexts();
		assertTrue(ib.getInstance().getContextMap().size() > 0);
		
		
		assertTrue(ib.getInstance().getContextMap()
				.get("C_0000789019_us-gaapStatementEquityComponentsAxis_us-gaapRetainedEarningsMember_20171231")
				.getPeriod() instanceof PeriodInstant);
		
		assertFalse(ib.getInstance().getContextMap()
				.get("C_0000789019_us-gaapStatementEquityComponentsAxis_us-gaapRetainedEarningsMember_20171231")
				.getPeriod() instanceof PeriodStartEnd);
		
		assertEquals("2017-12-31",
				((PeriodInstant) ib.getInstance().getContextMap()
				.get("C_0000789019_us-gaapStatementEquityComponentsAxis_us-gaapRetainedEarningsMember_20171231")
				.getPeriod()).getInstantPeriodvalue()
			);
		
		
		assertTrue(
				ib.getInstance().getContextMap()
				.get("C_0000789019_msftProductsOrServicesSecondaryCategorizationAxis_msftCommercialCloudMember_20160701_20170331")
				.getEntity().getCid().contains("0000789019")
			);
		
	}	
	
	@Test
	public void getContexts_IfFileNull() throws Exception {
		ib.setRootNodeFrom(null);
		ib.putContexts();
		assertNull(ib.getInstance().getContextMap());

	}
	
	@Test
	public void getSpecificAttributeValue_ifNotNull() throws SAXException, IOException {
		assertNull(ib.getSpecificAttributeValue(ib.getSpecificNode(ib.getNodeChildrenFrom(ib.getRootNode()), "ref"), "scheme"));
		assertEquals("aapl-20180331.xsd", ib.getSpecificAttributeValue(ib.getSpecificNode(ib.getNodeChildrenFrom(ib.getRootNode()), "ref"), "href"));
	
	}

	@Test
	public void getRootNode_ifThereIsFile() throws SAXException, IOException {
		assertNotNull(ib.getRootNode());
		assertTrue(ib.getRootNode().getNodeName().toLowerCase().contains("xbrl"));
	}
	
	@Test
	public void getRootNode_ifFileNull() throws SAXException, IOException {
		ib.setRootNodeFrom(null);
		assertNull(ib.getRootNode());
	}
	
	@Test
	public void putPrefixes_AppleFile() throws SAXException, IOException {
		ib.putPrefixes();
		assertEquals(24.0, ib.getInstance().getPrefixList().size(), 0.01);

	}
	
	@Test
	public void putPrefixes_NullFile() {
		ib.setRootNodeFrom(null);
		ib.putPrefixes();
		assertEquals(0.0, ib.getInstance().getPrefixList().size(), 0.01);
	}
		
	@Test
	public void setDtsList_AppleFile() throws SAXException, IOException {
		assertTrue(ib.getDtsList(ib.getRootNode()).size() > 0.0);
	}
	
	@Test
	public void setDtsListInInstance_FileNull() throws SAXException, IOException {
		ib.setRootNodeFrom(null);
		assertNull(ib.getDtsList(ib.getRootNode()));
	}
	
	@Test
	public void getSpecificNodeList_ifMany() throws SAXException, IOException {
    	assertNotNull(ib.getSpecificNodeList(ib.getRootNode().getChildNodes(), "context"));
    	assertTrue(ib.getSpecificNodeList(ib.getRootNode().getChildNodes(), "marcio.alexandre83").size() == 0);
    	assertTrue(ib.getSpecificNodeList(ib.getRootNode().getChildNodes(), "context").size() > 0);
	}
	
	@Test
    public void getSpecificNode_ifFileNotNull() throws SAXException, IOException {
    	assertNotNull(ib.getSpecificNode(ib.getRootNode().getChildNodes(), "context"));
    	assertNull(ib.getSpecificNode(ib.getRootNode().getChildNodes(), "marcio.alexandre83"));
    	assertNull(ib.getSpecificNode(ib.getRootNode().getChildNodes(), null));
    	assertTrue(ib.getSpecificNode(ib.getRootNode().getChildNodes(), "context").getNodeName().toLowerCase().contains("context"));
    }
    
	@Test
    public void getSpecificNode_ifFileNull() {
    	assertNull(ib.getSpecificNode(null, "context"));
    	assertNull(ib.getSpecificNode(null, "marcio"));
    	assertNull(ib.getSpecificNode(null, null));
    }
	
    @Test
    public void getRefType_ifSchema() {
    	String ref = "xbrli:schemaRef";
    	assertEquals("schema", ib.getRefType(ref));
    }
    
    @Test
    public void getRefType_ifLinkbase() {
    	String ref = "xbrli:linkbaseRef";
    	assertEquals("linkbase", ib.getRefType(ref));
    }

    @Test
    public void getRefType_ifRole() {
    	String ref = "xbrli:roleRef";
    	assertEquals("role", ib.getRefType(ref));
    }
    
    @Test
    public void getRefType_ifArcRole() {
    	String ref = "xbrli:arcroleRef";
    	assertEquals("arcrole", ib.getRefType(ref));
    }
    
    @Test
    public void getRefType_ifNull() {
    	String ref = "xbrli:anyanotherthing";
    	assertNull(ib.getRefType(ref));
    }
    
	@After
	public void closing() {
		ib = null;
	}
	
	@AfterClass
	public static void close() {
		documentBuilder = null;
		validFile = null;
		invalidFile = null;
	}
}
