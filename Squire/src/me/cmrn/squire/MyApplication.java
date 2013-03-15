package me.cmrn.squire;


public class MyApplication extends org.holoeverywhere.app.Application {
	public DataController data;
	public boolean editMode;
	
	@Override
	public void onCreate() {
		data = new DataController(getApplicationContext());
	}
}
