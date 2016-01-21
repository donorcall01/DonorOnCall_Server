/**
 * 
 */
package org.doc.donoroncall.doc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.doc.core.util.db.ConnectionProvider;
import org.doc.donoroncall.doc.info.BloodDonationInfo;
import org.doc.donoroncall.doc.info.DocDonorInfo;
import org.doc.donoroncall.doc.info.DocRequesterInfo;
import org.springframework.stereotype.Service;

/**
 * @author Pandiyaraja
 *
 */
@Service
public class DocDAOService extends ConnectionProvider implements DocDAOHandler{

	@Override
	public List<DocDonorInfo> getAllDonors() {

		DocDonorInfo dInfo;
		List<DocDonorInfo> donorsList = new ArrayList<DocDonorInfo>();
		String query = "select * from doc.donor";
		ResultSet rs = getResult(query);
		try {
			while(rs.next()){			
				dInfo = new DocDonorInfo();
				dInfo.setUserName(rs.getString("user_name"));
				dInfo.setLocation(rs.getString("location"));
				dInfo.setLastDonatedDate(rs.getString("lastdonated_date"));
				dInfo.setBloodGroup(rs.getString("blood_group"));				
				donorsList.add(dInfo);					
			} 
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return donorsList;
	}

	@Override
	public List<DocDonorInfo> getDonorsforRecipient(DocDonorInfo dDInfo) {
		DocDonorInfo dInfo;
		List<DocDonorInfo> donorsList = new ArrayList<DocDonorInfo>();
		String query = "select * from doc.donor where location = ? AND blood_group = ?";
		Connection con = getConnection();		 
		ResultSet rs;
		try {
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, dDInfo.getLocation());
			st.setString(2, dDInfo.getBloodGroup());
			rs = getResult(st);
			while(rs.next()){			
				dInfo = new DocDonorInfo();
				dInfo.setUserName(rs.getString("user_name"));
				dInfo.setLocation(rs.getString("location"));
				dInfo.setLastDonatedDate(rs.getString("lastdonated_date"));
				dInfo.setBloodGroup(rs.getString("blood_group"));				
				donorsList.add(dInfo);					
			} 
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return donorsList;
	}

	@Override
	public String selectDonor(BloodDonationInfo bloodDonationInfo) {
		Connection con = getConnection();
		String requesterQuery = "select * from doc.requester where user_name= ?";
		DocRequesterInfo drInfo = new DocRequesterInfo();
		try {
			PreparedStatement st = con.prepareStatement(requesterQuery);
			st.setString(1, bloodDonationInfo.getRequester());
	    	ResultSet rs = getResult(st);
	    	if(rs!=null){	    		
				while(rs.next()){
					drInfo.setUserName(rs.getString("user_name"));
					drInfo.setHospitalName(rs.getString("hospital_name"));
					drInfo.setBloodGroup(rs.getString("blood_group"));
					drInfo.setPhysicianName(rs.getString("physician_name"));
					drInfo.setPatient(rs.getString("patient"));
					drInfo.setUnit(rs.getInt("unit"));
				}
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
			return "fail";
		}
		String query = "insert into doc.boold_donation (`recipient`,`donor`,`hospital_name`,`physician_name`,`patient`,`unit`,`blood_group`,`accepted`,`verified`) VALUES (?,?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, drInfo.getUserName());
			ps.setString(2, bloodDonationInfo.getDonor());
			ps.setString(3, drInfo.getHospitalName());
			ps.setString(4, drInfo.getPhysicianName());
			ps.setString(5, drInfo.getPatient());
			ps.setInt(6, drInfo.getUnit());
			ps.setString(7, drInfo.getBloodGroup());
			ps.setString(8, "no");
			ps.setString(9, "no");
			int val = executeUpdate(ps);
			if(val>0){
				return "success";
			}else{
				return "false";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	@Override
	public List<BloodDonationInfo> getPendingRequests(String userName) {
		BloodDonationInfo brInfo;
		List<BloodDonationInfo> pendingList = new ArrayList<BloodDonationInfo>();		
		try {
			Connection connection = getConnection();		
			String query = "select * from doc.boold_donation where accepted = ? AND donor = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, "no");
			ps.setString(2, userName);
			ResultSet rs = getResult(ps);
			while(rs.next()){			
				brInfo = new BloodDonationInfo();
				brInfo.setDonor(rs.getString("donor"));
				brInfo.setHospitalName(rs.getString("hospital_name"));
				brInfo.setPhysicianName(rs.getString("physician_name"));
				brInfo.setBloodGroup(rs.getString("blood_group"));
				brInfo.setPatientName(rs.getString("patient"));
				brInfo.setRequester(rs.getString("recipient"));
				brInfo.setApproved(rs.getString("accepted"));
				pendingList.add(brInfo);					
			} 
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return pendingList;
	}

	@Override
	public String acceptBloodRequest(String userName) {
		String query = "update doc.boold_donation set accepted='yes' where recipient= ?";
		Connection con = getConnection();
		try {
				PreparedStatement st = con.prepareStatement(query);
				st.setString(1, userName);
				int retval = executeUpdate(st);
			    if(retval>0){
			    	return "success";	    
			    }else{
			    	return "fail";
			    }
			}catch(Exception e){
				e.printStackTrace();
				return "fail";
			}
	}

}
