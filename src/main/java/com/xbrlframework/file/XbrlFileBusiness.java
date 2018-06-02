package com.xbrlframework.file;

//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.web.multipart.MultipartFile;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.xbrlframework.instance.Context;
import com.xbrlframework.instance.Dts;
import com.xbrlframework.instance.Fact;
import com.xbrlframework.instance.Footnote;
import com.xbrlframework.instance.Instance;
import com.xbrlframework.instance.PeriodForever;
import com.xbrlframework.instance.PeriodInstant;
import com.xbrlframework.instance.PeriodStartEnd;
import com.xbrlframework.instance.Prefix;
import com.xbrlframework.instance.Unit;

public class XbrlFileBusiness {
	
	 private XbrlFile xfile;
	 
	 public XbrlFileBusiness() {
		 xfile = new XbrlFile();
	 }
	
	/**
	 * <p>
	 * <b>isXbrlDoc</b>
	 * </p>
	 * <p>
	 * Inform if file is a XBRL document.
	 * </p>
	 * 
	 * @return boolean
	 * @param Document
	 */
	public boolean isXbrlDoc(Document file) {
		if (file == null) {
			return false;
		} else if (file.getDocumentElement().getNodeName().toLowerCase().contains("xbrl")) {
			return true;
		} else {
			return false;
		}
	}
	 
	/**
	 * <p>
	 * <b>setFileAs</b>
	 * </p>
	 * <p>
	 * Set a multipartfile file (from apirest) into XbrlFile object. 
	 * The multipartfile must be a xbrl document.
	 * </p>
	 * 
	 * @param MultipartFile
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws Exception
	 */
	public void setFileAs(MultipartFile file) throws IOException, ParserConfigurationException, SAXException{
		if (file != null && !file.isEmpty()) {

			InputStream fileInputStream = file.getInputStream();
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.parse(fileInputStream);
			if (this.isXbrlDoc(doc)) {
				xfile.setName(file.getOriginalFilename());
				xfile.setSize(file.getSize());
				xfile.setDocumentFile(doc);
			}else {
				xfile = null;
			}
		}else {
			xfile = null;
		}
	}
	
	/**
	 * <p><b>setFileAs</b></p>
     * <p>Set a org.w3c.Document file into XbrlFile object.</p>
     * 
     * @param Document
     */
	private void setFileAs(Document file) {
		if (file != null) {
			if (this.isXbrlDoc(file)) {
				xfile.setDocumentFile(file);
			}else {
				xfile = null;
			}
		}else {
			xfile = null;
		}
	}
	
	/**
	 * <p><b>setFileAs</b></p>
     * <p>Set a org.w3c.Document file into XbrlFile object.</p>
     * 
     * @param Uri
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
     */
	public void setFileAs(String uri) throws ParserConfigurationException, SAXException, IOException {
		if (uri != null) {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.setFileAs(documentBuilder.parse(uri));
		}else {
			xfile = null;
		}
	}
	
	
	public Document getFileAsDocument() {
		if (xfile != null) {
			return xfile.getDocumentFile();
		}
		return null;
	}
	
	private StringBuilder printFact(StringBuilder json, Fact fact, Instance instance) {
		
		json.append("		{ \n"); //open fact
		if (fact.getId() != null && !fact.getId().isEmpty())
			json.append("			\"id\":\""+fact.getId()+"\", \n");
		if (fact.getValue() != null && !fact.getValue().isEmpty())
			json.append("			\"value\":\""+fact.getValue()+"\", \n");
		json.append("			\"aspect\": { \n");
		json.append("				\"xbrl:concept\":\""+fact.getName()+"\", \n");
		
		//context
		if (instance.getContextMap() != null) {
			xfile.setContextNumber(instance.getContextMap().size());
			// -- entity
			Map<String,Context> contextMap = instance.getContextMap();
			Context context = contextMap.values().stream()
					.filter(c -> c.getId().toLowerCase().contains(fact.getContextRef().toLowerCase()))
					.findFirst().get(); 
			json.append("				\"xbrl:entity\":\""+context.getEntity().getCid()+"\", \n");
			// -- period					
			if (context.getPeriod() instanceof PeriodInstant) {
				PeriodInstant period = (PeriodInstant) context.getPeriod();
				json.append("				\"xbrl:periodInstant\":\""+period.getInstantPeriodvalue()+"\", \n");
			}else if (context.getPeriod() instanceof PeriodStartEnd) {
				PeriodStartEnd period = (PeriodStartEnd) context.getPeriod();
				json.append("				\"xbrl:periodStart\":\""+period.getStartValue()+"\", \n");
				json.append("				\"xbrl:periodEnd\":\""+period.getEndValue()+"\", \n");
			}else {
				PeriodForever period = (PeriodForever) context.getPeriod();
				json.append("				\""+period.getValue()+"\", \n");
			}
		}
		// -- unit
		/*
		Optional<Unit> optUnit = null;
		try {
			optUnit = instance.getUnitMap().values().stream()
				.filter(u -> u.getId().toLowerCase().contains(fact.getUnitRef().toLowerCase()))
				.findFirst();
		}catch(Exception e) {
		}
		if (optUnit != null && optUnit.isPresent()) {
			Unit unit = optUnit.get();
			json.append("				\"xbrl:unit\":\""+unit.getValue()+"\" \n");
		}
		*/
		if (instance.getUnitMap() != null) {
			xfile.setUnitNumber(instance.getUnitMap().size());
			Unit unit = instance.getUnitMap().get(fact.getUnitRef());
			if (unit != null) {
				json.append("				\"xbrl:unit\":\""+unit.getValue()+"\" \n");
			}
			if (json.toString().trim().charAt(json.toString().trim().length()-1) == ',' ) {
				json.deleteCharAt(json.toString().trim().length()-1);
			}
		}
		json.append("			}, \n"); //closed aspect
		
		
		//footnote
		if (instance.getFootnoteMap() != null && instance.getFootnoteMap().size() > 0) {
			xfile.setFootnoteNumber(instance.getFootnoteMap().size());
			Footnote footnote = instance.getFootnoteMap().get("#" + fact.getId());
			if (footnote != null) {
				json.append("			\"footnote\": { \n");
				json.append("				\"group\":\"" + footnote.getGroup() + "\", \n");
				json.append("				\"footnoteType\":\"" + footnote.getFootnoteType() + "\", \n");
				json.append("				\"footnote\":\"" + footnote.getFootnote() + "\", \n");
				json.append("				\"language\":\"" + footnote.getLanguage() + "\" \n");
				json.append("			} \n");
			}
		}
		
		if (json.toString().trim().charAt(json.toString().trim().length()-1) == ',' ) {
			json.deleteCharAt(json.toString().trim().length()-1);
		}
		json.append("		}, \n"); //close fact
		return json;
	}
	
