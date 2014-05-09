package com.fruitmill.grapes.adapter;

import com.fruitmill.grapes.FeedFragment;
import com.fruitmill.grapes.MapFragment;
import com.fruitmill.grapes.MyVideosFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	
	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Feed fragment activity
			return new FeedFragment();
		case 1:
			// Map fragment activity
			return new MapFragment();
		case 2:
			// My videos fragment activity
			return new MyVideosFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}
}
