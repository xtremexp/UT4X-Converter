package org.xtx.ut4converter.tools.t3dmesh;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DBrush;
import org.xtx.ut4converter.t3d.T3DTestUtils;
import org.xtx.ut4converter.tools.objmesh.ObjStaticMesh;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;


public class StaticMeshTest {


    /**
     * Test read staticmesh from .t3d file and conversion to Wavefront (.obj) staticmesh file
     */
    @Test
    void testReadT3DSMAndConvertToObj() throws IOException, URISyntaxException {

        final File t3dSmFile = new File(StaticMeshTest.class.getResource("/meshes/Simple.forest.t3d").toURI());
        final StaticMesh t3dStaticMesh = new StaticMesh(t3dSmFile);

        File objFile = File.createTempFile("simpleForest", "obj");
        File mtlFile = File.createTempFile("simpleForest", "mtl");

        try {
            final ObjStaticMesh objStaticMesh = new ObjStaticMesh(t3dStaticMesh);
            objStaticMesh.export(mtlFile, objFile);
        } finally {
            FileUtils.deleteQuietly(objFile);
            FileUtils.deleteQuietly(mtlFile);
        }
    }

    @Test
    void testReadBrushAndConvertToObj() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);
        final T3DBrush brush = (T3DBrush) T3DTestUtils.parseFromT3d(mc, "Brush", T3DBrush.class, StaticMeshTest.class.getResource("/t3d/ue1/Brush.t3d").getPath());

        File objFile = File.createTempFile("brush", "obj");
        File mtlFile = File.createTempFile("brush", "mtl");

        try {
            ObjStaticMesh.writeMtlObjFile(mtlFile, brush.getPolyList().stream().filter(p -> p.getTexture() != null).map(p -> p.getTexture().getConvertedBaseName()).distinct().toList());
            ObjStaticMesh.writeObj(brush, objFile, mtlFile);
        } finally {
            FileUtils.deleteQuietly(objFile);
            FileUtils.deleteQuietly(mtlFile);
        }
    }

}