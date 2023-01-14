/*
 * UT Converter © 2023 by Thomas 'WinterIsComing/XtremeXp' P. is licensed under Attribution-NonCommercial-ShareAlike 4.0 International. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.tools.Geometry;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ucore.UnrealEngine;
import org.xtx.ut4converter.ucore.ue1.UnMath.FScale;

import javax.vecmath.Vector3d;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a polygon in unreal ascii format (t3d).
 * @author XtremeXp
 */
public class T3DPolygon {

	/**
	 * UV conversion scale factor when converting UT3 texture scaling to UT4
	 * texture scaling (this is equals to 100/128).
	 * Grid base unit is 128 in UE3.
	 * Grid base unit is 100 in UE4.
	 */
	private static final double UE3_UE4_UV_SCALE_FACTOR = 0.78125d;

	/**
	 * Original texture or material applied to the polygon
	 */
	private UPackageRessource texture;

	/**
     * Texture position on surface
     */
	public Vector3d origin;

	/**
     * Normal to the polygon
     */
	public Vector3d normal;

	/**
	 * How points are regrouped for polygon?
	 */
	private Integer link;

	/**
	 * Some flag about how the polygon should be rendered such as istranslucent,
	 * ismirror, is two-sided and so on. This value is a sum of values that are
	 * powers of two Flags=217890775
	 */
	private Integer flag;

	/**
	 * UE3+ Only
	 * Light map resolution
	 * UE4: LightMapScale
	 * UE3: ShadowMapScale
	 */
	private int lightMapScale;

	/**
	 * UE1 only
	 * Changes the texture position on surface
	 */
	public double panU;

	/**
     * UE1 only
	 * Changes the texture position on surface
     */
	public double panV;

	/**
	 * As seen in Polys.h
	 * "A mask used to determine which smoothing groups this polygon is in" No
	 * idea what it does.
	 */
	private Integer smoothingMask;


	/**
	 * Texture rotation and scale
	 */
	private Vector3d textureU;

	/**
	 * Texture rotation and scale
	 */
	private Vector3d textureV;

	/**
     * Vertices of this polygon
     */
	public List<Vector3d> vertices = new LinkedList<>();

	private MapConverter mapConverter;

	/**
     *
     */
	public T3DPolygon() {
		origin = new Vector3d(0d, 0d, 0d);
		normal = new Vector3d(0d, 0d, 1d);
	}

	/**
	 *
	 * @param t3dLine t3d line value
	 * @param mapConverter Map converter instance
	 */
	public T3DPolygon(String t3dLine, MapConverter mapConverter) {
		// Begin Polygon Texture=Rockwal4 Flags=32768 Link=322
		this.mapConverter = mapConverter;
		this.texture = mapConverter.getUPackageRessource(T3DUtils.getStringTEMP(t3dLine, "Texture"), T3DRessource.Type.TEXTURE);
		this.link = T3DUtils.getInteger(t3dLine, "Link");
		this.flag = T3DUtils.getInteger(t3dLine, "Flags");
	}

	/**
	 * Scales the polygon
	 * 
	 * @param newScale New scale
	 */
	public void scale(Double newScale) {

		this.origin.scale(newScale);
		scaleUV(newScale);

		for (Vector3d vertex : vertices) {
			vertex.scale(newScale);
		}
	}

	/**
	 * Scales UV
	 *
	 * @param newScale Scale factor
	 */
	private void scaleUV(Double newScale) {

		if (newScale != null) {

			panU *= newScale;
			panV *= newScale;

			if (textureU != null) {
				textureU.scale(1 / newScale);
			}

			if (textureV != null) {
				textureV.scale(1 / newScale);
			}
		}
	}

	/**
	 * Transform permanently the polygon as like in Unreal 1/2 editor
	 * "Transform permanently" when selecting brush
	 * 
	 * @param mainScale Main scale
	 * @param rotation Rotation
	 * @param postScale Post scale
	 */
	public void transformPermanently(FScale mainScale, Vector3d rotation, FScale postScale) {

		Geometry.transformPermanently(origin, mainScale, rotation, postScale, false);

		Geometry.transformPermanently(normal, mainScale, rotation, postScale, false);

		if (textureU != null) {
			Geometry.transformPermanently(textureU, mainScale, rotation, postScale, true);
		}

		if (textureV != null) {
			Geometry.transformPermanently(textureV, mainScale, rotation, postScale, true);
		}

		for (Vector3d vertex : vertices) {
			Geometry.transformPermanently(vertex, mainScale, rotation, postScale, false);
		}
	}

