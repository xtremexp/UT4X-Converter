package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.IOException;
import java.util.Objects;


class T3DDispatcherTest {


    /**
     * Special Event U1 to UT4 conversion
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing as t3dsound
     */
    @Test
    void testDispatcherConversionTest() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);
        final T3DDispatcher dispatcher = (T3DDispatcher) T3DTestUtils.parseFromT3d(mc, "Dispatcher", T3DDispatcher.class, Objects.requireNonNull(T3DDispatcherTest.class.getResource("/t3d/ue1/U1-Dispatcher.t3d")).getPath());

        Assertions.assertNotNull(dispatcher);
        final String convT3D = dispatcher.convertScaleAndToT3D(2d);
        Assertions.assertTrue(convT3D.contains("OutDelays(4)=7.0"));
    }
}