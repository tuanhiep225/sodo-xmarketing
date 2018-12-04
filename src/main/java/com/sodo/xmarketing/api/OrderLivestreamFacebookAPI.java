/**
 * 
 */
package com.sodo.xmarketing.api;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import com.sodo.xmarketing.api.model.OrderLivestreamFacebook;
import com.sodo.xmarketing.api.model.ResponseModel;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.service.impl.FaqServiceImpl;
import com.sodo.xmarketing.utils.ClientHelperUtils;
import com.sodo.xmarketing.utils.GenerateValueIdentifier;
import com.sodo.xmarketing.utils.Properties;

/**
 * @author tuanhiep225
 *
 */
@Component
public class OrderLivestreamFacebookAPI {

	private Client client;
	
	private static final Log LOGGER = LogFactory.getLog(OrderLivestreamFacebookAPI.class);

	@Autowired
	private Properties properties;

	public OrderLivestreamFacebookAPI() throws SodException {
		client = ClientHelperUtils.createClient();
	}

	public ResponseModel orderLivestreamFacebook(OrderLivestreamFacebook entity) {
		try {
			WebTarget target = client.target(properties.getUrl());

			Form form = new Form();
			form.param("account_user", entity.getAccount_user());
			form.param("account_pass", entity.getAccount_pass());
			form.param("method", entity.getMethod());
			form.param("video_id", entity.getVideo_id());
			form.param("view", entity.getView().toString());
			form.param("minute", entity.getMinute().toString());
			LOGGER.info(entity.getAccount_user() + "-"+ entity.getAccount_pass()+ "-"+ entity.getVideo_id()+ "-" + properties.getUrl()+ "-" + properties.getMethod());
			Response response = target.request(MediaType.ALL_VALUE).post(Entity.form(form));
			return response.readEntity(ResponseModel.class);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return null;
		}
	}
	
	public String orderLivestreamFacebookV2(OrderLivestreamFacebook entity) {
		try {
			WebTarget target = client.target(properties.getUrlV2()).queryParam("id", entity.getVideo_id()).queryParam("eye", entity.getView());
			LOGGER.info("Đặt qua API V2: 'id'= "+ entity.getVideo_id()+ " 'eye' =" + entity.getView().toString());
			Response response = target.request(MediaType.ALL_VALUE).get();
			return response.readEntity(String.class);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return null;
		}
	}
}
