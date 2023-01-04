/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.ucore.UnrealEngine;

/**
 * 
 * @author XtremeXp
 */
public class T3DPlayerStart extends T3DSound {

	private static final int DEFAULT_TEAM = 0;

	/**
	 * If not null then it's a team player start else not
	 */
	private Integer teamNum;

	private boolean isTeamPlayerStart;

	private String teamPlayerStartClass = "UTTeamPlayerStart";

	/**
	 * Indicate this player start is team player start
	 * 
	 * @param mc Map converter instance
	 */
	public T3DPlayerStart(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);

		if (mc.isTeamGameType()) {
			isTeamPlayerStart = true;
		}
	}

	@Override
	public boolean analyseT3DData(String line) {

		// TeamNumber=1
		if (line.contains("TeamNumber")) {
			teamNum = T3DUtils.getInteger(line);
			isTeamPlayerStart = true;

			// flag previous playerstarts analyszed as teamplayerstarts
			if(!mapConverter.isTeamGameType()){
				for(final T3DActor actor : mapConverter.getT3dLvlConvertor().getConvertedActors()){
					if(actor instanceof  T3DPlayerStart){
						((T3DPlayerStart) actor).setTeamPlayerStart(true);
					}
				}
			}

			mapConverter.setIsTeamGameType(true);
		} else {

			return super.analyseT3DData(line);
		}

		return true;
	}

	public void setTeamPlayerStart(boolean teamPlayerStart) {
		isTeamPlayerStart = teamPlayerStart;
	}

	/**
	 *
	 * @return T3d actor value
	 */
	public String toT3d() {

		sbf.append(IDT).append("Begin Actor Class=").append(isTeamPlayerStart ? teamPlayerStartClass : "PlayerStart").append(" Name=").append(name).append("\n");

		if (isTo(UnrealEngine.UE4)) {
			sbf.append(IDT).append("\tBegin Object Name=\"CollisionCapsule\"\n");
			writeLocRotAndScale();
			sbf.append(IDT).append("\tEnd Object\n");
		} else {
			writeLocRotAndScale();
		}

		if (isTeamPlayerStart) {
			sbf.append(IDT).append("\tTeamNum=").append(teamNum != null ? teamNum : DEFAULT_TEAM).append("\n");
		}

		writeEndActor();
		return super.toString();
	}

}
