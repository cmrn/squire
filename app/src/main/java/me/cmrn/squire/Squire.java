package me.cmrn.squire;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Locale;

public class Squire extends Activity implements
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

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Context context;
        private int layout;
        private String[] items;

        public MyAdapter(Context context, int layout, String[] items) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.inflater = inflater;
            this.context = context;
            this.layout = layout;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.length + 1;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if(position == getCount()-1) {
                View button = inflater.inflate(R.layout.actionbar_spinner_new_button, parent, false);
                return button;
            } else {
                TextView view;
                if (convertView != null && convertView instanceof TextView) {
                    view = (TextView) convertView;
                } else {
                    view = (TextView) inflater.inflate(layout, parent, false);
                }

                view.setText(items[position]);
                return view;
            }
        }

        // When getting the view for the actionbar, restyle the view to be a title
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(position == getCount()-1) {
                return new TextView(context);
            } else {
                TextView view = (TextView) getDropDownView(position, convertView, parent);
                view.setTextAppearance(getApplicationContext(), R.style.TextAppearance_Title);
                view.setTextColor(getResources().getColor(R.color.text_light_primary));
                view.setPadding(0, 0, 0, 0);
                return view;
            }
        }
    }
    
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
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        final Spinner spinner = new Spinner(this);

        String[] array = new String[]{"Abed Nadir", "Troy Barnes", "Brita Perry"};
        SpinnerAdapter mSpinnerAdapter = new MyAdapter(this, R.layout.actionbar_spinner_item, array);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == spinner.getCount()-1) {
                    CharacterDialogFragment dialog = new CharacterDialogFragment();
                    dialog.show(getFragmentManager(), "new");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        actionBar.setCustomView(spinner);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");


    	// Load up the fragments in a pager
    	
		// Create the adapter that will return a fragment for each of the
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getFragmentManager());
		
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
		getMenuInflater().inflate(R.menu.activity_main, menu);
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
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
		    FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
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
			MyFragment f;
			switch(position) {
			case 0:
				f = new StatsFragment();
				break;
			case 1:
				f = new ModifiersFragment();
				break;
			default:
				throw new IndexOutOfBoundsException("Unknown position");
			}
			Bundle b = new Bundle();
			b.putBoolean("editMode", editMode);
			f.setArguments(b);
			return f;
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
		return (StatsFragment) getFragmentManager().findFragmentByTag(makeFragmentName(0));
	}
	
	private ModifiersFragment getModifiersFragment() {
		return (ModifiersFragment) getFragmentManager().findFragmentByTag(makeFragmentName(1));
	}
	
	private String makeFragmentName(int index)
	{
	     return "android:switcher:" + mViewPager.getId() + ":" + index;
	}
}
