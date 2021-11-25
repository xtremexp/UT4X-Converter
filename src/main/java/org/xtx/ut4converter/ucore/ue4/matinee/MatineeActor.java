package org.xtx.ut4converter.ucore.ue4.matinee;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.MoverProperties;
import org.xtx.ut4converter.t3d.T3DActor;
import org.xtx.ut4converter.t3d.T3DMover;
import org.xtx.ut4converter.t3d.T3DUtils;

import java.util.List;

public class MatineeActor extends T3DActor {

	public MatineeActor(MapConverter mc, String t3dClass) {
		super(mc, "MatineeActor");
		initialize();
	}

	/**
	 * Builds a matinee actor from a UE1/UE2 mover actor
	 *
	 * @param mc
	 * @param mover
	 */
	public MatineeActor(MapConverter mc, T3DMover mover) {
		super(mc, "MatineeActor");
		initialize();

		MoverProperties movProp = mover.getMoverProperties();

		// TODO add movement track
		// TODO add sound tracks
	}

	public InterpData matineeData;

	/**
	 * Default false
	 */
	boolean bRewindOnPlay;

	public List<InterpGroup> groupActorInfos;

	/**
	 * ?
	 */
	Double interpPosition;

	private void initialize() {
		interpPosition = 0d;
	}

	@Override
	public void convert() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scale(Double newScale) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean analyseT3DData(String line) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toT3d() {

		final String prefix = "\t\t";

		writeBeginActor();

		// definitions
		writeObjDefinition(sbf, prefix);

		if (matineeData != null) {
			matineeData.writeBeginObj(sbf, prefix);

			if (matineeData.getInterpGroups() != null) {

				for (InterpGroup interpGroup : matineeData.getInterpGroups()) {
					interpGroup.writeBeginObj(sbf, prefix + "\t");
					interpGroup.toT3d(sbf, prefix);
					interpGroup.writeEndObj(sbf, prefix + "\t");
				}
			}

			matineeData.writeEndObj(sbf, prefix);
		}

		writeLocRotSceneComponent(prefix);
		T3DUtils.writeClassRef(sbf, "MatineeData", matineeData, prefix);
		T3DUtils.writeLine(sbf, "RewindOnPlay", bRewindOnPlay, prefix);
		T3DUtils.writeLine(sbf, "InterpPosition", interpPosition, prefix);
		T3DUtils.writeLine(sbf, "RootComponent", sceneComponent.getName(), prefix);
		// obj detail values

		writeEndActor();

		return sbf.toString();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
