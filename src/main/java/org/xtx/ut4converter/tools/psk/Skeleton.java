package org.xtx.ut4converter.tools.psk;

import org.xtx.ut4converter.tools.BinUtils;
import org.xtx.ut4converter.tools.psk.PSKStaticMesh.BinReadWrite;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 
 * @author XtremeXp
 *
 */
public class Skeleton implements BinReadWrite {

	protected static final int DATA_SIZE = 120;

	private String name;
	private int flags;
	private int numChildren;
	private int parentIndex;
	private Vector4d orientation;
	private Vector3d position;
	private float lenght;
	private Vector3d size;

	public Skeleton(ByteBuffer bf) {
		read(bf);
	}

	public void write(FileOutputStream bos) throws IOException {
		BinUtils.writeString(bos, name, 64); // 64 bytes
		BinUtils.writeInt(bos, flags);
		BinUtils.writeInt(bos, numChildren);
		BinUtils.writeInt(bos, parentIndex);
		BinUtils.writeVector4d(bos, orientation);
		BinUtils.writeVector3d(bos, position);
		BinUtils.writeFloat(bos, lenght);
		BinUtils.writeVector3d(bos, size);
	}

	@Override
	public void read(ByteBuffer bf) {
		name = BinUtils.readString(bf, 64);
		flags = bf.getInt();
		numChildren = bf.getInt();
		parentIndex = bf.getInt();
		orientation = BinUtils.readVector4d(bf);
		position = BinUtils.readVector3d(bf);
		lenght = bf.getFloat();
		size = BinUtils.readVector3d(bf);

	}
}
