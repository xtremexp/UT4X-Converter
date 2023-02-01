package org.xtx.ut4converter.t3d;


import org.junit.jupiter.api.*;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.xtx.ut4converter.t3d.T3DTestUtils.parseFromT3d;


/**
 * Disable JUNIT test Not usable for linux platform
 */
public class T3DUE2TerrainTest {


    private T3DUE4Terrain testUE2TerrainReadAndConvert(final String mapFile, final String t3dFile) throws IOException, ReflectiveOperationException, InterruptedException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT4);
        // TODO conversion should be done exactly like in T3DLevelConvertor
        T3DTestUtils.setMapFile(mc, mapFile);
        final T3DActor actor = parseFromT3d( mc, "TerrainInfo", T3DUE2Terrain.class, T3DUE2Terrain.class.getResource("/t3d/ue2/" + t3dFile).getPath());

        Assertions.assertNotNull(actor);
        Assertions.assertTrue(actor instanceof T3DUE2Terrain);

        final T3DUE2Terrain ue2Terrain = (T3DUE2Terrain) actor;

        // export and read heightmap from map package
        ue2Terrain.loadTerrainData();

        ue2Terrain.scale(mc.getScale());

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
    @Disabled("Dependant of .exe files")
    void testUT2004TerrainReadAndConvert() throws ReflectiveOperationException, IOException, InterruptedException {

        final T3DUE4Terrain t3DUE4Terrain = testUE2TerrainReadAndConvert("DM-1on1-Serpentine.ut2", "UT2004-Serpentine-TerrainInfo.t3d");
        Assertions.assertNotNull(t3DUE4Terrain);

        System.out.println(t3DUE4Terrain.toT3d());
    }

    @Test
    @Disabled("Dependant of .exe files")
    void testUT2004DriaTerrainReadAndConvert() throws ReflectiveOperationException, IOException, InterruptedException {

        final T3DUE4Terrain t3DUE4Terrain = testUE2TerrainReadAndConvert("ONS-Dria.ut2", "UT2004-Dria-TerrainInfo.t3d");
        Assertions.assertNotNull(t3DUE4Terrain);

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
    @Disabled("Dependant of .exe files")
    void testUnreal2TerrainReadAndConvert() throws Exception {

        File t3dFile = new File(T3DUE2TerrainTest.class.getResource("/t3d/ue2/U2-M08A1-TerrainInfo.t3d").toURI());
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U2, UTGames.UTGame.UT4, t3dFile, true, true);

        mc.getConversionSettings().setScaleFactor(2.2d);
        mc.convert();
    }
}
