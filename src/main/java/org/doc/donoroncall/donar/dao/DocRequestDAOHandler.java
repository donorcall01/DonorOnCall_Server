/**
 * 
 */
package org.doc.donoroncall.donar.dao;

import org.doc.donoroncall.doc.info.DocDonorInfo;
import org.doc.donoroncall.doc.info.DocRequesterInfo;

/**
 * @author pandiyaraja
 * 
 */
public interface DocRequestDAOHandler {
	public String donorRequestDao(DocRequesterInfo brInfo);
	public String docDonorRequestDao(DocDonorInfo ddInfo);
}
