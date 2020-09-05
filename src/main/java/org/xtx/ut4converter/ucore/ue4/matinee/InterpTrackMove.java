package org.xtx.ut4converter.ucore.ue4.matinee;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DUtils;

/**
 * Track in matinee view to handle actor movements
 * @author XtremeXp
 *
 */
public class InterpTrackMove extends InterpTrack {

	public InterpTrackMove(MapConverter mc) {
		super(mc, "InterpTrackMove");
		// TODO Auto-generated constructor stub
	}

	enum RotMode {
		/**
		 * Should take orientation from the .
		 */
		IMR_Keyframed,
		/**
		 * Point the X-Axis of the controlled AActor at the group specified by
		 * LookAtGroupName
		 */
		IMR_LookAtGroup,
		/*
		 * Should look along the direction of the translation path, with Z
		 * always up.
		 */

		/**
		 * Do not change rotation. Ignore it
		 */
		IMR_Ignore, IMR_MAX,
	}

    private enum ActiveCondition {
		ETAC_GoreEnabled, ETAC_GoreDisabled, ETAC_Always
	}

	private ActiveCondition activeCondition = ActiveCondition.ETAC_Always;

	private RotMode rotMode = RotMode.IMR_Keyframed;

	/**
	 * Default null
	 */
	private String lookAtGroupName;

	/**
	 * Default 0
	 */
	private Double linCurveTension, angCurveTension;

	/**
	 * Default false
	 */
	private Boolean bUseQuatInterpolation, bShowArrowAtKeys, bDisableMovement, bShowRotationOnCurveEd, bHide3DTrack;

	/**
	 * Default true
	 */
	private Boolean bShowTranslationOnCurveEd = Boolean.TRUE;

	private InterpCurve posTrack;

	private InterpCurve eulerTrack;

	private InterpCurve lookupTrack;

	@Override
	public void convert() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scale(Double newScale) {

		if (posTrack != null) {
			posTrack.scale(newScale);
		}

		if (eulerTrack != null) {
			eulerTrack.scale(newScale);
		}

		if (lookupTrack != null) {
			lookupTrack.scale(newScale);
		}
	}

	@Override
	public boolean analyseT3DData(String line) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void toT3d(StringBuilder sb, String prefix) {

		T3DUtils.writeBeginObj(sb, name, prefix + "\t\t");

		T3DUtils.writeLine(sb, "PosTrack", posTrack, prefix + "\t");
		T3DUtils.writeLine(sb, "EulerTrack", eulerTrack, prefix + "\t");
		T3DUtils.writeLine(sb, "LookupTrack", lookupTrack, prefix + "\t");

		T3DUtils.writeEndObj(sb, prefix + "\t\t");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPosTrack(InterpCurve posTrack) {
		this.posTrack = posTrack;
	}


}
