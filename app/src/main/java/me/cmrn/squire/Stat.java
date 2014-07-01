package me.cmrn.squire;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Stat implements Parcelable {
	public static final String ID_COLUMN = "id";
	public static final String NAME_COLUMN = "name";
	public static final String BASE_VALUE_COLUMN = "base_value";
	public static final String IS_SIGNED_COLUMN = "is_signed";
	public static final String SUFFIX_COLUMN = "suffix";
	
	private int id;
	private String name;
	private int value;
	private boolean signed;
	private List<Effect> effects;
	private String suffix;
	
	public Stat() { 
		effects = new ArrayList<Effect>();
	}
	
	public Stat(Cursor c) {
		this.id = DataPersistence.getInt(c, Stat.ID_COLUMN);
		this.name = DataPersistence.getString(c, Stat.NAME_COLUMN);
		this.value = DataPersistence.getInt(c, Stat.BASE_VALUE_COLUMN);
		this.signed = DataPersistence.getBoolean(c, Stat.IS_SIGNED_COLUMN);
		this.effects = new ArrayList<Effect>();
		this.suffix = DataPersistence.getString(c, Stat.SUFFIX_COLUMN);
	}
	
	public Stat(Parcel p) {
		this.id = p.readInt();
		this.name = p.readString();
		this.value = p.readInt();
		this.signed = (Boolean) p.readValue(boolean.class.getClassLoader());
		p.readList(effects, Effect.class.getClassLoader());
		this.suffix = p.readString();
	}
	
	public String toString() {
		return name;
	}

	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getBaseValue() {
		return value;
	}
	
	public int getCurrentValue() {
		int curVal = value;
		for(Effect effect : effects)
			curVal += effect.getValue();
		return curVal;
	}
	
	public void setBaseValue(int value) {
		this.value = value;
	}
	
	public boolean isSigned() {
		return signed;
	}
	
	public void setSigned(boolean signed) {
		this.signed = signed;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public List<Effect> getEffects() {
		return new ArrayList<Effect>(effects);
	}
	
	public void setEffects(List<Effect> effects) {
		this.effects = new ArrayList<Effect>(effects);
	}

	public void addEffect(Effect effect) {
		this.effects.add(effect);
	}
	
	public void removeEffect(Effect effect) {
		for(Effect e : effects) {
			if(e.getModifierID() == effect.getModifierID() && e.getStatID() == e.getStatID()) {
				this.effects.remove(e);
				break;
			}
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(value);
		dest.writeValue(signed);
		dest.writeList(effects);
		dest.writeString(suffix);
	}
	
	public static final Creator<Stat> CREATOR
    = new Creator<Stat>() {
		public Stat createFromParcel(Parcel in) {
		    return new Stat(in);
		}
		
		public Stat[] newArray(int size) {
		    return new Stat[size];
		}
	};
}
