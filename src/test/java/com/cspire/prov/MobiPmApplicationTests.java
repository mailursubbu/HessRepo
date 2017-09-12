package com.cspire.prov;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;

import com.brm.HessRestApp;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HessRestApp.class)
@WebAppConfiguration
public class MobiPmApplicationTests {

	@Test
	public void contextLoads() {
	}

}
