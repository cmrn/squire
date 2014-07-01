package me.cmrn.squire;

import android.app.DialogFragment;

import android.os.Bundle;

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
