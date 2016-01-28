/*
 * Copyright (c) 2011,2012,2013,2014,2015 Mutual Mobile. All rights reserved.
 */

package in.chandramouligoru.pinchofsalt.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;

public class NetworkConnectionUtils {

	private Context mContext;
	private ConnectivityManager mConnectionManager;

	@Inject
	public NetworkConnectionUtils(Context context) {
		mContext = context;
	}

	public boolean isConnected() {
		mConnectionManager = mConnectionManager != null ? mConnectionManager : (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = mConnectionManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}