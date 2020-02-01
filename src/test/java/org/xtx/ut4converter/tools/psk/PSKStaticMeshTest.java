package org.xtx.ut4converter.tools.psk;

import org.junit.Test;

import java.io.File;

public class PSKStaticMeshTest {

    @Test
    public void testReadAndConvert() throws Exception {

        final File pskFile = new File("C:\\dev\\temp\\SCBronzTankPipe2x.pskx");
        File objFileOut = new File("C:\\dev\\temp\\Tower.obj");
        File mtlFileOut = new File("C:\\dev\\temp\\Tower.mtl");


        final PSKStaticMesh pskStaticMesh = new PSKStaticMesh(pskFile);
        pskStaticMesh.exportToObj(mtlFileOut, objFileOut);
    }

}