package org.xtx.ut4converter.tools.psk;


import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class PSKStaticMeshTest {

    @Test
    void testReadAndExportToOBJ() throws Exception {

        final File pskFile = new File(Objects.requireNonNull(PSKStaticMeshTest.class.getResource("/meshes/DirtChunk_01aw.pskx")).toURI());

        File objFileOut = File.createTempFile("tower", "obj");
        File mtlFileOut = File.createTempFile("tower", "mtl");

        try {
            final PSKStaticMesh pskStaticMesh = new PSKStaticMesh(pskFile);
            pskStaticMesh.exportToObj(mtlFileOut, objFileOut);

            Assertions.assertTrue(objFileOut.length() > 0);
            Assertions.assertTrue(mtlFileOut.length() > 0);
        } finally {
            FileUtils.deleteQuietly(objFileOut);
            FileUtils.deleteQuietly(mtlFileOut);
        }
    }

    @Test
    void testReadAndExportToT3D() throws URISyntaxException, IOException {
        final File pskFile = new File(Objects.requireNonNull(PSKStaticMeshTest.class.getResource("/meshes/DirtChunk_01aw.pskx")).toURI());

        File t3dFile = File.createTempFile("DirtChunk_01aw", "t3d");

        try {
            final PSKStaticMesh pskStaticMesh = new PSKStaticMesh(pskFile);
            pskStaticMesh.exportToT3d(t3dFile);

            Assertions.assertTrue(t3dFile.length() > 0);
        } finally {
            FileUtils.deleteQuietly(t3dFile);
        }
    }

    @Test
    void testReadAndExportToASE() throws URISyntaxException, IOException {

        // uncomment to test conversion locally (no t3d file delete for testing import in editor)
        final File pskFile = new File(Objects.requireNonNull(PSKStaticMeshTest.class.getResource("/meshes/Cube256.pskx")).toURI());
        boolean isLocalTest = false;
        File t3dFile;

        if (isLocalTest) {
            t3dFile = new File("C:\\Temp\\UT-Converter\\Cube256.ase");
        } else {
            t3dFile = File.createTempFile("Cube256", "ase");
        }

        try {
            final PSKStaticMesh pskStaticMesh = new PSKStaticMesh(pskFile);
            pskStaticMesh.getMaterials().get(0).setMaterialName("Tex256x256-2_Mat");
            pskStaticMesh.getMaterials().get(1).setMaterialName("Tex256x256-3_Mat");
            pskStaticMesh.getMaterials().get(2).setMaterialName("Tex256x256-4_Mat");
            pskStaticMesh.getMaterials().get(3).setMaterialName("Tex256x256-1_Mat");
            pskStaticMesh.getMaterials().get(4).setMaterialName("Tex256x256-5_Mat");
            pskStaticMesh.getMaterials().get(5).setMaterialName("Tex256x256-6_Mat");
            pskStaticMesh.exportToAse(t3dFile);
            Assertions.assertTrue(t3dFile.length() > 0);
        } finally {
            if (!isLocalTest) {
                FileUtils.deleteQuietly(t3dFile);
            }
        }
    }
}