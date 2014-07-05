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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DropListener;

public class ModifiersFragment extends MyFragment {
	private ModifiersAdapter adapter;
	private boolean editMode;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.editMode = getArguments().getBoolean("editMode");
		View view = inflater.inflate(R.layout.fragment_modifiers, container, false);

		DragSortListView list = (DragSortListView) view.findViewById(R.id.modifiers_list);
		DragSortController controller = new DragSortController(list);
		TypedArray ta = getActivity().getTheme().obtainStyledAttributes( new int[] { android.R.attr.colorBackground });
		controller.setBackgroundColor(ta.getColor(0, 0xffffff));
		ta.recycle();
        controller.setDragHandleId(R.id.drag_handle);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DOWN);
        list.setFloatViewManager(controller);
        list.setOnTouchListener(controller);
        list.setDropListener(new DropListener() {
    		@Override
    		public void drop(int from, int to) {
            	data.moveModifier(from, to);
    		}
		});
        
		adapter = new ModifiersAdapter(getActivity());
		
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(editMode) {
					Modifier modifier = (Modifier)parent.getItemAtPosition(position);
					ModifierDialogFragment dialog = new ModifierDialogFragment();
					Bundle b = new Bundle();
					b.putParcelable("modifier", modifier);
					dialog.setArguments(b);
					dialog.show(getFragmentManager(), "modifier");
				} else {
				  CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);
				  checkbox.performClick();
				}
			}
		});
		
		ImageButton addButton = (ImageButton) view.findViewById(R.id.new_modifier_button);
		addButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ModifierDialogFragment dialog = new ModifierDialogFragment();
				dialog.show(getFragmentManager(), "modifier");
			}
		});
		addButton.setVisibility(editMode ? View.VISIBLE : View.GONE);
		
		return view;
	}
	
	@Override
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
		Activity activity = getActivity();
		ImageButton newButton = (ImageButton) activity.findViewById(R.id.new_modifier_button);
		DragSortListView list = (DragSortListView) activity.findViewById(R.id.modifiers_list);
		if(editMode) {
			list.setDragEnabled(true);
			
			TranslateAnimation anim = new TranslateAnimation(0,0,100,0);
			anim.setDuration(250);
			anim.setInterpolator(new DecelerateInterpolator());
			newButton.setVisibility(View.VISIBLE);
			newButton.setAnimation(anim);
		} else {			
			list.setDragEnabled(false);
			
			TranslateAnimation anim = new TranslateAnimation(0,0,0,100);
			anim.setDuration(250);
			anim.setInterpolator(new DecelerateInterpolator());
			newButton.setAnimation(anim);
			newButton.setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onModifiersUpdated() {
		if(adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

    @Override
    public void onStatsUpdated() {

    }

    @Override
    public void onCharactersUpdated() {

    }
	
	private class ModifiersAdapter extends ArrayAdapter<Modifier> {
		private static final int MODIFIER_VIEW = R.layout.list_item_modifier;
		private Context context;
		
		public ModifiersAdapter(Context context) {
			super(context, MODIFIER_VIEW, data.getModifiers());
			this.context = context;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			Modifier modifier = this.getItem(position);
			
			if(convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    convertView = inflater.inflate(MODIFIER_VIEW, parent, false);
			}
		    
		    TextView nameText = (TextView) convertView.findViewById(R.id.name);
		    TextView effectsText = (TextView) convertView.findViewById(R.id.effects);
		    CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
		    ImageView dragHandle = (ImageView) convertView.findViewById(R.id.drag_handle);
		    
		    nameText.setText(modifier.getName());
		    
		    String effectsString = "";
		    for(Effect effect : modifier.getEffects()) {
		    	if(!effectsString.isEmpty()) effectsString += ", ";
		    	Stat stat = data.getStat(effect.getStatID());
		    	effectsString += String.format("%+d", effect.getValue()) + " " + stat.getName();
		    }
		    effectsText.setText(effectsString);

		    checkbox.setTag(modifier);
		    checkbox.setChecked(modifier.isEnabled());
		    checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Modifier modifier = (Modifier) buttonView.getTag();
					if(isChecked && !modifier.isEnabled())
						data.enableModifier(modifier);
					else if(!isChecked && modifier.isEnabled())
						data.disableModifier(modifier);
				}
			});
		    
		    if(editMode) {
		    	checkbox.setVisibility(View.GONE);
		    	dragHandle.setVisibility(View.VISIBLE);
		    } else {
		    	checkbox.setVisibility(View.VISIBLE);
		    	dragHandle.setVisibility(View.GONE);
		    }

		    return convertView;
		}
	}
}
