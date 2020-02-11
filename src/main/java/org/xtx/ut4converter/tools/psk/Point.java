package org.xtx.ut4converter.tools.psk;

import org.xtx.ut4converter.tools.BinUtils;
import org.xtx.ut4converter.tools.psk.PSKStaticMesh.BinReadWrite;

import javax.vecmath.Vector3d;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 
 * @author XtremeXp
 *
 */
public class Point extends Vector3d implements BinReadWrite {

	protected static final int DATA_SIZE = 12;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Point(ByteBuffer bf) {
		read(bf);
	}

	@Override
	public void write(FileOutputStream bos) throws IOException {
		BinUtils.writeVector3d(bos, this);
	}

	@Override
	public void read(ByteBuffer bf) {
		Vector3d v = BinUtils.readVector3d(bf);
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
}