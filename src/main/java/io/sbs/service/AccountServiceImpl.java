package io.sbs.service;

import static com.mongodb.client.model.Filters.eq;
import io.sbs.constant.StringConstants;
import io.sbs.constant.UserType;
import io.sbs.dto.TransferOTPPostDTO;
import io.sbs.dto.TransferPostDTO;
import io.sbs.dto.WorkflowDTO;
import io.sbs.exception.BusinessException;
import io.sbs.model.Account;
import io.sbs.model.Transaction;
import io.sbs.repository.AccountRepository;
import io.sbs.security.EncryptDecrypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
	private UserService userService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private Environment env;

	final MongoClient mongoClient = MongoClients
			.create("mongodb://admin:myadminpassword@18.222.64.16:27017");
	final MongoDatabase database = mongoClient.getDatabase("mydb");

	// final String PRIMARY_ACCNT = "Checking";

	@Override
	public void transfer_funds(TransferPostDTO transferPostDTO) {
		String mode = transferPostDTO.getMode();
		String toBeneficiary = transferPostDTO.gettoBeneficiary();

		MongoCollection<Document> collection_accnt = database
				.getCollection("account");

		MongoCollection<Document> collection_user = database
				.getCollection("user");

		List<Account> accounts = new ArrayList<Account>();
		Document user = null;

		Document to_account = null;
		String from_accnt = transferPostDTO.getFrom_accnt();
		Document from_accnt_doc = collection_accnt.find(
				eq("account_number", from_accnt)).first();

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

			if (user != null) {
				to_account = collection_accnt
						.find(Filters
								.and(eq("acc_type",
										StringConstants.PRIMARY_ACCOUNT),
										eq("username", user.get("username"))))
						.first();
			}
			break;
		case "account":
			to_account = collection_accnt.find(
					eq("account_number", transferPostDTO.gettoBeneficiary()))
					.first();
			if (transferPostDTO.isSelf()) {
				// validate the both the accounts belong to same user
				if (!to_account.get("username").toString()
						.equals(from_accnt_doc.get("username").toString())) {
					throw new BusinessException(
							"Beneficiary account must belong to same user");
				}
			}

			break;
		}

		if (to_account != null) {
			Document from_user_doc = collection_user.find(
					eq("username", from_accnt_doc.get("username"))).first();
			String type = null;
			WorkflowDTO workDTO = null;

			if (transferPostDTO.getAmount() > 1000.0) {
				EmailService es = new EmailService();
				// create TransactionOTP details flow here in email service
				String subject = "One Time Password (OTP) for Critical Transfer";
				if (!es.send_criticaltransfer_email(
						from_user_doc.get("username").toString(), from_user_doc
								.get("email").toString(), subject)) {
					throw new BusinessException("Error in sending the email！");
				}
			} else {
				workDTO = saveWorkflow(transferPostDTO,
						to_account.get("account_number").toString(),
						from_accnt,
						StringConstants.WORKFLOW_NON_CRITICAL_TRANSFER,
						UserType.Tier1);
				mongoTemplate.save(workDTO, "workflow");
			}
		}
	}

	private WorkflowDTO saveWorkflow(TransferPostDTO transferPostDTO,
			String toBeneficiary, String from_accnt, String type, UserType role) {
		WorkflowDTO workDTO = new WorkflowDTO();
		workDTO.setType(type);
		List<TransferPostDTO> details = new ArrayList<TransferPostDTO>();
		TransferPostDTO obj = new TransferPostDTO();
		obj.setAmount(transferPostDTO.getAmount());
		obj.setFrom_accnt(from_accnt);
		obj.settoBeneficiary(toBeneficiary);
		obj.setAmount(transferPostDTO.getAmount());
		obj.setDescription(transferPostDTO.getDescription());
		obj.setSelf(transferPostDTO.isSelf());
		details.add(obj);
		workDTO.setDetails(details);
		workDTO.setRole(role);
		workDTO.setState(StringConstants.WORKFLOW_PENDING);
		return workDTO;
	}

	private Transaction saveTransaction(LinkedHashMap transferObjMap,
			String transactionType) {
		Transaction transaction = new Transaction();
		transaction.setAmount((double) transferObjMap.get("amount"));
		transaction.setFrom_accnt(transferObjMap.get("fromAccNo").toString());
		transaction.setTo_accnt(transferObjMap.get("toBeneficiary").toString());
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		transaction.setCreationTime(date);
		transaction.setTransaction_type(transactionType);
		return transaction;
	}

	@Override
	public boolean checkAndMatchOTP(String username, String otp) {
		// TODO Auto-generated method stub
		MongoCollection<Document> collection = database
				.getCollection("criticalTransferOTP");
		Document myDoc = collection.find(eq("username", username)).first();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		// otp = passwordEncoder.encode(otp)
		String otp_db = myDoc.get("otp").toString();
		EncryptDecrypt e;
		String otp_decoded = null;
		try {
			e = new EncryptDecrypt();
			otp_decoded = e.decrypt(otp_db);
		} catch (UnsupportedEncodingException | NoSuchPaddingException
				| NoSuchAlgorithmException | InvalidKeyException
				| InvalidAlgorithmParameterException | BadPaddingException
				| IllegalBlockSizeException e1) {
			throw new BusinessException("OTP not matched correctly");
		}
		if (otp_decoded.equals(otp)) {
			collection.updateOne(eq("username", username), new Document("$set",
					new Document("verified", true)));
			return true;
		}
		return false;
	}

	@Override
	public void transfer_criticalfunds(TransferOTPPostDTO transferPostDTO) {
		String mode = transferPostDTO.getMode();
		String toBeneficiary = transferPostDTO.gettoBeneficiary();
		MongoCollection<Document> collection_accnt = database
				.getCollection("account");
		MongoCollection<Document> collection_user = database
				.getCollection("user");
		Document user = null;
		Document to_account = null;
		String from_accnt = transferPostDTO.getFrom_accnt();
		Document from_accnt_doc = collection_accnt.find(
				eq("account_number", from_accnt)).first();

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

			if (user != null) {
				to_account = collection_accnt
						.find(Filters
								.and(eq("acc_type",
										StringConstants.PRIMARY_ACCOUNT),
										eq("username", user.get("username"))))
						.first();
			}
			break;
		case "account":
			to_account = collection_accnt.find(
					eq("account_number", transferPostDTO.gettoBeneficiary()))
					.first();
			if (transferPostDTO.isSelf()) {
				// validate the both the accounts belong to same user
				if (!to_account.get("username").toString()
						.equals(from_accnt_doc.get("username").toString())) {
					throw new BusinessException(
							"Beneficiary account must belong to same user");
				}
			}

			break;
		}
		if (to_account != null) {
			// WorkflowDTO workDTO = null;
			WorkflowDTO workDTO = new WorkflowDTO();
			workDTO.setType(StringConstants.WORKFLOW_CRITICAL_TRANSFER);
			List<TransferPostDTO> details = new ArrayList<TransferPostDTO>();
			TransferPostDTO obj = new TransferPostDTO();
			obj.setAmount(transferPostDTO.getAmount());
			obj.setFrom_accnt(from_accnt);
			obj.settoBeneficiary(toBeneficiary);
			obj.setAmount(transferPostDTO.getAmount());
			obj.setDescription(transferPostDTO.getDescription());
			obj.setSelf(transferPostDTO.isSelf());
			details.add(obj);
			workDTO.setDetails(details);
			workDTO.setRole(UserType.Tier2);
			workDTO.setState(StringConstants.WORKFLOW_PENDING);
			mongoTemplate.save(workDTO, "workflow");
		}
	}

	@Override
	public WorkflowDTO approveTransfer(WorkflowDTO workflowDTO) {
		LinkedHashMap map = (LinkedHashMap) workflowDTO.getDetails().get(0);
		Update update1 = new Update();
		Update update2 = new Update();
		UpdateResult accnObj1 = null;
		UpdateResult accnObj2 = null;

		if (map.get("fromAccNo").toString() != null) {
			Account account = mongoTemplate.findOne(
					Query.query(Criteria.where("account_number").is(
							map.get("fromAccNo"))), Account.class, "account");

			double new_balance = account.getAcc_balance()
					- (double) map.get("amount");
			if (new_balance < 0)
				throw new BusinessException("Insufficent Balance");
			update1.set("acc_balance", new_balance);
		}
		if (map.get("toBeneficiary").toString() != null) {
			Account account = mongoTemplate.findOne(
					Query.query(Criteria.where("account_number").is(
							map.get("toBeneficiary"))), Account.class,
					"account");

			double new_balance = account.getAcc_balance()
					+ (double) map.get("amount");
			update2.set("acc_balance", new_balance);
			accnObj1 = mongoTemplate.updateFirst(
					Query.query(Criteria.where("account_number").is(
							map.get("fromAccNo").toString())), update1,
					Account.class, "account");

			accnObj2 = mongoTemplate.updateFirst(
					Query.query(Criteria.where("account_number").is(
							map.get("toBeneficiary").toString())), update2,
					Account.class, "account");
		}
		if (accnObj1 == null || accnObj2 == null) {
			throw new BusinessException("cannot be updated！");
		}

		// Save Transaction in mongo and hyperledger
		String transactionType = null;
		if (workflowDTO.getType().equals(
				StringConstants.WORKFLOW_CRITICAL_TRANSFER)) {
			transactionType = StringConstants.CRITICAL_TRANSACTION;
		} else if (workflowDTO.getType().equals(
				StringConstants.WORKFLOW_NON_CRITICAL_TRANSFER)) {
			transactionType = StringConstants.NONCRITICAL_TRANSACTION;
		}
		Transaction transaction = saveTransaction(map, transactionType);
		mongoTemplate.save(transaction, "transaction");
		return workflowDTO;
	}

}