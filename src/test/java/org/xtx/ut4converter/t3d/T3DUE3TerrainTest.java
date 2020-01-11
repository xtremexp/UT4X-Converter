package org.xtx.ut4converter.t3d;

import org.junit.Assert;
import org.junit.Test;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.utils.TestUtils;

import java.io.IOException;


public class T3DUE3TerrainTest {


    /**
     * Test reading UT3 terrain and converting it to UT4 terrain
     */
    @Test
    public void testUe3TerrainReadAndConvert() throws ReflectiveOperationException, IOException {

        // terrain sample is from VCTF-Sandstorm reduced to 20X20 numpatches

        // read ue3TerrainData
        final T3DActor actor = TestUtils.parseFromT3d(UTGames.UTGame.UT3, "TerrainActor", T3DUE3Terrain.class, T3DUE3TerrainTest.class.getResource("/t3d/ue3/UT3-Terrain.t3d").getPath());

        Assert.assertNotNull(actor);
        Assert.assertTrue(actor instanceof T3DUE3Terrain);

        final T3DUE3Terrain ter = (T3DUE3Terrain) actor;

        // test properties read
        Assert.assertEquals(4, ter.getTerrainComponents().size());
        Assert.assertEquals(441, ter.getTerrainHeight().getCount());
        Assert.assertEquals(21, ter.getTerrainHeight().getWidth());
        Assert.assertEquals(21, ter.getTerrainHeight().getHeight());
        Assert.assertEquals(441, ter.getTerrainHeight().getHeightMap().size());

        // converts to UE4 Terrain
        final T3DUE4Terrain ue4Terrrain = new T3DUE4Terrain(ter);

        Assert.assertNotNull(ue4Terrrain);

        // UE4 terrain must have the same number of components as the UE3 one
        Assert.assertEquals(ter.getTerrainComponents().size(), ue4Terrrain.getCollisionComponents().length * ue4Terrrain.getCollisionComponents()[0].length);
    }
}