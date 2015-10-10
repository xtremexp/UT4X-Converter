package org.xtx.ut4converter.tools.psk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * PSK staticmesh file reader/writer.
 * 
 * @author XtremeXp
 */
public class PSKStaticMesh {

	/**
	 * Order how bytes are read for .psk files
	 */
	private static final ByteOrder BYTE_ORDER_LE = ByteOrder.LITTLE_ENDIAN;

	private List<Point> points;
	private List<Wedge> wedges;
	private List<Face> faces;

	/**
	 * Means faces are using wedges as integer rather than short type
	 */
	private boolean usingFace32;
	private List<Material> materials;
	private List<Skeleton> skeletons;
	private List<RawWeight> rawWeights;
	private List<ExtraUv> extraUvs;

	private static final String CHUNK_HEADER_HEAD_ID = "ACTRHEAD";

	private static final String CHUNK_HEADER_POINTS_ID = "PNTS0000";

	private static final String CHUNK_HEADER_WEDGES_ID = "VTXW0000";

	private static final String CHUNK_HEADER_FACES_ID = "FACE0000";

	private static final String CHUNK_HEADER_FACES32_ID = "FACE3200";

	private static final String CHUNK_HEADER_MATT_ID = "MATT0000";

	private static final String CHUNK_HEADER_SKEL_ID = "REFSKELT";

	private static final String CHUNK_HEADER_RAWWHT_ID = "RAWWEIGHTS";

	private static final String CHUNK_HEADER_EXTRAUV_ID = "EXTRAUVS0";

	/**
	 * Reference to .psk file
	 */
	private File pskFile;

	public PSKStaticMesh() {
	}

	/**
	 * 
	 * @param pskFile
	 *            .psk staticmesh file
	 * @throws Exception
	 */
	public PSKStaticMesh(File pskFile) throws Exception {
		this.pskFile = pskFile;
		initialise();
		read();
	}

	/**
	 * 
	 */
	private void initialise() {
		points = new ArrayList<>();
		wedges = new ArrayList<>();
		faces = new ArrayList<>();
		materials = new ArrayList<>();
		skeletons = new ArrayList<>();
		rawWeights = new ArrayList<>();
		extraUvs = new ArrayList<>();
	}

	interface BinReadWrite {
		public abstract void write(FileOutputStream bos) throws IOException;

		public abstract void read(ByteBuffer bf);
	}

