package in.chandramouligoru.pinchofsalt.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import in.chandramouligoru.pinchofsalt.R;
import in.chandramouligoru.pinchofsalt.dummy.DummyContent;
import in.chandramouligoru.pinchofsalt.view.adapter.ItemRecyclerViewAdapter;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setTitle(getTitle());

		View recyclerView = findViewById(R.id.item_list);
		assert recyclerView != null;
		setupRecyclerView((RecyclerView) recyclerView);

//		if (findViewById(R.id.item_detail_container) != null) {
//			// The detail container view will be present only in the
//			// large-screen layouts (res/values-w900dp).
//			// If this view is present, then the
//			// activity should be in two-pane mode.
//			mTwoPane = true;
//		}
	}

	private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
		recyclerView.setAdapter(new ItemRecyclerViewAdapter(DummyContent.ITEMS, (findViewById(R.id.item_detail_container) != null), getSupportFragmentManager()));
	}
}
