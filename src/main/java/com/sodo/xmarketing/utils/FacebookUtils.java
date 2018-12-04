/**
 * 
 */
package com.sodo.xmarketing.utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class FacebookUtils {
	private static final Log LOGGER = LogFactory.getLog(FacebookUtils.class);
	
	public static String getVideoId(String url) {
		  String rs1= "";
	      String rs2 ="";
	      String pattern = "(videos\\/(\\d+)|story_fbid=(\\d+))";
	      Pattern r = Pattern.compile(pattern);
	      Matcher m = r.matcher(url);
	      while (m.find()) {
	    	  rs1= m.group();
	    	  String regex2= "\\d+";
	    	  Pattern r1 = Pattern.compile(regex2);
	    	  Matcher m1 = r1.matcher(rs1);
	    	  rs2 =m1.find() ? m1.group(): "";
	    	  LOGGER.info("Found value1: " + rs1 );
	    	  LOGGER.info("Found value2: " + rs2 );
	      }
	      return rs2;
	      
	}
	
	public static Boolean checkIDVideo(String id) {
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(id);
		LOGGER.info(m.matches());
		return m.matches();
	}
}
