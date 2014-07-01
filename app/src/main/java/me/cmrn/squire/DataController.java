package me.cmrn.squire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;

/**
 * This class controls the synchronisation and storage of data.
 */
public class DataController {
	private List<Stat> stats;
	private List<Modifier> modifiers;
	
	private DataPersistence persistence;
	private List<DataListener> listeners;
	
	/**
	 * Create a new DataController and load all data from a new DataPersistence.
	 * 
	 * This should only be called once in the application lifecycle,
	 * when MyApplcation is created.
	 * 
	 * @param context The application context.
	 */
	public DataController(Context context) {
		persistence = new DataPersistence(context);
		stats = persistence.getAllStatsList();
		modifiers = persistence.getAllModifiersList();
		listeners = new ArrayList<DataListener>();
	}
	
	/**
	 * Register a DataListener with the controller.
	 * 
	 * This DataListener will now receive callbacks when data changes.
	 *  
	 * @param listener The listener to register.
	 */
	public void registerListener(DataListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Deregister a DataListener with the controller.
	 * 
	 * This DataListener will no longer receive callbacks when data changes.
	 * 
	 * @param listener The listener to deregister.
	 */
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
	
	/**
	 * Get a list of all the stats.
	 * 
	 * @return List containing all stats.
	 */
	public List<Stat> getStats() {
		return Collections.unmodifiableList(stats);
	}
	
	/**
	 * Get a list of all the modifiers.
	 * 
	 * @return List containing all modifiers.
	 */
	public List<Modifier> getModifiers() {
		return Collections.unmodifiableList(modifiers);
	}
	
	/**
	 * Get the stat with the given ID.
	 * 
	 * @param id The ID of the stat.
	 * @return The stat with the given ID.
	 */
	public Stat getStat(int id) {
		for(Stat s : stats)
			if(s.getID() == id)
				return s;
		throw new IllegalArgumentException("No stat with that ID");
	}
	
	
	/**
	 * Get the modifier with the given ID.
	 * 
	 * @param id The ID of the modifier.
	 * @return The modifier with the given ID.
	 */
	public Modifier getModifier(int id) {
		for(Modifier m : modifiers)
			if(m.getID() == id)
				return m;
		throw new IllegalArgumentException("No modifier with that ID");
	}
	
	/**
	 * Get the stat at the given position.
	 * 
	 * @param id The ID of the stat.
	 * @return The stat at the given position.
	 */
	public Stat getStatAtPos(int pos) {
		return stats.get(pos);
	}
	
	/**
	 * Get the modifier at the given position.
	 * 
	 * @param position The position of the modifier.
	 * @return The modifier at the given position.
	 */
	public Modifier getModifierAtPos(int pos) {
		return modifiers.get(pos);
	}
	
	/**
	 * Create a new stat using the given information.
	 * 
	 * @return A newly created stat.
	 */
	public Stat createStat(String name, int baseValue, boolean isSigned, String suffix) {
		Stat stat = persistence.createStat(name, baseValue, isSigned, suffix);
		stats.add(stat);
		return stat;
	}
	
	/**
	 * Create a new modifier using the given information.
	 * 
	 * @return A newly created modifier.
	 */
	public Modifier createModifier(String name) {
		Modifier modifier = persistence.createModifier(name);
		modifiers.add(modifier);
		return modifier;
	}
	
	/**
	 * Create a new effect using the given information.
	 * 
	 * @return A newly created effect.
	 */
	public Effect createEffect(int statID, int modifierID, int value) {
		Effect effect = persistence.createEffect(statID, modifierID, value);
		getModifier(modifierID).addEffect(effect);
		return effect;
	}

	/**
	 * Finds the stat with the same ID as the given stat and updates the values
	 * to the values contained in the given stat.
	 * 
	 * @param stat The stat which contains the new values.
	 */
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

	/**
	 * Finds the modifier with the same ID as the given modifier and updates the values
	 * to the values contained in the given modifier.
	 * 
	 * @param modifier The modifier which contains the new values.
	 */
	public void updateModifier(Modifier modifier) {
		final Modifier tModifier = modifier;
		new Thread(new Runnable() {
		   public void run() {
		   	persistence.updateModifier(tModifier);
		   }
		}).start();
		
		for(Modifier m : modifiers) {
			if(m.getID() == modifier.getID()) {
				modifiers.set(modifiers.indexOf(m), modifier);
				break;
			}
		}
		callModifiersListeners();
	}
	
	public void updateModifierEffects(Modifier modifier, List<Effect> newEffects) {
		if(!modifier.getEffects().equals(newEffects)) {
			final Modifier m = modifier;
			new Thread(new Runnable() {
				   public void run() {
					persistence.updateModifierEffects(m);
				   }
				}).start();
			
			// Disable modifier
			for(Effect effect : modifier.getEffects()) {
				Stat stat = getStat(effect.getStatID());
				stat.removeEffect(effect);
			}

			modifier.setEffects(newEffects);
			
			// Re-enable modifier
			if(modifier.isEnabled()) {
				for(Effect effect : modifier.getEffects()) {
					Stat stat = getStat(effect.getStatID());
					stat.addEffect(effect);
				}
				callStatsListeners();
			}
			callModifiersListeners();
		}
	}
	
	/**
	 * Deletes a modifier along with all effects that reference that modifier.
	 * 
	 * @param modifier The modifier to delete.
	 */
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
	
	/**
	 * Deletes a stat along with all effects that reference that stat.
	 * 
	 * @param stat The stat to delete.
	 */
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

	/**
	 * @param from Current position of the stat.
	 * @param to New position of the stat.
	 */
	public void moveStat(int from, int to) {
		if(from != to) {
			Stat stat = stats.get(from);
			stats.remove(from);
			stats.add(to, stat);
			callStatsListeners();
		}
	}
	
	/**
	 * @param from Current position of the modifier.
	 * @param to New position of the modifier.
	 */
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
	
	/**
	 * @param modifier The modifier to enable.
	 */
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
	
	/**
	 * @param modifier The modifier to disable.
	 */
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
