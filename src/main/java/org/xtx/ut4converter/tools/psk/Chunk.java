package org.xtx.ut4converter.tools.psk;

import org.xtx.ut4converter.tools.psk.PSKStaticMesh.BinReadWrite;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 
 * @author XtremeXp
 *
 */
public class Chunk {

	private ChunkHeader header;
	private List<? extends BinReadWrite> data;

	public Chunk(String chunkID, List<? extends BinReadWrite> data, int dataSize) {
		super();

		this.header = new ChunkHeader(chunkID, data, dataSize);
		this.data = data;
	}

	public ChunkHeader getHeader() {
		return header;
	}

	public void setHeader(ChunkHeader header) {
		this.header = header;
	}

	public List<? extends BinReadWrite> getData() {
		return data;
	}

	public void setData(List<BinReadWrite> data) {
		this.data = data;
	}

	public void write(FileOutputStream bos) throws IOException {
		header.write(bos);

		if (data != null && !data.isEmpty()) {
			for (BinReadWrite writer : data) {
				writer.write(bos);
			}
		}
	}
}
