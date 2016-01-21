/**
 * 
 */
package org.doc.donoroncall.donar;

import org.doc.core.util.DocMailingInterface;
import org.doc.core.util.DocMailingProcessor;
import org.doc.donoroncall.doc.info.DocDonorInfo;
import org.doc.donoroncall.doc.info.DocRequesterInfo;
import org.doc.donoroncall.donar.dao.DocRequestDAOHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author pandiyaraja
 * 
 */
@Service
public class DocRequestService implements DocRequestHandler {
	@Autowired
	DocRequestDAOHandler drDAOHandler;
	
	@Autowired
	DocMailingInterface dmi;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.doc.donoroncall.donar.BloodRequesterHandler#requestBlood(org.doc.
	 * donoroncall.donar.BloodRequesterInfo)
	 */
	@Override
	public String docRequest(DocRequesterInfo drInfo) {
		String regRes = drDAOHandler.donorRequestDao(drInfo);
		if(regRes.equalsIgnoreCase("success")){
			//dmi.sendMail(drInfo.getUserName(), "Blood request", "Hi, Please accept my blood request");			
		}
		return regRes;
	}
	@Override
	public String docDonorRequest(DocDonorInfo ddInfo) {
		return drDAOHandler.docDonorRequestDao(ddInfo);
	}
}
