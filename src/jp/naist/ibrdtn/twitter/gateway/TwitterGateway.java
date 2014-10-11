package jp.naist.ibrdtn.twitter.gateway;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import ibrdtn.api.APIException;
import ibrdtn.api.object.Bundle;
import jp.naist.ibrdtn.twitter.data.MiniStatus;
import jp.naist.ibrdtn.twitter.data.Reply;
import jp.naist.ibrdtn.twitter.data.Request;
import jp.naist.ibrdtn.wrapper.DTNCallback;
import jp.naist.ibrdtn.wrapper.DTNClient;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.io.IOException;
import java.util.ArrayList;

public class TwitterGateway implements DTNCallback {

	private DTNClient mClient;
	private Gson mGson;

	private static final int LIFETIME = 86400; // 24 hours

	public TwitterGateway(String primaryEid) throws APIException, IOException {
		mClient = new DTNClient(primaryEid, this);
		mGson = new Gson();
	}

	@Override
	public void onBundle(Bundle bundle, byte[] content) {
		Reply reply = new Reply();
		reply.status = 200; // OK

		try {
			System.out.println("Processing " + bundle);
			System.out.println("Content: " + new String(content));

			Request request = mGson.fromJson(new String(content), Request.class);
			Twitter twitter = TwitterFactory.getSingleton();

			if ("update_status".equals(request.act)) {
				Status status = twitter.updateStatus(request.message);
				reply.message = "Tweeted successfully id: " + status.getId();
			} else if ("get_home_timeline".equals(request.act)) {
				reply.statuses = new ArrayList<MiniStatus>();
				for (Status status : twitter.getHomeTimeline()) {
					reply.statuses.add(new MiniStatus(status));
				}
			} else if ("search".equals(request.act)) {
				reply.statuses = new ArrayList<MiniStatus>();
				for (Status status : twitter.search(new Query(request.message)).getTweets()) {
					reply.statuses.add(new MiniStatus(status));
				}
			} else {
				System.out.println("miss");
				reply.status = 400; // Bad request
				reply.message = "Unknown act '" + request.act + "'";
			}
		} catch (TwitterException e) {
			reply.status = e.getStatusCode();
			reply.message = e.getErrorMessage();
		} catch (JsonSyntaxException e) {
			reply.status = 400; // Bad request
			reply.message = "Bad JSON format";
		}

		// Send reply
		mClient.send(bundle.getSource(), mGson.toJson(reply), LIFETIME);
	}

}
