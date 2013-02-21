package me.cmrn.squire;

import java.util.ArrayList;
import java.util.List;

import me.cmrn.squire.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.PorterDuff;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;


import android.view.animation.DecelerateInterpolator;


import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class ModifierDialogFragment extends MyDialogFragment {
	private Modifier modifier;
	
	private TableLayout effectsTable;
	private EditText nameEdit;

	public ModifierDialogFragment() { }
	
	public ModifierDialogFragment(Modifier modifier) {
		this.modifier = modifier;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {		
	    Context context = getActivity();
	    AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View titleView = inflater.inflate(R.layout.dialog_modifier_title, null);
	    View view = inflater.inflate(R.layout.dialog_modifier, null);
	    effectsTable = (TableLayout) view.findViewById(R.id.effects_table);
	    nameEdit = (EditText) titleView.findViewById(R.id.modifier_name_edit);
	    ImageButton deleteButton = (ImageButton) titleView.findViewById(R.id.delete_modifier);
	    
	    if(modifier != null) {
	    	nameEdit.setText(modifier.getName());
	    	for(Effect e : modifier.getEffects()) {
	    		createNewRow(e);
	    	}
	    } else {
	    	deleteButton.setVisibility(View.GONE);
		    createNewRow();
	    }
	    
	    nameEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				validate();
			}
			@Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
			@Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
		});
	    
	    deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				data.deleteModifier(modifier);
	            ModifierDialogFragment.this.getDialog().cancel();
			}
		});
	    
	    ImageButton newButton = (ImageButton) view.findViewById(R.id.new_effect_button);
	    newButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    createNewRow();
			    validate();
			}
		});
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(view);
	    builder.setCustomTitle(titleView);
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		           @Override
		           public void onClick(DialogInterface dialog, int id) {
		               updateModifier();
		           }
		       });
		builder.setNegativeButton("Cancel", null);      
	    AlertDialog d = builder.create();
	    
	    if(modifier == null) {
		    // Disable save button (when the button is actually created)
		    d.setOnShowListener(new OnShowListener() {
	            @Override
	            public void onShow(DialogInterface dialog) {                    
	        		Button b = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
	        		b.setEnabled(false);
	            }
	        });
		    
		    // make keyboard show for name
		    d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		    nameEdit.requestFocus();
		}
	    
		return d;
	}
	
	private void validate() {
		AlertDialog dialog = (AlertDialog)this.getDialog();
		
		if(dialog != null) {
			Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			
			if(nameEdit.length() == 0) {
				b.setEnabled(false);
				return;
			}
			
			for(int i = 0; i < effectsTable.getChildCount(); i++) {
				TableRow row = (TableRow) effectsTable.getChildAt(i);
				Spinner spinner = (Spinner) row.findViewById(R.id.stat_spinner);
				EditText value = (EditText) row.findViewById(R.id.value_edit);
				if(spinner.getSelectedItemPosition() == Spinner.INVALID_POSITION ||
				   value.length() == 0) {
					b.setEnabled(false);
					return;
				}
			}
			b.setEnabled(true);
		}
	}
	
	private void updateModifier() {
		if(modifier == null) {
			modifier = data.createModifier(nameEdit.getText().toString());
		} else {
			modifier.setName(nameEdit.getText().toString());
		}
		
		ArrayList<Effect> effects = new ArrayList<Effect>(effectsTable.getChildCount());
		for(int i = 0; i < effectsTable.getChildCount(); i++) {
			TableRow row = (TableRow) effectsTable.getChildAt(i);
			Spinner spinner = (Spinner) row.findViewById(R.id.stat_spinner);
			EditText value = (EditText) row.findViewById(R.id.value_edit);
			Effect effect = new Effect();
			effect.setModifierID(modifier.getID());
			effect.setStatID(((Stat)spinner.getSelectedItem()).getID());
			
			String s = value.getText().toString();
			int v = Integer.parseInt(s.charAt(0) == '+' ? s.substring(1) : s); // workaround for + signs
			effect.setValue(v);
			effects.add(effect);
		}
		
		modifier.setEffects(effects);
		data.updateModifier(modifier);
	}
	
	private void createNewRow() {
		this.createNewRow(null);
	}
	
	private void createNewRow(Effect effect) {
		TableRow row = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.dialog_modifier_effect_row, null);
		
		List<Stat> stats = data.getStats();
	    Spinner statSpinner = (Spinner) row.findViewById(R.id.stat_spinner);
	    ImageButton removeButton = (ImageButton) row.findViewById(R.id.remove_effect_button);
	    EditText effectValue = (EditText) row.findViewById(R.id.value_edit);
	    
	    ArrayAdapter<Stat> adapter = new ArrayAdapter<Stat>(getActivity(),
	            android.R.layout.simple_spinner_item,
	            stats.toArray(new Stat[stats.size()]));
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    statSpinner.setAdapter(adapter);
	    
	    statSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				parent.getBackground().setColorFilter(0xff000000, PorterDuff.Mode.DST);
				int pos = parent.getSelectedItemPosition();
				for(int i = 0; i < effectsTable.getChildCount(); i++) {
					TableRow row = (TableRow) effectsTable.getChildAt(i);
					Spinner rowSpinner = (Spinner) row.findViewById(R.id.stat_spinner);
					if(rowSpinner != parent) {
						int thisPos = rowSpinner.getSelectedItemPosition();
						if(pos == thisPos) {
							rowSpinner.getBackground().setColorFilter(0xffff0000, PorterDuff.Mode.SRC_IN);
							parent.getBackground().setColorFilter(0xffff0000, PorterDuff.Mode.SRC_IN);
						} else {
							rowSpinner.getBackground().setColorFilter(0xff000000, PorterDuff.Mode.DST);
						}
					}
				}
				validate();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
	    	
		});
	    
	    effectValue.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				validate();
			}
			@Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
			@Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
		});
	    
	    removeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final View row = (View) v.getParent();
				effectsTable.removeView(row);
				validate();
			}
		});
	    effectsTable.addView(row);
	    
	    AlphaAnimation anim = new AlphaAnimation(0,1);
		anim.setDuration(250);
		anim.setInterpolator(new DecelerateInterpolator());
	    row.setAnimation(anim);
	    
	    if(effect != null) {
	    	effectValue.setText(String.format("%+d", effect.getValue()));
	    	statSpinner.setSelection(adapter.getPosition(data.getStat(effect.getStatID())));
	    }
	}
}
