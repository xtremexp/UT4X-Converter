package org.xtx.ut4converter.ucore.ue4.matinee;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DActor;
import org.xtx.ut4converter.t3d.T3DObject;
import org.xtx.ut4converter.t3d.T3DUtils;
import org.xtx.ut4converter.t3d.iface.T3D;

public class InterpGroupInst extends T3DObject implements T3D {

	private T3DActor groupActor;

	private InterpGroup group;

	public InterpGroupInst(MapConverter mc, InterpGroup group, T3DActor groupActor) {
		super(mc);
		this.group = group;
		this.groupActor = groupActor;
	}

	@Override
	public void scale(Double newScale) {
		// TODO Auto-generated method stub

	}

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
	public String toT3d(StringBuilder sb) {
		// Group=InterpGroup'InterpData_0.InterpGroup_2'
		T3DUtils.writeLine(sb, "Group", group.getInterpData().getName() + "." + group.getName(), "\t");
		T3DUtils.writeLine(sb, "GroupActor", groupActor.getLevelReference(), "\t");
		
		return sb.toString();
	}

	public T3DActor getGroupActor() {
		return groupActor;
	}

	public InterpGroup getGroup() {
		return group;
	}

}
