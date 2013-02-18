package me.cmrn.squire;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataPersistence extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "database"; 
    private static final String STATS_TABLE_NAME = "stats";
    private static final String MODIFIERS_TABLE_NAME = "modifiers";
    private static final String EFFECTS_TABLE_NAME = "effects";
    
    private SQLiteDatabase db;
    
	public DataPersistence(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		db = this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase thisDB) {
		thisDB.beginTransaction();
		try {
			thisDB.execSQL("CREATE TABLE " + STATS_TABLE_NAME + " (" +
	                Stat.ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
	                Stat.NAME_COLUMN + " TEXT NOT NULL, " +
	                Stat.BASE_VALUE_COLUMN + " INTEGER NOT NULL, " +
	                Stat.IS_SIGNED_COLUMN + " INTEGER NOT NULL, " +
	                Stat.SUFFIX_COLUMN + " TEXT NOT NULL " +
	                ");");
			thisDB.execSQL("CREATE TABLE " + MODIFIERS_TABLE_NAME + " (" +
	                Modifier.ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
	                Modifier.NAME_COLUMN + " TEXT NOT NULL, " +
	                Modifier.IS_ENABLED_COLUMN + " INTEGER NOT NULL, " +
	                Modifier.POSITION_COLUMN + " INTEGER NOT NULL " +
	                ");");
			thisDB.execSQL("CREATE TABLE " + EFFECTS_TABLE_NAME + " (" +
	                Effect.STAT_ID_COLUMN + " INTEGER NOT NULL, " +
	                Effect.MODIFIER_ID_COLUMN + " INTEGER NOT NULL, " +
	                Effect.VALUE_COLUMN + " INTEGER NOT NULL, " +
	                "FOREIGN KEY(" + Effect.STAT_ID_COLUMN + ") REFERENCES " + STATS_TABLE_NAME + "(" + Stat.ID_COLUMN + ")" +
	                "FOREIGN KEY(" + Effect.MODIFIER_ID_COLUMN + ") REFERENCES " + MODIFIERS_TABLE_NAME + "(" + Modifier.ID_COLUMN + ")" +
	                "PRIMARY KEY (" + Effect.STAT_ID_COLUMN  + "," + Effect.MODIFIER_ID_COLUMN + ")" +
	                ");");
			thisDB.setTransactionSuccessful();
		} finally {
			thisDB.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase thisDB, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public Cursor getAllStats() {
		Cursor cursor = db.query(STATS_TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}
	
	public Cursor getAllModifiers() {
		Cursor cursor = db.query(MODIFIERS_TABLE_NAME, null, null, null, null, null, Modifier.POSITION_COLUMN);
		return cursor;
	}
	
	public Stat createStat(String name, int baseValue, boolean isSigned, String suffix) {
		Stat stat = null;
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put(Stat.NAME_COLUMN, name);
			values.put(Stat.BASE_VALUE_COLUMN, baseValue);
			values.put(Stat.IS_SIGNED_COLUMN, isSigned ? 1 : 0);
			values.put(Stat.SUFFIX_COLUMN, suffix);
			long id = db.insert(STATS_TABLE_NAME, null, values);
			if(id != -1) {
				Cursor c = db.query(STATS_TABLE_NAME, null, "ROWID="+id, null, null, null, null);
				c.moveToFirst();
				stat = new Stat(c);
				c.close();
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return stat;
	}
	
	public Modifier createModifier(String name) {
		Modifier modifier;
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put(Modifier.NAME_COLUMN, name);
			values.put(Modifier.IS_ENABLED_COLUMN, false);
			Cursor c = db.rawQuery("SELECT MAX("+Modifier.POSITION_COLUMN+") AS maxpos FROM " + MODIFIERS_TABLE_NAME, null);
			c.moveToFirst();
			int maxPos = -1;
			if(!c.isNull(0)) maxPos = c.getInt(0);
			values.put(Modifier.POSITION_COLUMN, maxPos+1);
			long id = db.insert(MODIFIERS_TABLE_NAME, null, values);
			c = db.query(MODIFIERS_TABLE_NAME, null, "ROWID="+id, null, null, null, null);
			c.moveToFirst();
			modifier = new Modifier(c);
			c.close();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return modifier;
	}

	public Effect createEffect(Effect e) {
		return createEffect(e.getStatID(), e.getModifierID(), e.getValue());
	}
	
	public Effect createEffect(int statID, int modifierID, int value) {
		Effect effect;
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put(Effect.STAT_ID_COLUMN, statID);
			values.put(Effect.MODIFIER_ID_COLUMN, modifierID);
			values.put(Effect.VALUE_COLUMN, value);
			long id = db.insert(EFFECTS_TABLE_NAME, null, values);
			Cursor c = db.query(EFFECTS_TABLE_NAME, null, "ROWID="+id, null, null, null, null);
			c.moveToFirst();
			effect = new Effect(c);
			c.close();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return effect;
	}
	
	public void createEffects(List<Effect> effects) {
		db.beginTransaction();
		try {
			for(Effect e : effects) {
				ContentValues values = new ContentValues();
				values.put(Effect.STAT_ID_COLUMN, e.getStatID());
				values.put(Effect.MODIFIER_ID_COLUMN, e.getModifierID());
				values.put(Effect.VALUE_COLUMN, e.getValue());
				db.insert(EFFECTS_TABLE_NAME, null, values);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public List<Stat> getAllStatsList() {
		List<Stat> stats;
		db.beginTransaction();
		try {
			Cursor c = db.query(STATS_TABLE_NAME, null, null, null, null, null, null);
			stats = new ArrayList<Stat>(c.getCount());
			if(c.moveToFirst()) {
				do {
					Stat stat = new Stat(c);
					stat.setEffects(getStatEffects(stat.getID()));
					stats.add(stat);
				} while (c.moveToNext());
			}
			c.close();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		return stats;
	}
	
	public List<Modifier> getAllModifiersList() {
		List<Modifier> modifiers;
		db.beginTransaction();
		try {
			Cursor c = db.query(MODIFIERS_TABLE_NAME, null, null, null, null, null, Modifier.POSITION_COLUMN + " ASC");
			modifiers = new ArrayList<Modifier>(c.getCount());
			if(c.moveToFirst()) {
				do {
					Modifier modifier = new Modifier(c);
					modifier.setEffects(getModifierEffects(modifier.getID()));
					modifiers.add(modifier);
				} while (c.moveToNext());
			}
			c.close();
			} finally {
				db.endTransaction();
			}
		return modifiers;
	}

	public List<Effect> getModifierEffects(int modifierID) {
		List<Effect> effects;
		db.beginTransaction();
		try {
			Cursor c = db.query(EFFECTS_TABLE_NAME, null, Effect.MODIFIER_ID_COLUMN + "=" + modifierID, null, null, null, null);
			effects = new ArrayList<Effect>(c.getCount());
			if(c.moveToFirst()) {
				do {
					Effect effect = new Effect(c);
					effects.add(effect);
				} while (c.moveToNext());
			}
			c.close();
		} finally {
			db.endTransaction();
		}
		return effects;
	}

	private List<Effect> getStatEffects(int statID) {
		List<Effect> effects;
		db.beginTransaction();
		try {
			Cursor c = db.rawQuery("SELECT " + EFFECTS_TABLE_NAME + ".* " + 
			                       "FROM " + EFFECTS_TABLE_NAME + " INNER JOIN " + MODIFIERS_TABLE_NAME + " " +
			                       "ON " + EFFECTS_TABLE_NAME + ".modifier_id = " + MODIFIERS_TABLE_NAME + ".id " +
			                       "WHERE " + EFFECTS_TABLE_NAME + "." + Effect.STAT_ID_COLUMN + " = " + statID + " " +
			                       "AND " + MODIFIERS_TABLE_NAME + "." + Modifier.IS_ENABLED_COLUMN + " = 1"
			                       , null);
			effects = new ArrayList<Effect>(c.getCount());
			if(c.moveToFirst()) {
				do {
					Effect effect = new Effect(c);
					effects.add(effect);
				} while (c.moveToNext());
			}
			c.close();
		} finally {
			db.endTransaction();
		}
		return effects;
	}

	public void updateModifier(Modifier modifier) {
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put(Modifier.NAME_COLUMN, modifier.getName());
			values.put(Modifier.IS_ENABLED_COLUMN, modifier.isEnabled() ? 1 : 0);
			db.update(MODIFIERS_TABLE_NAME, values, Modifier.ID_COLUMN + "=" + modifier.getID(), null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public void updateModifierEffects(Modifier modifier) {
		List<Effect> prevEffects = getModifierEffects(modifier.getID());
		List<Effect> effects = modifier.getEffects();
		db.beginTransaction();
		try {
		for(int i = 0; i < effects.size(); i++) {
			Effect e = effects.get(i);
			for(int j = 0; i < prevEffects.size(); j++) {
				Effect pe = prevEffects.get(j);
				if(e.getStatID() == pe.getStatID()) {
					if(e.getValue() != pe.getValue()) {
						updateEffect(e);
					}
					prevEffects.remove(pe);
					effects.remove(e);
					i--;
					break;
				}
			}
		}

		for(Effect e : effects) {
			createEffect(e);
		}
		
		for(Effect pe : prevEffects) {
			deleteEffect(pe);
		}

		db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public void moveModifier(Modifier modifier, int oldPosition, int newPosition) {
		if(oldPosition == newPosition) return;
		db.beginTransaction();
		try {
			if(oldPosition > newPosition) {
				db.execSQL("UPDATE "+MODIFIERS_TABLE_NAME+" SET "+Modifier.POSITION_COLUMN+" = "+Modifier.POSITION_COLUMN+" + 1 " +
						"WHERE "+Modifier.POSITION_COLUMN + " >= " + newPosition + " " +
						"AND " + Modifier.POSITION_COLUMN + " < " + oldPosition);
			} else if(oldPosition < newPosition) {
				db.execSQL("UPDATE "+MODIFIERS_TABLE_NAME+" SET "+Modifier.POSITION_COLUMN+" = "+Modifier.POSITION_COLUMN+" - 1 " +
						"WHERE "+Modifier.POSITION_COLUMN + " > " + oldPosition + " " +
						"AND " + Modifier.POSITION_COLUMN + " <= " + newPosition);
			}

			ContentValues values = new ContentValues();
			values.put(Modifier.POSITION_COLUMN, newPosition);
			db.update(MODIFIERS_TABLE_NAME, values, Modifier.ID_COLUMN + " = " + modifier.getID(), null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		
		getAllModifiersList();
	}

	public void updateStat(Stat stat) {
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put(Stat.NAME_COLUMN, stat.getName());
			values.put(Stat.BASE_VALUE_COLUMN, stat.getBaseValue());
			values.put(Stat.IS_SIGNED_COLUMN, stat.isSigned() ? 1 : 0);
			values.put(Stat.SUFFIX_COLUMN, stat.getSuffix());
			db.update(STATS_TABLE_NAME, values, Stat.ID_COLUMN + "=" + stat.getID(), null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	private void updateEffect(Effect effect) {
		ContentValues values = new ContentValues();
		values.put(Effect.VALUE_COLUMN, effect.getValue());
		db.update(EFFECTS_TABLE_NAME, values, Effect.STAT_ID_COLUMN + "=" + effect.getStatID() + " AND " +
											Effect.MODIFIER_ID_COLUMN + "=" + effect.getModifierID(), null);
	}
	
	public void deleteModifier(Modifier modifier) {
		db.beginTransaction();
		try {
			db.delete(MODIFIERS_TABLE_NAME, Modifier.ID_COLUMN + "=" + modifier.getID(), null);
			db.delete(EFFECTS_TABLE_NAME, Effect.MODIFIER_ID_COLUMN + "=" + modifier.getID(), null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void deleteStat(Stat stat) {
		db.beginTransaction();
		try {
			db.delete(STATS_TABLE_NAME, Stat.ID_COLUMN + "=" + stat.getID(), null);
			db.delete(EFFECTS_TABLE_NAME, Effect.STAT_ID_COLUMN + "=" + stat.getID(), null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	private void deleteEffect(Effect effect) {
		db.delete(EFFECTS_TABLE_NAME, Effect.STAT_ID_COLUMN + "=" + effect.getStatID() + " AND " +
											Effect.MODIFIER_ID_COLUMN + "=" + effect.getModifierID(), null);
	}

	public static String getString(Cursor c, String columnName) {
		return c.getString(c.getColumnIndexOrThrow(columnName));
	}

	public static int getInt(Cursor c, String columnName) {
		return c.getInt(c.getColumnIndexOrThrow(columnName));
	}
	
	public static boolean getBoolean(Cursor c, String columnName) {
		return c.getInt(c.getColumnIndexOrThrow(columnName)) == 1;
	}

}
