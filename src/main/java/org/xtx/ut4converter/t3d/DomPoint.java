package org.xtx.ut4converter.t3d;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.javafx.collections.ObservableMapWrapper;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DMatch.UE4_RCType;

import java.util.Map;

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

	public void initLineDataHandlers() {
		// Initialize the map with line types and their corresponding handlers
		ObjectNode lineDataHandlers = null;
		lineDataHandlers.put("DefenseScriptTags", new DefenseScriptTagsHandler().toString());
		lineDataHandlers.put("PointName", new PointNameHandler().toString());
		// Add other line types and their handlers
	}
	@Override
	public boolean analyseT3DData(String line) {

		ObservableMapWrapper<Object, Object> lineDataHandlers = null	;
		for (Map.Entry<Object, Object> entry : lineDataHandlers.entrySet()) {
			String lineType = (String) entry.getKey();
			LineDataHandler handler = (LineDataHandler) entry.getValue();

			if (line.startsWith(lineType)) {
				return handler.analyseT3DData(line);
			}
		}

		return super.analyseT3DData(line);
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
