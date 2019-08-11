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

public class SendMail {

    private static final String USER = "test";
    private static final String PASSWORD = "test";
    public static String FROM = "admin@localhost";
    public static String TO = "test@localhost";

    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", "localhost");
            props.put("mail.smtp.port", "1125");
            props.put("mail.smtp.auth", "true");
            props.put("mail.debug", "true");
            //Session session = Session.getDefaultInstance(props);
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USER, PASSWORD);
                }
            });
            MimeMessage msg = new MimeMessage(session);
           // msg.setFrom(new InternetAddress(FROM));
            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(TO));
            msg.setSubject("You have a new mail");
            msg.setText("This is a test message send at " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSZ").format(new Date()));
            Transport.send(msg);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }
}
