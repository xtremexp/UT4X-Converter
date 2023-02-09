package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.IOException;
import java.util.Objects;


class T3DSpecialEventTest {

    /**
     * Special Event U1 to UT4 conversion
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing as t3dsound
     */
    @Test
    void testDefaultAmbientSoundConversionUT3toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);
        final T3DSpecialEvent specialEvent = (T3DSpecialEvent) T3DTestUtils.parseFromT3d(mc, "SpecialEvent", T3DSpecialEvent.class, Objects.requireNonNull(T3DSpecialEventTest.class.getResource("/t3d/ue1/U1-SpecialEvent.t3d")).getPath());

        Assertions.assertNotNull(specialEvent);
        final String convT3D = specialEvent.convertScaleAndToT3D(2d);
    }
}