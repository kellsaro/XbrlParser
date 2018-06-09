/*
 * created by Marcio Alexandre
 */
 
package com.xbrlframework;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping("/")
	public String goToIndex() {
		return "index";
	}
	
}
