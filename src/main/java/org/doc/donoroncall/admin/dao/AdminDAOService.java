package org.doc.donoroncall.admin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.doc.core.api.registration.info.RegistrationInfo;
import org.doc.core.util.db.ConnectionProvider;
import org.springframework.stereotype.Service;

@Service
public class AdminDAOService extends ConnectionProvider implements AdminDAOHandler{
		
	@Override
	public List<RegistrationInfo> getRegUserList() {
		RegistrationInfo regInfo;
		List<RegistrationInfo> regList = new ArrayList<RegistrationInfo>();
		String query = "select * from doc.user";
		ResultSet rs = getResult(query);
		try {
			while(rs.next()){			
				regInfo = new RegistrationInfo();
				regInfo.setName(rs.getString("username"));
				regInfo.setPassWord(rs.getString("password"));
				regInfo.setName(rs.getString("name"));
				regInfo.setBloodGroup(rs.getString("blood_group"));
				regInfo.setType(rs.getString("type"));
				regInfo.setDob(rs.getDate("dob").toString());
				regInfo.setAuthorized(rs.getString("authorized"));
				regList.add(regInfo);					
			} 
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return regList;
	}

	@Override
	public String authorizeUser(String userName) {
		String query = "update doc.user set authorized='yes' where username= ?";
		Connection con = getConnection();
		try {
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, userName);
			int retval = executeUpdate(st);
		    if(retval>0){
		    	String userQuery = "select * from doc.user where username= ?";
		    	PreparedStatement st1 = con.prepareStatement(userQuery);
				st1.setString(1, userName);
		    	ResultSet rs1 = getResult(st1);
		    	if(rs1!=null){
			    	String passWord="";
			    	String type="";			    	
					try {
						while(rs1.next()){
							passWord = rs1.getString("password");
							type = rs1.getString("type");
						}
				    	if(!passWord.isEmpty() && !type.isEmpty()){
				    		String loginQuery = "insert into doc.login_auth (`username`,`password`,`type`) VALUES (?,?,?)";
						    PreparedStatement st2 = con.prepareStatement(loginQuery);
						    st2.setString(1, userName);
						    st2.setString(2, passWord);
						    st2.setString(3, type);
						    retval = executeUpdate(st2);
						    if(retval>0){
						    	return "success";
						    }else{
						    	return "fail";
						    }
				    	}
					} catch (SQLException e) {
						e.printStackTrace();
					}			    	
		    	}else{
		    		return "fail";
		    	}
		    }else{
		    	return "fail";
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "fail";
	}

	@Override
	public String acceptRequest(String userName) {
		String query = "update doc.requester set verified='yes' where user_name= ?";
		Connection con = getConnection();
		try {
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, userName);
			int retval = executeUpdate(st);
		    if(retval>0){
		    	/*if(retval>0){
			    	String userQuery = "select * from doc.user where username= ?";
			    	PreparedStatement st1 = con.prepareStatement(userQuery);
					st1.setString(1, userName);
			    	ResultSet rs1 = getResult(st1);
			    	if(rs1!=null){			    	
						try {
							DocRequesterInfo dri;
							while(rs1.next()){
								dri = new DocRequesterInfo();
								dri.setUserName(rs1.getString(""));
								dri.setBloodGroup(rs1.getString(""));
								dri.setHospitalName(rs1.getString(""));
								dri.setPhysicianName(rs1.getString(""));
								dri.setPatient(rs1.getString(""));
								dri.setPurpose(rs1.getString(""));
								dri.setHowSoon(rs1.getInt(""));
								dri.setUnit(rs1.getInt(""));
							}
					    	if(dri!=null){
					    		String loginQuery = "INSERT INTO doc.donor (" + "`user_name`,"    + "`blood_group`,"    + "`hospital_name`,"    + "`physician_name`,"    + "`patient`,"    + "`purpose`,"    + "unit," +"how_soon," +"`accepted` ) VALUES ("
				    + "?, ?, ?, ?, ?, ?, ?, ?, ?)";
							    PreparedStatement st2 = con.prepareStatement(loginQuery);
							    st2.setString(1, dri.getUserName());
							    st2.setString(2, );
							    retval = executeUpdate(st2);
							    if(retval>0){
							    	return "success";
							    }else{
							    	return "fail";
							    }
					    	}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}			    	
			    	}else{
			    		return "fail";
			    	}
			    }*/
		    	return "success";
		    }else{
		    	return "fail";
		    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "fail";
	}

}
