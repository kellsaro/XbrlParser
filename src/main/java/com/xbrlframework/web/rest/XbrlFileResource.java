/*
 * created by github.com/marcioAlexandre
 * Jun01, 2018
 * 
 */
 
package com.xbrlframework.web.rest;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import io.xbrl.domain.service.XbrlParser;
import io.xbrl.domain.service.InstanceBusiness;

import static io.xbrl.domain.service.XbrlParser.getPreload;

@RestController
@RequestMapping("/rest")
public class XbrlFileResource {

	/**
	 * pre-load a XBRL instance by a given MultipartFile object
	 * 
	 * @param file
	 * @return
	 */
	@PostMapping(value="/preload-file")
	public String preloadFile(@RequestBody MultipartFile file) {
		
		System.out.println("/rest/preload-file -> preloadFile");
		
		long milli1 = System.currentTimeMillis();

		String result = "";
		
		if(file == null) {
			
			result = "{ \"file has not been loaded\"}";
			System.out.println(result);
		}
		else {
			
			System.out.println(String.format("## xbrl-parser | preloadFile ##: [File name: %s, size: %s]", file.getOriginalFilename(), file.getSize()));
			
			if ((file.getOriginalFilename().contains(".xml") 
				|| file.getOriginalFilename().contains(".xbrl"))) {
				
				if (!file.isEmpty()) {
					
					XbrlParser xbrlParser = null;
					
					try(InputStream fileInputStream = file.getInputStream()) {
						
						DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					
						Document doc = documentBuilder.parse(fileInputStream);
					
						xbrlParser = new XbrlParser(doc, file.getOriginalFilename(), file.getSize());
					
						InstanceBusiness ib = new InstanceBusiness();
						ib.setRootNodeFrom(xbrlParser.getXbrlFileAsDocument());
						ib.putPrefixes();
						ib.putDtsList();
						ib.putFacts();
						
						result = getPreload(ib.getInstance());
						
						long milli2 = System.currentTimeMillis();
						System.out.println(String.format("## xbrl-parser | preloadFile ##: [performance time: %s milliseconds]", (milli2-milli1)));
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
					
					return result;
				}
				else {
					
					System.out.println("{\"## xbrl-parser | preloadFile ##: [This file is empty]\"}");
				}
			}
			else {
				
				System.out.println("{\"## xbrl-parser | preloadFile ##: [It must be a XBRL or XML file]\"}");
			}
			
			result = "{\"## xbrl-parser | preloadFile ##: [file was NOT preloaded successfully]\"}";
			System.out.println(result);
			
			long milli2 = System.currentTimeMillis();
			System.out.println(String.format("## xbrl-parser | preloadFile ##: [performance time: %s milliseconds]", (milli2-milli1)));
		}
		
		return result;
	}	
	
	/**
	 * load a XBRL instance by a given MultipartFile object
	 * 
	 * @param file
	 * @return
	 */
	@PostMapping(value="/upload-file")
	public String uploadFile(@RequestBody MultipartFile file) {
		
		System.out.println("/rest/upload-file -> uploadFile");
		
		long milli1 = System.currentTimeMillis();

		String result = "";
		
		if(file == null) {
			
			result = "{ \"file has not been loaded\"}";
			System.out.println(result);
		}
		else {
			
			System.out.println(String.format("## xbrl-parser | uploadFile #: [File name: %s, size: %s]", file.getOriginalFilename(), file.getSize()));

			if ((file.getOriginalFilename().contains(".xml") 
					|| file.getOriginalFilename().contains(".xbrl"))) {
				
				if (!file.isEmpty()) {
					
					XbrlParser xbrlParser = null;
					
					try(InputStream fileInputStream = file.getInputStream()) {
					
						DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					
						Document doc = documentBuilder.parse(fileInputStream);
						
						xbrlParser = new XbrlParser(doc, file.getOriginalFilename(), file.getSize());
						
						result = xbrlParser.parseToJSON();
						
						long milli2 = System.currentTimeMillis();
						System.out.println(String.format("## xbrl-parser | uploadFile ##: [performance time: %s milliseconds]", (milli2-milli1)));
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
					
					return result;
				}
				else {
					
					System.out.println("{\"## xbrl-parser | uploadFile ##: [This file is empty]\"}");	
				}
			}
			else {
				
				System.out.println("{\"## xbrl-parser | uploadFile ##: [It must be a XBRL or XML file]\"}");
			}
			
			result = "{\"## xbrl-parser | uploadFile ##: [file was NOT uploaded successfully]\"}";
			System.out.println(result);
			
			long milli2 = System.currentTimeMillis();
			System.out.println(String.format("## xbrl-parser | uploadFile ##: [performance time: %s milliseconds]", (milli2-milli1)));
			
		}
		
		return result;
	}
		
	
	
	/**
	 * load a XBRL instance by a given URI
	 *  
	 * @param uri
	 * @return
	 */
	
	/* ARREGLAR!!
	@PostMapping(value="/upload-uri")
	public String uploadUri(@RequestBody String uri) {
		
		System.out.println("/rest/upload-file -> uploadUri");
		long milli1 = System.currentTimeMillis();
		System.out.println("#### xbrlapi ####: [url: "+uri+"]");
		
		try {
			if (uri != null && !uri.trim().isEmpty()) {
				
				XbrlFileBusiness xfb = new XbrlFileBusiness();
				try {
					
					
					if (uri != null) {
						DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
						this.setFileAs(documentBuilder.parse(uri));
					}else {
						xbrlFile = null;
					}
					
					
					
					
					xfb.setFileAs(uri);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (xfb.getFileAsDocument() != null) {
					InstanceBusiness ib = new InstanceBusiness();
					ib.setRootNodeFrom(xfb.getFileAsDocument());
					ib.build();
					String jsonReport = xfb.parseToJson(ib.getInstance());
					System.out.println("#### xbrlapi ####: [Contexts: "+xfb.getXbrlFile().getContextNumber()+", Units: "+xfb.getXbrlFile().getUnitNumber()+","
							+ "Facts: "+xfb.getXbrlFile().getFactNumber()+", Footnotes: "+xfb.getXbrlFile().getFootnoteNumber()+"]");
					long milli2 = System.currentTimeMillis();
					System.out.println("#### xbrlapi ####: [performance time: "+(milli2-milli1)+" milliseconds]");
					return jsonReport;
				}
			}
			
			return "{\"#### xbrlapi ####: [file has NOT been processed by server successfuly]\"}";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	*/
	
	/**
	 * pre-load a XBRL instance by a given URI
	 * 
	 * @param uri
	 * @return
	 */
	/*
	@PostMapping(value="/preload-uri")
	public String preloadUri(@RequestBody String uri) {
		long milli1 = System.currentTimeMillis();
		System.out.println("#### xbrlapi ####: [url: "+uri+"]");
		try {
			if (uri != null && !uri.trim().isEmpty()) {
				XbrlFileBusiness xfb = new XbrlFileBusiness();
				try {
					xfb.setFileAs(uri);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (xfb.getFileAsDocument() != null) {
					InstanceBusiness ib = new InstanceBusiness();
					ib.setRootNodeFrom(xfb.getFileAsDocument());
					ib.build();
					String jsonReport = xfb.getPreload(ib.getInstance());
					System.out.println("#### xbrlapi ####: [Contexts: "+xfb.getXbrlFile().getContextNumber()+", Units: "+xfb.getXbrlFile().getUnitNumber()+","
							+ "Facts: "+xfb.getXbrlFile().getFactNumber()+", Footnotes: "+xfb.getXbrlFile().getFootnoteNumber()+"]");
					long milli2 = System.currentTimeMillis();
					System.out.println("#### xbrlapi ####: [performance time: "+(milli2-milli1)+" milliseconds]");
					return jsonReport;
				}
			}
			return "{\"#### xbrlapi ####: [file has NOT been processed by server successfuly]\"}";
		} catch (Exception e) {
			return e.getMessage();
		}
	}*/
}
