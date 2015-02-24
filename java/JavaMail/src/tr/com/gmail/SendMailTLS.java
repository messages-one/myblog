package tr.com.gmail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailTLS {
	 
	public static void main(String[] args) {
 
		final String username = "ali.gelenler@gmail.com";
		final String password = "pfnsdomathebjsjh";
		/*
		 * google dan aldýk, kendi þifreni girdiðinde Application-specific password required hatasý verir.
		 * http://support.google.com/accounts/bin/answer.py?answer=185833 adresinden yukardaki pass u üretir. 
		 */
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");//localhost local TCP/IP monitorden izleyerek TLS /SSL (þifreli) farkýný görmek içi
		props.put("mail.smtp.port", "587");//80
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("ali.gelenler@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("ali.gelenler@gmail.com"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler,"
				+ "\n\n No spam to my email, please!");		
 
			Transport.send(message);
 
			System.out.println("Done");
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
