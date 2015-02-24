package tr.com.aligelenler;

import java.io.IOException;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

public class ReadMail {

	private static final String USER = "test";
	private static final String PASSWORD = "test";
	private static final boolean CLEAR = false;

	public static void main(String[] args) {
		Store store = null;
		Folder inbox = null;
		try {
			Properties props = new Properties();
			props.put("mail.pop3.host", "localhost");
			props.put("mail.pop3.port", "1110");
			props.put("mail.store.protocol", "pop3");
			props.put("mail.debug", "false");
			
			Session session = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(USER, PASSWORD);
				}
			});
			store = session.getStore();
			store.connect();
			Folder root = store.getDefaultFolder();
			inbox = root.getFolder("inbox");
			inbox.open(Folder.READ_WRITE);
			Message[] mails = inbox.getMessages();
			if (mails.length == 0) {
				System.out.println("There are no messages");
			}
			for (int i = 0; i < mails.length; i++) {
				MimeMessage mail = (MimeMessage) mails[i];
				System.out.println("From: " + mail.getFrom()[0]);
				System.out.println("To: " + mail.getRecipients(Message.RecipientType.TO)[0]);
				System.out.println("Subject: " + mail.getSubject());
				System.out.println("Content: " + mail.getContent());
				if (CLEAR) {
					mail.setFlag(Flags.Flag.DELETED, true);
				}
			}
		} catch (MessagingException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (inbox != null && inbox.isOpen())
					inbox.close(true);
				if (store != null)
					store.close();
			} catch (MessagingException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
