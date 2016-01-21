/**
 * 
 */
package org.doc.core.api.registration.service;

import org.doc.core.api.registration.dao.RegistrationDAOHandler;
import org.doc.core.api.registration.handler.LoginHandler;
import org.doc.core.api.registration.info.LoginInfo;
import org.doc.core.api.registration.info.RegistrationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author pandiyaraja
 * 
 */
@Service
public class LoginService implements LoginHandler {

	@Autowired
	RegistrationDAOHandler rDAOHand;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.doc.core.api.registration.handler.LoginHandler#loginAuthenticate(
	 * org.doc.core.api.registration.info.LoginInfo)
	 */
	@Override
	public RegistrationInfo loginAuthenticate(LoginInfo lInfo) {
		return rDAOHand.loginAuthenticationDAO(lInfo);		
	}

}
