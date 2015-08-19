package org.xtx.ut4converter.ucore.ue4.matinee;

import java.util.List;

import org.xtx.ut4converter.t3d.iface.T3D;

public class MatineeActor implements T3D {

	InterpData matineeData;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
