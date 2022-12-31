/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.geom.Vertex;
import org.xtx.ut4converter.t3d.T3DBrush;
import org.xtx.ut4converter.t3d.T3DPolygon;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.ucore.UPackageRessource;

import javax.vecmath.Vector3d;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Converts t3d static mesh files (generated with
 * "ucc.exe batchexport staticmesh" command) to brush.
 * 
 * Example of some .t3d staticmesh file "Begin StaticMesh Name=phobosradar
 * Version=2.000000 BoundingBox.Min.X=-31.097290 BoundingBox.Min.Y=-53.882950
 * BoundingBox.Min.Z=-53.234379 BoundingBox.Max.X=39.252708
 * BoundingBox.Max.Y=55.240028 BoundingBox.Max.Z=53.234379 Begin Triangle
 * Texture Epic_Phoboswing.Shaders.cf_RadarShader SmoothingMask 1 Vertex 0
 * -7.909100 -53.882950 -17.342461 0.006540 0.200720 Vertex 1 -5.630610
 * -50.535172 -0.801760 0.042930 0.098930 Vertex 2 1.082450 -51.566898 -5.899310
 * 0.114000 0.132040 End Triangle ... "
 * 
 * @author XtremeXp
 */
public class T3DStaticMeshFileLoader {

	public enum ExportFormat {
		ASE, // Ascii Scene (can be imported by UT3 not UT4)
		FBX, // Flimbox format (can be imported by UT4 not UT3)
		T3D // Unreal Engine 2 internal format (this one)
	}

	private File t3dStaticMeshFile;
	private MapConverter mapConverter;
	private T3DBrush brush;
	private ExportFormat exportFormat;

	/**
	 *
	 * @param mapConverter
	 *            Map converter
	 * @param t3dStaticMeshFile
	 *            T3d static mesh file
	 */
	public T3DStaticMeshFileLoader(MapConverter mapConverter, File t3dStaticMeshFile) throws IOException {

		this.mapConverter = mapConverter;
		this.t3dStaticMeshFile = t3dStaticMeshFile;

		if (mapConverter.isTo(UTGames.UnrealEngine.UE4)) {
			exportFormat = ExportFormat.FBX;
		}

		else if (mapConverter.isTo(UTGames.UnrealEngine.UE3)) {
			exportFormat = ExportFormat.ASE;
		}

		loadBrush();
	}

	/**
	 * Loads polygon data from t3d line
	 * 
	 * @return List of polygons
	 */
	private void loadBrush() throws IOException {

		brush = new T3DBrush(mapConverter, null);
		LinkedList<T3DPolygon> polygons = new LinkedList<>();

		try (FileReader fr = new FileReader(t3dStaticMeshFile); BufferedReader bfr = new BufferedReader(fr)) {

			String line;
			T3DPolygon p = null;

			while ((line = bfr.readLine()) != null) {

				line = line.trim();

				if (line.startsWith("Begin Triangle")) {

					p = new T3DPolygon();
					p.setMapConverter(mapConverter);
				}

				// E.G: "Texture Epic_Phoboswing.Shaders.cf_RadarShader"
				else if (line.startsWith("Texture")) {

					String texture = line.split("Texture ")[1];

					UPackageRessource textureRessource = mapConverter.getUPackageRessource(texture, T3DRessource.Type.TEXTURE);

					if (p != null) {
						p.setTexture(textureRessource);
					}
				}

				// TODO check what is smoothing mask
				else if (line.startsWith("SmoothingMask")) {

					if (p != null) {
						p.setSmoothingMask(Integer.valueOf(line.split("SmoothingMask ")[1]));
					}
				}

				// e.g:
				// "Vertex 0 -2.313340 -48.702381 16.483009 -0.004290 0.000590"
				else if (line.startsWith("Vertex")) {

					String[] s = line.split(" ");

					Double x = Double.valueOf(s[2]);
					Double y = Double.valueOf(s[3]);
					Double z = Double.valueOf(s[4]);

					float u = Float.parseFloat(s[5]);
					float v = Float.parseFloat(s[6]);

					Vertex vertex = new Vertex(x, y, z, u, v);

					if (p != null) {
						// Temp texU/texV until figuring out the formula
						p.setTextureU(new Vector3d(0, 1, 0));
						p.setTextureV(new Vector3d(1, 0, 0));

						p.addVertex(vertex);
					}
				}

				else if (line.startsWith("End Triangle")) {

					if (p != null) {
						p.calculateNormal();
						p.reverseVertexOrder();
						p.setOrigin(p.getVertices().getFirst().getCoordinates());
						polygons.add(p);
					}
				}

				else if (line.startsWith("Begin StaticMesh")) {

					String name = line.split("=")[1];
					brush.setName(name + "_SMBrush");
				}
			}
		}

		brush.setPolyList(polygons);
	}


}
