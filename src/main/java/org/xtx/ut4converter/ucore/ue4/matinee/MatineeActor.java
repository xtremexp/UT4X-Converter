package org.xtx.ut4converter.ucore.ue4.matinee;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.t3d.MoverProperties;
import org.xtx.ut4converter.t3d.T3DActor;
import org.xtx.ut4converter.t3d.T3DMover;
import org.xtx.ut4converter.t3d.T3DUtils;

import javax.vecmath.Vector3d;
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
	 * @param t3dClass
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
	public String toString() {

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

	public static void main(String... args) {
		MapConverter mc = new MapConverter(UTGame.UT99, UTGame.UT4);
		MatineeActor ma = new MatineeActor(mc, "MatineeActor");
		ma.location = new Vector3d(new double[] { 0d, 0d, 0d });
		// ma.interpGroupInsts = new ArrayList<>();

		InterpData id = new InterpData(mc);
		InterpGroup ig = new InterpGroup(mc, id, "Movement");
		InterpTrackMove moveTrack = new InterpTrackMove(mc);

		InterpCurve ic = new InterpCurve(mc);
		InterpCurvePoint loc1 = new InterpCurvePoint(0d, new Vector3d(0d, 0d, 0d));
		InterpCurvePoint loc2 = new InterpCurvePoint(5d, new Vector3d(0d, 0d, 1024d));
		ic.addPoint(loc1);
		ic.addPoint(loc2);

		moveTrack.setPosTrack(ic);
		
		ig.addTrack(moveTrack);
		id.addGroup(ig);
		// ma.interpGroupInsts.add(new InterpGroupInst(mc, ig, null));
		ma.matineeData = id;

		System.out.println(ma);
	}
}
