package org.xtx.ut4converter.t3d.ue2.u2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DTestUtils;

import java.io.IOException;
import java.util.Objects;


class T3DAlarmTriggerTest {

    /**
     * Special Event U1 to UT4 conversion
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing as t3dsound
     */
    @Test
    void testAlarmTriggerConversionTest() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U2, UTGames.UTGame.UT4);
        final T3DAlarmTrigger dispatcher = (T3DAlarmTrigger) T3DTestUtils.parseFromT3d(mc, "AlarmTrigger", T3DAlarmTrigger.class, Objects.requireNonNull(T3DAlarmTrigger.class.getResource("/t3d/ue2/U2-AlarmTrigger.t3d")).getPath());

        Assertions.assertNotNull(dispatcher);
        dispatcher.convertScaleAndToT3D(2d);
    }
}