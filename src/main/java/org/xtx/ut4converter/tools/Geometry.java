

/*
 * UT Converter © 2023 by Thomas 'WinterIsComing/XtremeXp' P. is licensed under Attribution-NonCommercial-ShareAlike 4.0 International. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package org.xtx.ut4converter.tools;


import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.xtx.ut4converter.t3d.T3DPolygon;
import org.xtx.ut4converter.ucore.ue1.UnMath.FScale;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import java.util.LinkedList;

/**
 * Utility class for geometry operations TODO refactor a bit (some functions
 * from oldy UT3 Converter)
 *
 * @author XtremeXp
 */
public class Geometry {

	/**
	 * Origin point
	 */
	private static final Vector3D v0 = new Vector3D(0, 0, 0);

	/**
	 * Rotates the vector
	 *
	 * @param v
	 *            Vector to be rotated
	 * @param rotation
	 *            Rotation vector with UE1/UE2 range (0->65536)
	 * @return Vector rotated
	 */
	public static Vector3d rotate(Vector3d v, Vector3d rotation) {
		if (rotation == null) {
			return v;
		}

		return rotate(v, rotation.x, rotation.y, rotation.z);
	}

	/**
	 * Make rotate input vector with Yaw(Z),Pitch(Y) and Roll(X) unreal rotation
	 * values in the YXZ UT Editor coordinate system.
	 * +00576.000000,+01088.000000,+00192.000000 ->
	 * -00192.000000,+00064.000000,+00192.000000
	 *
	 * @param v Vector
	 * @param pitch
	 *            Pitch in Unreal Value (65536 u.v. = 360°)
	 * @param yaw
	 *            Yaw
	 * @param roll Roll
	 * @return Rotated vector
	 */
	public static Vector3d rotate(Vector3d v, double pitch, double yaw, double roll) {

		pitch = UE12AngleToDegree(pitch);
		yaw = UE12AngleToDegree(yaw);
		roll = UE12AngleToDegree(roll);

		// TODO only divide by 360 if rotation values comes from Unreal Engine
		// 1/2
		double rot_x = ((roll) / 360D) * 2D * Math.PI; // Roll=Axis X with
														// Unreal Editor
		double rot_y = (((pitch)) / 360D) * 2D * Math.PI; // Pitch=Axis Y with
															// Unreal Editor
		double rot_z = ((yaw) / 360D) * 2D * Math.PI; // Yaw=Axis Z with Unreal
														// Editor

		double[] tmp = new double[] { v.x, v.y, v.z, 1D };
		Matrix4d m4d = getGlobalRotationMatrix(rot_x, rot_y, rot_z);

		tmp = getRot(tmp, m4d);
		v.x = tmp[0];
		v.y = tmp[1];
		v.z = tmp[2];

		return v;
	}

	/**
	 *
	 * @param rot_x X axis rotation value
	 * @param rot_y Y axis rotation value
	 * @param rot_z Z axis rotation value
	 * @return Rotation matrix
	 */
	public static Matrix4d getGlobalRotationMatrix(double rot_x, double rot_y, double rot_z) {
		Matrix4d m4d;

		// Checked
		Matrix4d m4d_x = new Matrix4d(1D, 0D, 0D, 0D, 0D, Math.cos(rot_x), -Math.sin(rot_x), 0D, 0D, Math.sin(rot_x), Math.cos(rot_x), 0D, 0D, 0D, 0D, 1D);

		// Checked
		Matrix4d m4d_y = new Matrix4d(Math.cos(rot_y), 0D, Math.sin(rot_y), 0D, 0D, 1D, 0D, 0D, -Math.sin(rot_y), 0, Math.cos(rot_y), 0D, 0D, 0D, 0D, 1D);

		// Checked
		Matrix4d m4d_z = new Matrix4d(Math.cos(rot_z), Math.sin(rot_z), 0D, 0D, -Math.sin(rot_z), Math.cos(rot_z), 0D, 0D, 0D, 0D, 1D, 0D, 0D, 0D, 0D, 1D);
		updateMatrix(m4d_x);
		updateMatrix(m4d_y);
		updateMatrix(m4d_z);

		m4d = m4d_x;
		m4d.mul(m4d_y);
		m4d.mul(m4d_z);

		return m4d;
	}

