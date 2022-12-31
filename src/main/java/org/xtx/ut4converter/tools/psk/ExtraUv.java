package org.xtx.ut4converter.tools.psk;

import org.xtx.ut4converter.tools.BinUtils;
import org.xtx.ut4converter.tools.psk.PSKStaticMesh.BinReadWrite;

import javax.vecmath.Vector2d;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serial;
import java.nio.ByteBuffer;

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
	@Serial
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
