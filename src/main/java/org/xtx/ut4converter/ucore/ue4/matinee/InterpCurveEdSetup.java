package org.xtx.ut4converter.ucore.ue4.matinee;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DObject;
import org.xtx.ut4converter.t3d.T3DUtils;
import org.xtx.ut4converter.t3d.iface.T3D;

/**
 * How the matinee ed scene is rendering (ui settings)
 * @author XtremeXp
 *
 */
public class InterpCurveEdSetup extends T3DObject implements T3D {

	public InterpCurveEdSetup(MapConverter mc) {
		super(mc);
		viewEndInput = 10d;
	}

	/**
	 * Max time being viewed in time-line. Should be set to max
	 */
	Double viewEndInput;


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
	public String toT3d(StringBuilder sb, String prefix) {

		T3DUtils.writeBeginObj(sb, name, "\t");
		// TODO implement tab class
		sb.append("\t\tTabs(0)=(ViewEndInput=").append(viewEndInput).append(")");
		T3DUtils.writeEndObj(sb, "\t");

		return sb.toString();
	}

	@Override
	public void scale(Double newScale) {
		// TODO Auto-generated method stub

	}

}
