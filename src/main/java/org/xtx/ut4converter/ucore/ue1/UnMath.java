package org.xtx.ut4converter.ucore.ue1;

import javax.vecmath.Vector3d;
import java.io.Serial;

/**
 * Basic java implementation of :
 * <a href="https://github.com/stephank/surreal/blob/master/Core/Inc/UnMath.h">...</a> Copyright
 * 1997-1999 Epic Games, Inc. All Rights Reserved.
 *
 * @author XtremeXp
 *
 */
public class UnMath {

	public static class FVector extends Vector3d {

		/**
		 * 
		 */
		@Serial
		private static final long serialVersionUID = -1491474389518786511L;

		public FVector(double arg0, double arg1, double arg2) {
			super(arg0, arg1, arg2);
		}

		public FVector(Vector3d vec3d) {
			super(vec3d.x, vec3d.y, vec3d.z);
		}

	}

	public enum ESheerAxis {
		SHEER_NONE, SHEER_XY, SHEER_XZ, SHEER_YX, SHEER_YZ, SHEER_ZX, SHEER_ZY
	}


	public static class FScale {

		public FVector scale;
		public float sheerRate;
		public ESheerAxis sheerAxis; // From ESheerAxis

		public FScale() {
			super();
			this.scale = new FVector(1d, 1d, 1d);
		}

	}

}
