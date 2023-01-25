package org.xtx.ut4converter.tools.t3dmesh;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.tools.objmesh.ObjStaticMesh;


import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.net.URISyntaxException;

public class StaticMeshTest {


    /**
     * Test read staticmesh from .t3d file and conversion to Wavefront (.obj) staticmesh file
     */
    @Test
    void testConvertT3DStaticMeshToObj() throws IOException, URISyntaxException {

        final File t3dSmFile = new File(Objects.requireNonNull(StaticMeshTest.class.getResource("/meshes/Simple.forest.t3d")).toURI());
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

    /**
     * Test read staticmesh from .t3d file and convert it to Ascii Scene (.ase) staticmesh file
     *
     * @throws IOException        Error reading .t3d or writing .ase file
     * @throws URISyntaxException Error reading resource
     */
    @Test
    void testConvertT3DStaticMeshToAse() throws IOException, URISyntaxException {

        final File t3dSmFile = new File(Objects.requireNonNull(StaticMeshTest.class.getResource("/meshes/Cube256.t3d")).toURI());
        final StaticMesh t3dStaticMesh = new StaticMesh(t3dSmFile);

        File aseFile = File.createTempFile("Cube256", "ase");
        //File aseFile = new File("C:\\dev3\\TEMP2\\Cube256.ase");
        try {
            t3dStaticMesh.exportToAse(aseFile);
            Assertions.assertTrue(aseFile.exists());
            Assertions.assertTrue(aseFile.length() > 0);
        } finally {
            FileUtils.deleteQuietly(aseFile);
        }
    }

}