package org.xtx.ut4converter.tools.psk;

import org.xtx.ut4converter.tools.objmesh.ObjStaticMesh;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * PSK staticmesh file reader/writer.
 *
 * @author XtremeXp
 */
public class PSKStaticMesh {

	public static final String FILE_EXTENSION_PSK = "psk";

	public static final String FILE_EXTENSION_PSKX = "pskx";

	/**
	 * Order how bytes are read for .psk files
	 */
	private static final ByteOrder BYTE_ORDER_LE = ByteOrder.LITTLE_ENDIAN;

	private final List<Point> points = new ArrayList<>();
	private final List<Wedge> wedges = new ArrayList<>();
	private final List<Face> faces = new ArrayList<>();

	/**
	 * Means faces are using wedges as integer rather than short type
	 */
	private boolean usingFace32;
	private final List<Material> materials = new ArrayList<>();
	private final List<Skeleton> skeletons = new ArrayList<>();
	private final List<RawWeight> rawWeights = new ArrayList<>();
	private final List<ExtraUv> extraUv0s = new ArrayList<>();
	private final List<ExtraUv> extraUv1s = new ArrayList<>();
	private final List<ExtraUv> extraUv2s = new ArrayList<>();

	private static final String CHUNK_HEADER_HEAD_ID = "ACTRHEAD";

	private static final String CHUNK_HEADER_POINTS_ID = "PNTS0000";

	private static final String CHUNK_HEADER_WEDGES_ID = "VTXW0000";

	private static final String CHUNK_HEADER_FACES_ID = "FACE0000";

	private static final String CHUNK_HEADER_FACES32_ID = "FACE3200";

	private static final String CHUNK_HEADER_MATT_ID = "MATT0000";

	private static final String CHUNK_HEADER_SKEL_ID = "REFSKELT";

	private static final String CHUNK_HEADER_RAWWHT_ID = "RAWWEIGHTS";

	private static final String CHUNK_HEADER_EXTRAUV0_ID = "EXTRAUVS0";

	private static final String CHUNK_HEADER_EXTRAUV1_ID = "EXTRAUVS1";

	private static final String CHUNK_HEADER_EXTRAUV2_ID = "EXTRAUVS2";

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
	public PSKStaticMesh(File pskFile) throws IOException {
		this.pskFile = pskFile;
		read();
	}


	public interface BinReadWrite {
		void write(FileOutputStream bos) throws IOException;

		void read(ByteBuffer bf);
	}

	/**
	 *
	 * @param f
	 * @throws IOException
	 */
	public void write(File f) throws IOException {

		try (final FileOutputStream fos = new FileOutputStream(f)) {

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

			// Write extra uvs 0
			Chunk chExtrauv0s = new Chunk(CHUNK_HEADER_EXTRAUV0_ID, extraUv0s, ExtraUv.DATA_SIZE);
			chExtrauv0s.write(fos);

			// Write extra uvs 1
			Chunk chExtrauv1s = new Chunk(CHUNK_HEADER_EXTRAUV1_ID, extraUv1s, ExtraUv.DATA_SIZE);
			chExtrauv1s.write(fos);

			// Write extra uvs 2
			Chunk chExtrauv2s = new Chunk(CHUNK_HEADER_EXTRAUV2_ID, extraUv2s, ExtraUv.DATA_SIZE);
			chExtrauv2s.write(fos);
		}
	}

