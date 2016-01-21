/**
 * 
 */
package org.doc.donoroncall.doc;

import java.util.List;

import org.doc.core.util.DocMailingInterface;
import org.doc.donoroncall.doc.dao.DocDAOHandler;
import org.doc.donoroncall.doc.info.BloodDonationInfo;
import org.doc.donoroncall.doc.info.DocDonorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Pandiyaraja
 *
 */
@Service
public class DocService implements DocHandler{

	@Autowired
	private DocDAOHandler dDHandler;
	@Autowired 
	DocMailingInterface dmi;
	@Override
	public List<DocDonorInfo> getAllDonors() {
		return dDHandler.getAllDonors();
	}

	@Override
	public List<DocDonorInfo> getDonorsForRecipient(DocDonorInfo dInfo) {
		return dDHandler.getDonorsforRecipient(dInfo);
	}

	@Override
	public String selectDonors(BloodDonationInfo bloodDonationInfo) {		
		String resString =  dDHandler.selectDonor(bloodDonationInfo);
		if(resString.equalsIgnoreCase("success")){
			dmi.sendMail(bloodDonationInfo.getDonor(), "Blood request", "Hi, please donate me blood");
		}
		return resString;
	}

	@Override
	public List<BloodDonationInfo> getPendingRequest(String userName) {		
		return dDHandler.getPendingRequests(userName);
	}

	@Override
	public String acceptBloodRequest(String userName) {
		String resString = dDHandler.acceptBloodRequest(userName);
		if(resString.equalsIgnoreCase("success")){
			dmi.sendMail(userName, "Blood request", "Hi, I am ready to donate blood");
		}
		return resString;
	}

}
