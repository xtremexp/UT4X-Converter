package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.IOException;
import java.util.Objects;


class T3DDynamicAmbientSoundTest extends T3DActorBaseTest {

    @Test
    void test() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT3, UTGames.UTGame.UT4);
        final T3DDynamicAmbientSound ut3Sound = (T3DDynamicAmbientSound) T3DTestUtils.parseFromT3d(mc, "DynamicAmbientSound", T3DDynamicAmbientSound.class, Objects.requireNonNull(T3DDynamicAmbientSoundTest.class.getResource("/t3d/ue1/U1-DynamicAmbientSound.t3d")).getPath());

        this.convT3d = ut3Sound.convertScaleAndToT3D(2d);
    }
}