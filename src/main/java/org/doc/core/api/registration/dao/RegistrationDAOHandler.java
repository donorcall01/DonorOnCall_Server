/**
 * 
 */
package org.doc.core.api.registration.dao;

import org.doc.core.api.registration.info.LoginInfo;
import org.doc.core.api.registration.info.RegistrationInfo;
import org.springframework.stereotype.Service;

/**
 * @author pandiyaraja
 * 
 */
@Service
public interface RegistrationDAOHandler {
	public RegistrationInfo loginAuthenticationDAO(LoginInfo logInfo);
	public String registrationAccountDAO(RegistrationInfo regInfo);
}
