/**
 * 
 */
package org.doc.donoroncall.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.doc.donoroncall.doc.DocHandler;
import org.doc.donoroncall.doc.info.BloodDonationInfo;
import org.doc.donoroncall.doc.info.DocDonorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

/**
 * @author pandiyaraja
 *
 */
@Controller
@RequestMapping("/donor")
public class DocController {
	
	@Autowired
	private DocHandler docHandler;
	
	@RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody <T> String listDonors(){
		Gson gson = new Gson();
		List<DocDonorInfo> donorsList = docHandler.getAllDonors();
		Map<String, T> resMap = new HashMap<String, T>();
		if(!donorsList.isEmpty()){
			resMap.put("status", (T) "success");
			resMap.put("donors", (T) donorsList);
		}else{
			resMap.put("status", (T) "fail");
			resMap.put("donors", (T) donorsList);
		}
		return gson.toJson(resMap);
	}
	
	@RequestMapping(value = "/forrecipient", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody <T> String listDonorsforRecipient(@RequestBody String recipient){
		Gson gson = new Gson();
		List<DocDonorInfo> donorsList = docHandler.getDonorsForRecipient(gson.fromJson(recipient, DocDonorInfo.class));
		Map<String, T> resMap = new HashMap<String, T>();
		if(!donorsList.isEmpty()){
			resMap.put("status", (T) "success");
			resMap.put("donors", (T) donorsList);
		}else{
			resMap.put("status", (T) "fail");
			resMap.put("donors", (T) donorsList);
		}
		return gson.toJson(resMap);
	}
	
	@RequestMapping(value = "/selectDonor", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String selectDonor(@RequestBody String donorInfo){
		Gson gson = new Gson();
		BloodDonationInfo bloodDonationInfo = gson.fromJson(donorInfo, BloodDonationInfo.class);
		String resString = docHandler.selectDonors(bloodDonationInfo);
		Map<String, String> resMap = new HashMap<String, String>();
		if(resString.equalsIgnoreCase("success")){
			resMap.put("status", "success");
			resMap.put("request", "sent");
		}else{
			resMap.put("status", "fail");
			resMap.put("request", "droped");
		}
		return gson.toJson(resMap);
	}
	
	@RequestMapping(value = "/pendingRequests", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody <T> String getPendingRequests(@RequestBody String userName){
		Gson gson = new Gson();
		DocDonorInfo docDonorInfo = gson.fromJson(userName, DocDonorInfo.class);
		List<BloodDonationInfo> pendingRequestList = docHandler.getPendingRequest(docDonorInfo.getUserName());
		Map<String, T> resMap = new HashMap<String, T>();
		if(!pendingRequestList.isEmpty()){
			resMap.put("status", (T) "success");
			resMap.put("donors", (T) pendingRequestList);
		}else{
			resMap.put("status", (T) "fail");
			resMap.put("donors", (T) pendingRequestList);
		}
		return gson.toJson(resMap);
	}
	
	@RequestMapping(value = "/acceptRequest", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String accept(@RequestBody String donorInfo){
		Gson gson = new Gson();
		DocDonorInfo docDonorInfo = gson.fromJson(donorInfo, DocDonorInfo.class);
		String resString = docHandler.acceptBloodRequest(docDonorInfo.getUserName());
		Map<String, String> resMap = new HashMap<String, String>();
		if(resString.equalsIgnoreCase("success")){
			resMap.put("status", "success");
			resMap.put("request", "accepted");
		}else{
			resMap.put("status", "fail");
			resMap.put("request", "unknown");
		}
		return gson.toJson(resMap);
	}
}
