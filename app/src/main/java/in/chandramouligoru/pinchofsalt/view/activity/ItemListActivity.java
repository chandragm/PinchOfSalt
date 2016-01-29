package in.chandramouligoru.pinchofsalt.view.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import in.chandramouligoru.pinchofsalt.BuildConfig;
import in.chandramouligoru.pinchofsalt.R;
import in.chandramouligoru.pinchofsalt.app.PinchOfSaltApplication;
import in.chandramouligoru.pinchofsalt.events.ItemAddedEvent;
import in.chandramouligoru.pinchofsalt.events.LoadingJsonCompletedEvent;
import in.chandramouligoru.pinchofsalt.events.LoadingJsonFailedEvent;
import in.chandramouligoru.pinchofsalt.response.JsonResponse;
import in.chandramouligoru.pinchofsalt.service.JsonLoaderService;
import in.chandramouligoru.pinchofsalt.view.adapter.ItemRecyclerViewAdapter;
import io.realm.Realm;
import io.realm.RealmResults;

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
	private RealmResults<JsonResponse> mRealmResults;
	private RecyclerView mRecyclerView;
	private ItemRecyclerViewAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);
		((PinchOfSaltApplication) getApplication()).getAppComponent().initialize(this);
		realm = Realm.getDefaultInstance();

		initUI();
		setupRecyclerView(null);
	}

	private void checkRealmDb() {
		mRealmResults = realm.where(JsonResponse.class).findAllAsync();
		mRealmResults.addChangeListener(() -> {
			if (mRealmResults != null && mRealmResults.size() > 0)
				ItemListActivity.this.setupRecyclerView(mRealmResults);
			else {
				if (!isNetworkAvailable())
					showNoNetworkError();
				else
					loadData();
			}
		});
	}

	private void initUI() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setTitle(getTitle());

		mRecyclerView = (RecyclerView) findViewById(R.id.item_list);
		assert mRecyclerView != null;
	}

	public void onEvent(ItemAddedEvent itemAddedEvent) {
		mAdapter.addData(itemAddedEvent.getJsonResponse());
	}

	public void onEvent(LoadingJsonCompletedEvent loadingJsonCompletedEvent) {
		showLoadCompletion();
	}

	public void onEvent(LoadingJsonFailedEvent loadingJsonFailedEvent) {
		showLoadFailed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkRealmDb();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mRealmResults.removeChangeListeners();
	}

	private void setupRecyclerView(List<JsonResponse> items) {
		if (mAdapter == null)
			mAdapter = new ItemRecyclerViewAdapter(items == null ? new ArrayList<>() : items, (findViewById(R.id.item_detail_container) != null), getSupportFragmentManager());
		mRecyclerView.setAdapter(mAdapter);
	}

	private void loadData() {
		JsonLoaderService.loadJsonData(this, BuildConfig.FILE_NAME);
	}
}
