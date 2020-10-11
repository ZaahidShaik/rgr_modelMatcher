package ubc.pavlab.rdp.services;

import org.springframework.web.multipart.MultipartFile;
import ubc.pavlab.rdp.model.PasswordResetToken;
import ubc.pavlab.rdp.model.User;
import ubc.pavlab.rdp.model.VerificationToken;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 *
 */
public interface EmailService {

    void sendSupportMessage( String message, String name, User user, HttpServletRequest request, MultipartFile attachment ) throws MessagingException;

    void sendResetTokenMessage( User user, PasswordResetToken token, Locale locale );

    void sendRegistrationMessage( User user, VerificationToken token );

    void sendUserRegisteredEmail( User user );
}
