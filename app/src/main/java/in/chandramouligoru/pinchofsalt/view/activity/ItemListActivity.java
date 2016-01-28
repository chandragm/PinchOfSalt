package in.chandramouligoru.pinchofsalt.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import javax.inject.Inject;

import in.chandramouligoru.pinchofsalt.BuildConfig;
import in.chandramouligoru.pinchofsalt.R;
import in.chandramouligoru.pinchofsalt.api.RetrofitService;
import in.chandramouligoru.pinchofsalt.app.PinchOfSaltApplication;
import in.chandramouligoru.pinchofsalt.dummy.DummyContent;
import in.chandramouligoru.pinchofsalt.response.JsonResponse;
import in.chandramouligoru.pinchofsalt.utils.JsonUtils;
import in.chandramouligoru.pinchofsalt.view.adapter.ItemRecyclerViewAdapter;
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

	@Inject
	protected JsonFactory mJsonFactory;

	@Inject
	protected RetrofitService mRetrofitService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);
		((PinchOfSaltApplication) getApplication()).getAppComponent().initialize(this);

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

		View recyclerView = findViewById(R.id.item_list);
		assert recyclerView != null;
		setupRecyclerView((RecyclerView) recyclerView);
	}

	private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
		recyclerView.setAdapter(new ItemRecyclerViewAdapter(DummyContent.ITEMS, (findViewById(R.id.item_detail_container) != null), getSupportFragmentManager()));
	}

	private void loadData() {
		compositeSubscription.add(mRetrofitService.getJson(BuildConfig.FILE_NAME)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.map((ResponseBody response) -> {
					if (response != null)
						try {
							return mJsonFactory.createParser(response.byteStream());
						} catch (IOException e) {
							e.printStackTrace();
						}
					return null;
				})
				.map(jsonParser -> {
					readLargeJson(jsonParser);
					return new JsonResponse();
				})
				.subscribe(new Subscriber<JsonResponse>() {
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
					public void onNext(JsonResponse result) {
//						Log.e(TAG, "title: " + result.title);
//						Log.e(TAG, "description: " + result.description);
//						Log.e(TAG, "image: " + result.image);
					}
				}));
	}

	int count = 0;

	private void readLargeJson(JsonParser jp) {
		try {
			JsonUtils.parseJson(jp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private JsonResponse parseJsonStream(JsonParser jp) {
		JsonToken current;
		JsonResponse result = null;
		try {
			current = jp.nextToken();
			if (current != JsonToken.START_ARRAY) {
				Log.e(TAG, "Error: root should be an array: quiting.");
				return null;
			}
			while ((current = jp.nextToken()) != JsonToken.END_ARRAY) {
				if (current == JsonToken.START_OBJECT) {
					while ((current = jp.nextToken()) != JsonToken.END_OBJECT) {
						count++;
						// read the record into a tree model,
						// this moves the parsing position to the end of it
						JsonNode node = jp.readValueAsTree();
						// And now we have random access to everything in the object
						result = new JsonResponse();
						result.image = node.get("image").textValue();
						result.description = node.get("description").textValue();
						result.title = node.get("title").textValue();

						Log.e(TAG, "count = "+ count);
						Log.e(TAG, "current = "+ current);
					}
				} else {
					if(current == JsonToken.END_ARRAY)
						break;
					System.out.println("Error: records should be an object: skipping.");
					jp.skipChildren();
				}
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
