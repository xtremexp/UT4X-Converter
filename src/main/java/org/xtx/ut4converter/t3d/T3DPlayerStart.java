/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGameTypes;
import org.xtx.ut4converter.UTGameTypes.GameType;
import org.xtx.ut4converter.UTGames.UTGame;

/**
 * 
 * @author XtremeXp
 */
public class T3DPlayerStart extends T3DSound {

	private static final int DEFAULT_TEAM = 0;

	/**
	 * If not null then it's a team player start else not
	 */
	Integer teamNum;

	boolean isTeamPlayerStart;

	String teamPlayerStartClass = "UTTeamPlayerStart";

	/**
	 * Indicate this player start is team player start
	 * 
	 * @param mc
	 */
	public T3DPlayerStart(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);

		if (mc.isTeamGameType()) {
			isTeamPlayerStart = true;
		}

		if (UTGameTypes.isUt99Assault(mc)) {
			teamPlayerStartClass = "UTASTeamStart_C";
		}
	}

	@Override
	public boolean analyseT3DData(String line) {

		// TeamNumber=1

		if (line.contains("TeamNumber")) {
			teamNum = T3DUtils.getInteger(line);
			isTeamPlayerStart = true;
		} else {

			return super.analyseT3DData(line);
		}

		return true;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString() {

		sbf.append(IDT).append("Begin Actor Class=").append(isTeamPlayerStart ? teamPlayerStartClass : "PlayerStart").append(" Name=").append(name).append("\n");

		if (mapConverter.toUnrealEngine4()) {
			sbf.append(IDT).append("\tBegin Object Name=\"CollisionCapsule\"\n");
			writeLocRotAndScale();
			sbf.append(IDT).append("\tEnd Object\n");
		}

		if (isTeamPlayerStart) {
			sbf.append(IDT).append("\tTeamNum=").append(teamNum != null ? teamNum : DEFAULT_TEAM).append("\n");
		}

		writeEndActor();
		return super.toString();
	}

}
