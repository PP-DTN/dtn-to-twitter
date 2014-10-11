package jp.naist.ibrdtn.twitter.data;

import twitter4j.Status;

import java.util.Date;

/**
 * Subset of a Twitter status containing only essential information
 */
public class MiniStatus {
	public long id;
	public String username;
	public Date createdAt;
	public String message;

	public MiniStatus(Status status) {
		id = status.getId();
		username = status.getUser().getScreenName();
		createdAt = status.getCreatedAt();
		message = status.getText();
	}
}
