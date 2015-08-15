/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.t3d;

import java.text.DecimalFormat;
import java.util.LinkedList;
import javax.vecmath.Vector3d;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.tools.Geometry;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.geom.Vertex;

/**
 * 
 * @author XtremeXp
 */
public class T3DPolygon {

	/**
	 * Original texture or material applied to the polygon
	 */
	UPackageRessource texture;

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
	Integer link;

	/**
	 * Some flag about how the polygon should be rendered such as istranslucent,
	 * ismirror, is two-sided and so on. This value is a sum of values that are
	 * powers of two Flags=217890775
	 */
	Integer flag;

	/**
	 * LightMapScale=64.000000 UE1: None UE2: ? UE3: ? UE4: Light Map
	 * resolution.
	 */
	Double lightMapScale;

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
	Integer smoothingMask;

	public Vector3d texture_u,

	/**
     *
     */
	texture_v;

	/**
     *
     */
	public LinkedList<Vertex> vertices = new LinkedList<>();

	MapConverter mapConverter;

	/**
     *
     */
	public T3DPolygon() {
		origin = new Vector3d(0d, 0d, 0d);
		normal = new Vector3d(0d, 0d, 1d);
	}

	/**
	 *
	 * @param t3dLine
	 * @param mapConverter
	 */
	public T3DPolygon(String t3dLine, MapConverter mapConverter) {
		// Begin Polygon Texture=Rockwal4 Flags=32768 Link=322
		this.mapConverter = mapConverter;
		this.texture = mapConverter.getUPackageRessource(T3DUtils.getString(t3dLine, "Texture"), T3DRessource.Type.TEXTURE);
		this.link = T3DUtils.getInteger(t3dLine, "Link");
		this.flag = T3DUtils.getInteger(t3dLine, "Flags");
	}

	/**
	 * Scales the polygon
	 * 
	 * @param newScale
	 */
	public void scale(Double newScale) {

		this.origin.scale(newScale);
		scaleUV(newScale, false);

		if (newScale != null) {

			for (Vertex vertex : vertices) {
				vertex.scale(newScale);
			}
		}
	}

	/**
	 * Scales UV
	 * 
	 * @param newScale
	 *            Scale factor
	 */
	private void scaleUV(Double newScale, boolean noPanScale) {

		if (newScale != null) {

			if (!noPanScale) {
				pan_u *= newScale;
				pan_v *= newScale;
			}

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
	 * @param mainScale
	 * @param rotation
	 * @param postScale
	 */
	public void transformPermanently(Vector3d mainScale, Vector3d rotation, Vector3d postScale) {

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
	 * @param sb
	 * @param df
	 *            Default Decimal format (not creating one each time for perf
	 *            issues)
	 * @param prefix
	 * @param numPoly
	 */
	public void toT3D(StringBuilder sb, DecimalFormat df, String prefix, int numPoly) {

		prefix += "\t\t\t";

		sb.append(prefix).append("Begin Polygon Item=Side");

		if (texture != null) {
			sb.append(" Texture=").append(texture.getConvertedName(mapConverter));
		}

		if (lightMapScale != null) {
			sb.append(" LightMapScale=").append(lightMapScale);
		}

		sb.append(" Link=").append(numPoly);

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

	public void addVertex(Vertex vertex) {
		vertices.add(vertex);
	}

	public T3DPolygon addVertex(Double x, Double y, Double z) {

		vertices.add(new Vertex(new Vector3d(x, y, z), this));
		return this;
	}

	/**
	 * Calculate the normal of this polygon
	 */
	public void calculateNormal() {

		Vector3d n = new Vector3d(0, 0, 0);

		for (int i = 2; i < vertices.size(); i++) {

			Vector3d edge1 = new Vector3d(vertices.get(i - 1).getCoordinates());
			edge1.sub(vertices.get(0).getCoordinates());

			Vector3d edge2 = new Vector3d(vertices.get(i).getCoordinates());
			edge2.sub(vertices.get(0).getCoordinates());

			Vector3d crsProd = new Vector3d(0, 0, 0);
			crsProd.cross(edge1, edge2);

			n.add(crsProd);
		}

		n.normalize();

		this.normal = n;
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

	public void setTextureU(Vector3d textureU) {
		this.texture_u = textureU;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @deprecated Use setTextureU
	 */
	public void setTexU(Double x, Double y, Double z) {
		texture_u = new Vector3d(x, y, z);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @deprecated Use setTextureV
	 */
	public void setTexV(Double x, Double y, Double z) {
		texture_v = new Vector3d(x, y, z);
	}

	public void setOrigin(Vector3d origin) {
		this.origin = origin;
	}

	public void convert() {

		if (mapConverter != null && mapConverter.convertTextures() && texture != null) {

			texture.export(UTPackageExtractor.getExtractor(mapConverter, texture));

			// For Unreal 3 and 4
			// we need to update the UV scaling which is dependant from texture
			// size
			if (mapConverter.isFromUE1UE2ToUE3UE4()) {

				texture.readTextureDimensions();

				// maybe bufferedimagereader could not read the dimensions of
				// texture
				if (texture.getTextureDimensions() != null) {

					if (texture_u != null) {
						texture_u.scale(1 / (texture.getTextureDimensions().width / 100d));
					}

					if (texture_v != null) {
						texture_v.scale(1 / (texture.getTextureDimensions().height / 100d));
					}

				}

				if (origin != null) {
					// i guess it depends of normal
					// todo check
					origin.x += pan_u;
					origin.y += pan_v;
				}
			}
		}
	}

	public void setMapConverter(MapConverter mapConverter) {
		this.mapConverter = mapConverter;
	}

	public void setTexture(UPackageRessource texture) {
		this.texture = texture;
	}

	/**
	 * Set the smoothing mask (?)
	 * 
	 * @param smoothingMask
	 */
	public void setSmoothingMask(Integer smoothingMask) {
		this.smoothingMask = smoothingMask;
	}

	public LinkedList<Vertex> getVertices() {
		return vertices;
	}
}
