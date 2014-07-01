package me.cmrn.squire;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DropListener;

;

public class StatsFragment extends MyFragment {
	private StatsAdapter adapter;
	private boolean editMode;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.editMode = getArguments().getBoolean("editMode");
		View view = inflater.inflate(R.layout.fragment_stats, container, false);
		
		DragSortListView listView = (DragSortListView) view.findViewById(R.id.stats_list);
		
		DragSortController controller = new DragSortController(listView);
		TypedArray ta = getActivity().getTheme().obtainStyledAttributes( new int[] { android.R.attr.colorBackground });
		controller.setBackgroundColor(ta.getColor(0, 0xffffff));
		ta.recycle();
        controller.setDragHandleId(R.id.drag_handle);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DOWN);
        listView.setFloatViewManager(controller);
        listView.setOnTouchListener(controller);
        listView.setDropListener(new DropListener() {
    		@Override
    		public void drop(int from, int to) {
            	data.moveStat(from, to);
    		}
		});

		adapter = new StatsAdapter(getActivity());
		listView.setAdapter(adapter); 

		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
					Stat stat = (Stat) parent.getItemAtPosition(position);
					MyDialogFragment dialog;
					if(editMode) {
						dialog = new StatEditDialogFragment();
					} else {
						dialog = new StatDialogFragment();
					}
					Bundle b = new Bundle();
					b.putParcelable("stat", stat);
					dialog.setArguments(b);
					dialog.show(getFragmentManager(), "stat");
			  }
		});
		
		ImageButton addButton = (ImageButton) view.findViewById(R.id.new_stat_button);
		addButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StatEditDialogFragment dialog = new StatEditDialogFragment();
				dialog.show(getFragmentManager(), "stat");
			}
		});
		addButton.setVisibility(editMode ? View.VISIBLE : View.GONE);
		
		return view;
	}

	@Override
	public void onModifiersUpdated() {
	}

	@Override
	public void onStatsUpdated() {
		if(adapter != null) {
			adapter.notifyDataSetChanged();
		}		
	}
	
	private class StatsAdapter extends ArrayAdapter<Stat> {
		private static final int STAT_VIEW = R.layout.list_item_stat;
		private Context context;
		
		public StatsAdapter(Context context) {
			super(context, STAT_VIEW, data.getStats());
			this.context = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Stat stat = this.getItem(position);
			if(convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    convertView = inflater.inflate(STAT_VIEW, parent, false);
			}
		    
		    TextView name = (TextView) convertView.findViewById(R.id.name);
		    name.setText(stat.getName());
		    

		    TextView value = (TextView) convertView.findViewById(R.id.value);
		    String valStr = String.format("%+d", stat.getCurrentValue());
		    valStr = valStr.substring(1); // remove sign
		    value.setText(valStr);
		    
		    TextView sign = (TextView) convertView.findViewById(R.id.sign);
		    if(stat.isSigned()) {
		    	if(stat.getCurrentValue() > 0) sign.setText("+");
		    	else if(stat.getCurrentValue() < 0) sign.setText("-");
		    } else {
			    sign.setText("");
		    }
		    
		    TextView suffix = (TextView) convertView.findViewById(R.id.suffix);
		    suffix.setText(stat.getSuffix());

			TypedArray ta = getActivity().getTheme().obtainStyledAttributes(
					new int[] { R.attr.default_stat_color, 
								R.attr.positive_stat_color,
								R.attr.negative_stat_color
							  });
			int defColor = ta.getColor(0, 0x000);
			int posColor = ta.getColor(1, 0x000);
			int negColor = ta.getColor(2, 0x000);
			ta.recycle();

		    suffix.setTextColor(defColor);
		    int color;
		    if     (stat.getCurrentValue() > stat.getBaseValue()) color = posColor;
		    else if(stat.getCurrentValue() < stat.getBaseValue()) color = negColor;
		    else                                                  color = defColor;
		    value.setTextColor(color);
		    sign.setTextColor(color);

		    View dragHandle = convertView.findViewById(R.id.drag_handle);
		    
		    if(editMode) {
		    	dragHandle.setVisibility(View.VISIBLE);
		    } else {
		    	if(dragHandle.getVisibility() == View.VISIBLE) dragHandle.setVisibility(View.GONE);
		    }
		    
		    return convertView;
		}
	}

	@Override
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;

		Activity activity = getActivity();
		ImageButton newButton = (ImageButton) activity.findViewById(R.id.new_stat_button);
		DragSortListView list = (DragSortListView) activity.findViewById(R.id.stats_list);
		
		float newButtonHeight = getResources().getDimensionPixelSize(R.dimen.bottom_bar_height);
		float handleWidth = getResources().getDimensionPixelSize(R.dimen.list_control_width);
		
		if(editMode) {
			list.setDragEnabled(true);

			TranslateAnimation anim = new TranslateAnimation(0,0,newButtonHeight,0);
			anim.setDuration(250);
			anim.setInterpolator(new DecelerateInterpolator());
			newButton.setVisibility(View.VISIBLE);
			newButton.setAnimation(anim);
			
			for(int i = 0; i < list.getChildCount(); i++) {
				View item = list.getChildAt(i);
				View dragHandle = item.findViewById(R.id.drag_handle);
			    View valueLayout = item.findViewById(R.id.value_layout);
			    TranslateAnimation anim2 = new TranslateAnimation(handleWidth,0,0,0);
				anim2.setDuration(250);
				valueLayout.setAnimation(anim2);
				dragHandle.setAnimation(anim2);
		    	dragHandle.setVisibility(View.VISIBLE);
			}
		} else {			
			list.setDragEnabled(false);
			 
			TranslateAnimation anim = new TranslateAnimation(0,0,0,newButtonHeight);
			anim.setDuration(250);
			anim.setInterpolator(new DecelerateInterpolator());
			newButton.setAnimation(anim);
			newButton.setVisibility(View.GONE);
			
			for(int i = 0; i < list.getChildCount(); i++) {
				View item = list.getChildAt(i);
				View dragHandle = item.findViewById(R.id.drag_handle);
			    View valueLayout = item.findViewById(R.id.value_layout);
			    TranslateAnimation anim2 = new TranslateAnimation(0,handleWidth,0,0);
				anim2.setDuration(250);
				anim2.setFillAfter(true);
				valueLayout.setAnimation(anim2);
				dragHandle.setAnimation(anim2);
		    	dragHandle.setVisibility(View.INVISIBLE);
			}
		}
	}

}