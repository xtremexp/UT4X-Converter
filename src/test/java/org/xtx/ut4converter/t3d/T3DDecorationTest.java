package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.IOException;
import java.util.Objects;


class T3DDecorationTest {

    /**
     * Plant U1 to UT4 conversion
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing as t3dsound
     */
    @Test
    void testPlantConversionTest() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);
        final T3DDecoration plant = (T3DDecoration) T3DTestUtils.parseFromT3d(mc, "Plant5", T3DDecoration.class, Objects.requireNonNull(T3DDecorationTest.class.getResource("/t3d/ue1/U1-Plant.t3d")).getPath());
        Assertions.assertNotNull(plant);

        final String convT3D = plant.convertScaleAndToT3D(2.5d);
        // DrawScale3d must be equals to DrawScale3d * DrawScale = 2.5 * 3 = 7.5
        Assertions.assertTrue(convT3D.contains("RelativeScale3D=(X=7.500000,Y=7.500000,Z=7.500000)"));
    }
}