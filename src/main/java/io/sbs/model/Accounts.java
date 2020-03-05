package io.sbs.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Accounts")
public class Accounts {
	
	private String acc_type;
    private String acc_holder_name;
}
