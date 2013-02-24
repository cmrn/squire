package me.cmrn.squire;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

public class NagDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	final TextView message = new TextView(getActivity());
    	// i.e.: R.string.dialog_message =>
	            // "Test this dialog following the link to dtmilano.blogspot.com"
    	final SpannableString s = 
	               new SpannableString("I'm sorry to nag you like this, but I just wanted to remind you that this is a beta. " + 
	            		               "Please check here for new versions: cmrn.github.com/squire");
    	Linkify.addLinks(s, Linkify.WEB_URLS);
    	message.setText(s);
    	message.setTextSize(16);
    	float scale = getResources().getDisplayMetrics().density;
    	int px = (int) (16 * scale + 0.5f);
    	message.setPadding(px, px, px, px);
    	message.setMovementMethod(LinkMovementMethod.getInstance());
	  
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("This is a beta!").setView(message).setPositiveButton("Okay", null);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}