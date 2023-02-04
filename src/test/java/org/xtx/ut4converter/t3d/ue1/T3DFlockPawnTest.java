package org.xtx.ut4converter.t3d.ue1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DTestUtils;

import java.io.IOException;
import java.util.Objects;


class T3DFlockPawnTest {

    /**
     * Plant U1 to UT4 conversion
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing as t3dsound
     */
    @Test
    void testPlantConversionTest() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);
        final T3DFlockPawn bird = (T3DFlockPawn) T3DTestUtils.parseFromT3d(mc, "Bird1", T3DFlockPawn.class, Objects.requireNonNull(T3DFlockPawnTest.class.getResource("/t3d/ue1/U1-Bird.t3d")).getPath());
        Assertions.assertNotNull(bird);

        final String convT3D = bird.convertScaleAndToT3D(2.5d);
        System.out.println(convT3D);

        // properties have been scaled
        // default property value scaled
        Assertions.assertTrue(convT3D.contains("SightRadius=6250.0"));

        // property value scaled
        Assertions.assertTrue(convT3D.contains("CircleRadius=2560.0"));
    }
}