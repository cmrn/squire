package me.cmrn.squire;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public abstract class MyDialogFragment extends DialogFragment {
	protected DataController data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.data = ((MyApplication)this.getActivity().getApplication()).data;
	}
	
	@Override
	public void onDestroyView() {
		  if (getDialog() != null && getRetainInstance())
		    getDialog().setOnDismissListener(null);
		  super.onDestroyView();
	}
	
}
