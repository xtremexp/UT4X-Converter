package org.xtx.ut4converter.ucore.ue1;

import javax.vecmath.Vector3d;

/**
 * Basic java implementation of :
 * <a href="https://github.com/stephank/surreal/blob/master/Core/Inc/UnMath.h">...</a> Copyright
 * 1997-1999 Epic Games, Inc. All Rights Reserved.
 *
 * @author XtremeXp
 *
 */
public class UnMath {


	public enum ESheerAxis {
		SHEER_NONE, SHEER_XY, SHEER_XZ, SHEER_YX, SHEER_YZ, SHEER_ZX, SHEER_ZY
	}


	public static class FScale {

		public Vector3d scale;
		public float sheerRate;
		public ESheerAxis sheerAxis; // From ESheerAxis

		public FScale() {
			super();
			this.scale = new Vector3d(1d, 1d, 1d);
			this.sheerRate = 0f;
		}

		public FScale(Vector3d scale, float sheerRate, ESheerAxis sheerAxis) {
			this.scale = scale;
			this.sheerRate = sheerRate;
			this.sheerAxis = sheerAxis;
		}
	}

}
