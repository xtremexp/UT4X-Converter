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
        final File pskFile = new File(Objects.requireNonNull(PSKStaticMeshTest.class.getResource("/meshes/DirtChunk_01aw.pskx")).toURI());

        File t3dFile = File.createTempFile("DirtChunk_01aw", "ase");
        //File t3dFile = new File("C:\\dev3\\TEMP2\\DirtChunk_01aw.ase");
        FileUtils.delete(t3dFile);

        try {
            final PSKStaticMesh pskStaticMesh = new PSKStaticMesh(pskFile);
            pskStaticMesh.exportToAse(t3dFile);
            Assertions.assertTrue(t3dFile.length() > 0);
        } finally {
            FileUtils.deleteQuietly(t3dFile);
        }
    }
}