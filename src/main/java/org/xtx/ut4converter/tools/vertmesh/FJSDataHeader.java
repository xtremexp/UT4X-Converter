package org.xtx.ut4converter.tools.vertmesh;

import org.xtx.ut4converter.tools.BinUtils;
import org.xtx.ut4converter.tools.psk.PSKStaticMesh.BinReadWrite;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Datafile header for vertmesh UE1 files see: -
 * - <a href="https://udn.epicgames.com/Two/rsrc/Two/BinaryFormatSpecifications/UnrealVertexAnimation.h">...</a> -
 * - <a href="http://paulbourke.net/dataformats/unreal/">...</a>
 *
 * @author XtremeXp
 *
 */
public class FJSDataHeader implements BinReadWrite {

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
	private byte[] unknown; // 12 bytes

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

		unknown = new byte[12];
		unknown[0] = bf.get();
		unknown[1] = bf.get();
		unknown[2] = bf.get();
		unknown[3] = bf.get();
		unknown[4] = bf.get();
		unknown[5] = bf.get();
		unknown[6] = bf.get();
		unknown[7] = bf.get();
		unknown[8] = bf.get();
		unknown[9] = bf.get();
		unknown[10] = bf.get();
		unknown[11] = bf.get();
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
		bos.write(unknown);
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
		s += "unknown: " + Arrays.toString(unknown) + "\n";

		return s;
	}

	public short getNumPolys() {
		return numPolys;
	}

	
}
