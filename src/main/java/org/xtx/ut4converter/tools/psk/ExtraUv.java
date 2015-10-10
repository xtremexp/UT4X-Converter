package org.xtx.ut4converter.tools.psk;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.vecmath.Vector2d;

import org.xtx.ut4converter.tools.BinUtils;
import org.xtx.ut4converter.tools.psk.PSKStaticMesh.BinReadWrite;

/**
 * 
 * @author XtremeXp
 *
 */
public class ExtraUv extends Vector2d implements BinReadWrite {

	/**
	 * 2 * 4
	 */
	protected static final int DATA_SIZE = 8;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6092254432611264842L;

	public ExtraUv(ByteBuffer bf) {
		read(bf);
	}

	@Override
	public void write(FileOutputStream bos) throws IOException {
		BinUtils.writeVector2d(bos, this);
	}

	@Override
	public void read(ByteBuffer bf) {
		Vector2d v = BinUtils.readVector2d(bf);
		this.x = v.x;
		this.y = v.y;
	}

}
