/*
 * created by Marcio Alexandre
 */
 
package com.xbrlframework.file;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xbrlframework.instance.InstanceBusiness;

@RestController
public class XbrlFileController {
	
	/*
	@PostMapping(value="/upload")
	public String loadXbrl(@RequestBody MultipartFile apifile) {
			long milli1 = System.currentTimeMillis();
			System.out.println("#### xbrlapi ####: [File name: "+apifile.getOriginalFilename()+", size: "+apifile.getSize()+"]");
			String message = "";
			if ((apifile.getOriginalFilename().contains(".xml") 
					|| apifile.getOriginalFilename().contains(".xbrl"))) 
			{
				if (!apifile.isEmpty() ) 
				{
					XbrlFileBusiness xfb = new XbrlFileBusiness();
					try {
						xfb.setFileAs(apifile);
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
				}else {
					System.out.println("{\"#### xbrlapi ####: [This file is empty]\"}");
					message = "{\"#### xbrlapi ####: [This file is empty]\"}";
				}
			}else {
				System.out.println("{\"#### xbrlapi ####: [It must be a XBRL or XML file]\"}");
				message = "{\"#### xbrlapi ####: [It must be a XBRL or XML file]\"}"; 
			}
			System.out.println("{\"#### xbrlapi ####: [file was NOT uploaded successfuly]\"}");
			long milli2 = System.currentTimeMillis();
			System.out.println("#### xbrlapi ####: [performance time:"+(milli2-milli1)+" milliseconds]");
			return message;
	}
	
	@PostMapping(value="/upload-uri")
	public String loadXbrl(@RequestBody String uri) {
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
	
	@PostMapping(value="/prefixes-file")
	public String getPrefixesFromFile(@RequestBody MultipartFile apifile) {
		long milli1 = System.currentTimeMillis();
		if (apifile != null) {
			System.out.println("#### xbrlapi ####: [File name: "+apifile.getOriginalFilename()+", size: "+apifile.getSize()+"]");
			String message = "";
			if ((apifile.getOriginalFilename().contains(".xml") 
					|| apifile.getOriginalFilename().contains(".xbrl"))) 
			{
				if (!apifile.isEmpty() ) 
				{
					XbrlFileBusiness xfb = new XbrlFileBusiness();
					try {
						xfb.setFileAs(apifile);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (xfb.getFileAsDocument() != null) {
						InstanceBusiness ib = new InstanceBusiness();
						ib.setRootNodeFrom(xfb.getFileAsDocument());
						ib.putPrefixes();
						String jsonReport = xfb.getJustPrefixes(ib.getInstance());
						long milli2 = System.currentTimeMillis();
						System.out.println("#### xbrlapi ####: [performance time: "+(milli2-milli1)+" milliseconds]");
						return jsonReport;
					}
				}else {
					System.out.println("{\"#### xbrlapi ####: [This file is empty]\"}");
					message = "{\"#### xbrlapi ####: [This file is empty]\"}";
				}
			}else {
				System.out.println("{\"#### xbrlapi ####: [It must be a XBRL or XML file]\"}");
				message = "{\"#### xbrlapi ####: [It must be a XBRL or XML file]\"}"; 
			}
			System.out.println("{\"#### xbrlapi ####: [file was NOT uploaded successfuly]\"}");
			long milli2 = System.currentTimeMillis();
			System.out.println("#### xbrlapi ####: [performance time:"+(milli2-milli1)+" milliseconds]");
			System.out.println(message);
			return message;
		}else {
			System.out.println("file is null");
			return "{ \"file has not been loaded\" }";
		}
	}
	
	
	@PostMapping(value="/dts-file")
	public String getDtsFromFile(@RequestBody MultipartFile apifile) {
		long milli1 = System.currentTimeMillis();
		if (apifile != null) {
			System.out.println("#### xbrlapi ####: [File name: "+apifile.getOriginalFilename()+", size: "+apifile.getSize()+"]");
			String message = "";
			if ((apifile.getOriginalFilename().contains(".xml") 
					|| apifile.getOriginalFilename().contains(".xbrl"))) 
			{
				if (!apifile.isEmpty() ) 
				{
					XbrlFileBusiness xfb = new XbrlFileBusiness();
					try {
						xfb.setFileAs(apifile);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (xfb.getFileAsDocument() != null) {
						InstanceBusiness ib = new InstanceBusiness();
						ib.setRootNodeFrom(xfb.getFileAsDocument());
						ib.putDtsList();
						String jsonReport = xfb.getJustDts(ib.getInstance());
						long milli2 = System.currentTimeMillis();
						System.out.println("#### xbrlapi ####: [performance time: "+(milli2-milli1)+" milliseconds]");
						return jsonReport;
					}
				}else {
					System.out.println("{\"#### xbrlapi ####: [This file is empty]\"}");
					message = "{\"#### xbrlapi ####: [This file is empty]\"}";
				}
			}else {
				System.out.println("{\"#### xbrlapi ####: [It must be a XBRL or XML file]\"}");
				message = "{\"#### xbrlapi ####: [It must be a XBRL or XML file]\"}"; 
			}
			System.out.println("{\"#### xbrlapi ####: [file was NOT uploaded successfuly]\"}");
			long milli2 = System.currentTimeMillis();
			System.out.println("#### xbrlapi ####: [performance time:"+(milli2-milli1)+" milliseconds]");
			return message;
		}else {
			System.out.println("file is null");
			return "{ \"file has not been loaded\" }";
		}
	}
	
	@PostMapping(value="/facts-file")
	public String getFactsFromFile(@RequestBody MultipartFile apifile) {
		long milli1 = System.currentTimeMillis();
		if (apifile != null) {
			System.out.println("#### xbrlapi ####: [File name: "+apifile.getOriginalFilename()+", size: "+apifile.getSize()+"]");
			String message = "";
			if ((apifile.getOriginalFilename().contains(".xml") 
					|| apifile.getOriginalFilename().contains(".xbrl"))) 
			{
				if (!apifile.isEmpty() ) 
				{
					XbrlFileBusiness xfb = new XbrlFileBusiness();
					try {
						xfb.setFileAs(apifile);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (xfb.getFileAsDocument() != null) {
						InstanceBusiness ib = new InstanceBusiness();
						ib.setRootNodeFrom(xfb.getFileAsDocument());
						ib.putFacts();
						String jsonReport = xfb.getJustFacts(ib.getInstance());
						System.out.println("#### xbrlapi ####: [Contexts: "+xfb.getXbrlFile().getContextNumber()+", Units: "+xfb.getXbrlFile().getUnitNumber()+","
								+ "Facts: "+xfb.getXbrlFile().getFactNumber()+", Footnotes: "+xfb.getXbrlFile().getFootnoteNumber()+"]");
						long milli2 = System.currentTimeMillis();
						System.out.println("#### xbrlapi ####: [performance time: "+(milli2-milli1)+" milliseconds]");
						return jsonReport;
					}
				}else {
					System.out.println("{\"#### xbrlapi ####: [This file is empty]\"}");
					message = "{\"#### xbrlapi ####: [This file is empty]\"}";
				}
			}else {
				System.out.println("{\"#### xbrlapi ####: [It must be a XBRL or XML file]\"}");
				message = "{\"#### xbrlapi ####: [It must be a XBRL or XML file]\"}"; 
			}
			System.out.println("{\"#### xbrlapi ####: [file was NOT uploaded successfuly]\"}");
			long milli2 = System.currentTimeMillis();
			System.out.println("#### xbrlapi ####: [performance time:"+(milli2-milli1)+" milliseconds]");
			return message;
		}else {
			System.out.println("file is null");
			return "{ \"file has not been loaded\" }";
		}
	}
	
}
