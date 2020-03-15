package io.sbs.service;

import static com.mongodb.client.model.Filters.eq;
import io.sbs.dto.TransferPostDTO;
import io.sbs.model.Account;
import io.sbs.repository.AccountRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	public AccountRepository accountRepository;

	// public void testMethod() {
	// //TODO Implement this
	// }

	final MongoClient mongoClient = MongoClients
			.create("mongodb://admin:myadminpassword@18.222.64.16:27017");
	final MongoDatabase database = mongoClient.getDatabase("mydb");

	@Override
	public void transfer_funds(TransferPostDTO transferPostDTO) {
		// TODO Auto-generated method stub
		// String mode = transferPostDTO.getMode();
		// switch (mode) {
		// case "phone":
		// case "email":
		// if (mode.equals("phone")) {
		// long phoneNumber = transferPostDTO.getPhoneNumber();
		// //Get the
		// }
		// // accountRepository
		// int s = 2;
		// break;
		// case "account":
		// /*
		// *
		// * Code for transfering the amount via accounts 1.if to_accn is
		// * null, update the
		// */
		//
		// }
		String from_accnt = transferPostDTO.getFrom_accnt();
		String to_accnt = transferPostDTO.getTo_accnt();

		MongoCollection<Document> collection = database
				.getCollection("account");
		Document from_accnt_doc = collection.find(eq("_id", from_accnt))
				.first();
		Document to_accnt_doc = collection.find(eq("_id", to_accnt)).first();
		//TODO
		// Update from_accnt_doc collection, save it
		// Update to_accnt_doc collection, save it
		// Add the Transaction in the mongo collection for transaction
		// collection.updateon
	}

	@Override
	public List<Account> getAllMatchingAccounts(Map<String, ?> query) {
		// TODO Auto-generated method stub
		MongoCollection<Document> collection_accnt = database
				.getCollection("account");

		MongoCollection<Document> collection_user = database
				.getCollection("user");

		Double query_param = null;
		List<Account> accounts = new ArrayList<Account>();
		Document user = null;
		if (query.containsKey("phone")) {
			// query_param = (Double)query.get("phone");
			user = collection_user.find(eq("phone", query.get("phone")))
					.first();
		}

		if (query.containsKey("email")) {
			user = collection_user.find(eq("email", query.get("email")))
					.first();
		}

		String userId = user.get("userid").toString();

		List<Document> cursor_accounts = collection_accnt.find(
				eq("userid", userId)).into(new ArrayList<Document>());

		for (Document account : cursor_accounts) {
			Account a = new Account();
			a.setAcc_holder_name(user.get("name").toString());
			a.setAccount_number(Double.parseDouble(account.get("account_num")
					.toString()));
			a.setAcc_type(account.get("type").toString());
			a.setAcc_balance(Double.parseDouble(account.get("balance")
					.toString()));
			a.setUser_id(userId);
			accounts.add(a);
		}

		return accounts;
	}
}
