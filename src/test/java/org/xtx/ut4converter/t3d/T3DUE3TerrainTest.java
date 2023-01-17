package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.*;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class T3DUE3TerrainTest {


    private MapConverter mc;

    @BeforeAll
    public void setUp() throws IOException {
        this.mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT3, UTGames.UTGame.UT4);
    }

    /**
     * Test reading UT3 terrain and converting it to UT4 terrain
     */
    @Disabled("Dependant of .exe files")
    void testUe3TerrainReadAndConvert() throws ReflectiveOperationException, IOException {

        // terrain sample is from VCTF-Sandstorm reduced to 20X20 numpatches

        // read ue3TerrainData
        final T3DActor actor = T3DTestUtils.parseFromT3d(this.mc, "TerrainActor", T3DUE3Terrain.class, T3DUE3TerrainTest.class.getResource("/t3d/ue3/UT3-Terrain-Sandstorm.t3d").getPath());

        Assertions.assertNotNull(actor);
        Assertions.assertTrue(actor instanceof T3DUE3Terrain);

        final T3DUE3Terrain ter = (T3DUE3Terrain) actor;
        // default scale factor for UT3 -> UT4 conversion
        ter.scale(2.2);


        // converts to UE4 Terrain
        final T3DUE4Terrain ue4Terrrain = new T3DUE4Terrain(ter);

        Assertions.assertNotNull(ue4Terrrain);

        try (FileWriter fw = new FileWriter("C:\\TEMP\\terrain.t3d")){
            fw.write(ue4Terrrain.toT3d());
        }
    }
}
