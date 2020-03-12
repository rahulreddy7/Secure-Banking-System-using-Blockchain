package io.sbs.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Accounts")
public class Account {
	
	private String acc_type;
    private String acc_holder_name;
}
