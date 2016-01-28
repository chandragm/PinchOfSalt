package in.chandramouligoru.pinchofsalt.view.activity;

import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

import in.chandramouligoru.pinchofsalt.R;
import in.chandramouligoru.pinchofsalt.api.RetrofitService;
import in.chandramouligoru.pinchofsalt.app.PinchOfSaltApplication;

public class SplashActivity extends BaseActivity {

	private static final String TAG = "SplashActivity";

	@Inject
	RetrofitService mRetrofitService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		((PinchOfSaltApplication) getApplicationContext()).getAppComponent().initialize(this);
		if (!isNetworkAvailable())
			showNoNetworkError();
		else {
			Intent intent = new Intent(this, ItemListActivity.class);
			startActivity(intent);
			finish();
		}
	}


//	private void loadTopStories() {
//		//Initially load section home.
//		Subscription subscription = mRetrofitService
//				.getTopStories("home", "json", BuildConfig.API_KEY)
//				.map(topStoriesResponse -> {
//					if (topStoriesResponse.status.equalsIgnoreCase("OK")) {
//						return topStoriesResponse.results;
//					} else {
//						List<Article> articles = new ArrayList<>(1);
//						return articles;
//					}
//				})
//				.subscribeOn(Schedulers.io())
//				.observeOn(AndroidSchedulers.mainThread())
//				.subscribe(articles -> {
//					//Now that response is cached lets launch the TopStoriesActivity to show it.
//					Intent intent = new Intent(this, TopStoriesActivity.class);
//					startActivity(intent);
//					finish();
//				});
//		compositeSubscription.add(subscription);
//	}
}
