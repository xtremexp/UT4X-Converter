package org.xtx.ut4converter.ucore.ue4.matinee;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DObject;
import org.xtx.ut4converter.t3d.T3DUtils;
import org.xtx.ut4converter.t3d.iface.T3D;

public class InterpCurve extends T3DObject implements T3D {

	public InterpCurve(MapConverter mc) {
		super(mc);
		// TODO Auto-generated constructor stub
	}

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
	public void toT3d(StringBuilder sb, String prefix) {

		T3DUtils.write(sb, "Points", points, prefix);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addPoint(Double time, Vector3d location) {

		if (points == null) {
			points = new ArrayList<>();
		}

		points.add(new InterpCurvePoint(time, location));
	}

	public void addPoint(InterpCurvePoint point) {

		if (points == null) {
			points = new ArrayList<>();
		}

		points.add(point);
	}

}
