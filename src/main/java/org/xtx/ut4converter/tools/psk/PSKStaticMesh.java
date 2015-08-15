package org.xtx.ut4converter.tools.psk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DBrush;

/**
 * PSK staticmesh file reader.
 * 
 * Code partially ported to java from actorx importer source code -
 * http://www.gildor.org/projects/unactorx
 * 
 */
public class PSKStaticMesh {

	/**
	 * Reference to .psk file
	 */
	File pskFile;

	public PSKStaticMesh(File pskFile) {
		super();
		this.pskFile = pskFile;
		initialise();
		load();
	}

	private void initialise() {
		vertices = new ArrayList<>();
		wedges = new ArrayList<>();
		triangles = new ArrayList<>();
		materials = new ArrayList<>();
		bones = new ArrayList<>();
		rawWeights = new ArrayList<>();
	}

	List<Vector3d> vertices;
	List<Vertex> wedges;
	List<Triangle> triangles;
	List<Material> materials;
	List<Bone> bones;
	List<RawWeight> rawWeights;

	class ChunkHeader {

		public String chunkID;
		public long typeFlag;
		public long dataSize;
		public long dataCount;

		public ChunkHeader(ByteBuffer bf) {

			chunkID = readString(bf, 20);
			typeFlag = bf.getInt();
			dataSize = bf.getInt();
			dataCount = bf.getInt();
		}
	}

	private String readString(ByteBuffer bf, int length) {

		String s = "";

		for (int i = 0; i < length; i++) {
			s += (char) bf.get();
		}

		return s;
	}

	public class Vertex {

		public long pointIndex;
		public float u, v;
		byte matIndex;
		byte reserved;
		short pad;

		public Vertex(ByteBuffer bf, boolean x) {

			if (x) {
				pointIndex = bf.getShort();
				bf.getShort();
				u = bf.getFloat();
				v = bf.getFloat();
				matIndex = bf.get();
				reserved = bf.get();
				pad = bf.getShort();

				System.out.println("pointIndex:" + pointIndex + " u:" + u + " v:" + v + " reserved:" + reserved + " pad:" + pad);
			} else {
				pointIndex = bf.getInt();
				u = bf.getFloat();
				v = bf.getFloat();
				matIndex = bf.get();
				reserved = bf.get();
				pad = bf.getShort();
			}
		}
	}

	public class Triangle {

		long wedge0, wedge1, wedge2;
		byte matIndex, auxMatIndex;
		long smoothingGroups;

		public Triangle(ByteBuffer bf) {
			wedge0 = bf.getShort();
			wedge1 = bf.getShort();
			wedge2 = bf.getShort();
			matIndex = bf.get();
			auxMatIndex = bf.get();
			smoothingGroups = bf.getInt();
			System.out.println("W0:" + wedge0 + " W1:" + wedge1 + " W2:" + wedge2 + " smoothingGroups:" + smoothingGroups);
		}
	}

	public class Material {

		String materialName;
		long textureIndex;
		long polyFlags;
		long auxMaterial;
		long auxFlags;
		long lodBias;
		long lodStyle;

		public Material(ByteBuffer bf) {
			materialName = readString(bf, 64).trim();
			textureIndex = bf.getInt();
			polyFlags = bf.getInt();
			auxMaterial = bf.getInt();
			auxFlags = bf.getInt();
			lodBias = bf.getInt();
			lodStyle = bf.getInt();

			System.out.println("materialName:" + materialName + " textureIndex:" + textureIndex + " polyFlags:" + polyFlags + " auxMaterial:" + auxMaterial);
		}
	}

	public class Bone {

		String name;
		long flags;
		long numChildren;
		long parentIndex;
		Vector4d orientation;
		Vector3d position;
		float lenght;
		Vector3d size;

		public Bone(ByteBuffer bf) {
			name = readString(bf, 64);
			flags = bf.getInt();
			numChildren = bf.getInt();
			parentIndex = bf.getInt();
			orientation = readVector4d(bf);
			position = readVector3d(bf);
			lenght = bf.getFloat();
			size = readVector3d(bf);
		}
	}

	public class RawWeight {

		float weight;
		long pointIndex;
		long boneIndex;

