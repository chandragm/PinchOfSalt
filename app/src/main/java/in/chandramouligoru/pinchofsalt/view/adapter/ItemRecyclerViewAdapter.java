package in.chandramouligoru.pinchofsalt.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.chandramouligoru.pinchofsalt.R;
import in.chandramouligoru.pinchofsalt.dummy.DummyContent;
import in.chandramouligoru.pinchofsalt.view.activity.ItemDetailActivity;
import in.chandramouligoru.pinchofsalt.view.fragment.ItemDetailFragment;

/**
 * Created by cgoru on 28/01/16.
 */
public class ItemRecyclerViewAdapter
		extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

	private final List<DummyContent.DummyItem> mValues;
	private boolean mTwoPane;
	private FragmentManager mSupportFragmentManager;

	public ItemRecyclerViewAdapter(List<DummyContent.DummyItem> items, boolean isTwoPane, FragmentManager fragmentManager) {
		mValues = items;
		mTwoPane = isTwoPane;
		mSupportFragmentManager = fragmentManager;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_list_content, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		holder.mItem = mValues.get(position);
		holder.mIdView.setText(mValues.get(position).id);
		holder.mContentView.setText(mValues.get(position).content);

		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTwoPane) {
					Bundle arguments = new Bundle();
					arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
					ItemDetailFragment fragment = new ItemDetailFragment();
					fragment.setArguments(arguments);
					mSupportFragmentManager.beginTransaction()
							.replace(R.id.item_detail_container, fragment)
							.commit();
				} else {
					Context context = v.getContext();
					Intent intent = new Intent(context, ItemDetailActivity.class);
					intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);

					context.startActivity(intent);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return mValues.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final TextView mIdView;
		public final TextView mContentView;
		public DummyContent.DummyItem mItem;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mIdView = (TextView) view.findViewById(R.id.id);
			mContentView = (TextView) view.findViewById(R.id.content);
		}

		@Override
		public String toString() {
			return super.toString() + " '" + mContentView.getText() + "'";
		}
	}
}