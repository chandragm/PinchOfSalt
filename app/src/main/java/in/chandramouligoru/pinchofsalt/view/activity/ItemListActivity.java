package in.chandramouligoru.pinchofsalt.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import in.chandramouligoru.pinchofsalt.BuildConfig;
import in.chandramouligoru.pinchofsalt.R;
import in.chandramouligoru.pinchofsalt.api.RetrofitService;
import in.chandramouligoru.pinchofsalt.app.PinchOfSaltApplication;
import in.chandramouligoru.pinchofsalt.response.JsonResponse;
import in.chandramouligoru.pinchofsalt.view.adapter.ItemRecyclerViewAdapter;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends BaseActivity {

	private static final String TAG = "ItemListActivity";

	private Realm realm;

	@Inject
	protected JsonFactory mJsonFactory;

	@Inject
	protected RetrofitService mRetrofitService;

	private RecyclerView mRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);
		((PinchOfSaltApplication) getApplication()).getAppComponent().initialize(this);

		RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
		realm = Realm.getInstance(realmConfiguration);

		initUI();
		if (!isNetworkAvailable())
			showNoNetworkError();
		else
			loadData();
	}

	private void initUI() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setTitle(getTitle());

		mRecyclerView = (RecyclerView) findViewById(R.id.item_list);
		assert mRecyclerView != null;
	}

	private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<JsonResponse> items) {
		recyclerView.setAdapter(new ItemRecyclerViewAdapter(items, (findViewById(R.id.item_detail_container) != null), getSupportFragmentManager()));
	}

	private void loadData() {
		compositeSubscription.add(mRetrofitService.getJson(BuildConfig.FILE_NAME)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.map((ResponseBody response) -> {
					if (response != null)
						return response.byteStream();
					return null;
				})
				.map((InputStream stream) -> {
					try {
						return loadJsonFromStream(stream);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				})
				.map((Boolean aBoolean) -> {
					if (aBoolean) {
						return realm.where(JsonResponse.class).findAllAsync();
					}
					return null;
				})
				.filter((RealmResults<JsonResponse> jsonResponses) -> {
					if (jsonResponses != null)
						return jsonResponses.load();
					else
						return false;
				})
				.map((RealmResults<JsonResponse> jsonResponses) -> jsonResponses.subList(0, jsonResponses.size()))
				.subscribe(new Subscriber<List<JsonResponse>>() {
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
					public void onNext(List<JsonResponse> jsonResponses) {
						if (jsonResponses != null && jsonResponses.size() > 0)
							setupRecyclerView(mRecyclerView, jsonResponses);
					}
				}));
	}

	private boolean loadJsonFromStream(InputStream stream) throws IOException {
		boolean result = false;
		// Open a transaction to store items into the realm
		realm.beginTransaction();
		try {
			realm.createAllFromJson(JsonResponse.class, stream);
			realm.commitTransaction();
			result = true;
		} catch (IOException e) {
			// Remember to cancel the transaction if anything goes wrong.
			realm.cancelTransaction();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return result;
	}
}
