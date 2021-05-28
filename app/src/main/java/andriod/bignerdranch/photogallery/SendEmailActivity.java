package andriod.bignerdranch.photogallery;

import android.util.Log;

import com.sun.mail.smtp.SMTPMessage;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class SendEmailActivity {

    private static final String TAG = "sendEmail";

    public static Message buildMessageWithAttach(Session session) throws MessagingException, IOException {
        SMTPMessage msg = null;
        try {

            msg = new SMTPMessage(session);
            MimeMultipart content = new MimeMultipart();

            MimeBodyPart mainPart = new MimeBodyPart();
            mainPart.setText("Hello there! This is simple demo message");
            content.addBodyPart(mainPart);

            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.attachFile("resources/bill_up_close.jpg");
            content.addBodyPart(imagePart);


            msg.setContent(content);
            msg.setSubject("Demo message with Nurain image");

        } catch (MessagingException ioe) {
            Log.i(TAG, "Unable to get Session", ioe);
        } catch (IOException ioe) {
            Log.i(TAG, "Unable to get Session", ioe);
        }

        return msg;
    }


    public static Session buildSimpleSession() {
        Properties mailProps = new Properties();

        mailProps.put("mail.transport.protocol", "smtp");

        mailProps.put("mail.host", "localhost");

        mailProps.put("mail.from", "lawsondawuda6@gmail.com");

        return Session.getDefaultInstance(mailProps);
    }


    public static Session buildGoogleSession() {
        Properties mailProps = new Properties();

        mailProps.put("mail.transport.protocol", "smtp");

        mailProps.put("mail.host", "smtp.gmail.com");

        mailProps.put("mail.form", "lawsondawuda6@gmail.com");

        mailProps.put("mail.smtp.starttls.enable", "true");

        mailProps.put("mail.smtp.port", "587");

        mailProps.put("mail.smtp.auth", "true");

        final PasswordAuthentication usernamePassword = new
                PasswordAuthentication("lawsondawuda6@gmail.com",
                "dawuda32244");

        Authenticator auth  = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return usernamePassword;
            }
        };

        Session session = Session.getInstance(mailProps, auth);
        session.setDebug(true);

        return session;
    }

    public static void addressAndSendMessage(Message message, String recipient)
            throws AddressException, MessagingException {
       try {
           message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
           Transport.send(message);
       } catch (AddressException ade) {
           Log.i(TAG, "Unable to send Address", ade);
       } catch (MessagingException msg) {
           Log.i(TAG, "Unable to send msg", msg);
       }

    }


}
