package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.tools.Geometry;

/**
 * UE3/UE4 Trigger volume
 * (replacing original Trigger actor in UE1/UE2)
 */
public class T3DTriggerVolume extends T3DBrush {

	/**
	 * If true trigger is initially active
	 */
	private Boolean bInitiallyActive;

	/**
	 * If true trigger should be triggered once
	 */
	private Boolean bTriggerOnceOnly;

	/**
	 * If non null, trigger should activate specified unreal class only
	 */
	private String classProximityType;
	private Float damageThreshold;

	/**
	 * Delay before it can be triggered again
	 */
	private Float repeatTriggerTime;
	private Float reRriggerDelay = 1f;

	/**
	 * Trigger type
	 */
	private TriggerType triggerType = TriggerType.TT_AnyProximity;

	/**
	 * Message displayed to player when triggered
	 */
	private String message;

	/**
	 * UE1/UE2
	 * 
	 * @author XtremeXp
	 *
	 */
	enum TriggerType {
		TT_AnyProximity, TT_Shoot, TT_ClassProximity, TT_PawnProximity, TT_PlayerProximity,
		TT_HumanPlayerProximity, TT_LivePlayerProximity,
		/**
		 * Unreal 2 - Trigger activated by using it
		 */
		TT_Use,
		/**
		 * Duke Nukem Forever
		 */
		TT_PlayerProximityAndLookUse,
		/**
		 * Duke Nukem Forever
		 */
		TT_PlayerProximityAndUse
	}

	/**
	 * Do not remove unused t3dclass param, it's being used by MapConverter instance
	 * <code>utActorClass.getConstructor(MapConverter.class, String.class)</code>
	 *
	 * @param t3dClass T3d class
	 * @param mc Map converter instance
	 */
	public T3DTriggerVolume(MapConverter mc, String t3dClass) {
		super(mc, T3DBrush.BrushClass.TriggerVolume.toString());
		collisionHeight = 40d;
		collisionRadius = 40d;
	}
	
	@Override
	public void convert() {

		// TriggerVolume must have radius or height else UT4 editor will crash !
		if(collisionHeight == 0d){
			collisionHeight = 40d;
		}

		if(collisionRadius == 0d){
			collisionRadius = 40d;
		}

		setPolyList(Geometry.createCylinder(collisionRadius, collisionHeight, 8));

		super.convert();
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
