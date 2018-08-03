package org.xtx.ut4converter.ucore.ue4.matinee;

import java.util.ArrayList;
import java.util.List;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DObject;
import org.xtx.ut4converter.t3d.T3DUtils;
import org.xtx.ut4converter.t3d.iface.T3D;

public class InterpData extends T3DObject implements T3D {

	public InterpData(MapConverter mc) {
		super(mc);

		interpCurveEdSetup = new InterpCurveEdSetup(mc);
	}

	public InterpCurveEdSetup interpCurveEdSetup;
	public List<InterpGroup> interpGroups;

	@Override
	public void convert() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean analyseT3DData(String line) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void scale(Double newScale) {
		// TODO Auto-generated method stub
	}

	public void addGroup(InterpGroup group) {

		if (interpGroups == null) {
			interpGroups = new ArrayList<>();
		}

		group.setName(group.getName() + "_" + interpGroups.size());
		interpGroups.add(group);
	}

	public InterpCurveEdSetup getInterpCurveEdSetup() {
		return interpCurveEdSetup;
	}

	public List<InterpGroup> getInterpGroups() {
		return interpGroups;
	}

	@Override
	public void toT3d(StringBuilder sb, String prefix) {

		if (interpCurveEdSetup != null) {
			interpCurveEdSetup.toT3d(sb, prefix);
		}

		if (interpGroups != null && !interpGroups.isEmpty()) {
			for (InterpGroup group : interpGroups) {
				group.toT3d(sb, prefix);
			}
		}

		T3DUtils.writeClassRef(sb, "CurveEdSetup", interpCurveEdSetup, prefix + "\t");
		T3DUtils.writeClassRef(sb, "InterpGroups", interpGroups, prefix + "\t");
		T3DUtils.writeLine(sb, "SelectedFilter", "InterpFilter'FilterAll'", prefix + "\t");
	}

}
