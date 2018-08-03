package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

public class DefensePoint extends T3DSound {

	/**
	 * bSniping for UT3
	 */
	private Boolean bSniperSpot;

	private String defendedObjectiveName;

	/**
	 * Game objective actor like ctf flag
	 */
	private T3DActor defendedObjectiveActor;

	/**
	 * Similar as defenss*ùe priority but for UT4
	 */
	private Integer basePriority;

	private UT3_DefensePriority defensePriority = UT3_DefensePriority.DEFPRI_Low;

	enum UT3_DefensePriority {
		DEFPRI_Low, DEFPRI_High
	}

	public DefensePoint(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
		// TODO Auto-generated constructor stub
	}

	public boolean analyseT3DData(String line) {

		// UT3
		if (line.startsWith("DefensePriority=")) {
			defensePriority = UT3_DefensePriority.valueOf(line.split("\\=")[1]);
		}
		// DefendedObjective=UTCTFRedFlagBase'UTCTFRedFlagBase_0'
		else if (line.startsWith("DefendedObjective=")) {
			if (!line.endsWith("None") && line.contains("\\'")) {
				defendedObjectiveName = line.split("\\'")[1];
			}
		} else if (line.startsWith("bSniping=")) {
			bSniperSpot = T3DUtils.getBoolean(line);
		} else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	@Override
	public void convert() {

		if (defensePriority == UT3_DefensePriority.DEFPRI_Low) {
			basePriority = 3;
		} else if (defensePriority == UT3_DefensePriority.DEFPRI_High) {
			basePriority = 10;
		}

		if (defendedObjectiveName != null) {
			defendedObjectiveActor = mapConverter.getT3dLvlConvertor().findActorByName(defendedObjectiveName);
		}

		super.convert();

	}

	public String toString() {
		sbf.append(IDT).append("Begin Actor Class=UTDefensePoint Name=").append(name).append("\n");
		sbf.append(IDT).append("\tBegin Object Name=\"Icon\"\n");
		writeLocRotAndScale();
		sbf.append(IDT).append("\tEnd Object\n");

		if (bSniperSpot != null && bSniperSpot == Boolean.TRUE) {
			sbf.append(IDT).append("\tbSniperSpot=True\n");

		}

		if (basePriority != null) {
			sbf.append(IDT).append("\tBasePriority=").append(basePriority).append("\n");
		}

		writeEndActor();
		return sbf.toString();

	}
}
