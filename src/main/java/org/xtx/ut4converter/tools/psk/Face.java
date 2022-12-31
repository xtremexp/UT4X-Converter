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
public class Face implements BinReadWrite {

	protected static final int DATA_SIZE = 12;

	protected static final int DATA_SIZE_FACE32 = 18;
	
	private int wedge0, wedge1, wedge2;
	private byte matIndex, auxMatIndex;
	private int smoothingGroups;
	
	boolean isFace32;

	public Face(ByteBuffer bf, boolean isFace32) {
		this.isFace32 = isFace32;
		read(bf);
	}

	public void write(FileOutputStream bos) throws IOException {
		
		if(!isFace32){
			BinUtils.writeShort(bos, (short) wedge0);
			BinUtils.writeShort(bos, (short) wedge1);
			BinUtils.writeShort(bos, (short) wedge2);
		} else {
			BinUtils.writeInt(bos, wedge0);
			BinUtils.writeInt(bos, wedge1);
			BinUtils.writeInt(bos, wedge2);
		}
		bos.write(matIndex);
		bos.write(auxMatIndex);
		BinUtils.writeInt(bos, smoothingGroups);
	}

	@Override
	public void read(ByteBuffer bf) {
		
		if(!isFace32){
			wedge0 = bf.getShort();
			wedge1 = bf.getShort();
			wedge2 = bf.getShort();
		} else {
			wedge0 = bf.getInt();
			wedge1 = bf.getInt();
			wedge2 = bf.getInt();
		}
		matIndex = bf.get();
		auxMatIndex = bf.get();
		smoothingGroups = bf.getInt();

	}

	public int getWedge0() {
		return wedge0;
	}

	public int getWedge1() {
		return wedge1;
	}

	public int getWedge2() {
		return wedge2;
	}

	public byte getMatIndex() {
		return matIndex;
	}

	public int getSmoothingGroups() {
		return smoothingGroups;
	}
	
	

}
