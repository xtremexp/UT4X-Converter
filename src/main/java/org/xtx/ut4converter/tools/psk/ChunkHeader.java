package org.xtx.ut4converter.tools.psk;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.xtx.ut4converter.tools.BinUtils;
import org.xtx.ut4converter.tools.psk.PSKStaticMesh.BinReadWrite;

/**
 * 
 * @author XtremeXp
 *
 */
class ChunkHeader implements BinReadWrite {

	private static final int CHUNK_HEADER_TYPEFLAG = 1999801;

	public String chunkID;
	public int typeFlag;
	public int dataSize;
	public int dataCount;

	public ChunkHeader(String chunkID, List<? extends BinReadWrite> data, int dataSize) {
		super();
		this.chunkID = chunkID;
		this.typeFlag = CHUNK_HEADER_TYPEFLAG;
		this.dataSize = dataSize;

		if (data != null) {
			this.dataCount = data.size();
		} else {
			this.dataCount = 0;
		}
	}

	public ChunkHeader(String chunkID) {
		this.chunkID = chunkID;
	}

	public ChunkHeader(ByteBuffer bf)  {
		read(bf);
	}

	public void read(ByteBuffer bf) {
		chunkID = BinUtils.readString(bf, 20);
		typeFlag = bf.getInt();
		dataSize = bf.getInt();
		dataCount = bf.getInt();
	}

	public void write(FileOutputStream bos) throws IOException {
		BinUtils.writeString(bos, chunkID, 20);
		BinUtils.writeInt(bos, typeFlag);
		BinUtils.writeInt(bos, dataSize);
		BinUtils.writeInt(bos, dataCount);
	}
}
