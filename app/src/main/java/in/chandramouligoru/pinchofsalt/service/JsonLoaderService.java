package in.chandramouligoru.pinchofsalt.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;

import in.chandramouligoru.pinchofsalt.api.RetrofitService;
import in.chandramouligoru.pinchofsalt.app.PinchOfSaltApplication;
import in.chandramouligoru.pinchofsalt.events.ItemAddedEvent;
import in.chandramouligoru.pinchofsalt.realm.RealmDao;
import in.chandramouligoru.pinchofsalt.response.JsonResponse;
import in.chandramouligoru.pinchofsalt.utils.JsonUtils;
import in.chandramouligoru.pinchofsalt.utils.NetworkConnectionUtils;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class JsonLoaderService extends Service {
	private static final String TAG = "JsonLoaderService";

	public static final String ACTION_LOAD_JSON = "in.chandramouligoru.pinchofsalt.service.action.LOAD_JSON";
	public static final String EXTRA_JSON_FILE_NAME = "in.chandramouligoru.pinchofsalt.service.extra.JSON_FILE_NAME";

	private RetrofitService mRetrofitService;
	private CompositeSubscription compositeSubscription;
	private NetworkConnectionUtils mNetworkConnectionUtils;
	private EventBus eventBus;
	private JsonUtils mJsonUtils;
	private RealmDao realmDao;

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
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			final String action = intent.getAction();
			if (ACTION_LOAD_JSON.equals(action)) {
				final String fileName = intent.getStringExtra(EXTRA_JSON_FILE_NAME);
				handleActionFoo(fileName);
			}
		}
		return START_STICKY;
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
		realmDao = ((PinchOfSaltApplication) getApplication()).getAppComponent().getRealmDao();
	}

	private void parseJsonStream(String jsonFileName) {
		Observable.create(new Observable.OnSubscribe<JsonResponse>() {
			@Override
			public void call(Subscriber<? super JsonResponse> subscriber) {
				try {
					Response<ResponseBody> response = mRetrofitService.getJson().execute();
					if (response != null && response.isSuccess()) {
						readLargeJson(response.body().byteStream(), subscriber);
					}
				} catch (IOException e) {
					subscriber.onError(e);
				}
			}
		}).observeOn(Schedulers.io())
				.subscribeOn(Schedulers.computation())
				.subscribe(new Subscriber<JsonResponse>() {
					@Override
					public void onCompleted() {
						Log.e(TAG, "successfully completed the fetch.");
						stopSelf();
					}

					@Override
					public void onError(Throwable e) {
						e.printStackTrace();
						Log.e(TAG, "Failed to fetch JSON file.");
						stopSelf();
					}

					@Override
					public void onNext(JsonResponse jsonResponse) {
						if (jsonResponse != null) {
							realmDao.addItem(jsonResponse);
							eventBus.post(new ItemAddedEvent(jsonResponse));
						}
					}
				});
	}

	private boolean readLargeJson(InputStream inputStream, Subscriber subscriber) {
		boolean result = true;
		try {
			mJsonUtils.parseJson(inputStream, subscriber);
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

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