		public RawWeight(ByteBuffer bf) {
			weight = bf.getFloat();
			pointIndex = bf.getInt();
			boneIndex = bf.getInt();
		}
	}

	private Vector3d readVector3d(ByteBuffer bf) {

		Vector3d v = new Vector3d();
		v.x = bf.getFloat();
		v.y = bf.getFloat();
		v.z = bf.getFloat();

		System.out.println(v);
		return v;
	}

	private Vector4d readVector4d(ByteBuffer bf) {

		Vector4d v = new Vector4d();
		v.x = bf.getFloat();
		v.y = bf.getFloat();
		v.z = bf.getFloat();
		v.w = bf.getFloat();

		return v;
	}

	public void load() {

		FileChannel inChannel = null;

		try (FileInputStream fis = new FileInputStream(pskFile)) {

			inChannel = fis.getChannel();
			ByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int) pskFile.length());
			buffer.order(ByteOrder.LITTLE_ENDIAN);

			ChunkHeader header = new ChunkHeader(buffer);

			if (!"ACTRHEAD".equals(header.chunkID.trim())) {
				throw new Exception("Not a psk file");
			}

			while (buffer.hasRemaining()) {

				ChunkHeader ch2 = new ChunkHeader(buffer);

				if ("PNTS0000".equals(ch2.chunkID.trim())) {
					long numVertex = ch2.dataCount;

					for (int i = 0; i < numVertex; i++) {
						vertices.add(readVector3d(buffer));
					}

				} else if ("VTXW0000".equals(ch2.chunkID.trim())) {
					long numWedges = ch2.dataCount;

					boolean x = numWedges <= 65536;

					for (int i = 0; i < numWedges; i++) {
						wedges.add(new Vertex(buffer, x));
					}

				} else if ("FACE0000".equals(ch2.chunkID.trim())) {

					long numTriangles = ch2.dataCount;

					for (int i = 0; i < numTriangles; i++) {
						triangles.add(new Triangle(buffer));
					}

				} else if ("FACE3200".equals(ch2.chunkID.trim())) {

					long numTriangles = ch2.dataCount;
					System.out.println("TODO");

				}

				else if ("MATT0000".equals(ch2.chunkID.trim())) {

					long numMat = ch2.dataCount;

					for (int i = 0; i < numMat; i++) {
						materials.add(new Material(buffer));
					}
				}

				else if ("REFSKELT".equals(ch2.chunkID.trim())) {

					long numRefSklt = ch2.dataCount;

					for (int i = 0; i < numRefSklt; i++) {
						bones.add(new Bone(buffer));
					}
				}

				else if ("RAWWEIGHTS".equals(ch2.chunkID.trim())) {

					long numRawWeights = ch2.dataCount;

					for (int i = 0; i < numRawWeights; i++) {
						rawWeights.add(new RawWeight(buffer));
					}
				}
				// TODO EXTRAUV0
				// TODO FACE3200
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				inChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public File getPskFile() {
		return pskFile;
	}

	public List<Vector3d> getVertices() {
		return vertices;
	}

	public List<Vertex> getWedges() {
		return wedges;
	}

	public List<Triangle> getTriangles() {
		return triangles;
	}

	public List<Material> getMaterials() {
		return materials;
	}

	public List<Bone> getBones() {
		return bones;
	}

	public List<RawWeight> getRawWeights() {
		return rawWeights;
	}

	public static void main(String args[]) {

		File file = new File("Z:\\BarrenHardware_Lights_ELight01BA.psk");
		PSKStaticMesh psk = new PSKStaticMesh(file);

		System.out.println(file);

		System.out.println("Num Points: " + psk.getVertices().size());
		System.out.println("Num Wedges: " + psk.getWedges().size());
		System.out.println("Num Triangles: " + psk.getTriangles().size());
		System.out.println("Num Materials: " + psk.getMaterials().size());
		System.out.println("Num RawWeights: " + psk.getRawWeights().size());

		for (Material mat : psk.getMaterials()) {
			System.out.println("Material: " + mat.materialName);
		}

		System.out.println("Num Bones: " + psk.getBones().size());
	}
}
