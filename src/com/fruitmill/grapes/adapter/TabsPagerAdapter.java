package com.fruitmill.grapes.adapter;

import java.util.HashMap;

import com.fruitmill.grapes.FeedFragment;
import com.fruitmill.grapes.MapFragment;
import com.fruitmill.grapes.MyVideosFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	
	private HashMap<Integer,Fragment> fragmentTrackerMap;
	
	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
		fragmentTrackerMap = new HashMap<Integer,Fragment>();
		fragmentTrackerMap.put(0, new FeedFragment());
		fragmentTrackerMap.put(1, new MapFragment());
		fragmentTrackerMap.put(2, new MyVideosFragment());
	}

	@Override
	public Fragment getItem(int index) {
		return fragmentTrackerMap.get(index);
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}
	
	public Fragment getFragment(int key) {
		return fragmentTrackerMap.get(key);
	}
	
}
