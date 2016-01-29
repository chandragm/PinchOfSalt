package in.chandramouligoru.pinchofsalt.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.chandramouligoru.pinchofsalt.R;
import in.chandramouligoru.pinchofsalt.response.JsonResponse;
import in.chandramouligoru.pinchofsalt.view.activity.ItemDetailActivity;
import in.chandramouligoru.pinchofsalt.view.activity.ItemListActivity;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private JsonResponse mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			loadSelectedItem(getArguments().getString(ARG_ITEM_ID));

			Activity activity = this.getActivity();
			CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
			if (appBarLayout != null) {
				appBarLayout.setTitle(mItem.getTitle());
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.item_detail, container, false);

		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.id)).setText(mItem.getTitle());
			((TextView) rootView.findViewById(R.id.content)).setText(mItem.getDescription());
			ImageView imageView = (ImageView) rootView.findViewById(R.id.image);


			Glide.with(getActivity())
					.load(mItem.getImage())
					.centerCrop()
					.crossFade()
					.into(imageView);
		}

		return rootView;
	}

	private void loadSelectedItem(String itemId) {
		RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getActivity()).build();
		Realm realm = Realm.getInstance(realmConfiguration);
		mItem = realm.where(JsonResponse.class).equalTo("title", itemId).findFirst();
		realm.close();
	}
}
