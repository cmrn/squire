package me.cmrn.squire;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

public class StatEditDialogFragment extends MyDialogFragment {
	private static final int PICKER_RANGE = 100;
	
	private Stat stat;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		try { stat = getArguments().getParcelable("stat"); } catch(NullPointerException e) { }
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View view = inflater.inflate(R.layout.dialog_edit_stat, null);
	    View titleView = inflater.inflate(R.layout.dialog_edit_stat_title, null);
	    
	    final EditText nameEdit = (EditText) titleView.findViewById(R.id.stat_name_edit);
	    nameEdit.clearFocus();
	    
	    ImageButton deleteButton = (ImageButton) titleView.findViewById(R.id.delete_stat);
	    deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				data.deleteStat(stat);
	            StatEditDialogFragment.this.getDialog().cancel();
			}
		});
	    
	    final EditText baseValue = (EditText) view.findViewById(R.id.base_value);
	    
	    final EditText suffixEdit = (EditText) view.findViewById(R.id.suffix);

	    final CheckBox isSigned = (CheckBox) view.findViewById(R.id.signed);
	    final DialogFragment t = this;
	    TextWatcher watcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				AlertDialog dialog = (AlertDialog)t.getDialog();
				if(dialog != null) {
				    EditText nameEdit = (EditText) dialog.findViewById(R.id.stat_name_edit);
				    EditText baseValue = (EditText) dialog.findViewById(R.id.base_value);
					
					Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
					
					if(nameEdit.length() == 0 || baseValue.length() == 0) {
						b.setEnabled(false);
					} else {
						b.setEnabled(true);
					}
				}
			}
			@Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
			@Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
		};
	    
	    
	    nameEdit.addTextChangedListener(watcher);
	    
	    baseValue.addTextChangedListener(watcher);

	    if(stat == null) {
	    	deleteButton.setVisibility(View.GONE);
	    } else {
		    nameEdit.setText(stat.getName());
	    	baseValue.setText("" + stat.getBaseValue());
		    suffixEdit.setText(stat.getSuffix());
		    isSigned.setChecked(stat.isSigned());
	    }

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(view);
	    builder.setCustomTitle(titleView);
	    // Add action buttons
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		           @Override
		           public void onClick(DialogInterface dialog, int id) {
		        	   String name;
		        	   int value;
		        	   String suffix;
		        	   boolean signed;

		        	   name = nameEdit.getText().toString();
		        	   value = Integer.parseInt(baseValue.getText().toString());
		        	   suffix = suffixEdit.getText().toString();
		        	   signed = isSigned.isChecked();
		        	   
		        	   if(stat == null) {
		        		   stat = data.createStat(name, value, signed, suffix);
		        	   } else {
		        		   stat.setName(name);
		        		   stat.setBaseValue(value);
		        		   stat.setSigned(signed);
		        		   stat.setSuffix(suffix);
			               data.updateStat(stat);
		        	   }
		           }
		       });
		builder.setNegativeButton("Cancel", null);
		AlertDialog d = builder.create();
		
		if(stat == null) {
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
		} else {
			// make numpad show for value
		    d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		    baseValue.requestFocus();
		}
	    
	    return d;
	}
}
