/*
 * created by github.com/marcioAlexandre
 * Jun01, 2018
 * 
 */

package com.xbrlframework;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.*;

@SpringBootTest
public class HomeControllerTest {

	@Test
	public void testGoToIndex() {
		HomeController hc = new HomeController();
		assertEquals("index", hc.goToIndex());
	}
	
}
