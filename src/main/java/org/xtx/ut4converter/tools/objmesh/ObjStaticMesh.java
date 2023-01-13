package org.xtx.ut4converter.tools.objmesh;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.xtx.ut4converter.t3d.T3DActor;
import org.xtx.ut4converter.t3d.T3DBrush;
import org.xtx.ut4converter.t3d.T3DPolygon;
import org.xtx.ut4converter.tools.Geometry;
import org.xtx.ut4converter.tools.t3dmesh.StaticMesh;
import org.xtx.ut4converter.tools.t3dmesh.Triangle;
import org.xtx.ut4converter.tools.t3dmesh.Vertex;
import org.xtx.ut4converter.tools.vertmesh.FJSMeshTri;
import org.xtx.ut4converter.tools.vertmesh.VertMesh;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ObjStaticMesh {

    public static final String FILE_EXTENSION_OBJ = "obj";

    public static final String FILE_EXTENSION_MTL = "mtl";

    private final List<ObjMaterial> materials;

    private final List<Vector3d> vertices;

    private final List<Vector2d> uvs;

    private final List<ObjFace> faces;

    public List<ObjMaterial> getMaterials() {
        return materials;
    }

    public List<Vector3d> getVertices() {
        return vertices;
    }

    public List<Vector2d> getUvs() {
        return uvs;
    }

    public List<ObjFace> getFaces() {
        return faces;
    }

    /**
     * Creates an .obj staticmesh from vertmesh .3d
     * (used in unreal 1 engine)
     *
     * @param vertMesh Unreal vertmesh
     */
    public ObjStaticMesh(final VertMesh vertMesh) {
        this.vertices = new LinkedList<>();
        this.uvs = new LinkedList<>();
        this.faces = new LinkedList<>();
        this.materials = new LinkedList<>();

        int globalVertexIdx = 0;

        for (final FJSMeshTri faceVm : vertMesh.getFaces()) {
            final ObjFace face = new ObjFace();

            // TODO get real texture name
            final String matName = "Texture_" + faceVm.getTextureNum();
            final ObjMaterial material = materials.stream().filter(e -> e.getMaterialName().equals(matName)).findAny().orElse(new ObjMaterial(matName));
            face.setMaterial(material);

            if (!this.materials.contains(material)) {
                materials.add(material);
            }

            face.setVertex2Idx(++globalVertexIdx);
            face.setVertex1Idx(++globalVertexIdx);
            face.setVertex0Idx(++globalVertexIdx);

            faces.add(face);

            // FIXME
            this.vertices.add(new Vector3d(faceVm.getiVertex()[2], faceVm.getiVertex()[1], faceVm.getiVertex()[0]));
            this.uvs.add(new Vector2d((faceVm.getTex()[0]).getU(), (faceVm.getTex()[0]).getV()));
        }
    }



    /**
     * Creates an .obj staticmesh from .td3 staticmesh
     *
     * @param t3dSm Unreal ascii static mesh
     */
    public ObjStaticMesh(final StaticMesh t3dSm) {
        this.vertices = new LinkedList<>();
        this.uvs = new LinkedList<>();
        this.faces = new LinkedList<>();
        this.materials = new LinkedList<>();

        int globalVertexIdx = 0;
        int globalUvIdx = 0;

        for (final Triangle t3dTriangle : t3dSm.getTriangles()) {

            final ObjFace face = new ObjFace();

            final ObjMaterial material = materials.stream().filter(e -> e.getMaterialName().equals(t3dTriangle.getTexture())).findAny().orElse(new ObjMaterial(t3dTriangle.getTexture()));
            face.setMaterial(material);
            face.setSmoothingGroup(t3dTriangle.getSmoothingMask());

            if (!this.materials.contains(material)) {
                materials.add(material);
            }

            face.setVertex0Idx(++globalVertexIdx);
            face.setVertex1Idx(++globalVertexIdx);
            face.setVertex2Idx(++globalVertexIdx);

            // FIXME good mesh rendering but bad UV !
            face.setIdx1(++globalUvIdx);
            face.setIdx3(++globalUvIdx);
            face.setIdx5(++globalUvIdx);


            faces.add(face);

            for (final Vertex t3dVertex : t3dTriangle.getVertices()) {
                // it seems we need to flip
                t3dVertex.getXyz().setY(t3dVertex.getXyz().getY() * -1d);
                this.vertices.add(t3dVertex.getXyz());
                t3dVertex.getUv().negate();
                this.uvs.add(t3dVertex.getUv());
            }
        }
    }

    public void export(final File mtlFile, final File objFile) throws IOException {
        writeMtlObjFile(mtlFile, this.getMaterials().stream().map(ObjMaterial::getMaterialName).toList());
        writeObjFile(objFile, mtlFile);
    }

    /**
     * Write .mtl wavefront file
     * @param mtlFile .mtl file to write
     * @param materialNameList List of material names
     * @throws IOException Error writing .mtl file
     */
    public static void writeMtlObjFile(final File mtlFile, List<String> materialNameList) throws IOException {

        try (FileWriter fw = new FileWriter(mtlFile)) {

            fw.write("# UT4 Converter MTL File:\n");
            for (final String materialName : materialNameList) {
                fw.write("newmtl " + materialName + " \n");
                fw.write("Ns 96.078431\n");
                fw.write("Ka 1.000000 1.000000 1.000000\n");
                fw.write("Kd 0.640000 0.640000 0.640000\n");
                fw.write("Ks 0.500000 0.500000 0.500000\n");
                fw.write("Ke 0.000000 0.000000 0.000000\n");
                fw.write("Ni 1.000000\n");
                fw.write("d 1.000000\n");
                fw.write("illum 2\n");
            }
        }
    }


    private void writeObjFile(final File objFile, final File mtlFile) {

        try (FileWriter fw = new FileWriter(objFile)) {

            if (mtlFile != null && !this.getMaterials().isEmpty()) {
                fw.write("mtllib " + mtlFile.getName() + "\n");
            }

            fw.write("# Vertices\n");
            for (final Vector3d w : this.getVertices()) {
                fw.write("v " + w.x + " " + w.y + " " + w.z + "\n");
            }

            fw.write("# UV\n");
            for (Vector2d w : this.getUvs()) {
                fw.write("vt " + w.x + " " + w.y + "\n");
            }

            fw.write("# Faces\n");
            String currentMat = null;
            Integer currentSmoothingGroup = null;

            for (final ObjFace fc : this.getFaces()) {

                if (currentMat == null || !currentMat.equals(fc.getMaterial().getMaterialName())) {
                    fw.write("usemtl " + fc.getMaterial().getMaterialName() + "\n");
                    currentMat = fc.getMaterial().getMaterialName();
                }


                if (currentSmoothingGroup == null || fc.getSmoothingGroup() != currentSmoothingGroup) {
                    currentSmoothingGroup = fc.getSmoothingGroup();
                    fw.write("s " + currentSmoothingGroup + "\n");
                }

                fw.write("f ");
                fw.write(fc.getVertex0Idx() + "/" + fc.getIdx1() + " ");
                fw.write(fc.getVertex1Idx() + "/" + fc.getIdx3() + " ");
                fw.write(fc.getVertex2Idx() + "/" + fc.getIdx5() + "\n");
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Transform a t3d brush into a staticmesh
     * @param brush
     * @param objFile
     * @param mtlFile
     * @throws IOException
     */
    public static void writeObj(T3DBrush brush, File objFile, File mtlFile) throws IOException {

        final List<String> matNameList = brush.getPolyList().stream().filter(p -> p.getTexture() != null).map(p -> p.getTexture().getName()).distinct().toList();
        final List<Vector3d> vertices = new ArrayList<>();//brush.getPolyList().stream().map(T3DPolygon::getVertices).flatMap(List::stream).toList();
        final List<Vector2d> uvs = new ArrayList<>();

        writeMtlObjFile(mtlFile, matNameList);

        try (FileWriter fw = new FileWriter(objFile)) {

            if (mtlFile != null && !matNameList.isEmpty()) {
                fw.write("mtllib " + mtlFile.getName() + "\n");
            }

            // TextureU, TextureV Vector3d to [u,v] conversion
            for (T3DPolygon poly : brush.getPolyList()) {
                /*
                 * As seen in UnShadow.cpp
                 * FVector Vertex	= Poly.Vertex[k] - Poly.Base;
                 * 			FLOAT	U		= Vertex | Poly.TextureU;
                 * 			FLOAT	V		= Vertex | Poly.TextureV;
                 */
                Vector3d vect = new Vector3d(poly.getOrigin());
                uvs.add(new Vector2d(vect.dot(poly.getTextureU())/256d, vect.dot(poly.getTextureV())/256d));
                vertices.addAll(poly.getVertices());
            }

            fw.write("# Vertices\n");
            for (final Vector3d w : vertices) {
                fw.write("v " + w.x + " " + w.y + " " + w.z + "\n");
            }


            fw.write("# UV\n");
            for (Vector2d w : uvs) {
                fw.write("vt " + w.x + " " + w.y + "\n");
            }

            int idx = 1;
            int idxUV = 1;

            fw.write("# Faces\n");
            String currentMat = null;
            fw.write("s 0\n");

            for (T3DPolygon poly : brush.getPolyList()) {

                if (poly.getTexture() != null && (currentMat == null || !currentMat.equals(poly.getTexture().getName()))) {
                    fw.write("usemtl " + poly.getTexture().getConvertedBaseName() + "\n");
                    currentMat = poly.getTexture().getName();
                }

                if (poly.getVertices().size() == 4) {
                    //fw.write("f " + idx + " " + (idx + 1) + " " + (idx + 2) + " " + (idx + 3) + "\n");
                    fw.write("f " + idx + "/" + idxUV + " " + (idx + 1) + "/" + idxUV + " " + (idx + 2) + "/" + idxUV  + " " + (idx + 3) + "/" + idxUV +"\n");
                } else if (poly.getVertices().size() == 3) {
                    //fw.write("f " + idx + " " + (idx + 1) + " " + (idx + 2) + "\n");
                    fw.write("f " + idx + "/" + idxUV + " " + (idx + 1) + "/" + idxUV + " " + (idx + 2) + "/" + idxUV + "\n");
                }

                idx += poly.getVertices().size();
                idxUV ++;
            }
        }

    }

}
