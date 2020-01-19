package org.xtx.ut4converter.t3d;

import org.junit.Assert;
import org.junit.Test;
import org.xtx.ut4converter.UTGames;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class T3DUE3TerrainTest extends T3DBaseTestTool{


    public T3DUE3TerrainTest() {
        super(UTGames.UTGame.UT3, null);
    }

    /**
     * Test reading UT3 terrain and converting it to UT4 terrain
     */
    @Test
    public void testUe3TerrainReadAndConvert() throws ReflectiveOperationException, IOException {

        // terrain sample is from VCTF-Sandstorm reduced to 20X20 numpatches

        // read ue3TerrainData
        final T3DActor actor = parseFromT3d( "TerrainActor", T3DUE3Terrain.class, T3DUE3TerrainTest.class.getResource("/t3d/ue3/UT3-Terrain.t3d").getPath());

        Assert.assertNotNull(actor);
        Assert.assertTrue(actor instanceof T3DUE3Terrain);

        final T3DUE3Terrain ter = (T3DUE3Terrain) actor;


        // converts to UE4 Terrain
        final T3DUE4Terrain ue4Terrrain = new T3DUE4Terrain(ter);

        Assert.assertNotNull(ue4Terrrain);

        try (FileWriter fw = new FileWriter(new File("C:\\TEMP\\terrain.t3d"))){
            fw.write(ue4Terrrain.toT3d());
        }
    }
}