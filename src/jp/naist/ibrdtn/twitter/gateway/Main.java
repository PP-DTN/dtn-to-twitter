package jp.naist.ibrdtn.twitter.gateway;

import ibrdtn.api.APIException;

import java.io.IOException;

public class Main {

	public static final String PRIMARY_EID = "twitter";

	public static void main(String[] args) throws APIException, IOException {
		new TwitterGateway(PRIMARY_EID);
	}

}
