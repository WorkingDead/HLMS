package utils.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class EmailUtil extends Thread {

	private final Logger log4j = LoggerFactory.getLogger( this.getClass().getName() );
	
	private String smtpHost = "";
	private String smtpPort = "";
	private boolean _ssl = false;
	
	private String from = "";
	private String fromUsername = "";
	private String fromPassword = "";
	private String to = "";
	
	private String subject = "";
	private String content = "";

	public EmailUtil(String smtpHost, String smtpPort, boolean _ssl, String from, String fromUsername, String fromPassword, String to, String subject, String content) {
		this.smtpHost = smtpHost;
		this.smtpPort = smtpPort;
		this._ssl = _ssl;
		
		this.from = from;
		this.fromUsername = fromUsername;
		this.fromPassword = fromPassword;
		this.to = to;
		
		this.subject = subject;
		this.content = content;
	}

	public void sendEmail() throws Exception {

		if ( _ssl == false ) {
			Properties props = new Properties();
			if ( fromUsername.equals("") && fromPassword.equals("") ) {
			}
			else
				props.put("mail.smtp.auth", "true"); 
			//props.put("mail.smtp.starttls.enable", "true");	\\Only for TLS

			Session session = Session.getInstance(props);

			MimeMessage message = new MimeMessage( session ); 
			message.setFrom( new InternetAddress( from ) ); 
			message.addRecipient( Message.RecipientType.TO, new InternetAddress( to ) );
			message.setSubject( subject, "UTF-8" ); 
			message.setContent( content, "text/html; charset=utf-8" );

			Transport t = session.getTransport("smtp");
		    try {
		    	t.connect(smtpHost, Integer.parseInt(smtpPort), fromUsername, fromPassword);
		    	t.sendMessage(message, message.getAllRecipients());
		    } finally {
		    	t.close();
		    }
		}
		else {
	    	Properties props = new Properties();
	    	props.put("mail.smtp.host", smtpHost);
	    	props.put("mail.smtp.socketFactory.port", smtpPort);
	    	props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
			if ( fromUsername.equals("") && fromPassword.equals("") ) {
			}
			else
				props.put("mail.smtp.auth", "true"); 
	    	props.put("mail.smtp.port", smtpPort);

	    	Session session = Session.getDefaultInstance(props,
	    			new javax.mail.Authenticator() 
	    			{
	    				protected PasswordAuthentication getPasswordAuthentication()
	    				{ return new PasswordAuthentication( fromUsername, fromPassword );	}
	    			});
			
			MimeMessage message = new MimeMessage( session ); 
			message.setFrom( new InternetAddress( from ) ); 
			message.addRecipient( Message.RecipientType.TO, new InternetAddress( to ) );
			message.setSubject( subject, "UTF-8" ); 
			message.setContent( content, "text/html; charset=utf-8" );
			
			Transport.send(message);
		}
	}

	public void run() {

		try {
			//sendEmail();
			sendEmailInClearText();
		}
		catch (Exception e) {
			log4j.error("run: ", e);
		}
	}
	
	public void sendEmailInClearText() throws Exception {

		if ( _ssl == false ) {
			Properties props = new Properties();
			if ( fromUsername.equals("") && fromPassword.equals("") ) {
			}
			else
				props.put("mail.smtp.auth", "true"); 
			//props.put("mail.smtp.starttls.enable", "true");	\\Only for TLS

			Session session = Session.getInstance(props);

			MimeMessage message = new MimeMessage( session ); 
			message.setFrom( new InternetAddress( from ) ); 
			message.addRecipient( Message.RecipientType.TO, new InternetAddress( to ) );
			message.setSubject( subject, "UTF-8" ); 
			//message.setContent( content, "text/html; charset=utf-8" );
			message.setText(content);

			Transport t = session.getTransport("smtp");
		    try {
		    	t.connect(smtpHost, Integer.parseInt(smtpPort), fromUsername, fromPassword);
		    	t.sendMessage(message, message.getAllRecipients());
		    } finally {
		    	t.close();
		    }
		}
		else {
	    	Properties props = new Properties();
	    	props.put("mail.smtp.host", smtpHost);
	    	props.put("mail.smtp.socketFactory.port", smtpPort);
	    	props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
			if ( fromUsername.equals("") && fromPassword.equals("") ) {
			}
			else
				props.put("mail.smtp.auth", "true"); 
	    	props.put("mail.smtp.port", smtpPort);

	    	Session session = Session.getDefaultInstance(props,
	    			new javax.mail.Authenticator() 
	    			{
	    				protected PasswordAuthentication getPasswordAuthentication()
	    				{ return new PasswordAuthentication( fromUsername, fromPassword );	}
	    			});
			
			MimeMessage message = new MimeMessage( session ); 
			message.setFrom( new InternetAddress( from ) ); 
			message.addRecipient( Message.RecipientType.TO, new InternetAddress( to ) );
			message.setSubject( subject, "UTF-8" ); 
			//message.setContent( content, "text/html; charset=utf-8" );
			message.setText(content);
			
			Transport.send(message);
		}
	}
}