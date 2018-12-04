/**
 * 
 */
package com.sodo.xmarketing.repository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sodo.xmarketing.utils.FacebookUtils;

/**
 * @author tuanhiep225
 *
 */
@RunWith(SpringRunner.class)
public class ServicePriceRepositoryTest {
	private static final Log LOGGER = LogFactory.getLog(ServicePriceRepositoryTest.class);
	FacebookUtils util = new FacebookUtils();
	
	@Test
	public void test() {
		System.out.println(util.getVideoId("https://m.facebook.com/story.php?story_fbid=128612354765723&id=100028508779690"));
//		System.out.println(util.checkIDVideo("128612354765723"));
	}
}
