package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.IOException;
import java.util.Objects;


class T3DTranslatorEventTest {


    @Test
    void test() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);
        final T3DTranslatorEvent ut3Sound = (T3DTranslatorEvent) T3DTestUtils.parseFromT3d(mc, "TranslatorEvent", T3DTranslatorEvent.class, Objects.requireNonNull(T3DTranslatorEventTest.class.getResource("/t3d/ue1/U1-TranslatorEvent.t3d")).getPath());

        System.out.println(ut3Sound.convertScaleAndToT3D(2d));
    }
}