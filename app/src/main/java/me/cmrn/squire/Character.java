package me.cmrn.squire;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Character implements Parcelable {
    public static final String ID_COLUMN = "id";
    public static final String NAME_COLUMN = "name";
    private int id;
    private String name;

    public Character() {
    }

    public Character(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Character(Cursor c) {
        this.id = DataPersistence.getInt(c, Character.ID_COLUMN);
        this.name = DataPersistence.getString(c, Character.NAME_COLUMN);
    }

    public Character(Parcel p) {
        this.id = p.readInt();
        this.name = p.readString();
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

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Character))
            return false;

        Character c = (Character) obj;

        return (this.getID() == c.getID() && this.getName() == c.getName());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    public static final Creator<Character> CREATOR
            = new Creator<Character>() {
        public Character createFromParcel(Parcel in) {
            return new Character(in);
        }

        public Character[] newArray(int size) {
            return new Character[size];
        }
    };
}
