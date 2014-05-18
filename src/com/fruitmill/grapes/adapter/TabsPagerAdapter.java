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
	}

	@Override
	public Fragment getItem(int index) {
		
		Fragment tempFragment = null; 
		
		switch (index) {
		case 0:
			// Feed fragment activity
			tempFragment = new FeedFragment();
			break;
		case 1:
			// Map fragment activity
			tempFragment = new MapFragment();
			break;
		case 2:
			// My videos fragment activity
			tempFragment = new MyVideosFragment();
			break;
		}
		fragmentTrackerMap.put(index, tempFragment);
		return tempFragment;
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
