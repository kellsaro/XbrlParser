/*
 * created by Marcio Alexandre
 */
 
package com.xbrlframework.instance;

public class Dts {
	
	private String name;
	private String href;
	
	public Dts() {
		
	}
	
	public Dts(String name, String href) {
		this.name = name;
		this.href = href;
	}
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	

}
