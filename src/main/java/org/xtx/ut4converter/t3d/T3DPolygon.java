/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.geom.Vertex;
import org.xtx.ut4converter.tools.Geometry;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ucore.ue1.UnMath.FScale;

import javax.vecmath.Vector3d;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.LinkedList;

/**
 * 
 * @author XtremeXp
 */
public class T3DPolygon {

	/**
	 * Original texture or material applied to the polygon
	 */
	private UPackageRessource texture;

	/**
     *
     */
	public Vector3d origin;

	/**
     *
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
	 * LightMapScale=64.000000 UE1: None UE2: ? UE3: ? UE4: Light Map
	 * resolution.
	 */
	private int lightMapScale;

	/**
	 * Only in UE1/UE2 Basically changes the origin
	 */
	public double pan_u,

	/**
     *
     */
	pan_v;

	/**
	 * As seen in Polys.h
	 * "A mask used to determine which smoothing groups this polygon is in" No
	 * idea what it does.
	 */
	private Integer smoothingMask;

	private Vector3d original_tex_u, original_tex_v;

	/**
	 * UV texture align
	 */
	private Vector3d texture_u, texture_v;

	/**
     *
     */
	public LinkedList<Vertex> vertices = new LinkedList<>();

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

		for (Vertex vertex : vertices) {
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

			pan_u *= newScale;
			pan_v *= newScale;

			if (texture_u != null) {
				texture_u.scale(1 / newScale);
			}

			if (texture_v != null) {
				texture_v.scale(1 / newScale);
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

		if (texture_u != null) {
			Geometry.transformPermanently(texture_u, mainScale, rotation, postScale, true);
		}

		if (texture_v != null) {
			Geometry.transformPermanently(texture_v, mainScale, rotation, postScale, true);
		}

		for (Vertex vertex : vertices) {
			Geometry.transformPermanently(vertex.getCoordinates(), mainScale, rotation, postScale, false);
		}
	}
	
	
	

	/**
	 * Revert the order of vertices
	 */
	public void reverseVertexOrder() {

		LinkedList<Vertex> verticesReverted = new LinkedList<>();

		for (Vertex vertex : vertices) {
			verticesReverted.addFirst(vertex);
		}

		vertices = verticesReverted;
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
			sb.append(" Texture=").append(texture.getConvertedName(mapConverter));
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

		if (pan_u > 0d || pan_v > 0d) {
			sb.append(prefix).append("\tPan      ");

			if (pan_u > 0d) {
				sb.append("U=").append(pan_u);
			}

			if (pan_v > 0d) {
				sb.append(" V=").append(pan_v);
			}

			sb.append("\n");
		}

		sb.append(prefix).append("\tTextureU ").append(T3DUtils.toPolyStringVector3d(texture_u, df)).append("\n");
		sb.append(prefix).append("\tTextureV ").append(T3DUtils.toPolyStringVector3d(texture_v, df)).append("\n");

		for (Vertex vertex : vertices) {
			sb.append(prefix).append("\tVertex   ").append(T3DUtils.toPolyStringVector3d(vertex.getCoordinates(), df)).append("\n");
		}

		sb.append(prefix).append("End Polygon\n");

	}

	public T3DPolygon addVertex(Double x, Double y, Double z) {

		vertices.add(new Vertex(new Vector3d(x, y, z)));
		return this;
	}

	public void setNormal(Vector3d v) {
		normal = v;
	}

	public void setNormal(Double x, Double y, Double z) {
		normal = new Vector3d(x, y, z);
	}

	public void setTextureV(Vector3d textureV) {
		this.texture_v = textureV;
	}
	
	public void setTextureV(Double x, Double y, Double z) {
		this.texture_v = new Vector3d(x, y, z);
	}
	
	/**
	 * 
	 * @param x X
	 * @param y Y
	 * @param z Z
	 */
	public void setTextureU(Double x, Double y, Double z) {
		this.texture_u = new Vector3d(x, y, z);
	}

	public void setTextureU(Vector3d textureU) {
		this.texture_u = textureU;
	}

	/**
	 * 
	 * @param x X
	 * @param y Y
	 * @param z Z
	 * @deprecated Use setTextureU
	 */
	@Deprecated
	public void setTexU(Double x, Double y, Double z) {
		texture_u = new Vector3d(x, y, z);
	}

	/**
	 * 
	 * @param x X
	 * @param y Y
	 * @param z Z
	 * @deprecated Use setTextureV
	 */
	@Deprecated
	public void setTexV(Double x, Double y, Double z) {
		texture_v = new Vector3d(x, y, z);
	}

	public void setOrigin(Vector3d origin) {
		this.origin = origin;
	}

	/**
	 * UV conversion scale factor when converting UT3 texture scaling to UT4
	 * texture scaling (this is equals to 200/256)
	 */
	private static final double UE3_UE4_UV_SCALE_FACTOR = 0.78125d;

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

			// FOR UE3/UE4 conversion, need to update texture scale with texture dimension values
			if (mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE2) && mapConverter.isTo(UnrealEngine.UE3, UnrealEngine.UE4)) {

				// maybe bufferedimagereader could not read the dimensions of
				// texture
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

					if (texDimension != null) {

						// since UE4, grid base unit is 100
						if (texture_u != null) {
							texture_u.scale(1 / (texDimension.width / (mapConverter.isTo(UnrealEngine.UE3) ? 128d : 100d)));
						}

						if (texture_v != null) {
							texture_v.scale(1 / (texDimension.height / (mapConverter.isTo(UnrealEngine.UE3) ? 128d : 100d)));
						}
					}

					if (origin != null) {
						// FIXME texture coordinates are incorrect if PanU or PanV set
						origin.x += pan_u;
						origin.y += pan_v;
					}
				}

				// UE3->UE4, viewport grid has changed of scale, need to update texture scale
				else if (mapConverter.isFrom(UnrealEngine.UE3)) {

					if (texture_u != null) {
						texture_u.scale(UE3_UE4_UV_SCALE_FACTOR);
					}

					if (texture_v != null) {
						texture_v.scale(UE3_UE4_UV_SCALE_FACTOR);
					}
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

	public LinkedList<Vertex> getVertices() {
		return vertices;
	}

	public UPackageRessource getTexture() {
		return texture;
	}

	public void setLightMapScale(int lightMapScale) {
		this.lightMapScale = lightMapScale;
	}

	public void setTexture_u(Vector3d texture_u) {
		this.texture_u = texture_u;
	}

	public void setTexture_v(Vector3d texture_v) {
		this.texture_v = texture_v;
	}
}
