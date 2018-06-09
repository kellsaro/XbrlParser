/*
 * created by Marcio Alexandre
 */
 
package com.xbrlframework.instance;

public class Entity {

	private String cid = "cid:";
	
	public Entity(String cid) {
		this.cid += cid;
	}
		
	public String getCid() {
		return this.cid;
	}
	
	
	
}
