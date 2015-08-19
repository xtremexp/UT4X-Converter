package org.xtx.ut4converter.ucore.ue4.matinee;

import java.util.List;

import org.xtx.ut4converter.t3d.T3DUtils;
import org.xtx.ut4converter.t3d.iface.T3D;

public class InterpCurve implements T3D {

	List<InterpCurvePoint> points;

	@Override
	public void convert() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scale(Double newScale) {

		if (points != null && !points.isEmpty()) {
			for (InterpCurvePoint point : points) {
				point.scale(newScale);
			}
		}

	}

	@Override
	public boolean analyseT3DData(String line) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toT3d(StringBuilder sb) {

		T3DUtils.write(sb, "Points", points);

		return sb.toString();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
