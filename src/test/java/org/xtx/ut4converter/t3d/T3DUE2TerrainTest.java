package org.xtx.ut4converter.t3d;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.xtx.ut4converter.t3d.BaseTest.parseFromT3d;


public class T3DUE2TerrainTest {

    private MapConverter mc;

    @Before
    public void setUp(){
        this.mc = BaseTest.getMapConverterInstance(UTGames.UTGame.UT2004);
    }


    private T3DUE4Terrain testUE2TerrainReadAndConvert(final String mapFile, final String t3dFile) throws IOException, ReflectiveOperationException, InterruptedException {

        // TODO conversion should be done exactly like in T3DLevelConvertor
        BaseTest.setMapFile(this.mc, mapFile);
        final T3DActor actor = parseFromT3d( this.mc, "TerrainInfo", T3DUE2Terrain.class, T3DUE2Terrain.class.getResource("/t3d/ue2/" + t3dFile).getPath());

        Assert.assertNotNull(actor);
        Assert.assertTrue(actor instanceof T3DUE2Terrain);

        final T3DUE2Terrain ue2Terrain = (T3DUE2Terrain) actor;

        // export and read heightmap from map package
        ue2Terrain.loadTerrainData();

        ue2Terrain.scale(this.mc.getScale());

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

        final T3DUE4Terrain t3DUE4Terrain = testUE2TerrainReadAndConvert("DM-1on1-Serpentine.ut2", "UT2004-Serpentine-TerrainInfo.t3d");
        Assert.assertNotNull(t3DUE4Terrain);

        System.out.println(t3DUE4Terrain.toT3d());
    }

    @Test
    public void testUT2004DriaTerrainReadAndConvert() throws ReflectiveOperationException, IOException, InterruptedException {

        final T3DUE4Terrain t3DUE4Terrain = testUE2TerrainReadAndConvert("ONS-Dria.ut2", "UT2004-Dria-TerrainInfo.t3d");
        Assert.assertNotNull(t3DUE4Terrain);

        try (FileWriter fw = new FileWriter(new File("C:\\TEMP\\terrain.t3d"))){
            fw.write(t3DUE4Terrain.toT3d());
        }
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

        this.mc = BaseTest.getMapConverterInstance(UTGames.UTGame.U2);
        BaseTest.setMapFile(this.mc, "mm_waterfront.un2");
        final T3DUE4Terrain t3DUE4Terrain = testUE2TerrainReadAndConvert("mm_waterfront.un2", "U2-WaterFront-TerrainInfo.t3d");
        Assert.assertNotNull(t3DUE4Terrain);

        try (FileWriter fw = new FileWriter(new File("C:\\TEMP\\terrain.t3d"))){
            fw.write(t3DUE4Terrain.toT3d());
        }
    }
}