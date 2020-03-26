package io.sbs.service;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;

public class EmailService {

	MongoClient mongoClient = MongoClients.create("mongodb://admin:myadminpassword@18.222.64.16:27017");
	MongoDatabase database = mongoClient.getDatabase("mydb");

	Properties mailServerProperties;
	Session getMailSession;
	MimeMessage generateMailMessage;
	
	public boolean send_email(String userName, String to_emailID, String subject) {
		try {
			mailServerProperties = System.getProperties();
			mailServerProperties.put("mail.smtp.port", "587");
			mailServerProperties.put("mail.smtp.auth", "true");
			mailServerProperties.put("mail.smtp.starttls.enable", "true");
			getMailSession = Session.getDefaultInstance(mailServerProperties, null);
			generateMailMessage = new MimeMessage(getMailSession);
			generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to_emailID));
			generateMailMessage.setSubject(subject);
			String otp_string = new String(generate_otp(6));
			System.out.println("Generated otp: " + otp_string);
			String emailBody = "Your One Time Password (OTP) is: "+ otp_string +"." + "<br><br> Regards, <br>SBS Admin";
			generateMailMessage.setContent(emailBody, "text/html");
			Transport transport = getMailSession.getTransport("smtp");	
			transport.connect("smtp.gmail.com", "sbs.cse545.softsec@gmail.com", "asucse545");
			transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
			transport.close();

			saveToDB(userName,to_emailID,otp_string);
			return true;

		} catch (AddressException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return false;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void saveToDB(String userName, String to_emailID, String otp_string) {
		MongoCollection<Document> collection = database.getCollection("loginOTP");
		Bson filter = Filters.eq("username", userName);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		otp_string=passwordEncoder.encode(otp_string);
		Bson update =  new Document("$set",
                new Document()
                      .append("username", userName)
                      .append("email", to_emailID)
                      .append("otp", otp_string)
                      .append("verified", false)
                      .append("opt_created", new Date()));
		UpdateOptions options = new UpdateOptions().upsert(true);
		collection.updateOne(filter, update, options);
	}

	private char[] generate_otp(int len) {
		String numbers = "0123456789"; 
		Random rndm_method = new Random();
		
		char[] otp = new char[len]; 
		for (int i = 0; i < len; i++) 
        { 
            otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length())); 
        }
		return otp;
	}
	
}