	/**
	 * 
	 * @param sb String builder
	 * @param df
	 *            Default Decimal format (not creating one each time for perf
	 *            issues)
	 * @param prefix Prefix
	 * @param polyIndex Polygon index
	 */
	public void toT3D(StringBuilder sb, DecimalFormat df, String prefix, int polyIndex, UnrealEngine ue) {

		prefix += "\t\t\t";

		sb.append(prefix).append("Begin Polygon Item=Side");

		if (texture != null) {
			sb.append(" Texture=").append(texture.getConvertedName());
		}

		if (ue == UnrealEngine.UE4) {
			sb.append(" LightMapScale=").append(lightMapScale);
		} else if (ue == UnrealEngine.UE3) {
			sb.append(" ShadowMapScale=").append(lightMapScale);
		}

		sb.append(" Link=").append(polyIndex);

		sb.append("\n");

		sb.append(prefix).append("\tOrigin   ").append(T3DUtils.toPolyStringVector3d(origin, df)).append("\n");
		sb.append(prefix).append("\tNormal   ").append(T3DUtils.toPolyStringVector3d(normal, df)).append("\n");
		sb.append(prefix).append("\tTextureU ").append(T3DUtils.toPolyStringVector3d(textureU, df)).append("\n");
		sb.append(prefix).append("\tTextureV ").append(T3DUtils.toPolyStringVector3d(textureV, df)).append("\n");

		for (Vector3d vertex : vertices) {
			sb.append(prefix).append("\tVertex   ").append(T3DUtils.toPolyStringVector3d(vertex, df)).append("\n");
		}

		sb.append(prefix).append("End Polygon\n");
	}

	/**
	 * Adds a vertex to the polygon
	 * @param x X value of vertex
	 * @param y Y value of vertex
	 * @param z Z value of vertex
	 * @return
	 */
	public T3DPolygon addVertex(Double x, Double y, Double z) {

		vertices.add(new Vector3d(x, y, z));
		return this;
	}

	public void setNormal(Vector3d v) {
		normal = v;
	}

	public void setNormal(Double x, Double y, Double z) {
		normal = new Vector3d(x, y, z);
	}

	public void setTextureV(Vector3d textureV) {
		this.textureV = textureV;
	}
	
	public void setTextureV(Double x, Double y, Double z) {
		this.textureV = new Vector3d(x, y, z);
	}

	public void setTextureU(Double x, Double y, Double z) {
		this.textureU = new Vector3d(x, y, z);
	}

	public void setTextureU(Vector3d textureU) {
		this.textureU = textureU;
	}


	public void setOrigin(Vector3d origin) {
		this.origin = origin;
	}


	public void convert() {

		if (mapConverter != null && mapConverter.convertTextures() && texture != null) {

			texture.export(UTPackageExtractor.getExtractor(mapConverter, texture));

			if (texture.getMaterialInfo() != null) {
				texture.getMaterialInfo().findRessourcesFromNames(mapConverter);

				if (texture.getMaterialInfo().getDiffuse() != null) {
					texture.replaceWith(texture.getMaterialInfo().getDiffuse());
				}
			}

			if (texture.getReplacement() != null) {
				texture.getReplacement().export(UTPackageExtractor.getExtractor(mapConverter, texture.getReplacement()));
			}

			// UE1/UE2 -> UE3+
			if (mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE2)) {

				// need to get texture dimension from UE1/UE2 to UE4 to get
				// the right UV scaling
				texture.readTextureDimensions();

				Dimension texDimension;

				if (texture.getReplacement() != null) {
					texDimension = texture.getReplacement().getTextureDimensions();
				} else {
					texDimension = texture.getTextureDimensions();
				}

				// Recompute origin if panU and panV > 0 (these properties no longer exist in UE2+)
				if (origin != null) {
					// Since UE2, panU, panV is reverted
					origin = Geometry.computeNewOrigin(origin, textureU, textureV, -panU, -panV);
					this.panU = 0;
					this.panV = 0;
				}

				if (texDimension != null) {

					// since UE3, texture U/V depends on texture dimension
					// since UE4, grid base unit is 100
					if (textureU != null) {
						textureU.scale(1 / (texDimension.width / (mapConverter.isTo(UnrealEngine.UE3) ? 128d : 100d)));
					}

					if (textureV != null) {
						textureV.scale(1 / (texDimension.height / (mapConverter.isTo(UnrealEngine.UE3) ? 128d : 100d)));
					}
				}

			}

			// UE3->UE4+
			// since UE4, grid base unit is 100
			else if (mapConverter.isFrom(UnrealEngine.UE3)) {

				if (textureU != null) {
					textureU.scale(100d / 128d);
				}

				if (textureV != null) {
					textureV.scale(100d / 128d);
				}
			}
		}

		// originally UE1 had a low light resolution
		// default is 32 in UE4
		// 128 seems good enough
		if (mapConverter != null) {
			lightMapScale = mapConverter.getLightMapResolution();
		}
	}

	public void setMapConverter(MapConverter mapConverter) {
		this.mapConverter = mapConverter;
	}

	public void setTexture(UPackageRessource texture) {
		this.texture = texture;
	}

	public List<Vector3d> getVertices() {
		return vertices;
	}

	public UPackageRessource getTexture() {
		return texture;
	}

	public void setLightMapScale(int lightMapScale) {
		this.lightMapScale = lightMapScale;
	}

	public Vector3d getNormal() {
		return normal;
	}

	public Vector3d getTextureU() {
		return textureU;
	}

	public Vector3d getTextureV() {
		return textureV;
	}

	public Vector3d getOrigin() {
		return origin;
	}

	@Override
	public String toString() {
		return "T3DPolygon{" +
				"texture=" + texture +
				", origin=" + origin +
				", normal=" + normal +
				", panU=" + panU +
				", panV=" + panV +
				", textureU=" + textureU +
				", textureV=" + textureV +
				", vertices=" + vertices +
				'}';
	}
}
