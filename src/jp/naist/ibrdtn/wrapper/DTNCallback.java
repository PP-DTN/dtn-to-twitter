package jp.naist.ibrdtn.wrapper;

import ibrdtn.api.object.Bundle;

public interface DTNCallback {

	/**
	 * A bundle was received
	 */
	void onBundle(Bundle bundle, byte[] content);

}
