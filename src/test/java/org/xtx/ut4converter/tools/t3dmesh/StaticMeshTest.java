package org.xtx.ut4converter.tools.t3dmesh;

import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DActorBaseTest;
import org.xtx.ut4converter.t3d.T3DBrush;
import org.xtx.ut4converter.t3d.T3DTestUtils;
import org.xtx.ut4converter.t3d.T3DUE2Terrain;
import org.xtx.ut4converter.tools.objmesh.ObjStaticMesh;

import java.io.File;
import java.io.IOException;


public class StaticMeshTest {


    /**
     * Test read staticmesh from .t3d file and conversion to Wavefront (.obj) staticmesh file
     */
    @Test
    void testReadAndConvertToObj() throws IOException {
        final File t3dFile = new File("C:\\dev\\Temp\\crane_cab.t3d");
        final StaticMesh t3dStaticMesh = new StaticMesh(t3dFile);

        final File mtlFile = new File("E:\\TEMP\\crane_cab.mtl");
        final File objFile = new File("E:\\TEMP\\crane_cab.obj");

        final ObjStaticMesh objStaticMesh = new ObjStaticMesh(t3dStaticMesh);
        objStaticMesh.export(mtlFile, objFile);
    }

    @Test
    void testReadBrushAndConvertToObj() throws IOException, ReflectiveOperationException {



        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);
        final T3DBrush brush = (T3DBrush) T3DTestUtils.parseFromT3d(mc, "Brush", T3DBrush.class, StaticMeshTest.class.getResource("/t3d/ue1/Brush.t3d").getPath());

        final File mtlFile = new File("C:\\dev2\\brush.mtl");
        final File objFile = new File("C:\\dev2\\brush.obj");
        System.out.println("OK");

        ObjStaticMesh.writeMtlObjFile(mtlFile, brush.getPolyList().stream().filter(p -> p.getTexture() != null ).map( p -> p.getTexture().getConvertedBaseName()).distinct().toList());
        ObjStaticMesh.writeObj(brush, objFile, mtlFile);
    }

}