	/**
	 * Replaces low values near zero with zero
	 *
	 * @param v Vector
	 */
	public static void updateDoubleZeroes(Vector3d v) {
		if (Math.abs(v.x) < 0.001D)
			v.x = 0d;
		if (Math.abs(v.y) < 0.001D)
			v.y = 0d;
		if (Math.abs(v.z) < 0.001D)
			v.z = 0d;

	}

	/**
	 * Replaces low values in matrix by zeroes.
	 *
	 * @param m4d
	 *            Input 4x4 matrix
	 */
	private static void updateMatrix(Matrix4d m4d) {
		double tmp;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				tmp = m4d.getElement(i, j);
				if (Math.abs(tmp) < 0.01) {
					m4d.setElement(i, j, 0D);
				}
			}
		}
	}

	/**
	 * Convert an unreal engine 1/2 angle to degree. 65536 Unreal Angle (UE1, 2)
	 * = 360°
	 *
	 * @param angle
	 *            Unreal Angle from Unreal Engine 1 or 2
	 * @return Unreal angle in degrees
	 */
	public static double UE12AngleToDegree(double angle) {
		int num = (int) (angle / 65536D);

		if (angle >= 0) {
			angle = angle - num * 65536D;
		} else if (angle < 0) {
			angle = angle + num * 65536D;
		}

		return (angle / 65536D) * 360D;
	}

	public static double[] getRot(double[] d2, Matrix4d m4d) {
		double[] d = new double[] { d2[0], d2[1], d2[2], 1D };

		double dx = m4d.m00 * d[0] + m4d.m10 * d[1] + m4d.m20 * d[2] + m4d.m30 * d[3];

		double dy = m4d.m01 * d[0] + m4d.m11 * d[1] + m4d.m21 * d[2] + m4d.m31 * d[3];
		double dz = m4d.m02 * d[0] + m4d.m12 * d[1] + m4d.m22 * d[2] + m4d.m32 * d[3];

		if (Math.abs(dx) < 0.00001D) {
			dx = 0D;
		}
		if (Math.abs(dy) < 0.00001D) {
			dy = 0D;
		}
		if (Math.abs(dz) < 0.00001D) {
			dz = 0D;
		}

		return new double[] { dx, dy, dz };
	}

	/**
	 * Transform Permanently a vector like in U1/UT Editor (Brush->Transform
	 * Permanently) generally only used for brush data such as vertices, normal
	 * and so on. Scale the vertice then make it rotate and scale it again (post
	 * scale)
	 *
	 * @param v
	 *            Vector to be transformed permanently
	 * @param mainScale
	 *            Main Scale (only available for Unreal Engine 1 UT ...
	 * @param rotation
	 *            Rotation
	 * @param postScale
	 *            Post Scale only available for Unreal Engine 1 UT ...
	 * @param isUV
	 *            if true then vector is TextureU or TextureV data (T3D Brush)
	 *
	 */
	public static void transformPermanently(Vector3d v, FScale mainScale, Vector3d rotation, FScale postScale, boolean isUV) {

		if (v == null) {
			return;
		}

		if (mainScale != null) {

			if (!isUV) {
				v.x *= mainScale.scale.x;
				v.y *= mainScale.scale.y;
				v.z *= mainScale.scale.z;

				//v = UnMath.applySheerRate(v, mainScale);
			} else {
				v.x /= mainScale.scale.x;
				v.y /= mainScale.scale.y;
				v.z /= mainScale.scale.z;
			}

			// TODO handle sheer info
		}

		if (rotation != null) {
			Geometry.rotate(v, rotation.x, rotation.y, rotation.z);
		}

		if (postScale != null) {

			if (!isUV) {
				v.x *= postScale.scale.x;
				v.y *= postScale.scale.y;
				v.z *= postScale.scale.z;

				//v = UnMath.applySheerRate(v, postScale);
			} else {
				v.x /= postScale.scale.x;
				v.y /= postScale.scale.y;
				v.z /= postScale.scale.z;
			}

		}

		// avoid values like 1000.00000001 -> 1000.0
		updateDoubleZeroes(v);
	}


	/**
	 * Null-safe operation to subtract vectors
	 *
	 * @param a First vector
	 * @param b Second vector
	 * @return Vector a - Vector b
	 */
	public static Vector3d sub(Vector3d a, Vector3d b) {

		if (a == null && b == null) {
			return new Vector3d(0d, 0d, 0d);
		}

		else if (a == null) {
			return new Vector3d(-b.x, -b.y, -b.z);
		}

		else if (b == null) {
			return new Vector3d(a.x, a.y, a.z);
		}

		else {
			return new Vector3d(a.x - b.x, a.y - b.y, a.z - b.z);
		}
	}

	/**
	 * Create poly data for creating a box brush
	 *
	 * @param width Box width
	 * @param length Box length
	 * @param height Box hiehgt
	 * @return List of polygons for a box
	 */
	public static LinkedList<T3DPolygon> createBox(Double width, Double length, Double height) {
		LinkedList<T3DPolygon> polyList = new LinkedList<>();

		Double w = width / 2d;
		Double l = length / 2d;
		Double h = height / 2d;

		T3DPolygon p = new T3DPolygon();
		p.setNormal(-1d, 0d, 0d);
		p.setTextureU(0d, 1d, 0d);
		p.setTextureV(0d, 0d, -1d);
		p.addVertex(-w, -l, -h).addVertex(-w, -l, h).addVertex(-w, l, h).addVertex(-w, l, -h);
		polyList.add(p);

		p = new T3DPolygon();
		p.setNormal(0d, 1d, 0d);
		p.setTextureU(1d, 0d, 0d);
		p.setTextureV(0d, 0d, -1d);
		p.addVertex(-w, l, -h).addVertex(-w, l, h).addVertex(w, l, h).addVertex(w, l, -h);
		polyList.add(p);

		p = new T3DPolygon();
		p.setNormal(1d, 0d, 0d);
		p.setTextureU(0d, -1d, 0d);
		p.setTextureV(0d, 0d, -1d);
		p.addVertex(w, l, -h).addVertex(w, l, h).addVertex(w, -l, h).addVertex(w, -l, -h);
		polyList.add(p);

		p = new T3DPolygon();
		p.setNormal(0d, -1d, 0d);
		p.setTextureU(-1d, 0d, 0d);
		p.setTextureV(0d, 0d, -1d);
		p.addVertex(w, -l, -h).addVertex(w, -l, h).addVertex(-w, -l, h).addVertex(-w, -l, -h);
		polyList.add(p);

		p = new T3DPolygon();
		p.setNormal(0d, 0d, 1d);
		p.setTextureU(1d, 0d, 0d);
		p.setTextureV(0d, 1d, 0d);
		p.addVertex(-w, l, h).addVertex(-w, -l, h).addVertex(w, -l, h).addVertex(w, l, h);
		polyList.add(p);

		p = new T3DPolygon();
		p.setNormal(0d, 0d, -1d);
		p.setTextureU(1d, 0d, 0d);
		p.setTextureV(0d, -1d, 0d);
		p.addVertex(-w, -l, -h).addVertex(-w, l, -h).addVertex(w, l, -h).addVertex(w, -l, -h);
		polyList.add(p);

		return polyList;
	}

	/**
	 * Creates a cylinder
	 *
	 * @param radius Cylinder radius
	 * @param height Cylinder height
	 * @param sides
	 *            Number of sides
	 * @return List of polygons that make a cylinder brush
	 */
	public static LinkedList<T3DPolygon> createCylinder(Double radius, Double height, int sides) {

		LinkedList<T3DPolygon> polyList = new LinkedList<>();

		Double h = height / 2;

		double angle = 2 * Math.PI / sides;
		double a = angle / 2d;
		double r = radius / (Math.cos(a)); // Circle radius

		// Sides polygons
		for (int i = 0; i < sides; i++) {
			T3DPolygon p = new T3DPolygon();
			p.setNormal(1d, 0d, 0d);
			p.setTextureU(new Vector3d(0d, -1d, 0d));
			p.setTextureV(new Vector3d(0d, 0d, -1d)); // unrealiable - values

			for (int j = 0; j < 4; j++) {

				if (j == 0 || j == 3) {
					p.addVertex(Math.sin(a) * r, Math.cos(a) * r, -h);
				} else {
					p.addVertex(Math.sin(a) * r, Math.cos(a) * r, h);
				}

				if (j == 1) {
					a += angle;
				}
			}

			Vector3d firstVertCoord = p.vertices.get(0);
			p.setOrigin(new Vector3d(firstVertCoord.x, firstVertCoord.y, firstVertCoord.z));
			polyList.add(p);
		}

		h = Math.abs(h);

		// Reset angle
		a = -(angle / 2d);

		// Top polygon
		T3DPolygon p = new T3DPolygon();

		for (int i = 0; i < sides; i++) {
			p.setNormal(1d, 0d, 0d);
			p.setTextureU(new Vector3d(0d, -1d, 0d));
			p.setTextureV(new Vector3d(0d, 0d, -1d)); // unrealiable - values
			p.addVertex(Math.sin(a) * r, Math.cos(a) * r, +h);

			a -= angle;
		}

		Vector3d firstVertCoord = p.vertices.get(0);
		p.setOrigin(new Vector3d(firstVertCoord.x, firstVertCoord.y, firstVertCoord.z));
		polyList.add(p);

		a = angle / 2d;

		// Bottom polygon
		p = new T3DPolygon();

		for (int i = 0; i < sides; i++) {
			p.setNormal(1d, 0d, 0d);
			p.setTextureU(new Vector3d(0d, -1d, 0d));
			p.setTextureV(new Vector3d(0d, 0d, -1d)); // unrealiable - values
			p.addVertex(Math.sin(a) * r, Math.cos(a) * r, -h);

			a += angle;
		}

		firstVertCoord = p.vertices.get(0);
		p.setOrigin(new Vector3d(firstVertCoord.x, firstVertCoord.y, firstVertCoord.z));
		polyList.add(p);

		return polyList;
	}


	/**
	 * Converts an Unreal Engine 1/2/3 rotation vector to UE4 rotation vector.
	 * UE123 rotation is within 0-> 65536 range while UE4 is within 0-360 range
	 *
	 * @param rotation Rotation vector
	 */
	public static Vector3d UE123ToUE4Rotation(final Vector3d rotation) {

		if (rotation != null) {
			rotation.scale(360d / 65536d);
		}

		return rotation;
	}




	/**
	 * UE1 only
	 * Recompute origin so that panU and panV are equals to zero.
	 *
	 * @param origin Texture origin
	 * @param texU TextureU (Scale/Rotation)
	 * @param texV TextureV (Scale/Rotation)
	 * @param panU PanU Texture position
	 * @param panV PanV Texture position
	 * @return Recomputed origin with PanU and PanV integrated
	 */
	public static Vector3d computeNewOrigin(Vector3d origin, Vector3d texU, Vector3d texV, double panU, double panV) {

		final Vector3D origin2 = new Vector3D(origin.getX(), origin.getY(), origin.getZ());

		final Vector3D texU2 = new Vector3D(texU.getX(), texU.getY(), texU.getZ());
		double texUScale = texU2.distance(v0);
		Vector3D originOffsetU = texU2.scalarMultiply(panU / (texUScale * texUScale));

		final Vector3D texV2 = new Vector3D(texV.getX(), texV.getY(), texV.getZ());
		double texVScale = texV2.distance(v0);
		Vector3D originOffsetV = texV2.scalarMultiply(panV / (texVScale * texVScale));

		final Vector3D newOri = origin2.add(originOffsetU).add(originOffsetV);

		return new Vector3d(newOri.getX(), newOri.getY(), newOri.getZ());
	}

}
