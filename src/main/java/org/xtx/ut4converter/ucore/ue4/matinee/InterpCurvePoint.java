package org.xtx.ut4converter.ucore.ue4.matinee;

import javax.vecmath.Vector3d;

import org.xtx.ut4converter.t3d.T3DUtils;
import org.xtx.ut4converter.t3d.iface.T3D;

/**
 * 
 * 'very light' java port of InterpCurvePoint.h
 *
 */
public class InterpCurvePoint implements T3D {

	/**
	 * 
	 * @author Epic Games
	 *
	 */
	enum EInterpCurveMode {

		/** A straight line between two keypoint values. */
		CIM_Linear,

		/**
		 * A cubic-hermite curve between two keypoints, using Arrive/Leave
		 * tangents. These tangents will be automatically updated when points
		 * are moved, etc. Tangents are unclamped and will plateau at curve
		 * start and end points.
		 */
		CIM_CurveAuto,

		/**
		 * The out value is held constant until the next key, then will jump to
		 * that value.
		 */
		CIM_Constant,

		/**
		 * A smooth curve just like CIM_Curve, but tangents are not
		 * automatically updated so you can have manual control over them (eg.
		 * in Curve Editor).
		 */
		CIM_CurveUser,

		/**
		 * A curve like CIM_Curve, but the arrive and leave tangents are not
		 * forced to be the same, so you can create a 'corner' at this key.
		 */
		CIM_CurveBreak,

		/**
		 * A cubic-hermite curve between two keypoints, using Arrive/Leave
		 * tangents. These tangents will be automatically updated when points
		 * are moved, etc. Tangents are clamped and will plateau at curve start
		 * and end points.
		 */
		CIM_CurveAutoClamped,

		/** Invalid or unknown curve type. */
		CIM_Unknown
	}

    /**
	 * Time where the point should be reached by actor
	 */
	Double inVal;

	/**
	 * Position of point
	 */
	Vector3d outVal;

	/**
	 * How the actor should move between previous and this point
	 */
	EInterpCurveMode interpMode = EInterpCurveMode.CIM_CurveAutoClamped;

	public InterpCurvePoint() {

	}

	/**
	 * 
	 * @param time
	 *            Time the actor should be at position
	 * @param positionAtTime
	 *            Position of actor at time set
	 */
	public InterpCurvePoint(Double time, Vector3d positionAtTime) {
		this.inVal = time;
		this.outVal = positionAtTime;
	}

	@Override
	public void convert() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scale(Double newScale) {

		if (outVal != null) {
			outVal.scale(newScale);
		}

	}

	@Override
	public boolean analyseT3DData(String line) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void toT3d(StringBuilder sb, String prefix) {

		T3DUtils.write(sb, "InVal", inVal, prefix);
		T3DUtils.write(sb, "OutVal", outVal, ",");
		T3DUtils.write(sb, "InterpMode", interpMode, ",");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
