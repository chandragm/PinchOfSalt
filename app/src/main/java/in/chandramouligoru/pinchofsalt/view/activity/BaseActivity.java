package in.chandramouligoru.pinchofsalt.view.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import in.chandramouligoru.pinchofsalt.R;
import in.chandramouligoru.pinchofsalt.app.PinchOfSaltApplication;
import in.chandramouligoru.pinchofsalt.utils.NetworkConnectionUtils;
import rx.subscriptions.CompositeSubscription;

public class BaseActivity extends AppCompatActivity {
	protected CompositeSubscription compositeSubscription;

	@Inject
	protected NetworkConnectionUtils mNetworkConnectionUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((PinchOfSaltApplication) getApplicationContext()).getAppComponent().initialize(this);
		compositeSubscription = new CompositeSubscription();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (compositeSubscription != null) {
			compositeSubscription.unsubscribe();
			compositeSubscription = null;
		}

		mNetworkConnectionUtils = null;
	}

	public boolean isNetworkAvailable() {
		return mNetworkConnectionUtils.isConnected();
	}

	public void showNoNetworkError() {
		Snackbar.make(findViewById(android.R.id.content), getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
	}

	public void showLoadCompletion() {
		Snackbar.make(findViewById(android.R.id.content), getString(R.string.load_completed), Snackbar.LENGTH_LONG).show();
	}

	public void showLoadFailed() {
		Snackbar.make(findViewById(android.R.id.content), getString(R.string.load_failed), Snackbar.LENGTH_LONG).show();
	}
}
