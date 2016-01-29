package in.chandramouligoru.pinchofsalt.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.common.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;

import in.chandramouligoru.pinchofsalt.api.RetrofitService;
import in.chandramouligoru.pinchofsalt.app.PinchOfSaltApplication;
import in.chandramouligoru.pinchofsalt.events.LoadingJsonCompletedEvent;
import in.chandramouligoru.pinchofsalt.utils.JsonUtils;
import in.chandramouligoru.pinchofsalt.utils.NetworkConnectionUtils;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class JsonLoaderService extends IntentService {
	private static final String TAG = "JsonLoaderService";

	public static final String ACTION_LOAD_JSON = "in.chandramouligoru.pinchofsalt.service.action.LOAD_JSON";
	public static final String EXTRA_JSON_FILE_NAME = "in.chandramouligoru.pinchofsalt.service.extra.JSON_FILE_NAME";

	private RetrofitService mRetrofitService;
	private CompositeSubscription compositeSubscription;
	private NetworkConnectionUtils mNetworkConnectionUtils;
	private EventBus eventBus;
	private JsonUtils mJsonUtils;

	public JsonLoaderService() {
		super("JsonLoaderService");
	}

	/**
	 * Starts this service to perform action Foo with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 *
	 * @see IntentService
	 */
	public static void loadJsonData(Context context, String param1) {
		Intent intent = new Intent(context, JsonLoaderService.class);
		intent.setAction(ACTION_LOAD_JSON);
		intent.putExtra(EXTRA_JSON_FILE_NAME, param1);
		context.startService(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			final String action = intent.getAction();
			if (ACTION_LOAD_JSON.equals(action)) {
				final String fileName = intent.getStringExtra(EXTRA_JSON_FILE_NAME);
				handleActionFoo(fileName);
			}
		}
	}

	/**
	 * Handle action Foo in the provided background thread with the provided
	 * parameters.
	 */
	private void handleActionFoo(String fileName) {
		parseJsonStream(fileName);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mRetrofitService = ((PinchOfSaltApplication) getApplication()).getAppComponent().getRetrofitService();
		compositeSubscription = new CompositeSubscription();
		mJsonUtils = ((PinchOfSaltApplication) getApplication()).getAppComponent().getJsonUtils();
		mNetworkConnectionUtils = ((PinchOfSaltApplication) getApplication()).getAppComponent().getNetworkConnectionUtils();
		eventBus = ((PinchOfSaltApplication) getApplication()).getAppComponent().getEventBus();
	}

	private void parseJsonStream(String jsonFileName) {
		compositeSubscription.add(mRetrofitService.getJson(jsonFileName)
				.map(response -> {
					if (response != null)
						return readLargeJson(response.byteStream());
					return false;
				})
				.filter(aBoolean -> aBoolean)
				.subscribe(new Subscriber<Boolean>() {
					@Override
					public void onCompleted() {
						Log.e(TAG, "successfully completed the fetch.");
					}

					@Override
					public void onError(Throwable e) {
						e.printStackTrace();
						Log.e(TAG, "Failed to fetch JSON file.");
					}

					@Override
					public void onNext(Boolean result) {
						if (result)
							eventBus.post(new LoadingJsonCompletedEvent());
					}
				}));
	}

	private boolean readLargeJson(InputStream inputStream) {
		boolean result = true;
		try {
			mJsonUtils.parseJson(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	@Override
	public void onDestroy() {
		if (compositeSubscription != null) {
			compositeSubscription.unsubscribe();
			compositeSubscription = null;
		}
	}
}
