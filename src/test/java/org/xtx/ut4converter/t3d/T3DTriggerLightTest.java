package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.IOException;
import java.util.Objects;


class T3DTriggerLightTest extends T3DActorBaseTest{

    @Test
    void testDefaultAmbientSoundConversionUT3toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);
        final T3DTriggerLight light = (T3DTriggerLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DTriggerLight.class, Objects.requireNonNull(T3DTriggerLightTest.class.getResource("/t3d/ue1/U1-TriggerLight.t3d")).getPath());

        Assertions.assertNotNull(light);
    }
}