package org.xtx.ut4converter.ucore.ue4;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DObject;
import org.xtx.ut4converter.t3d.T3DUE2Terrain;
import org.xtx.ut4converter.t3d.iface.T3D;

/**
 * Visibility component
 * 
 * @author XtremeXp
 *
 */
public class VisibilityComponent extends T3DObject implements T3D {

	public VisibilityComponent(MapConverter mc, T3DUE2Terrain ue2Terrain) {
		super(mc);
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
	public String toT3d(StringBuilder sb, String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
