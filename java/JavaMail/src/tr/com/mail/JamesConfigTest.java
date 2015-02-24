package tr.com.mail;

public class JamesConfigTest {
	public static void main(String[] args) throws Exception {
		// CREATE CLIENT INSTANCES
		MailClient myClient = new MailClient("admin", "localhost", true);
	
		//myClient.checkInbox(MailClient.SHOW_MESSAGES);
		
		Thread.sleep(500); // Let the server catch up

		
		myClient.sendMessage("test@localhost", "Testing it is okkk", "This is a test message");
		/*
		 * gmaile göndermek için james in adresi ile domain adresi ayný olmasý lazým, yoksa gmail reverse kontrol ile maili almaz.
		 */
	
		Thread.sleep(500); // Let the server catch up

		myClient.checkInbox(MailClient.SHOW_MESSAGES);
	}
}
