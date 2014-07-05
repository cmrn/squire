package me.cmrn.squire;

public class DefaultData {
	
	private DefaultData() { }
	
	public static void create(DataController data) {
        Character chr = data.createCharacter("Drizzt Do'Urden");

		Stat atkB = data.createStat("Attack Bonus", 12, true, "", chr.getID());
		Stat atkD = data.createStat("Attack Damage", 7, true, "", chr.getID());
		Stat ac = data.createStat("Armour Class", 25, false, "", chr.getID());
		Stat touch = data.createStat("Touch AC", 12, false, "", chr.getID());
		data.createStat("Flat-Footed AC", 22, false, "", chr.getID());
		data.createStat("Fortitude", 11, true, "", chr.getID());
		Stat reflex = data.createStat("Reflex", 9, true, "", chr.getID());
		data.createStat("Will", 4, true, "", chr.getID());
		data.createStat("CMB", 12, true, "", chr.getID());
		data.createStat("CMD", 25, false, "", chr.getID());
		data.createStat("Initiative", 7, true, "", chr.getID());
		Stat move = data.createStat("Move Speed", 30, false, "ft", chr.getID());
		
		Modifier power = data.createModifier("Power Attack", chr.getID());
		Modifier bard = data.createModifier("Bard Song (Courage)", chr.getID());
		Modifier haste = data.createModifier("Haste", chr.getID());

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
