package org.xtx.ut4converter.t3d;

import org.junit.Assert;
import org.junit.Test;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.ui.ConversionSettingsController;

import java.io.IOException;


public class T3DUE2TerrainTest extends T3DBaseTestTool {


    public T3DUE2TerrainTest() {
        super(UTGames.UTGame.UT2004, null);
    }


    private T3DUE4Terrain testUE2TerrainReadAndConvert(final String mapFile, final String t3dFile) throws IOException, ReflectiveOperationException, InterruptedException {

        this.setMapFile(mapFile);
        final T3DActor actor = parseFromT3d( "TerrainInfo", T3DUE2Terrain.class, T3DUE2Terrain.class.getResource("/t3d/ue2/" + t3dFile).getPath());

        Assert.assertNotNull(actor);
        Assert.assertTrue(actor instanceof T3DUE2Terrain);

        final T3DUE2Terrain ue2Terrain = (T3DUE2Terrain) actor;

        // export and read heightmap from map package
        ue2Terrain.loadTerrainData();

        // default UT2004 -> UT4 scale
        if (getMc().getInputGame() == UTGames.UTGame.U2) {
            ue2Terrain.scale(Double.parseDouble(ConversionSettingsController.DEFAULT_SCALE_UNREAL2_UE4));
        } else {
            ue2Terrain.scale(Double.parseDouble(ConversionSettingsController.DEFAULT_SCALE_FACTOR_UE2_UE4));
        }

        // convert UE2 terrain to UE4 terrain
        return new T3DUE4Terrain(ue2Terrain);
    }

    /**
     * Test conversion of Unreal Engine 2 terrain to Unreal Engine 4
     * @throws ReflectiveOperationException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testUT2004TerrainReadAndConvert() throws ReflectiveOperationException, IOException, InterruptedException {

        final T3DUE4Terrain t3DUE4Terrain = testUE2TerrainReadAndConvert("DM-1on1-Serpentine.ut2", "UT2004-TerrainInfo.t3d");
        Assert.assertNotNull(t3DUE4Terrain);

        System.out.println(t3DUE4Terrain.toT3d());
    }

    /**
     * Test Unreal 2 to Unreal Tournament 4 terrain conversion
     *
     * @throws ReflectiveOperationException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testUnreal2TerrainReadAndConvert() throws ReflectiveOperationException, IOException, InterruptedException {

        initForInputGame(UTGames.UTGame.U2, "mm_waterfront.un2");
        final T3DUE4Terrain t3DUE4Terrain = testUE2TerrainReadAndConvert("mm_waterfront.un2", "U2-WaterFront-TerrainInfo.t3d");
        Assert.assertNotNull(t3DUE4Terrain);

        System.out.println(t3DUE4Terrain.toT3d());
    }
}