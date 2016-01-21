/**
 * 
 */
package org.doc.core.api.registration.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.doc.core.api.registration.info.LoginInfo;
import org.doc.core.api.registration.info.RegistrationInfo;
import org.doc.core.util.db.ConnectionProvider;
import org.doc.donoroncall.doc.info.DocRequesterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author pandiyaraja
 * 
 */
@Service
public class RegistrationDAOService extends ConnectionProvider implements RegistrationDAOHandler {

	Logger log = LoggerFactory.getLogger(RegistrationDAOService.class);
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.doc.core.api.registration.dao.RegistrationDAOHandler#
	 * loginAuthenticationDAO(org.doc.core.api.registration.info.LoginInfo)
	 */
	private final Logger logger = LoggerFactory.getLogger(RegistrationDAOService.class);	
	public RegistrationInfo loginAuthenticationDAO(LoginInfo logInfo) {
		String query = "select * from doc.login_auth where username='"+logInfo.getUserName()+"'";
		ResultSet rs = getResult(query);
		String dbUser = "";
		String dbPass = "";
		try {
			 while (rs.next()){
				dbUser = rs.getString("username");
				dbPass = rs.getString("password");
			 }
			logger.debug(RegistrationDAOService.class+"   USER NSME   "+dbUser);
			logger.debug(RegistrationDAOService.class+"  PASSWORD "+dbPass);
			if((dbUser.equals(logInfo.getUserName())) && (dbPass.equals(logInfo.getPassword()))){
				return getUserObject(dbUser);
			}else{
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.doc.core.api.registration.dao.RegistrationDAOHandler#
	 * registrationAccountDAO
	 * (org.doc.core.api.registration.info.RegistrationInfo)
	 */
	public String registrationAccountDAO(RegistrationInfo regInfo) {
		int retval=0;
		try {
			log.info(RegistrationDAOService.class+"  RegistrationInfo : "+regInfo.toString());
			String query = "INSERT INTO doc.user (" + "`username`,"    + "`password`,"    + "`name`,"    + "`blood_group`,"    + "`type`,"    + "`dob`,"    + "`authorized` ) VALUES ("
				    + "?, ?, ?, ?, ?, ?, ?)";
			Connection con = getConnection();
		    PreparedStatement st = con.prepareStatement(query);
		    st.setString(1, regInfo.getUserName());
		    st.setString(2, regInfo.getPassWord());
		    st.setString(3, regInfo.getName());
		    st.setString(4, regInfo.getBloodGroup());
		    st.setString(5, regInfo.getType());
		    st.setString(6, regInfo.getDob());
		    st.setString(7, "no");
		    
		    retval = executeUpdate(st);
		    if(retval==1){
		    	return "success";
		    }else{
		    	return "fail";
		    }
		  } 
		  catch (Exception se)
		  {
		    se.printStackTrace();
		    return "fail";
		  }
	}
	private RegistrationInfo getUserObject(String userName){
		Connection con = getConnection();
		String requesterQuery = "select * from doc.user where `username` = ?";
		RegistrationInfo regInfo = new RegistrationInfo();
		try {
			PreparedStatement st = con.prepareStatement(requesterQuery);
			st.setString(1, userName);
	    	ResultSet rs1 = getResult(st);
	    	if(rs1!=null){	    		
				while(rs1.next()){
					regInfo.setUserName(rs1.getString("username"));
					regInfo.setPassWord(rs1.getString("password"));
					regInfo.setBloodGroup(rs1.getString("blood_group"));
					regInfo.setType(rs1.getString("type"));
					regInfo.setDob(rs1.getString("dob"));
				}
				return regInfo;
	    	}else{
	    		return null;
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
