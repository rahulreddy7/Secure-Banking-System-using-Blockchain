package io.sbs.service;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.sbs.model.Account;
import io.sbs.model.User;


public class UserServiceImpl implements UserService {
	
	MongoClient mongoClient = MongoClients.create("mongodb://admin:myadminpassword@18.222.64.16:27017");
	MongoDatabase database = mongoClient.getDatabase("mydb");
	
	@Override
	public List<Account> getUserAccountDetails(String userid) {

	    MongoCollection<Document> collection = database.getCollection("user");
	    Document myDoc = collection.find(eq("userid", userid)).first();
	    
	    MongoCollection<Document> collection_acc = database.getCollection("account");
	    List<Document> cursor_accounts = collection_acc.find(eq("userid", userid)).into(new ArrayList<Document>());
	      
		List<Account> acc_list = new ArrayList<Account>();
	    for (Document account : cursor_accounts) {
	    	Account a = new Account();
	    	a.setAcc_holder_name(myDoc.get("name").toString());
	        a.setAccount_number(Double.parseDouble(account.get("account_num").toString()));
	        a.setAcc_type(account.get("type").toString());
	        a.setAcc_balance(Double.parseDouble(account.get("balance").toString()));
	        a.setUser_id(userid);
	        acc_list.add(a);
	    }

		return acc_list;		
	}

	@Override
	public User getUserInfo(String userid) {
		MongoCollection<Document> collection = database.getCollection("user");
		Document myDoc = collection.find(eq("userid", userid)).first();
		User user = new User();
		user.setName(myDoc.get("name").toString());
		user.setEmailString(myDoc.get("email").toString());
		user.setAddress(myDoc.get("address").toString());
		return user;
	}

}