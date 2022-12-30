package org.xtx.ut4converter.tools.t3dmesh;

import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.tools.objmesh.ObjStaticMesh;

import java.io.File;


public class StaticMeshTest {


    /**
     * Test read staticmesh from .t3d file and conversion to Wavefront (.obj) staticmesh file
     */
    @Test
    void testReadAndConvertToObj() {
        final File t3dFile = new File("C:\\dev\\Temp\\crane_cab.t3d");
        final StaticMesh t3dStaticMesh = new StaticMesh(t3dFile);

        final File mtlFile = new File("E:\\TEMP\\crane_cab.mtl");
        final File objFile = new File("E:\\TEMP\\crane_cab.obj");

        final ObjStaticMesh objStaticMesh = new ObjStaticMesh(t3dStaticMesh);
        objStaticMesh.export(mtlFile, objFile);
    }

}