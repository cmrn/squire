package me.cmrn.squire;

/**
 * This interface defines the callback methods used by the DataController
 */
public interface DataListener {
	/**
	 * Called when one or more modifiers are added, removed, or updated.
	 */
	void onModifiersUpdated();
	/**
	 * Called when one or more stats are added, removed, or updated.
	 */
	void onStatsUpdated();
}
