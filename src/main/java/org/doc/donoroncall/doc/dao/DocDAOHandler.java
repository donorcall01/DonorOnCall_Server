/**
 * 
 */
package org.doc.donoroncall.doc.dao;

import java.util.List;

import org.doc.donoroncall.doc.info.BloodDonationInfo;
import org.doc.donoroncall.doc.info.DocDonorInfo;
import org.springframework.stereotype.Service;

/**
 * @author Pandiyaraja
 *
 */
@Service
public interface DocDAOHandler {
	public List<DocDonorInfo> getAllDonors();
	public List<DocDonorInfo> getDonorsforRecipient(DocDonorInfo dDInfo);
	public String selectDonor(BloodDonationInfo bloodDonationInfo);
	public List<BloodDonationInfo> getPendingRequests(String userName);
	public String acceptBloodRequest(String userName);
}
