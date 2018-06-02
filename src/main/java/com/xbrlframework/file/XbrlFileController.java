package com.xbrlframework.file;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xbrlframework.instance.InstanceBusiness;

@RestController
public class XbrlFileController {
	
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value="/upload")
	public String loadXbrl(@RequestBody MultipartFile apifile) {
			System.out.println("xbrlapi: ["+LocalDateTime.now()+"] File name: "+apifile.getOriginalFilename()+", size: "+apifile.getSize());
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
						System.out.println("xbrlapi: ["+LocalDateTime.now()+"] Contexts: "+xfb.getXbrlFile().getContextNumber()+", Units: "+xfb.getXbrlFile().getUnitNumber()+","
								+ "Facts: "+xfb.getXbrlFile().getFactNumber()+", Footnotes: "+xfb.getXbrlFile().getFootnoteNumber()+".");
						return jsonReport;
					}
				}else {
					System.out.println("empty file");
					message = "This file is empty\n";
				}
			}else {
				System.out.println("It must be a XBRL or XML file");
				message = "It must be a XBRL or XML file\n"; 
			}
			System.out.println("file was NOT uploaded successfuly");
			message += "file was NOT uploaded successfuly\n";
			return message;
	}
	
	@PostMapping(value="/upload-uri")
	public String loadXbrl(@RequestBody String uri) {
		try {
			if (uri != null && !uri.trim().isEmpty()) {
				XbrlFileBusiness xFileBusiness = new XbrlFileBusiness();
				xFileBusiness.setFileAs(uri);
				if (xFileBusiness.getFileAsDocument() != null) {
					return "file was uploaded successfuly";
				}
			}
			return "file was NOT uploaded successfuly";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	
}
