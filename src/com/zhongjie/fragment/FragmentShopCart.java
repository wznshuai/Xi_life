package com.zhongjie.fragment;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhongjie.BaseFragment;
import com.zhongjie.MainActivity;
import com.zhongjie.R;
import com.zhongjie.activity.anytimebuy.CommodityDetailsActivity;
import com.zhongjie.activity.shoppingcar.FillOrderActivity;
import com.zhongjie.global.Session;
import com.zhongjie.util.Constants;

public class FragmentShopCart extends BaseFragment {
	private static FragmentShopCart mInstance;

	private ListView mListView;
	private Session mSession;
	private int mCount;
	private View mEmptyView, mRealContent, mBottomView;
	private ImageView mTopRightImg;
	private Map<Integer, Boolean> mCheckdMap;

	public static FragmentShopCart newInstance() {
		if (null == mInstance)
			mInstance = new FragmentShopCart();
		return mInstance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_shoppingcar, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		findViews();
		initViews();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			initViews();
		} else {
			getActivityMine().setTopCenterLogo(R.drawable.ic_top_logo);
			mTopRightImg.setImageResource(0);
			mTopRightImg.setVisibility(View.GONE);
		}
	}

	@Override
	protected void initData() {
		super.initData();
		mSession = Session.getSession();
	}

	@Override
	protected void findViews() {
		super.findViews();
		mTopRightImg = (ImageView) getActivity().findViewById(
				R.id.topbar_rightImg);
		mEmptyView = findViewById(R.id.fra_shoppingcar_empty);
		mListView = (ListView) findViewById(R.id.fra_shoppingcar_listview);
		mRealContent = findViewById(R.id.fra_shoppingcar_realcontent);
		mBottomView = findViewById(R.id.fra_shoppingcar_bottom);
	}

	@Override
	protected void initViews() {
		super.initViews();
		mCheckdMap = new HashMap<Integer, Boolean>();
		getActivityMine().setTopCenterLogo(R.drawable.ic_logo_shoppingcar);
		mTopRightImg.setImageResource(R.drawable.ic_trash_big);
		mCount = mSession.getInt(Constants.SHOP_CART_KEY);
		if (mCount > 0) {
			mEmptyView.setVisibility(View.GONE);
			mListView.setAdapter(new MyShoppingAdapter());
			mRealContent.setVisibility(View.VISIBLE);
			mBottomView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(getActivity(),
							FillOrderActivity.class));
				}
			});
		} else {
			mEmptyView.setVisibility(View.VISIBLE);
			mListView.setAdapter(null);
			mRealContent.setVisibility(View.GONE);
			findViewById(R.id.fra_shoppingcar_goAnytimeBuy).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							((MainActivity) getActivity())
									.setCurrentTabByTag(MainActivity.TAB_1);
							;
						}
					});
		}
	}

	class MyShoppingAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (null == convertView) {
				vh = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.listview_item_shoppingcar, null);
				vh.checkbox = (CheckBox) convertView
						.findViewById(R.id.list_item_shoppingcar_checkbox);
				vh.edittext = (EditText) convertView
						.findViewById(R.id.list_item_shoppingcar_edittext);
				convertView.setTag(vh);
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(getActivity(),
								CommodityDetailsActivity.class));
					}
				});
				vh.checkbox.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CheckBox check = (CheckBox) v;
						if (null == mCheckdMap)
							mCheckdMap = new HashMap<Integer, Boolean>();
						if (check.isChecked()) {
							mCheckdMap.put((Integer) check.getTag(),
									check.isChecked());
							mTopRightImg.setVisibility(View.VISIBLE);
							mBottomView.setVisibility(View.VISIBLE);
						} else {
							mCheckdMap.remove((Integer) check.getTag());
							if (mCheckdMap.size() < 1) {
								mTopRightImg.setVisibility(View.GONE);
								mBottomView.setVisibility(View.GONE);
							}
						}

					}
				});
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			if (null != mCheckdMap) {
				for (int i : mCheckdMap.keySet()) {
					System.out.println("key pos : " + i);
				}
			}

			if (null != mCheckdMap && mCheckdMap.size() > 0) {
				if (mCheckdMap.containsKey(position)) {
					vh.checkbox.setChecked(true);
				} else {
					vh.checkbox.setChecked(false);
				}
			}

			vh.checkbox.setTag(position);

			return convertView;
		}

		class ViewHolder {
			CheckBox checkbox;
			EditText edittext;
		}

	}
}
