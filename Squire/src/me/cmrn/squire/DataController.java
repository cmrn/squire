package me.cmrn.squire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;

public class DataController {
	private List<Stat> stats;
	private List<Modifier> modifiers;
	
	private DataPersistence persistence;
	private List<DataListener> listeners;
	
	public DataController(Context context) {
		persistence = new DataPersistence(context);
		stats = persistence.getAllStatsList();
		modifiers = persistence.getAllModifiersList();
		listeners = new ArrayList<DataListener>();
	}
	
	public void registerListener(DataListener listener) {
		listeners.add(listener);
	}
	
	public void deregisterListener(DataListener listener) {
		listeners.remove(listener);
	}

	private void callStatsListeners() {
		for(DataListener listener : listeners) {
			listener.onStatsUpdated();
		}
	}
	
	private void callModifiersListeners() {
		for(DataListener listener : listeners) {
			listener.onModifiersUpdated();
		}
		
	}
	
	public List<Stat> getStats() {
		return Collections.unmodifiableList(stats);
	}
	
	public List<Modifier> getModifiers() {
		return Collections.unmodifiableList(modifiers);
	}
	
	public Stat getStat(int id) {
		for(Stat s : stats)
			if(s.getID() == id)
				return s;
		throw new IllegalArgumentException("No stat with that ID");
	}

	public Modifier getModifier(int id) {
		for(Modifier m : modifiers)
			if(m.getID() == id)
				return m;
		throw new IllegalArgumentException("No modifier with that ID");
	}

	public Stat getStatAtPos(int pos) {
		return stats.get(pos);
	}

	public Modifier getModifierAtPos(int pos) {
		return modifiers.get(pos);
	}
	
	public Stat createStat(String name, int baseValue, boolean isSigned, String suffix) {
		Stat stat = persistence.createStat(name, baseValue, isSigned, suffix);
		stats.add(stat);
		return stat;
	}
	
	public Modifier createModifier(String name) {
		Modifier modifier = persistence.createModifier(name);
		modifiers.add(modifier);
		return modifier;
	}
	
	public Effect createEffect(int statID, int modifierID, int value) {
		Effect effect = persistence.createEffect(statID, modifierID, value);
		getModifier(modifierID).addEffect(effect);
		return effect;
	}

	public void updateStat(Stat stat) {
		final Stat tStat = stat;
		new Thread(new Runnable() {
		   public void run() {
		   	persistence.updateStat(tStat);
		   }
		}).start();
		
		for(Stat s : stats) {
			if(s.getID() == stat.getID()) {
				stats.set(stats.indexOf(s), stat);
				break;
			}
		}
		callStatsListeners();
	}

	public void updateModifier(Modifier modifier) {
		final Modifier tModifier = modifier;
		new Thread(new Runnable() {
		   public void run() {
		   	persistence.updateModifier(tModifier);
		   }
		}).start();
		
		for(Modifier m : modifiers) {
			if(m.getID() == modifier.getID()) {
				if(m.getEffects().equals(modifier.getEffects())) {
					persistence.updateModifierEffects(modifier);
					for(Effect effect : m.getEffects()) {
						Stat stat = getStat(effect.getStatID());
						stat.removeEffect(effect);
					}
					if(modifier.isEnabled()) {
						for(Effect effect : modifier.getEffects()) {
							Stat stat = getStat(effect.getStatID());
							stat.addEffect(effect);
						}
					}
					callStatsListeners();
				}
				modifiers.set(modifiers.indexOf(m), modifier);
				break;
			}
		}
		callModifiersListeners();
	}
	
	public void deleteModifier(Modifier modifier) {
		final Modifier tModifier = modifier;
		new Thread(new Runnable() {
		   public void run() {
		   	persistence.deleteModifier(tModifier);
		   }
		}).start();
		
		for(Modifier m : modifiers) {
			if(m.getID() == modifier.getID()) {
				modifiers.remove(m);
				break;
			}
		}
		
		for(Effect effect : modifier.getEffects()) {
			Stat stat = getStat(effect.getStatID());
			stat.removeEffect(effect);
		}
		callModifiersListeners();
		callStatsListeners();
	}

	public void deleteStat(Stat stat) {
		final Stat tStat = stat;
		new Thread(new Runnable() {
		   public void run() {
		   	persistence.deleteStat(tStat);
		   }
		}).start();
		
		for(Stat s : stats) {
			if(s.getID() == stat.getID()) {
				stats.remove(s);
				break;
			}
		}
		
		for(Modifier m : modifiers) {
			for(Effect e : m.getEffects()) {
				if(e.getStatID() == stat.getID()) {
					m.removeEffect(e);
					break;
				}
			}
		}
		callModifiersListeners();
		
		callStatsListeners();
	}

	public void moveStat(int from, int to) {
		if(from != to) {
			Stat stat = stats.get(from);
			stats.remove(from);
			stats.add(to, stat);
			callStatsListeners();
		}
	}

	public void moveModifier(final int from, final int to) {
		if(from != to) {
			Modifier modifier = modifiers.get(from);
			modifiers.remove(from);
			modifiers.add(to, modifier);
			final Modifier tModifier = modifier;
			new Thread(new Runnable() {
			   public void run() {
				   persistence.moveModifier(tModifier, from, to);
			   }
			}).start();
			callModifiersListeners();
		}
	}

	public void enableModifier(Modifier modifier) {
		modifier.setEnabled(true);
		for(Effect effect : modifier.getEffects()) {
			Stat stat = getStat(effect.getStatID());
			stat.addEffect(effect);
		}
		callStatsListeners();

		final Modifier tModifier = modifier;
		new Thread(new Runnable() {
		   public void run() {
		   	persistence.updateModifier(tModifier);
		   }
		}).start();
	}
	
	public void disableModifier(Modifier modifier) {
		modifier.setEnabled(false);
		for(Effect effect : modifier.getEffects()) {
			Stat stat = getStat(effect.getStatID());
			stat.removeEffect(effect);
		}
		callStatsListeners();

		final Modifier tModifier = modifier;
		new Thread(new Runnable() {
		   public void run() {
		   	persistence.updateModifier(tModifier);
		   }
		}).start();
	}
}
