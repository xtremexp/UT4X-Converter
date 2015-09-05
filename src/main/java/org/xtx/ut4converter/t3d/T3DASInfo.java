package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

/**
 * Basic implementation of "AssaultInfo" to "UTASInfo" from UT4 Assault Mode
 * 
 * @author XtremeXp
 *
 */
public class T3DASInfo extends T3DActor {

	String mapDescription;

	/**
	 * Map time limit (in seconds)
	 */
	int mapTimeLimit;

	public T3DASInfo(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
		initialise();
	}

	private void initialise() {
		mapTimeLimit = 600;
		name = "AsssaultInfo";
		tag = "AssaultInfo";
	}

	@Override
	public boolean analyseT3DData(String line) {
		if (line.startsWith("ObjDesc(")) {

			if (mapDescription == null) {
				mapDescription = "";
			}

			mapDescription += T3DUtils.getString(line);
		}

		else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	@Override
	public String toString() {

		sbf.append(IDT).append("Begin Actor Class=UTASInfo_C \n");
		sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"DefaultSceneRoot\"\n");
		sbf.append(IDT).append("\tEnd Object\n");
		sbf.append(IDT).append("\tBegin Object Name=\"DefaultSceneRoot\"\n");
		writeLocRotAndScale();
		sbf.append(IDT).append("\tEnd Object\n");
		sbf.append(IDT).append("\tDefaultSceneRoot=DefaultSceneRoot\n");
		sbf.append(IDT).append("\tMapDesc=\"").append(mapDescription).append("\"\n");
		sbf.append(IDT).append("\tMapTimeLimit=").append(mapTimeLimit).append("\n");
		sbf.append(IDT).append("\tRootComponent=DefaultSceneRoot\n");

		writeEndActor();
		
		return sbf.toString();
	}

}
