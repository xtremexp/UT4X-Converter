package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


class T3DDynamicAmbientSoundTest extends T3DActorBaseTest {

    @Test
    void testConversion() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);
        mc.setUseUbClasses(true);
        final T3DDynamicAmbientSound ut3Sound = (T3DDynamicAmbientSound) T3DTestUtils.parseFromT3d(mc, "DynamicAmbientSound", T3DDynamicAmbientSound.class, Objects.requireNonNull(T3DDynamicAmbientSoundTest.class.getResource("/t3d/ue1/U1-DynamicAmbientSound.t3d")).getPath());

        this.convT3d = ut3Sound.convertScaleAndToT3D(2d);
    }

    /**
     * Using version with sound ressources conversion (need unreal game installed)
     *
     * @throws Exception Exception thrown
     */
    @Disabled
    @Test
    void testWithRessourcesConversion() throws Exception {

        final File t3d = new File(Objects.requireNonNull(T3DDynamicAmbientSoundTest.class.getResource("/t3d/ue1/U1-DynamicAmbientSound.t3d")).toURI());
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4, t3d, true, true);
        mc.setUseUbClasses(true);
        mc.convert();
    }
}