	private void read() throws IOException {

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
				throw new IllegalArgumentException("Not a psk file");
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
						wedges.add(new Wedge(buffer, ch2.dataCount > 65536));
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

				case CHUNK_HEADER_EXTRAUV0_ID:

					for (int i = 0; i < ch2.dataCount; i++) {
						extraUv0s.add(new ExtraUv(buffer));
					}
					break;

				case CHUNK_HEADER_EXTRAUV1_ID:

					for (int i = 0; i < ch2.dataCount; i++) {
						extraUv1s.add(new ExtraUv(buffer));
					}
					break;

				case CHUNK_HEADER_EXTRAUV2_ID:

					for (int i = 0; i < ch2.dataCount; i++) {
						extraUv2s.add(new ExtraUv(buffer));
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

	/**
	 * TODO move/delete to ObjStaticMesh class
	 * @param mtlFile
	 * @param objFile
	 */
	@Deprecated
	public void exportToObj(final File mtlFile, final File objFile) throws IOException {
		ObjStaticMesh.writeMtlObjFile(mtlFile, this.getMaterials().stream().map(Material::getMaterialName).toList());
		writeObjFile(objFile, mtlFile);
	}


	/**
	 * Export .psk staticmesh to wavefront (.obj) staticmesh
	 */
	private void writeObjFile(final File objFile, final File mtlFile) {

		try (FileWriter fw = new FileWriter(objFile)) {

			if (mtlFile != null && !this.getMaterials().isEmpty()) {
				fw.write("mtllib " + mtlFile.getName() + "\n");
			}

			fw.write("# Vertices\n");
			for (Point w : this.getPoints()) {
				fw.write("v " + w.x + " " + w.y + " " + w.z + "\n");
			}

			fw.write("# UV\n");
			for (Wedge w : this.getWedges()) {
				fw.write("vt " + w.getU() + " " + w.getV() + "\n");
			}

			fw.write("# Faces\n");
			String currentMat = null;
			Integer currentSmoothingGroup = null;

			for (Face fc : this.getFaces()) {
				Material mat = this.getMaterials().get(fc.getMatIndex());

				if (currentMat == null || !currentMat.equals(mat.getMaterialName())) {
					fw.write("usemtl " + mat.getMaterialName() + "\n");
					currentMat = mat.getMaterialName();
				}

				if (currentSmoothingGroup == null || fc.getSmoothingGroups() != currentSmoothingGroup) {
					currentSmoothingGroup = fc.getSmoothingGroups();
					fw.write("s " + currentSmoothingGroup + "\n");
				}

				fw.write("f ");
				fw.write((this.getWedges().get(fc.getWedge2()).getPointIndex() + 1) + "/" + (fc.getWedge2() + 1) + " ");
				fw.write((this.getWedges().get(fc.getWedge1()).getPointIndex() + 1) + "/" + (fc.getWedge1() + 1) + " ");
				fw.write((this.getWedges().get(fc.getWedge0()).getPointIndex() + 1) + "/" + (fc.getWedge0() + 1) + "\n");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Export .psk file to .t3d static mesh
	 *
	 * @param t3dStaticMesh
	 */
	public void exportAsT3d(final File t3dStaticMesh) throws IOException {

		try (FileWriter fw = new FileWriter(t3dStaticMesh)) {

			final DecimalFormat df = new DecimalFormat("0.000000", new DecimalFormatSymbols(Locale.US));
			fw.write("Begin StaticMesh Name=SkyBoxCylinder\n");
			fw.write("\tVersion=2.000000\n");

			for (final Face face : this.getFaces()) {

				fw.write("\tBegin Triangle\n");
				fw.write("\t\tTexture " + this.getMaterials().get(face.getMatIndex()).getMaterialName() + "\n");
				fw.write("\t\tSmoothingMask " + face.getSmoothingGroups() + "\n");

				final Wedge w0 = this.getWedges().get(face.getWedge0());
				final Wedge w1 = this.getWedges().get(face.getWedge1());
				final Wedge w2 = this.getWedges().get(face.getWedge2());

				final Point p0 = this.getPoints().get(w0.getPointIndex());
				final Point p1 = this.getPoints().get(w1.getPointIndex());
				final Point p2 = this.getPoints().get(w2.getPointIndex());

				fw.write("\t\tVertex 0 " + df.format(p0.x) + " " + df.format(p0.y) + " " + df.format(p0.z) + " " + df.format(w0.getU()) + " " + df.format(w0.getV()) + " \n");
				fw.write("\t\tVertex 1 " + df.format(p1.x) + " " + df.format(p1.y) + " " +df.format(p1.z) + " " + df.format(w1.getU()) + " " + df.format(w1.getV()) + " \n");
				fw.write("\t\tVertex 2 " + df.format(p2.x) + " " + df.format(p2.y) + " " + df.format(p2.z) + " " + df.format(w2.getU()) + " " + df.format(w2.getV()) + " \n");
				fw.write("\tEnd Triangle\n");
			}

			fw.write("End StaticMesh\n");
		}
	}
}
