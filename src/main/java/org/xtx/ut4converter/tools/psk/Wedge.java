package org.xtx.ut4converter.tools.psk;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.xtx.ut4converter.tools.BinUtils;
import org.xtx.ut4converter.tools.psk.PSKStaticMesh.BinReadWrite;

/**
 * 
 * @author XtremeXp
 *
 */
public class Wedge implements BinReadWrite {

	protected static final int DATA_SIZE = 16;

	/**
	 * If index bytes as integer rather than short type
	 */
	protected static final int DATA_SIZE_INT = 18;

	private long pointIndex;
	private float u;
	private float v;
	private byte matIndex;
	private byte reserved;
	private short pad;

	/**
	 * 
	 */
	boolean isPointIndexAsShort;

	/**
	 * 
	 * @param bf
	 *            Byte reader
	 * @param isPointIndexAsShort
	 *            If point index is stored as a short else is stored as integer
	 */
	public Wedge(ByteBuffer bf, boolean isPointIndexAsShort) {

		this.isPointIndexAsShort = isPointIndexAsShort;
		read(bf);
	}

	public void write(FileOutputStream bos) throws IOException {

		if (isPointIndexAsShort) {
			BinUtils.writeShort(bos, (short) pointIndex);
			bos.write(ByteBuffer.allocate(2).array());
		} else {
			BinUtils.writeInt(bos, (int) pointIndex);
		}

		BinUtils.writeFloat(bos, u);
		BinUtils.writeFloat(bos, v);
		bos.write(matIndex);
		bos.write(reserved);
		BinUtils.writeShort(bos, pad);
	}

	public float getU() {
		return u;
	}

	public float getV() {
		return v;
	}

	@Override
	public void read(ByteBuffer bf) {

		if (isPointIndexAsShort) {
			pointIndex = bf.getShort();
			bf.getShort();
		} else {
			pointIndex = bf.getInt();
		}

		u = bf.getFloat();
		v = bf.getFloat();
		matIndex = bf.get();
		reserved = bf.get();
		pad = bf.getShort();
	}

}
