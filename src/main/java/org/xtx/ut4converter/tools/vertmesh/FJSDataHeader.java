package org.xtx.ut4converter.tools.vertmesh;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.xtx.ut4converter.tools.BinUtils;
import org.xtx.ut4converter.tools.psk.PSKStaticMesh.BinReadWrite;

/**
 * Datafile header for vertmesh UE1 files see: -
 * - https://udn.epicgames.com/Two/rsrc/Two/BinaryFormatSpecifications/UnrealVertexAnimation.h -
 * - http://paulbourke.net/dataformats/unreal/
 * 
 * @author XtremeXp
 *
 */
public class FJSDataHeader implements BinReadWrite {

	/**
	 * _WORD	NumPolys;
	_WORD	NumVertices;
	_WORD	BogusRot;  		 //(unused)
	_WORD	BogusFrame;		 //(unused)
	DWORD	BogusNormX,BogusNormY,BogusNormZ; //(unused)
	DWORD	FixScale; 		 //(unused)
	DWORD	Unused1,Unused2,Unused3; //(unused)
	 */

	/**
	 * _WORD	NumPolys
	 */
	private short numPolys;
	private short numVertices;
	private short bogusRot;
	private short bogusFrame;
	private int bogusNormX, bogusNormY, bogusNormZ;
	private int fixScale;
	
	private int unused1, unused2, unused3; // 36 bytes
	private String unknown; // 12 bytes

	@Override
	public void read(ByteBuffer bf) {
		numPolys = bf.getShort();
		numVertices = bf.getShort();
		bogusRot = bf.getShort();
		bogusFrame = bf.getShort();
		
		bogusNormX = bf.getInt();
		bogusNormY = bf.getInt();
		bogusNormZ = bf.getInt();
		fixScale = bf.getInt();
		unused1 = bf.getInt();
		unused2 = bf.getInt();
		unused3 = bf.getInt();
		unknown = BinUtils.readString(bf, 12);
	}

	@Override
	public void write(FileOutputStream bos) throws IOException {
		BinUtils.writeShort(bos, numPolys);
		BinUtils.writeShort(bos, numVertices);
		BinUtils.writeShort(bos, bogusRot);
		BinUtils.writeShort(bos, bogusFrame);
		BinUtils.writeInt(bos, bogusNormX);
		BinUtils.writeInt(bos, bogusNormY);
		BinUtils.writeInt(bos, bogusNormZ);
		BinUtils.writeInt(bos, fixScale);
		BinUtils.writeInt(bos, unused1);
		BinUtils.writeInt(bos, unused2);
		BinUtils.writeInt(bos, unused3);
		BinUtils.writeString(bos, unknown, 12);
	}

	public String toString() {
		String s = "";
		s += "numPolys: " + numPolys + "\n";
		s += "numVertices: " + numVertices + "\n";
		s += "bogusRot: " + bogusRot + "\n";
		s += "bogusFrame: " + bogusFrame + "\n";
		s += "bogusNormX: " + bogusNormX + "\n";
		s += "bogusNormY: " + bogusNormY + "\n";
		s += "bogusNormZ: " + bogusNormZ + "\n";
		s += "fixScale: " + fixScale + "\n";
		s += "unused1: " + unused1 + "\n";
		s += "unused2: " + unused2 + "\n";
		s += "unused3: " + unused3 + "\n";
		s += "unknown: " + unknown + "\n";

		return s;
	}

	public short getNumPolys() {
		return numPolys;
	}

	
}
