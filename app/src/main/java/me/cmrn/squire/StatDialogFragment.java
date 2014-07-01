package me.cmrn.squire;

import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StatDialogFragment extends MyDialogFragment {	
	private Stat stat;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		try { stat = getArguments().getParcelable("stat"); } catch(NullPointerException e) { }
		if(savedInstanceState != null) stat = (Stat) savedInstanceState.get("stat");
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View view = inflater.inflate(R.layout.dialog_stat, null);

	    TextView sign = (TextView) view.findViewById(R.id.sign);
	    if(stat.isSigned()) {
	    	if(stat.getBaseValue() > 0) sign.setText("+");
	    	else if(stat.getBaseValue() < 0) sign.setText("-");
	    } else {
		    sign.setText("");
	    }
	    
	    TextView value = (TextView) view.findViewById(R.id.value);
	    String valStr = String.format(Locale.getDefault(), "%+d", stat.getBaseValue());
	    valStr = valStr.substring(1); // remove sign
	    value.setText(valStr);
	    
	    TextView suffix = (TextView) view.findViewById(R.id.suffix);
	    suffix.setText(stat.getSuffix());

	    LinearLayout modifierTexts = (LinearLayout) view.findViewById(R.id.modifiers);
	    
	    if(stat.getEffects().isEmpty()) {
	    	TextView text = new TextView(getActivity());
	    	text.setText("None");
	    	modifierTexts.addView(text);
	    } else {
		    for(Effect effect : stat.getEffects()) {
		    	TextView text = new TextView(getActivity());
		    	text.setText(String.format("%+d", effect.getValue()) + stat.getSuffix() + " " + data.getModifier(effect.getModifierID()).getName());
		    	
				TypedArray ta = getActivity().getTheme().obtainStyledAttributes(
						new int[] { R.attr.default_stat_color, 
									R.attr.positive_stat_color,
									R.attr.negative_stat_color
								  });
				int defColor = ta.getColor(0, 0x000);
				int posColor = ta.getColor(1, 0x000);
				int negColor = ta.getColor(2, 0x000);
				ta.recycle();
			    int color;
			    if     (effect.getValue() > 0) color = posColor;
			    else if(effect.getValue() < 0) color = negColor;
			    else                      color = defColor;
			    text.setTextColor(color);
			    
		    	modifierTexts.addView(text);
		    }
	    }

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(view);
	    // Add action buttons
	    builder.setTitle(stat.getName());
		builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
		           @Override
		           public void onClick(DialogInterface dialog, int id) {
		           }
		       });
	    return builder.create();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable("stat", stat);
	}
}
