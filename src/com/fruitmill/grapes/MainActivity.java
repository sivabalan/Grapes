package com.fruitmill.grapes;

import com.fruitmill.grapes.adapter.TabsPagerAdapter;
import com.fruitmill.grapes.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View.OnClickListener;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.app.ActionBar;
import android.app.SearchManager;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private Tab prevTab;
	// Tab titles
	private String[] tabs = { "Feed", "Map", "My Videos" };
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
 
        viewPager.setAdapter(mAdapter);
        //actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
 
        // Adding Tabs
	    for(String tab_name : tabs) {
	        actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
	    }
	    
	    /**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
    }

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		prevTab = tab;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		
		searchView.setOnSearchClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.v("jii","Baka Ouji");
				actionBar.setSelectedNavigationItem(1); // 1 - Map fragment
				viewPager.setCurrentItem(actionBar.getSelectedNavigationIndex());
			}
		});
			
		searchView.setOnCloseListener(new OnCloseListener() {
			
			@Override
			public boolean onClose() {
				actionBar.setSelectedNavigationItem(prevTab.getPosition()); // 1 - Map fragment
				viewPager.setCurrentItem(actionBar.getSelectedNavigationIndex());
				return false;
			}
		});	
		
		return super.onCreateOptionsMenu(menu);
		//return true;
	}
	
	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
//			case R.id.action_search:
//				Log.v("jii","Baka Ouji");
//				actionBar.setSelectedNavigationItem(1); // 1 - Map fragment
//				viewPager.setCurrentItem(actionBar.getSelectedNavigationIndex());
//				return true;
			case R.id.action_settings:
				Log.v("jii","Hasegawa");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}