	private void printFacts(StringBuilder json, Instance instance){
		if (instance.getFactList() != null) {
			xfile.setFactNumber(instance.getFactList().size());
			json.append("	\"fact\" : [ \n");
			
			Thread printFactThread = null;
			List<Thread> threads = new ArrayList<>();
			for (Fact fact: instance.getFactList()) {
				Runnable printFactRun = () -> {
					synchronized (json) {
						this.printFact(json, fact, instance);
					}
				};
				printFactThread = new Thread(printFactRun);
				threads.add(printFactThread);
				printFactThread.start();
				
				//Thread limitations on Heroku free account (i.e. < 256)
				if (threads.size() == 250) {
					Iterator<Thread> tempThreads = threads.iterator();
					while (tempThreads.hasNext()) {
						Thread t = tempThreads.next();
						while (t.isAlive()) {
							//do noting, just waiting to finish...
						}
						tempThreads.remove();
					}
					threads = new ArrayList<>();
				}
				
			}
			if (threads.size() > 0) {
				for (Thread t: threads) {
					while (t.isAlive()) {
						//do nothing, just waiting...
					}
					t.interrupt();
					t = null;
				}
			}
			
			json.deleteCharAt(json.toString().trim().length()-1);  //delete last "," of object
			json.append("	] \n"); //closed fact
		}
	}
	
	private void printPrefixes(StringBuilder json, Instance instance) {
		if (instance.getPrefixList() != null) {
			xfile.setPrefixNumber(instance.getPrefixList().size());
			Optional<Prefix> optXbrliPrefix = instance.getPrefixList().stream()
					.filter(p -> p.getName().equals("xbrli"))
					.findFirst();
			if (!optXbrliPrefix.isPresent()) {
				instance.getPrefixList().add(new Prefix("xbrli", "http://www.xbrl.org/2003/instance"));
			}
			
			Optional<Prefix> optXbrlPrefix = instance.getPrefixList().stream()
					.filter(p -> p.getName().equals("xbrl"))
					.findFirst();
			if (optXbrlPrefix.isPresent()) {
				instance.getPrefixList().remove(optXbrlPrefix.get());
			}
			
			instance.getPrefixList().add(new Prefix("xbrl","http://www.xbrl.org/CR/2017-05-02/oim"));
			json.append("	\"prefix\" : { \n");
			for (Prefix prefix: instance.getPrefixList()) {
				json.append("		\""+prefix.getName()+"\":\""+prefix.getValue()+"\", \n");
			}
			json.deleteCharAt(json.toString().trim().length()-1);  //delete last "," of object
			json.append("	}, \n");
		}
	}
	
	private void printDtses(StringBuilder json, Instance instance) {
		if (instance.getDtsList() != null) {
			xfile.setDtsNumber(instance.getDtsList().size());
			json.append("	\"dts\" : { \n");
			for (Dts dts: instance.getDtsList()) {
				json.append("		\""+dts.getName()+"\":\""+dts.getHref()+"\", \n");
			}
			json.deleteCharAt(json.toString().trim().length()-1); //delete last "," of object
			json.append("	}, \n");
		}
	}
	
	public String parseToJson(Instance instance) {
		// report
		StringBuilder json = new StringBuilder("{ \n"); //root
		json.append(" \"report\" : { \n"); //start of report
		if (instance != null) {
			try {
				json.append("	\"documentType\":\""+instance.getDocumentType()+"\", \n");
				printPrefixes(json, instance);
				printDtses(json, instance);
				printFacts(json, instance);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		json.append(" } \n"); //end of report
		json.append("} \n"); //root

		/*
		final String data = json.toString();
		Runnable saveData = () -> { this.setFileWithJson(data); };
		new Thread(saveData).start();
		 */
		return json.toString().trim();
	}
	
	/*
	private void setFileWithJson(String json) {
		try {
			File file = new File("d:\\", "xbrlFile.json");
			if (!file.exists()){
				file.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream("d:/xbrlFile.json");
			OutputStreamWriter opw = new OutputStreamWriter(fos, "UTF-8");
			BufferedWriter bw = new BufferedWriter(opw);
			bw.write(json);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	
	public XbrlFile getXbrlFile() {
		return this.xfile;
	}
	
}
