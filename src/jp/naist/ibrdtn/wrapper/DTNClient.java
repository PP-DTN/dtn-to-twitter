package jp.naist.ibrdtn.wrapper;

import ibrdtn.api.APIException;
import ibrdtn.api.ExtendedClient;
import ibrdtn.api.object.Block;
import ibrdtn.api.object.Bundle;
import ibrdtn.api.object.BundleID;
import ibrdtn.api.object.EID;
import ibrdtn.api.object.PayloadBlock;
import ibrdtn.api.sab.CallbackHandler;
import ibrdtn.api.sab.Custody;
import ibrdtn.api.sab.StatusReport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A wrapper class for the IBR-DTN library
 */
public class DTNClient {

	private DTNCallback mCallback;
	private ExecutorService mExecutor;
	private ExtendedClient mExClient;

	private CallbackHandler mHandler = new CallbackHandler() {

		private Bundle mBundle;
		private ByteArrayOutputStream mBaos = new ByteArrayOutputStream();

		@Override
		public void notify(final BundleID id) {
			mExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						mExClient.loadBundle(id);
						mExClient.getBundle();
					} catch (APIException e) {
						e.printStackTrace();
					}
				}
			});
		}

		@Override
		public void notify(StatusReport r) {
		}

		@Override
		public void notify(Custody c) {
		}

		@Override
		public void startBundle(Bundle bundle) {
			mBundle = bundle;
		}

		@Override
		public void endBundle() {
			// Get content
			final byte[] content = mBaos.toByteArray();
			mBaos.reset();

			// Mark as delivered and begin processing (bundle needs to be mark as delivered first)
			mExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						mExClient.markDelivered(new BundleID(mBundle));
						mCallback.onBundle(mBundle, content);
					} catch (APIException e) {
						e.printStackTrace();
					}
				}
			});
		}

		@Override
		public void startBlock(Block block) {
		}

		@Override
		public void endBlock() {
			// Currently, we handle everything in endBundle
		}

		@Override
		public OutputStream startPayload() {
			return mBaos;
		}

		@Override
		public void endPayload() {
		}

		@Override
		public void progress(long pos, long total) {
		}

	};

	public DTNClient() throws APIException, IOException {
		this(null);
	}

	public DTNClient(String primaryEid) throws APIException, IOException {
		this(primaryEid, null);
	}

	public DTNClient(String primaryEid, DTNCallback callback) throws APIException, IOException {
		mCallback = callback;

		// All commands should be ran on this executor
		mExecutor = Executors.newSingleThreadExecutor();

		mExClient = new ExtendedClient();
		mExClient.setHandler(mHandler);
		mExClient.open();

		if (primaryEid != null) {
			mExClient.setEndpoint(primaryEid);
		}
	}

	public void send(final Bundle bundle) {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					mExClient.send(bundle);
				} catch (APIException e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Convenience function for sending text
	public void send(EID destination, String content, int lifetime) {
		Bundle bundle = new Bundle(destination, lifetime);
		bundle.appendBlock(new PayloadBlock(content.getBytes()));
		send(bundle);
	}
}
