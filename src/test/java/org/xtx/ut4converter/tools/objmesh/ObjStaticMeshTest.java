package org.xtx.ut4converter.tools.objmesh;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DMover;
import org.xtx.ut4converter.t3d.T3DTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


class ObjStaticMeshTest {


    /**
     * Test converting a brush to an .obj staticmesh file
     */
    @Test
    void testBrushConversion() throws IOException, ReflectiveOperationException {
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT4);

        // mover with poly with 5 vertices
        final T3DMover moverBrush = (T3DMover) T3DTestUtils.parseFromT3d(mc, "Mover", T3DMover.class, Objects.requireNonNull(ObjStaticMeshTest.class.getResource("/t3d/ue1/U1-Passage-Mover28.t3d")).getPath());
        // get rid of PanU, PanV, MainScale, PostScale stuff
        moverBrush.transformPermanently();

        //final File mtlFile = new File("C:\\temp\\mover28.mtl");
        //final File objFile = new File("C:\\temp\\mover28.obj");
        final File mtlFile = File.createTempFile("mover28", ".mtl");
        final File objFile = File.createTempFile("mover28", ".obj");

        try {
            ObjStaticMesh.writeMtlObjFile(moverBrush, mtlFile, true);
            ObjStaticMesh.writeObj(moverBrush, objFile, mtlFile);
        } finally {
            FileUtils.deleteQuietly(mtlFile);
            FileUtils.deleteQuietly(objFile);
        }
    }

}