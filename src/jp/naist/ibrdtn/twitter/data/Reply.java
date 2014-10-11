package jp.naist.ibrdtn.twitter.data;

import java.util.List;

public class Reply {
	public int status; // Status code
	public String message; // Message explaining the status
	public List<MiniStatus> statuses; // Status list for get_home_timeline, etc. API
}
