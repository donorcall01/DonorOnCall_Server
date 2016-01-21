package org.doc.donoroncall.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.doc.core.api.registration.handler.LoginHandler;
import org.doc.core.api.registration.handler.RegisterationHandler;
import org.doc.core.api.registration.info.LoginInfo;
import org.doc.core.api.registration.info.RegistrationInfo;
import org.doc.donoroncall.doc.info.DocDonorInfo;
import org.doc.donoroncall.doc.info.DocRequesterInfo;
import org.doc.donoroncall.donar.DocRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

@Controller
public class GatewayController {
	@Autowired
	private LoginHandler loginHand;
	@Autowired
	RegisterationHandler regHand;
	@Autowired
	DocRequestHandler drHandler;
	private static final String clz = "GatewayController: ";

	private final Logger logger = LoggerFactory.getLogger(GatewayController.class);

	public GatewayController() {
		
	}

	@RequestMapping(value = "doc/gateWay", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	<T> String getOAuth(@RequestBody String inputAsJson) {
		logger.info(clz + "getOAuth GET start.");
		Gson gson=new Gson();
		LoginInfo loginInfo = gson.fromJson(inputAsJson, LoginInfo.class);
		RegistrationInfo regInfo = loginHand.loginAuthenticate(loginInfo);
		Map<String, T> obj = new HashMap<String,T>();
		if(regInfo!=null){		
			obj.put("success", (T) "true");			
			/*String uuid = UUID.randomUUID().toString()+loginInfo.getPassword();
			byte [] tokenByte = new Base64(true).encodeBase64(uuid.getBytes());
			String token = new String(tokenByte);
			obj.put("accessToken", token);*/
			obj.put("user", (T) regInfo);
			logger.info(clz + "getOAuth GET end."+loginInfo.getUserName());		
		}else{
			obj.put("success", (T)"false");
			obj.put("user", (T)"null");		
			logger.info(clz + "getOAuth GET end."+loginInfo.getUserName());
		}
		return gson.toJson(obj);
	}
	@RequestMapping(value = "doc/register", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	String register(@RequestBody String inputAsJson) {
		try{
		logger.info(GatewayController.class+"  Input String "+inputAsJson);	
		Gson gson=new Gson();
		RegistrationInfo regInfo = gson.fromJson(inputAsJson, RegistrationInfo.class);
		String resString = regHand.registerAccount(regInfo);		
		Map<String, String> obj = new HashMap<String,String>();
		if(resString.equalsIgnoreCase("success")){
		obj.put("registration", "success");
		obj.put("status", "pending");
		}else{
			obj.put("registration", "fail");
			obj.put("status", "unkwnon");
		}		
		return gson.toJson(obj);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping(value = "doc/requester", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	String requesterRegister(@RequestBody String inputAsJson) {
		try{
		Gson gson=new Gson();
		DocRequesterInfo drInfo = gson.fromJson(inputAsJson, DocRequesterInfo.class);
		String resString = drHandler.docRequest(drInfo);
		Map<String, String> obj = new HashMap<String,String>();
		if(resString.equalsIgnoreCase("success")){
		obj.put("registration", "success");
		obj.put("status", "pending");
		}else{
			obj.put("registration", "fail");
			obj.put("status", "unkwnon");
		}		
		return gson.toJson(obj);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping(value = "doc/donor", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	String donorRegister(@RequestBody String inputAsJson) {
		try{
		Gson gson=new Gson();
		DocDonorInfo drInfo = gson.fromJson(inputAsJson, DocDonorInfo.class);
		String resString = drHandler.docDonorRequest(drInfo);
		Map<String, String> obj = new HashMap<String,String>();
		if(resString.equalsIgnoreCase("success")){
		obj.put("registration", "success");
		obj.put("status", "pending");
		}else{
			obj.put("registration", "fail");
			obj.put("status", "unkwnon");
		}		
		return gson.toJson(obj);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
