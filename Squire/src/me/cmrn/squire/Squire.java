package me.cmrn.squire;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Squire extends SherlockFragmentActivity implements
		ActionBar.TabListener {
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	private boolean editMode;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState == null) {
			editMode = false;
		} else {
			editMode = savedInstanceState.getBoolean("editMode");
		}

		// if no data exists, add example data
		DataController data = ((MyApplication)getApplicationContext()).data;
		if(data.getStats().size() == 0 && data.getModifiers().size() == 0) {
			DefaultData.create(data);
		}

		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    	// Load up the fragments in a pager
    	
		// Create the adapter that will return a fragment for each of the
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		if(savedInstanceState != null) {
			mViewPager.setCurrentItem(savedInstanceState.getInt("page"));
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("editMode", editMode);
		savedInstanceState.putInt("page", mViewPager.getCurrentItem());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
	    if(editMode) {
	    	TypedValue typedvalueattr = new TypedValue();
		    getTheme().resolveAttribute(R.attr.menu_done_icon, typedvalueattr, true);
	    	MenuItem item = menu.findItem(R.id.menu_edit);
	    	item.setIcon(typedvalueattr.resourceId);
	    	item.setTitle(R.string.menu_done);
	    } else {
	    	TypedValue typedvalueattr = new TypedValue();
		    getTheme().resolveAttribute(R.attr.menu_edit_icon, typedvalueattr, true);
	    	MenuItem item = menu.findItem(R.id.menu_edit);
	    	item.setIcon(typedvalueattr.resourceId);
	    	item.setTitle(R.string.menu_edit);
	    }
	    return true;
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
	        case R.id.menu_edit:
	        	editMode = !editMode;
	        	getStatsFragment().setEditMode(editMode);
	        	getModifiersFragment().setEditMode(editMode);
	        	invalidateOptionsMenu();
		        return true;
	        default:
	    		return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			android.support.v4.app.FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			android.support.v4.app.FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			android.support.v4.app.FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch(position) {
			case 0:
				return new StatsFragment(editMode);
			case 1:
				return new ModifiersFragment(editMode);
			default:
				throw new IndexOutOfBoundsException("Unknown position");
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_stats).toUpperCase(Locale.getDefault());
			case 1:
				return getString(R.string.title_modifiers).toUpperCase(Locale.getDefault());
			default:
				throw new IndexOutOfBoundsException("Unknown position");
			}
		}
	}
	
	private StatsFragment getStatsFragment() {
		return (StatsFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(0));
	}
	
	private ModifiersFragment getModifiersFragment() {
		return (ModifiersFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(1));
	}
	
	private String makeFragmentName(int index)
	{
	     return "android:switcher:" + mViewPager.getId() + ":" + index;
	}
}
