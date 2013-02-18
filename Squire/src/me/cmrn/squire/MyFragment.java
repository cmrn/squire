package me.cmrn.squire;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;

public abstract class MyFragment extends SherlockFragment implements DataListener {
	protected DataController data;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.data = ((MyApplication)this.getActivity().getApplication()).data;
		data.registerListener(this);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroy() {
		data.deregisterListener(this);
		super.onDestroy();
	}
	
	public abstract void setEditMode(boolean editMode);
}
