package org.xtx.ut4converter.ucore.ue4.matinee;

import java.util.List;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.t3d.MoverProperties;
import org.xtx.ut4converter.t3d.T3DActor;
import org.xtx.ut4converter.t3d.T3DMover;
import org.xtx.ut4converter.t3d.iface.T3D;

public class MatineeActor extends T3DActor implements T3D {

	public MatineeActor(MapConverter mc, String t3dClass) {
		super(mc, "MatineeActor");
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Builds a matinee actor from a UE1/UE2 mover actor
	 * @param mc
	 * @param t3dClass
	 * @param mover
	 */
	public MatineeActor(MapConverter mc, String t3dClass, T3DMover mover) {
		super(mc, "MatineeActor");
		
		MoverProperties movProp = mover.getMoverProperties();
		
		// TODO add movement track
		// TODO add sound tracks
	}

	public InterpData matineeData;

	/**
	 * Default false
	 */
	boolean bRewindOnPlay;

	List<InterpGroup> groupActorInfos;

	/**
	 * ?
	 */
	Double interpPosition;

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
	public String toT3d(StringBuilder sb) {
		
		final String prefix = "\t\t";
		
		writeBeginActor();
		
		// definitions
		writeObjDefinition(sb, prefix);
		
		// obj detail values

		
		
		writeEndActor();

		return sb.toString();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public static void main(String ... args){
		MapConverter mc = new MapConverter(UTGame.UT99, UTGame.UT4);
		MatineeActor ma = new MatineeActor(mc, "MatineeActor");

		InterpData id = new InterpData(mc);
		InterpGroup ig = new InterpGroup(mc, id, "TEST");
		ig.addTrack(new InterpTrackMove(mc));
		id.addGroup(ig);
		ma.matineeData = id;
		StringBuilder sb = new StringBuilder();
		ma.writeObjDefinition(sb, "\t");
		
		System.out.println(sb.toString());
	}
}
