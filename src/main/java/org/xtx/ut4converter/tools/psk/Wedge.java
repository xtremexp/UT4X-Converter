package org.xtx.ut4converter.tools.psk;

import org.xtx.ut4converter.tools.BinUtils;
import org.xtx.ut4converter.tools.psk.PSKStaticMesh.BinReadWrite;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 
 * @author XtremeXp
 *
 */
public class Wedge implements BinReadWrite {

	protected static final int DATA_SIZE = 16;

	private int pointIndex;
	private float u;
	private float v;
	private byte matIndex;
	/**
	 * For mesh with many wedges
	 */
	private int matIndexInt;
	private byte reserved;
	private short pad;

	boolean isBigWedge = false;

	/**
	 * 
	 * @param bf
	 *            Byte reader
	 * @param isBigWedge
	 *            True if staticmesh has more than 65536 wedges
	 */
	public Wedge(ByteBuffer bf, boolean isBigWedge) {

		this.isBigWedge = isBigWedge;
		read(bf);
	}

	public void write(FileOutputStream bos) throws IOException {

		if (!isBigWedge) {
			BinUtils.writeInt(bos, (int) pointIndex);
			BinUtils.writeFloat(bos, u);
			BinUtils.writeFloat(bos, v);
			BinUtils.writeInt(bos, matIndexInt);
		} else {
			BinUtils.writeShort(bos, (short) pointIndex);
			bos.write(ByteBuffer.allocate(2).array());
			BinUtils.writeFloat(bos, u);
			BinUtils.writeFloat(bos, v);
			bos.write(matIndex);
			bos.write(reserved);
			BinUtils.writeShort(bos, pad);
		}
	}

	public float getU() {
		return u;
	}

	public float getV() {
		return v;
	}
	

	public int getPointIndex() {
		return pointIndex;
	}

	@Override
	public void read(ByteBuffer bf) {

		if (isBigWedge) {
			pointIndex = bf.getInt(); // 4
			u = bf.getFloat(); // 4
			v = bf.getFloat(); // 4
			matIndexInt = bf.getInt(); // 4
		} else {
			pointIndex = bf.getShort(); // 2
			bf.getShort(); // 2
			u = bf.getFloat(); // 4
			v = bf.getFloat(); // 4
			matIndex = bf.get(); // 1
			reserved = bf.get(); // 1
			pad = bf.getShort(); // 2
		}
	}

}
