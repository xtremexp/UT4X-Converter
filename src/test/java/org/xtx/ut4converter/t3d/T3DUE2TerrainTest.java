package org.xtx.ut4converter.t3d;

import org.junit.Assert;
import org.junit.Test;
import org.xtx.ut4converter.UTGames;

import java.io.IOException;


public class T3DUE2TerrainTest extends T3DBaseTestTool{


    public T3DUE2TerrainTest() {
        super(UTGames.UTGame.UT2004, null);
    }


    /**
     * Test conversion of Unreal Engine 2 terrain to Unreal Engine 4
     * @throws ReflectiveOperationException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testUe2TerrainReadAndConvert() throws ReflectiveOperationException, IOException, InterruptedException {

        // read ue3TerrainData
        this.setMapFile("DM-1on1-Serpentine.ut2");
        final T3DActor actor = parseFromT3d( "TerrainInfo", T3DUE2Terrain.class, T3DUE2Terrain.class.getResource("/t3d/ue2/UT2004-TerrainInfo.t3d").getPath());

        Assert.assertNotNull(actor);
        Assert.assertTrue(actor instanceof T3DUE2Terrain);

        final T3DUE2Terrain ue2Terrain = (T3DUE2Terrain) actor;

        // export and read heightmap from map package
        ue2Terrain.loadTerrainData();

        // default UT2004 -> UT4 scale
        ue2Terrain.scale(2.2d);

        // convert UE2 terrain to UE4 terrain
        final T3DUE4Terrain t3DUE4Terrain = new T3DUE4Terrain(ue2Terrain);
        Assert.assertNotNull(t3DUE4Terrain);

        System.out.println(t3DUE4Terrain.toT3d());
    }
}