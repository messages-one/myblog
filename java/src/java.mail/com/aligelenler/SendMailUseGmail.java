package java.mail.com.aligelenler;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailUseGmail {

	private static final String USER = "your_email@gmail.com";
	private static final String PASSWORD = "your_app_password";

	public static void main(String[] args) {
		try {
			Properties props = new Properties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.auth", "true");	
			props.put("mail.debug", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			
			props.put("mail.smtp.port", "587");			
			props.put("mail.smtp.starttls.enable", "true");
			
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

			Session session = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(USER, PASSWORD);
				}
			});
			MimeMessage msg = new MimeMessage(session);
			// msg.setFrom(new InternetAddress(USER)); No need to specify FROM attribute
			msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse("sendmail1@gmail.com, sendmail2@gmail.com"));
			msg.setSubject("You have a new mail");
			msg.setText("This is a test message send at " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSZ").format(new Date()));
			Transport.send(msg);
		} catch (MessagingException e) {
			System.out.println(e.getMessage());
		}
	}
}
