package org.xtx.ut4converter.ucore.ue1;

import javax.vecmath.Vector3d;

/**
 * Basic java implementation of :
 * https://github.com/stephank/surreal/blob/master/Core/Inc/UnMath.h Copyright
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
		private static final long serialVersionUID = -1491474389518786511L;

		public FVector(double arg0, double arg1, double arg2) {
			super(arg0, arg1, arg2);
		}

		public FVector() {
			super(0d, 0d, 0d);
		}

		public FVector(Vector3d vec3d) {
			super(vec3d.x, vec3d.y, vec3d.z);
		}

		/**
		 * Transform a point by a coordinate system, moving it by the coordinate
		 * system's origin if nonzero.
		 * 
		 * @param Coords
		 * @return
		 */
		public FVector TransformPointBy(FCoords Coords) {
			/*
			 * #if ASM FVector Temp; ASMTransformPoint( Coords, *this, Temp);
			 * return Temp; #elif ASMLINUX static FVector Temp;
			 * ASMTransformPoint( Coords, *this, Temp); return Temp; #else
			 */
			// FVector Temp = *this - Coords.Origin;
			FVector Temp = new FVector(this);
			Temp.sub(Coords.Origin);

			// return FVector(Temp | Coords.XAxis, Temp | Coords.YAxis, Temp |
			// Coords.ZAxis);
			return new FVector(xxx(Temp, Coords.XAxis), xxx(Temp, Coords.YAxis), xxx(Temp, Coords.ZAxis));
			// return FVector(Temp | Coords.XAxis, Temp | Coords.YAxis, Temp |
			// Coords.ZAxis);
			// #endif
		}

		/**
		 * Transform a directional vector by a coordinate system. Ignore's the
		 * coordinate system's origin.
		 * 
		 * @param Coords
		 * @return
		 */
		public FVector TransformVectorBy(FCoords Coords) {
			// return FVector( *this | Coords.XAxis, *this | Coords.YAxis, *this
			// | Coords.ZAxis );
			return new FVector(xxx(this, Coords.XAxis), xxx(this, Coords.YAxis), xxx(this, Coords.ZAxis));
		}

		/**
		 * FLOAT operator|( const FVector& V ) const return X*V.X + Y*V.Y +
		 * Z*V.Z;
		 * 
		 * @param V
		 * @return
		 */
		public double xxx(FVector U, FVector V) {
			return U.x * V.x + U.y * V.y + U.z * V.z;
		}

		/**
		 * FVector operator*( const FVector& V ) const
		 * 
		 * @param V
		 * @return
		 */
		public FVector multiply(FVector V) {
			return new FVector(x * V.x, y * V.y, z * V.z);
		}

	}

	public static enum ESheerAxis {
		SHEER_NONE, SHEER_XY, SHEER_XZ, SHEER_YX, SHEER_YZ, SHEER_ZX, SHEER_ZY
	}

	public class FCoords {

		public FVector Origin;
		public FVector XAxis;
		public FVector YAxis;
		public FVector ZAxis;

		public FCoords(FVector origin, FVector xAxis, FVector yAxis, FVector zAxis) {
			super();
			Origin = origin;
			XAxis = xAxis;
			YAxis = yAxis;
			ZAxis = zAxis;
		}

		public FCoords() {
			this.Origin = new FVector(0d, 0d, 0d);
			this.XAxis = new FVector(1d, 0d, 0d);
			this.YAxis = new FVector(0d, 1d, 0d);
			this.ZAxis = new FVector(0d, 0d, 1d);
		}

		//
		// Transform this coordinate system by another coordinate system.
		//
		public FCoords transformToNewCoordinates(FCoords TransformCoords) {
			// !! Proper solution:
			// Origin = Origin.TransformPointBy(
			// TransformCoords.Inverse().Transpose() );
			// Fast solution assuming orthogonal coordinate system:
			this.Origin = Origin.TransformPointBy(TransformCoords);
			this.XAxis = XAxis.TransformVectorBy(TransformCoords);
			this.YAxis = YAxis.TransformVectorBy(TransformCoords);
			this.ZAxis = ZAxis.TransformVectorBy(TransformCoords);
			return this;
		}

		public FCoords transformByScale(FCoords coords, FScale scale) {

			// Apply sheering.
			float sheer = FSheerSnap(scale.sheerRate);
			// FCoords TempCoords = GMath.UnitCoords;
			FCoords TempCoords = new FCoords();

			switch (scale.sheerAxis) {
			case SHEER_XY:
				TempCoords.XAxis.y = sheer;
				break;
			case SHEER_XZ:
				TempCoords.XAxis.z = sheer;
				break;
			case SHEER_YX:
				TempCoords.YAxis.x = sheer;
				break;
			case SHEER_YZ:
				TempCoords.YAxis.y = sheer;
				break;
			case SHEER_ZX:
				TempCoords.ZAxis.x = sheer;
				break;
			case SHEER_ZY:
				TempCoords.ZAxis.y = sheer;
				break;
			default:
				break;
			}
			coords = coords.transformToNewCoordinates(TempCoords);

			// Apply scaling.
			// coords.XAxis *= scale.scale;
			coords.XAxis = coords.XAxis.multiply(scale.scale);

			// YAxis *= Scale.Scale;
			coords.YAxis = coords.YAxis.multiply(scale.scale);

			// YAxis *= Scale.Scale;
			coords.ZAxis = coords.ZAxis.multiply(scale.scale);

			coords.Origin.x /= scale.scale.x;
			coords.Origin.y /= scale.scale.y;
			coords.Origin.z /= scale.scale.z;

			return coords;
		}
	}

	public static class FScale {

		public FVector scale;
		public float sheerRate;
		public ESheerAxis sheerAxis; // From ESheerAxis

		public FScale() {
			super();
			this.scale = new FVector(1d, 1d, 1d);
		}

		public FScale(FVector scale, float sheerRate, ESheerAxis sheerAxis) {
			super();
			this.scale = scale;
			this.sheerRate = sheerRate;
			this.sheerAxis = sheerAxis;
		}

		public FScale(Vector3d scale) {
			super();
			this.scale = new FVector(scale);
			this.sheerAxis = ESheerAxis.SHEER_NONE;
		}

		public FScale(FVector scale) {
			super();
			this.scale = scale;
			this.sheerAxis = ESheerAxis.SHEER_NONE;
		}

	};

	/**
	 * 
	 * Code from:
	 * https://github.com/stephank/surreal/blob/master/Core/Inc/UnMath.h
	 * Internal sheer adjusting function so it snaps nicely at 0 and 45 degrees.
	 * 
	 * @param Sheer
	 * @return
	 */
	public static float FSheerSnap(float Sheer) {
		if (Sheer < -0.65f)
			return Sheer + 0.15f;
		else if (Sheer > +0.65f)
			return Sheer - 0.15f;
		else if (Sheer < -0.55f)
			return -0.50f;
		else if (Sheer > +0.55f)
			return 0.50f;
		else if (Sheer < -0.05f)
			return Sheer + 0.05f;
		else if (Sheer > +0.05f)
			return Sheer - 0.05f;
		else
			return 0.f;
	}
}
