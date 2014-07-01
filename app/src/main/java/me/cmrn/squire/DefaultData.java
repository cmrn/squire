package me.cmrn.squire;

public class DefaultData {
	
	private DefaultData() { }
	
	public static void create(DataController data) {
		Stat atkB = data.createStat("Attack Bonus", 12, true, "");
		Stat atkD = data.createStat("Attack Damage", 7, true, "");
		Stat ac = data.createStat("Armour Class", 25, false, "");
		Stat touch = data.createStat("Touch AC", 12, false, "");
		data.createStat("Flat-Footed AC", 22, false, "");
		data.createStat("Fortitude", 11, true, "");
		Stat reflex = data.createStat("Reflex", 9, true, "");
		data.createStat("Will", 4, true, "");
		data.createStat("CMB", 12, true, "");
		data.createStat("CMD", 25, false, "");
		data.createStat("Initiative", 7, true, "");
		Stat move = data.createStat("Move Speed", 30, false, "ft");
		
		Modifier power = data.createModifier("Power Attack");
		Modifier bard = data.createModifier("Bard Song (Courage)");
		Modifier haste = data.createModifier("Haste");
		data.createEffect(atkB.getID(), power.getID(), -2);
		data.createEffect(atkD.getID(), power.getID(), 4);
		
		data.createEffect(atkB.getID(), bard.getID(), 2);
		data.createEffect(atkD.getID(), bard.getID(), 2);

		data.createEffect(atkB.getID(), haste.getID(), 1);
		data.createEffect(ac.getID(), haste.getID(), 1);
		data.createEffect(touch.getID(), haste.getID(), 1);
		data.createEffect(reflex.getID(), haste.getID(), 1);
		data.createEffect(move.getID(), haste.getID(), 30);
	}
}
