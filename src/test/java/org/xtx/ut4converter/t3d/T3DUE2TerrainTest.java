package org.xtx.ut4converter.t3d;

import org.junit.Assert;
import org.junit.Test;
import org.xtx.ut4converter.UTGames;

import java.io.IOException;


public class T3DUE2TerrainTest extends T3DBaseTestTool{


    public T3DUE2TerrainTest() {
        super(UTGames.UTGame.UT2004, null);
    }

    @Test
    public void testUe3TerrainReadAndConvert() throws ReflectiveOperationException, IOException {

        // read ue3TerrainData
        this.setMapFile("DM-1on1-Serpentine.ut2");
        final T3DActor actor = parseFromT3d( "TerrainInfo", T3DUE2Terrain.class, T3DUE2Terrain.class.getResource("/t3d/ue2/UT2004-TerrainInfo.t3d").getPath());

        Assert.assertNotNull(actor);
        Assert.assertTrue(actor instanceof T3DUE2Terrain);

        final T3DUE2Terrain ue2Terrain = (T3DUE2Terrain) actor;

        // export heightmap texture and so on..
        ue2Terrain.convert();

        Assert.assertNotNull(ue2Terrain);
    }
}