	/**
	 * 
	 * @param f
	 * @throws IOException
	 */
	public void write(File f) throws IOException {

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(f);

			// Write header
			ChunkHeader ch = new ChunkHeader(CHUNK_HEADER_HEAD_ID, null, 0);
			ch.write(fos);

			// Write points
			Chunk chPoints = new Chunk(CHUNK_HEADER_POINTS_ID, points, Point.DATA_SIZE);
			chPoints.write(fos);

			// Write wedges
			Chunk chWedges = new Chunk(CHUNK_HEADER_WEDGES_ID, wedges, Wedge.DATA_SIZE);
			chWedges.write(fos);

			// Write faces
			Chunk chFaces = null;

			if (usingFace32) {
				chFaces = new Chunk(CHUNK_HEADER_FACES32_ID, faces, Face.DATA_SIZE_FACE32);
			} else {
				chFaces = new Chunk(CHUNK_HEADER_FACES_ID, faces, Face.DATA_SIZE);
			}
			chFaces.write(fos);

			// Write mat info
			Chunk chMatt = new Chunk(CHUNK_HEADER_MATT_ID, materials, Material.DATA_SIZE);
			chMatt.write(fos);

			// Write skeleton info
			Chunk chSkl = new Chunk(CHUNK_HEADER_SKEL_ID, skeletons, Skeleton.DATA_SIZE);
			chSkl.write(fos);

			// Write raw weights
			Chunk chRawWght = new Chunk(CHUNK_HEADER_RAWWHT_ID, rawWeights, RawWeight.DATA_SIZE);
			chRawWght.write(fos);
		} finally {
			fos.close();
		}
	}

	private void read() throws Exception {

		if (pskFile == null || !pskFile.exists()) {
			return;
		}

		FileChannel inChannel = null;

		try (FileInputStream fis = new FileInputStream(pskFile)) {

			inChannel = fis.getChannel();
			ByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int) pskFile.length());
			buffer.order(BYTE_ORDER_LE);

			ChunkHeader header = new ChunkHeader(buffer);

			if (!CHUNK_HEADER_HEAD_ID.equals(header.chunkID.trim())) {
				throw new Exception("Not a psk file");
			}

			while (buffer.hasRemaining()) {

				ChunkHeader ch2 = new ChunkHeader(buffer);
				final String chunkId = ch2.chunkID.trim();
				switch (chunkId) {

				case CHUNK_HEADER_POINTS_ID:

					for (int i = 0; i < ch2.dataCount; i++) {
						points.add(new Point(buffer));
					}
					break;

				case CHUNK_HEADER_WEDGES_ID:

					for (int i = 0; i < ch2.dataCount; i++) {
						wedges.add(new Wedge(buffer, ch2.dataCount <= 65536));
					}
					break;

				case CHUNK_HEADER_FACES_ID:
					for (int i = 0; i < ch2.dataCount; i++) {
						faces.add(new Face(buffer, false));
					}
					break;

				// Unreal Engine >= 3
				case CHUNK_HEADER_FACES32_ID:

					usingFace32 = true;

					for (int i = 0; i < ch2.dataCount; i++) {
						faces.add(new Face(buffer, true));
					}
					break;

				case CHUNK_HEADER_MATT_ID:

					for (int i = 0; i < ch2.dataCount; i++) {
						materials.add(new Material(buffer));
					}
					break;

				case CHUNK_HEADER_SKEL_ID:

					for (int i = 0; i < ch2.dataCount; i++) {
						skeletons.add(new Skeleton(buffer));
					}
					break;

				case CHUNK_HEADER_RAWWHT_ID:

					for (int i = 0; i < ch2.dataCount; i++) {
						rawWeights.add(new RawWeight(buffer));
					}
					break;

				case CHUNK_HEADER_EXTRAUV_ID:

					for (int i = 0; i < ch2.dataCount; i++) {
						extraUvs.add(new ExtraUv(buffer));
					}
					break;

				default:
					break;
				}

			}
		} finally {
			try {
				inChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public File getPskFile() {
		return pskFile;
	}

	public List<Point> getPoints() {
		return points;
	}

	public List<Wedge> getWedges() {
		return wedges;
	}

	public List<Face> getFaces() {
		return faces;
	}

	public List<Material> getMaterials() {
		return materials;
	}

	public List<Skeleton> getSkeletons() {
		return skeletons;
	}

	public List<RawWeight> getRawWeights() {
		return rawWeights;
	}

	public static void main(String args[]) {

		System.out.println("OK");

		try {

			File meshFolder = new File("C:\\Temp\\psktest\\ut2004");
			File reWriteFolder = new File("C:\\Temp\\psktest\\ut2004\\out");

			if (!reWriteFolder.exists()) {
				reWriteFolder.mkdirs();
			}

			for (File pskFile : meshFolder.listFiles()) {
				if (!pskFile.getName().endsWith(".psk")) {
					continue;
				}

				System.out.print("Loading " + pskFile.getName() + " ... ");

				PSKStaticMesh mesh = null;

				try {
					mesh = new PSKStaticMesh(pskFile);
					System.out.println(" OK ! Size: " + pskFile.length());
				} catch (Exception e) {
					System.out.println(" ERROR ! Size: " + pskFile.length());
					e.printStackTrace();
					continue;
				}

				File out = new File(reWriteFolder + File.separator + pskFile.getName());

				try {
					System.out.print("Wrtting " + pskFile.getName() + " ... ");
					mesh.write(out);
					System.out.println(" OK ! ");
				} catch (Exception e) {
					System.out.println(" ERROR ! ");
					e.printStackTrace();
					continue;
				}
			}

			// mesh.write(newFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
