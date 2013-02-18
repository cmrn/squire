package me.cmrn.squire;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class Modifier {
	public static final String ID_COLUMN = "id";
	public static final String NAME_COLUMN = "name";
	public static final String IS_ENABLED_COLUMN = "is_enabled";
	public static final String POSITION_COLUMN = "position";
	
	private int id;
	private String name;
	private List<Effect> effects;
	private boolean enabled;
	
	public Modifier() {
		this.effects = new ArrayList<Effect>();
	}
	
	public Modifier(Cursor c) {
		this.id = DataPersistence.getInt(c, Modifier.ID_COLUMN);
		this.name = DataPersistence.getString(c, Modifier.NAME_COLUMN);
		this.enabled = DataPersistence.getBoolean(c, Modifier.IS_ENABLED_COLUMN);
		this.effects = new ArrayList<Effect>();
	}
	
	public String toString() {
		return name;
	}
	
	public int getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
			if(e.getModifierID() == effect.getModifierID() && e.getStatID() == effect.getStatID()) {
				this.effects.remove(e);
				break;
			}
		}
	}

	public void updateEffect(Effect effect) {
		for(Effect e : effects) {
			if(e.getModifierID() == effect.getModifierID() && e.getStatID() == effect.getStatID()) {
				this.effects.set(effects.indexOf(e), effect);
				break;
			}
		}
	}
	
	public boolean hasEffect(Effect effect) {
		for(Effect e : effects) {
			if(e.getModifierID() == effect.getModifierID() && e.getStatID() == effect.getStatID() && e.getValue() == effect.getValue()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
