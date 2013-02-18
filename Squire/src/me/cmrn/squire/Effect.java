package me.cmrn.squire;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Effect implements Parcelable {
	public static final String STAT_ID_COLUMN = "stat_id";
	public static final String MODIFIER_ID_COLUMN = "modifier_id";
	public static final String VALUE_COLUMN = "value";
	private int statID;
	private int modifierID;
	private int value;

	public Effect() {
	}
	
	public Effect(int statID, int modifierID, int value) {
		this.statID = statID;
		this.modifierID = modifierID;
		this.value = value;
	}
	
	public Effect(Cursor c) {
		this.modifierID = DataPersistence.getInt(c, Effect.MODIFIER_ID_COLUMN);
		this.statID = DataPersistence.getInt(c, Effect.STAT_ID_COLUMN);
		this.value = DataPersistence.getInt(c, Effect.VALUE_COLUMN);
	}
	
	public Effect(Parcel p) {
		this.modifierID = p.readInt();
		this.statID = p.readInt();
		this.value = p.readInt();
	}

	public int getStatID() {
		return statID;
	}

	public void setStatID(int statID) {
		this.statID = statID;
	}

	public int getModifierID() {
		return modifierID;
	}

	public void setModifierID(int modifierID) {
		this.modifierID = modifierID;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public void blah() {
		
	}

	public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
		if (!(obj instanceof Effect))
            return false;
		
        Effect e = (Effect) obj;
        
        return (this.getStatID() == e.getStatID() && this.getModifierID() == e.getModifierID() && this.getValue() == e.getValue());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(statID);
		dest.writeInt(modifierID);
		dest.writeInt(value);
	}
	
	public static final Parcelable.Creator<Effect> CREATOR
    = new Parcelable.Creator<Effect>() {
		public Effect createFromParcel(Parcel in) {
		    return new Effect(in);
		}
		
		public Effect[] newArray(int size) {
		    return new Effect[size];
		}
	};
}