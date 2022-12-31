package org.xtx.ut4converter.tools.vertmesh;

import org.xtx.ut4converter.tools.psk.PSKStaticMesh.BinReadWrite;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Representation of face for VertMesh
 */
public class FJSMeshTri implements BinReadWrite {

	/**
	 * _WORD iVertex[3]; // Vertex indices. = Short
	 * BYTE Type; // James' mesh type.(unused) 
	 * BYTE Color; // Color for flat and Gouraud shaded. (unused)
	 * FMeshByteUV Tex[3]; // Texture UV coordinates. 
	 * BYTE TextureNum; // Source texture offset. 
	 * BYTE Flags; // Unreal mesh flags (currently unused).
	 */

	private short[] iVertex; // 3
	private byte type;
	private byte color;
	private FMeshByteUV[] tex; // 3
	private byte textureNum;
	private byte flags;

	/**
	 * Texture coordinates associated with a vertex and one or more mesh
	 * triangles. All triangles sharing a vertex do not necessarily have the
	 * same texture coordinates at the vertex.
	 *
	 */
	public static class FMeshByteUV {
		byte u;
		byte v;

		public byte getU() {
			return u;
		}

		public byte getV() {
			return v;
		}
	}

    @Override
	public void write(FileOutputStream bos) throws IOException {
		// TODO Auto-generated method stub

	}

	public short[] getiVertex() {
		return iVertex;
	}

	public FMeshByteUV[] getTex() {
		return tex;
	}

	public byte getTextureNum() {
		return textureNum;
	}

	@Override
	public void read(ByteBuffer bf) {
		iVertex = new short[3];
		iVertex[0] = bf.getShort();
		iVertex[1] = bf.getShort();
		iVertex[2] = bf.getShort();

		type = bf.get();
		color = bf.get();
		FMeshByteUV tex_0 = new FMeshByteUV();
		tex_0.u = bf.get();
		tex_0.v = bf.get();

		FMeshByteUV tex_1 = new FMeshByteUV();
		tex_1.u = bf.get();
		tex_1.v = bf.get();

		FMeshByteUV tex_2 = new FMeshByteUV();
		tex_2.u = bf.get();
		tex_2.v = bf.get();

		tex = new FMeshByteUV[3];
		tex[0] = tex_0;
		tex[1] = tex_1;
		tex[2] = tex_2;

		textureNum = bf.get();
		flags = bf.get();
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("iVertex: ").append(iVertex[0]).append(", ").append(iVertex[1]).append(",").append(iVertex[1]).append("\n");
		s.append("type: ").append(type).append("\n");
		s.append("color: ").append(color).append("\n");

		for (FMeshByteUV fMeshByteUV : tex) {
			s.append("tex: ").append(fMeshByteUV.u).append(", ").append(fMeshByteUV.v).append("\n");
		}
		
		
		s.append("textureNum: ").append(textureNum).append("\n");
		s.append("flags: ").append(flags).append("\n");

		return s.toString();
	}

}
