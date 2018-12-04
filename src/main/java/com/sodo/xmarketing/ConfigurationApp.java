/**
 * 
 */
package com.sodo.xmarketing;
import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.Decimal128;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.lang.NonNull;

/**
 * @author tuanhiep225
 *
 */
@Configuration
public class ConfigurationApp {

	@Bean
	public MongoCustomConversions mongoCustomConversions() {
	    return new MongoCustomConversions(Arrays.asList(

	        new Converter<BigDecimal, Decimal128>() {

	            @Override
	            public Decimal128 convert(@NonNull BigDecimal source) {
	                return new Decimal128(source);
	            }
	        },

	        new Converter<Decimal128, BigDecimal>() {

	            @Override
	            public BigDecimal convert(@NonNull Decimal128 source) {
	                return source.bigDecimalValue();
	            }

	        }


	    ));

	}
}
