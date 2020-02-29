package org.xtx.ut4converter.tools;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Utility class for write/read binary data
 * 
 * // BYTE = 8 Bits = Byte CHAR = 8 bits WORD = 16 bits = Short DWORD = 32 bits
 * = Int int = 4 bytes long = 8 bytes
 * 
 * @author XtremeXp
 *
 *         DWORD = int in java
 */
public class BinUtils {

	private static final ByteOrder BYTE_ORDER_LE = ByteOrder.LITTLE_ENDIAN;

	public static void writeInt(FileOutputStream bos, int someInt) throws IOException {
		byte[] bytes = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).order(BYTE_ORDER_LE).putInt(someInt).array();
		bos.write(bytes);
	}

	public static void writeFloat(FileOutputStream bos, float someFloat) throws IOException {
		byte[] bytes = ByteBuffer.allocate(Float.SIZE / Byte.SIZE).order(BYTE_ORDER_LE).putFloat(someFloat).array();
		bos.write(bytes);
	}

	public static void writeShort(FileOutputStream bos, short someShort) throws IOException {
		byte[] bytes = ByteBuffer.allocate(Short.SIZE / Byte.SIZE).order(BYTE_ORDER_LE).putShort(someShort).array();
		bos.write(bytes);
	}

	public static void writeLong(FileOutputStream bos, long someLong) throws IOException {
		byte[] bytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).order(BYTE_ORDER_LE).putLong(someLong).array();
		bos.write(bytes);
	}

	public static Vector4d readVector4d(ByteBuffer bf) {

		Vector4d v = new Vector4d();
		v.x = bf.getFloat();
		v.y = bf.getFloat();
		v.z = bf.getFloat();
		v.w = bf.getFloat();

		return v;
	}

	public static Vector2d readVector2d(ByteBuffer bf) {

		Vector2d v = new Vector2d();
		v.x = bf.getFloat();
		v.y = bf.getFloat();

		return v;
	}

	public static void writeVector2d(FileOutputStream bos, Vector2d v) throws IOException {

		writeFloat(bos, (float) v.x);
		writeFloat(bos, (float) v.y);
	}

	public static Vector3d readVector3d(ByteBuffer bf) {

		Vector3d v = new Vector3d();
		v.x = bf.getFloat();
		v.y = bf.getFloat();
		v.z = bf.getFloat();

		return v;
	}

	public static void writeVector3d(FileOutputStream bos, Vector3d v) throws IOException {

		writeFloat(bos, (float) v.x);
		writeFloat(bos, (float) v.y);
		writeFloat(bos, (float) v.z);
	}

	public static void writeVector4d(FileOutputStream bos, Vector4d v) throws IOException {

		writeFloat(bos, (float) v.x);
		writeFloat(bos, (float) v.y);
		writeFloat(bos, (float) v.z);
		writeFloat(bos, (float) v.x);
	}

	public static String readString(ByteBuffer bf, int length) {

		StringBuilder s = new StringBuilder();

		for (int i = 0; i < length; i++) {
			if (bf.hasRemaining()) {
				s.append((char) bf.get());
			}
		}

		return s.toString();
	}

	public static void writeString(FileOutputStream bos, String s, int length) throws IOException {

		int count;

		if (s == null) {

			count = length;
		} else {
			if (s.length() > length) {
				s = s.substring(0, length - 1);
			}
			bos.write(s.getBytes());
			count = length - s.getBytes().length;
		}

		if (count > 0) {
			byte[] xx = new byte[count];

			for (int i = 0; i < count; i++) {
				xx[i] = 0;
			}

			bos.write(xx);
		}
	}
}
