package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DMatch.UE4_RCType;

/**
 * 
 * https://ut.rushbase.net/Snake/UTDomGameMode
 * 
 * @author XtremeXp
 *
 */
public class DomPoint extends T3DSound {

	private final static String UT4_DOMPOINT_CLASS = "BP_ControlPoint_C";

	private String name;

	public DomPoint(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
		this.name = "Control Point";
	}

	@Override
	public boolean analyseT3DData(String line) {

		// UT2004
		if (line.startsWith("DefenseScriptTags")) {
			this.name = T3DUtils.getString(line);
		}
		// UT99 PointName
		else if (line.startsWith("PointName")) {
			this.name = T3DUtils.getString(line);
		} else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	/**
	 *
	 * @return
	 */
	public String toT3d() {

		sbf.append(IDT).append("Begin Actor Class=").append(UT4_DOMPOINT_CLASS).append(" Name=").append(name).append(" ");
		sbf.append(" Archetype=BP_ControlPoint_C'/Game/Domination/BP_ControlPoint.Default__BP_ControlPoint_C'\n");

		T3DUtils.writeRootComponentAndLoc(this, UE4_RCType.SPHERE);
		sbf.append(IDT).append("\t").append("Discription=\"").append(name).append("\"\n");
		writeEndActor();

		return sbf.toString();
	}
}
