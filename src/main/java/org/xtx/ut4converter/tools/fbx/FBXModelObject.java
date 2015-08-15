/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools.fbx;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.Locale;

import javax.vecmath.Vector3d;

import org.xtx.ut4converter.geom.Vertex;
import org.xtx.ut4converter.t3d.T3DBrush;
import org.xtx.ut4converter.t3d.T3DPolygon;
import org.xtx.ut4converter.tools.psk.PSKStaticMesh;
import org.xtx.ut4converter.tools.psk.PSKStaticMesh.Triangle;

/**
 *
 * @author XtremeXp
 */
public class FBXModelObject extends FBXObject {

	final short MODEL_VERSION = 232;
	final short GEOMETRY_VERSION = 124;
	public static DecimalFormat df = new DecimalFormat("#0.000000", new DecimalFormatSymbols(Locale.US));

	short geometryVersion;

	LinkedList<FBXLayerElement> layerElements;

	LinkedList<Double> vertices;
	LinkedList<Double> normals;

	/**
	 * Vertex index, last one of poly is always negated
	 */
	LinkedList<Integer> polygonVertexIndices;

	LinkedList<Float> uvs;

	/**
	 * Reference to brush
	 */
	Object source;

	public FBXModelObject(T3DBrush brush) {
		super(FBXObjectType.Model);

		version = MODEL_VERSION;
		geometryVersion = GEOMETRY_VERSION;
		this.source = brush;
		this.subName = "Mesh";
		initialise(brush);
	}

	public FBXModelObject(PSKStaticMesh pskMesh) {
		super(FBXObjectType.Model);

		version = MODEL_VERSION;
		geometryVersion = GEOMETRY_VERSION;
		layerElements = new LinkedList<>();

		this.name = pskMesh.getPskFile().getName();

		vertices = new LinkedList<>();
		normals = new LinkedList<>();
		polygonVertexIndices = new LinkedList<>();
		uvs = new LinkedList<>();

		loadPsk(pskMesh);
		FBXLayerElementNormal layerNormal = new FBXLayerElementNormal(normals, FBXLayerElementNormal.MappingInformationType.ByPolygon);
		layerElements.add(layerNormal);
		layerElements.add(new FBXLayerElementMaterial());
	}

	private void initialise(T3DBrush brush) {

		layerElements = new LinkedList<>();

		if (brush.getName() != null) {
			this.name = brush.getName();
		} else {
			this.name = "SomeSM";
		}

		vertices = new LinkedList<>();
		normals = new LinkedList<>();
		polygonVertexIndices = new LinkedList<>();
		uvs = new LinkedList<>();

		load(brush.getPolyList());
		FBXLayerElementNormal layerNormal = new FBXLayerElementNormal(normals, FBXLayerElementNormal.MappingInformationType.ByPolygon);
		layerElements.add(layerNormal);
		layerElements.add(new FBXLayerElementMaterial());
	}

	private void loadPsk(PSKStaticMesh pskMesh) {

		for (Vector3d v : pskMesh.getVertices()) {

			vertices.add(v.x);
			vertices.add(v.y);
			vertices.add(v.z);
		}

		for (org.xtx.ut4converter.tools.psk.PSKStaticMesh.Vertex v : pskMesh.getWedges()) {
			uvs.add(v.u);
			uvs.add(v.v);

		}

	}

	private void load(LinkedList<T3DPolygon> polygons) {

		for (T3DPolygon p : polygons) {

			for (Vertex v : p.vertices) {

				vertices.add(v.getX());
				vertices.add(v.getY());
				vertices.add(v.getZ());

				uvs.add(v.getU());
				uvs.add(v.getV());

				// Last one always negates the index and adds -1
				if (v == p.vertices.getLast()) {
					polygonVertexIndices.add((v.getVertexPolyIdx() * -1) - 1);
				}

				else {
					polygonVertexIndices.add(v.getVertexPolyIdx());
				}

			}

			normals.add(p.normal.x);
			normals.add(p.normal.y);
			normals.add(p.normal.z);
		}
	}

	@Override
	public void writeFBX(StringBuilder sb) {

		sb.append("\tModel: \"Model::").append(this.name).append("\", \"").append(subName).append("\" {\n");
		sb.append("\t\tVersion: ").append(version).append("\n");
		sb.append("\t\tVertices: ");

		int idx = 1;

		for (Double v : vertices) {

			if (idx > 1) {
				sb.append(",").append(df.format(v));
			} else {
				sb.append(df.format(v));
			}

			if (idx % 12 == 0 && idx < vertices.size() - 1) {
				sb.append("\n\t\t");
			}

			idx++;
		}

		sb.append("\n");

		writePolygonVertexIndex(sb);

		sb.append("\t\tUV: ");

		for (Float uv : uvs) {
			sb.append(df.format(uv)).append(",");
		}

		sb.deleteCharAt(sb.length() - 1);
		sb.append("\n");

		sb.append("\t\tGeometryVersion: ").append(geometryVersion).append("\n");

		for (FBXLayerElement layerElement : layerElements) {
			layerElement.writeFBX(sb);
			sb.append("\n");
		}

		sb.append("\t\tLayer: 0 {\n");
		sb.append("\t\tVersion: 100 \n");

		for (FBXLayerElement layerElement : layerElements) {

			sb.append("\t\tLayerElement:  {\n");
			sb.append("\t\t\tType: \"").append(layerElement.type.name()).append("\"\n");
			sb.append("\t\t\tTypedIndex: 0\n");
			sb.append("\t\t}\n");
		}

		sb.append("\t}\n\n");

		sb.append("}\n");
	}

	/**
	 * Adapted to java from export_fbx.py python script for blender
	 * 
	 * @param sb
	 */
	private void writePolygonVertexIndex(StringBuilder sb) {

		sb.append("\t\tPolygonVertexIndex: ");

		int idx = 0;

		for (Integer polyVertexIdx : polygonVertexIndices) {

			if (idx > 0) {
				sb.append(",").append(polyVertexIdx);
			} else {
				sb.append(polyVertexIdx);
			}

			if (idx > 0 && idx % 12 == 0 && idx < polygonVertexIndices.size() - 1) {
				sb.append("\n\t\t");
			}

			idx++;
		}

		sb.append("\n");
	}
}
