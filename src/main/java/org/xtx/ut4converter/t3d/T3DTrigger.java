package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.tools.Geometry;

public class T3DTrigger extends T3DBrush {

	Boolean bInitiallyActive;
	Boolean bTriggerOnceOnly;
	String classProximityType;
	Float damageThreshold;
	Float repeatTriggerTime;
	Float reRriggerDelay = 1f;
	TriggerType triggerType = TriggerType.TT_AnyProximity;
	String message;

	/**
	 * UE1/UE2
	 * 
	 * @author XtremeXp
	 *
	 */
	enum TriggerType {
		TT_AnyProximity, TT_Shoot, TT_ClassProximity, TT_PawnProximity, TT_PlayerProximity,
	}

	public T3DTrigger(MapConverter mc, String t3dClass) {
		super(mc, T3DBrush.BrushClass.TriggerVolume.toString());
		collisionHeight = 40d;
		collisionRadius = 40d;
	}
	
	@Override
	public void convert() {
		polyList = Geometry.createCylinder(collisionRadius, collisionHeight, 8);
	}

	@Override
	public boolean analyseT3DData(String line) {

		if (line.startsWith("TriggerType=")) {
			triggerType = TriggerType.valueOf(T3DUtils.getString(line));
		} else if (line.startsWith("bInitiallyActive=")) {
			bInitiallyActive = Boolean.getBoolean((T3DUtils.getString(line)));
		} else if (line.startsWith("ClassProximityType=")) {
			classProximityType = T3DUtils.getString(line);
		} else if (line.startsWith("ReTriggerDelay=")) {
			reRriggerDelay = T3DUtils.getFloat(line);
		} else if (line.startsWith("RepeatTriggerTime=")) {
			repeatTriggerTime = T3DUtils.getFloat(line);
		} else if (line.startsWith("Message=")) {
			message = T3DUtils.getString(line);
		} else if (line.startsWith("DamageThreshold=")) {
			damageThreshold = T3DUtils.getFloat(line);
		}
		return super.analyseT3DData(line);
	}

}
