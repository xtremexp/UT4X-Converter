package org.xtx.ut4converter.tools.t3dmesh;

import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.tools.ase.AseStaticMesh;
import org.xtx.ut4converter.tools.psk.Face;
import org.xtx.ut4converter.tools.psk.Material;
import org.xtx.ut4converter.tools.psk.Point;
import org.xtx.ut4converter.tools.psk.Wedge;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.xtx.ut4converter.tools.ase.AseStaticMesh.dfAse;

/**
 *
 */
public class StaticMesh {

    public static final String FILE_EXTENSION_T3D = "t3d";

    /**
     * Original t3d file
     */
    private final File t3dFile;

    /**
     * List of trianges
     */
    private final List<Triangle> triangles;

    public StaticMesh(final File t3dFile) throws IOException {
        this.t3dFile = t3dFile;
        this.triangles = new LinkedList<>();

        read();
    }

    private void read() throws IOException {

        if (this.t3dFile == null || !this.t3dFile.exists()) {
            return;
        }

        try (final BufferedReader bfr = new BufferedReader(new FileReader(this.t3dFile))) {

            String line;
            Triangle triangle = null;

            while ((line = bfr.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("Begin Triangle")) {
                    triangle = new Triangle();
                } else if (triangle != null && !line.startsWith("Begin StaticMesh") && !line.startsWith("End StaticMesh") && !line.startsWith("End Triangle")) {
                    triangle.parseLine(line);
                } else if (line.startsWith("End Triangle")) {
                    triangles.add(triangle);
                }
            }
        }
    }

    public void replaceMaterialNamesBy(Map<String, String> matNameToNewName) {
        for (final Triangle tri : this.getTriangles()) {
            if (matNameToNewName.containsKey(tri.getTexture())) {
                tri.setTexture(matNameToNewName.get(tri.getTexture()));
            }
        }
    }

    /**
     * Export current mesh to ASCII Scene format (.ase)
     *
     * @param aseStaticMeshFile File to export to
     * @throws IOException Error writing .ase file
     */
    public void exportToAse(final File aseStaticMeshFile) throws IOException {

        try (FileWriter fw = new FileWriter(aseStaticMeshFile)) {

            AseStaticMesh.writeHeader(fw, this.t3dFile);
            AseStaticMesh.writeMaterialWithSubMats(fw, this.getTriangles().stream().map(Triangle::getTexture).distinct().toList());

            fw.write("*GEOMOBJECT {\n");
            fw.write("\t*MESH {\n");
            fw.write("\t\t*TIMEVALUE 0\n");
            fw.write("\t\t*MESH_NUMVERTEX " + this.getTriangles().size() * 3 + "\n");
            fw.write("\t\t*MESH_NUMFACES " + this.getTriangles().size() + "\n");

            writeAseMeshVertexList(fw);
            writeAseMeshFaceList(fw);
            writeAseTVertList(fw);
            writeAseTFaceList(fw);

            fw.write("\t}\n");
            fw.write("\t*MATERIAL_REF 0\n");
            fw.write("}\n");
        }
    }

    /**
     * Write list of vertice values
     *
     * @param fw Write
     * @throws IOException Error writting
     */
    private void writeAseMeshVertexList(FileWriter fw) throws IOException {

        int idx = 0;
        fw.write("\t\t*MESH_VERTEX_LIST {\n");

        // note, need to flip y value else mesh is upside down in ut3 editor after .ase import
        for (final Triangle tri : this.getTriangles()) {
            fw.write("\t\t\t*MESH_VERTEX " + idx * 3 + " " + dfAse.format(tri.getVertices().get(0).getXyz().x) + " " + dfAse.format(-tri.getVertices().get(0).getXyz().y) + " " + dfAse.format(tri.getVertices().get(0).getXyz().z) + "\n");
            fw.write("\t\t\t*MESH_VERTEX " + ((idx * 3) + 1) + " " + dfAse.format(tri.getVertices().get(1).getXyz().x) + " " + dfAse.format(-tri.getVertices().get(1).getXyz().y) + " " + dfAse.format(tri.getVertices().get(1).getXyz().z) + "\n");
            fw.write("\t\t\t*MESH_VERTEX " + ((idx * 3) + 2) + " " + dfAse.format(tri.getVertices().get(2).getXyz().x) + " " + dfAse.format(-tri.getVertices().get(2).getXyz().y) + " " + dfAse.format(tri.getVertices().get(2).getXyz().z) + "\n");
            idx++;
        }

        fw.write("\t\t}\n");
    }

    private void writeAseMeshFaceList(FileWriter fw) throws IOException {

        final List<String> texList = this.getTriangles().stream().map(Triangle::getTexture).distinct().toList();
        int idx = 0;

        fw.write("\t\t*MESH_FACE_LIST {\n");

        for (final Triangle tri : this.getTriangles()) {
            fw.write("\t\t\t*MESH_FACE " + idx + ": A: " + (idx * 3) + " B: " + ((idx * 3) + 1) + " C: " + ((idx * 3) + 2) + " AB: 0 BC: 0 CA: 0 *MESH_SMOOTHING " + tri.getSmoothingMask() + " *MESH_MTLID " + texList.indexOf(tri.getTexture()) + "\n");
            idx++;
        }

        fw.write("\t\t}\n");
    }

    /**
     * Write list of uv values
     *
     * @param fw Writer
     * @throws IOException Error writing file
     */
    private void writeAseTVertList(FileWriter fw) throws IOException {

        int idx = 0;
        fw.write("\t\t*MESH_NUMTVERTEX " + (this.getTriangles().size() * 3) + "\n");
        fw.write("\t\t*MESH_TVERTLIST {\n");

        for (final Triangle tri : this.getTriangles()) {
            fw.write("\t\t\t*MESH_TVERT " + idx * 3 + " " + dfAse.format(tri.getVertices().get(0).getUv().x) + " " + dfAse.format(-tri.getVertices().get(0).getUv().y) + " 0.0000\n");
            fw.write("\t\t\t*MESH_TVERT " + ((idx * 3) + 1) + " " + dfAse.format(tri.getVertices().get(1).getUv().x) + " " + dfAse.format(-tri.getVertices().get(1).getUv().y) + " 0.0000\n");
            fw.write("\t\t\t*MESH_TVERT " + ((idx * 3) + 2) + " " + dfAse.format(tri.getVertices().get(2).getUv().x) + " " + dfAse.format(-tri.getVertices().get(2).getUv().y) + " 0.0000\n");
            idx++;
        }

        fw.write("\t\t}\n");
    }

    private void writeAseTFaceList(FileWriter fw) throws IOException {

        fw.write("\t\t*MESH_NUMTVFACES " + this.getTriangles().size() + "\n");
        fw.write("\t\t*MESH_TFACELIST {\n");

        for (int idx = 0; idx < this.getTriangles().size(); idx++) {
            fw.write("\t\t\t*MESH_TFACE " + idx + " " + idx * 3 + " " + ((idx * 3) + 1) + " " + ((idx * 3) + 2) + "\n");
        }

        fw.write("\t\t}\n");
    }


    public List<Triangle> getTriangles() {
        return triangles;
    }
}
