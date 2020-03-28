package io.sbs.service;

import static com.mongodb.client.model.Filters.eq;
import io.sbs.constant.StringConstants;
import io.sbs.constant.UserType;
import io.sbs.dto.TransferPostDTO;
import io.sbs.dto.WorkflowDTO;
import io.sbs.exception.BusinessException;
import io.sbs.model.Account;
import io.sbs.model.Transaction;
import io.sbs.repository.AccountRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private Environment env;

	// public void testMethod() {
	// //TODO Implement this
	// }

	final MongoClient mongoClient = MongoClients
			.create("mongodb://admin:myadminpassword@18.222.64.16:27017");
	final MongoDatabase database = mongoClient.getDatabase("mydb");

	final String PRIMARY_ACCNT = "Checking";

	/*
	 * 
	 * Phone { mode : phone, self : false, fromAccNo : 1231231, amt : 11,
	 * toBeneficiary : 148099099 }
	 * 
	 * Email { mode : email, self : false, fromAccNo : 1231231, amt : 11,
	 * toBeneficiary : abc@test.com }
	 * 
	 * 
	 * Account { mode : account, self : false, fromAccNo : 1231231, amt : 11,
	 * toBeneficiary : 123333(will change according to the mode) }
	 * 
	 * Self { mode : account, self : true, fromAccNo : 1231231, amt : 11,
	 * toBeneficiary : 123333 }
	 */

	@Override
	public void transfer_funds(TransferPostDTO transferPostDTO) {
		// TODO Auto-generated method stub
		String mode = transferPostDTO.getMode();
		String toBeneficiary = transferPostDTO.gettoBeneficiary();

		MongoCollection<Document> collection_accnt = database
				.getCollection("account");

		MongoCollection<Document> collection_user = database
				.getCollection("user");

		Double query_param = null;
		List<Account> accounts = new ArrayList<Account>();
		Document user = null;

		Document to_account = null;

		switch (mode) {
		case "phone":
		case "email":
			if (mode.equals("phone")) {
				// Get the user from the phonenumber
				user = collection_user.find(eq("phone", toBeneficiary)).first();

			}
			if (mode.equals("email")) {
				// Get the user from the email
				user = collection_user.find(eq("email", toBeneficiary)).first();
			}

			to_account = collection_accnt.find(
					Filters.and(eq("acc_type", PRIMARY_ACCNT),
							eq("username", user.get("username")))).first();

			// accountRepository
			int s = 2;
			break;
		case "account":
			/*
			 * 
			 * Code for transfering the amount via accounts 1.if to_accn is
			 * null, update the
			 */
			to_account = collection_accnt.find(
					eq("account_number", PRIMARY_ACCNT)).first();
		}

		String from_accnt = transferPostDTO.getFrom_accnt();
		Document from_accnt_doc = collection_accnt.find(eq("_id", from_accnt))
				.first();
		// Document to_accnt_doc = collection.find(eq("_id", to_accnt)).first();
		// TODO
		// Add the workflow object and, we need from accnt, to accnt, amount
		String type = null;
		WorkflowDTO workDTO = null;
		if (transferPostDTO.getAmount() > 1000) {
			type = "type.criticaltransfer";
			workDTO = saveWorkflow(transferPostDTO, toBeneficiary, from_accnt,
					type, UserType.Tier2);
		} else {
			type = "type.noncriticaltransfer";
			workDTO = saveWorkflow(transferPostDTO, toBeneficiary, from_accnt,
					type, UserType.Tier1);
		}

		mongoTemplate.save(workDTO, "workflow");

		// Update from_accnt_doc collection, save it
		// Update to_accnt_doc collection, save it
		// Add the Transaction in the mongo collection for transaction
		// collection.updateon
	}

	private WorkflowDTO saveWorkflow(TransferPostDTO transferPostDTO,
			String toBeneficiary, String from_accnt, String type, UserType role) {
		WorkflowDTO workDTO = new WorkflowDTO();
		workDTO.setType(env.getProperty(type));
		List<TransferPostDTO> details = new ArrayList<TransferPostDTO>();
		TransferPostDTO obj = new TransferPostDTO();
		obj.setAmount(transferPostDTO.getAmount());
		obj.setFrom_accnt(from_accnt);
		obj.settoBeneficiary(toBeneficiary);
		obj.setAmount(transferPostDTO.getAmount());
		obj.setDescription(transferPostDTO.getDescription());
		details.add(obj);
		workDTO.setDetails(details);
		UserType usertype = null;
		workDTO.setRole(role);
		return workDTO;
	}

	// @Override
	// public List<Account> getAllMatchingAccounts(Map<String, ?> query) {
	// // TODO Auto-generated method stub
	// MongoCollection<Document> collection_accnt = database
	// .getCollection("account");
	//
	// MongoCollection<Document> collection_user = database
	// .getCollection("user");
	//
	// Double query_param = null;
	// List<Account> accounts = new ArrayList<Account>();
	// Document user = null;
	// if (query.containsKey("phone")) {
	// // query_param = (Double)query.get("phone");
	// user = collection_user.find(eq("phone", query.get("phone")))
	// .first();
	// }
	//
	// if (query.containsKey("email")) {
	// user = collection_user.find(eq("email", query.get("email")))
	// .first();
	// }
	//
	// String userId = user.get("userid").toString();
	//
	// List<Document> cursor_accounts = collection_accnt.find(
	// eq("userid", userId)).into(new ArrayList<Document>());
	//
	// for (Document account : cursor_accounts) {
	// Account a = new Account();
	// a.setAcc_holder_name(user.get("name").toString());
	// a.setAccount_number(account.get("account_num").toString());
	// a.setAcc_type(account.get("type").toString());
	// a.setAcc_balance(Double.parseDouble(account.get("balance")
	// .toString()));
	// a.setUsername(account.get("username").toString());
	// accounts.add(a);
	// }
	//
	// return accounts;
	// }

	@Override
	public WorkflowDTO approveCriticalTransfer(WorkflowDTO workflowDTO) {
		// TODO Auto-generated method stub
		return updateAccount(workflowDTO,
				StringConstants.WORKFLOW_CRITICAL_TRANSFER);
	}

	private WorkflowDTO updateAccount(WorkflowDTO workflowDTO,
			String transferType) {
		if (transferType.equals(StringConstants.WORKFLOW_CRITICAL_TRANSFER)) {
			// Do something about OTP
		}

		LinkedHashMap map = (LinkedHashMap) workflowDTO.getDetails().get(0);
		Update update = new Update();
		UpdateResult userObj = null;
		if (map.get("fromAccNo").toString() != null) {
			double existing_balance = 0.0;
			double new_balance = existing_balance - (double) map.get("amount");
			update.set("acc_balance", map.get("address").toString());
			userObj = mongoTemplate.updateFirst(
					Query.query(Criteria.where("account_number").is(
							map.get("fromAccNo").toString())), update,
					Account.class, "account");
		}
		if (map.get("toBeneficiary").toString() != null) {
			double existing_balance = 0.0;
			double new_balance = existing_balance + (double) map.get("amount");
			userObj = mongoTemplate.updateFirst(
					Query.query(Criteria.where("account_number").is(
							map.get("toBeneficiary").toString())), update,
					Account.class, "account");
		}
		if (userObj == null) {
			throw new BusinessException("cannot be updated！");
		}

		// Save Transaction in mongo and hyperledger
		Transaction transaction = saveTransaction(map);
		// transaction.setTransaction_type(transaction_type)
		mongoTemplate.save(transaction, "transaction");
		return workflowDTO;
	}

	private Transaction saveTransaction(LinkedHashMap transferObjMap) {
		Transaction transaction = new Transaction();
		transaction.setAmount((double) transferObjMap.get("amount"));
		transaction.setFrom_accnt(transferObjMap.get("fromAccNo").toString());
		transaction.setTo_accnt(transferObjMap.get("toBeneficiary").toString());
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		transaction.setCreationTime(date);
		return transaction;
	}

	@Override
	public WorkflowDTO approveNonCriticalTransfer(WorkflowDTO workflowDTO) {
		// TODO Auto-generated method stub
		return updateAccount(workflowDTO,
				StringConstants.WORKFLOW_NON_CRITICAL_TRANSFER);
	}
}
