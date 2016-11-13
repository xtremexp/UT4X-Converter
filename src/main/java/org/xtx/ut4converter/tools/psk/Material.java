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
public class Material implements BinReadWrite {

	protected static final int DATA_SIZE = 88;

	/**
	 * 
	 */
	public static final int MATNAME_MAX_SIZE = 64;

	/**
	 * 64 bytes
	 */
	private String materialName;

	/**
	 * 4 bytes
	 */
	private int textureIndex;
	private int polyFlags;
	private int auxMaterial;
	private int auxFlags;
	private int lodBias;
	private int lodStyle;

	public Material() {
	}

	public Material(ByteBuffer bf) {
		read(bf);
	}

	public String getMaterialName() {
		return materialName;
	}

	public void write(FileOutputStream bos) throws IOException {
		BinUtils.writeString(bos, materialName, MATNAME_MAX_SIZE);
		BinUtils.writeInt(bos, textureIndex);
		BinUtils.writeInt(bos, polyFlags);
		BinUtils.writeInt(bos, auxMaterial);
		BinUtils.writeInt(bos, auxFlags);
		BinUtils.writeInt(bos, lodBias);
		BinUtils.writeInt(bos, lodStyle);
	}

	@Override
	public void read(ByteBuffer bf) {
		materialName = BinUtils.readString(bf, MATNAME_MAX_SIZE).trim();
		textureIndex = bf.getInt();
		polyFlags = bf.getInt();
		auxMaterial = bf.getInt();
		auxFlags = bf.getInt();
		lodBias = bf.getInt();
		lodStyle = bf.getInt();

	}

	/**
	 * Allow chaning material name. Used to change original matname with
	 * converter naming convention (e.g: <packagename>_<group>_<name>_mat )
	 * 
	 * @param materialName
	 */
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

}
