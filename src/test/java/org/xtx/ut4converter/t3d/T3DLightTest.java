package org.xtx.ut4converter.t3d;


import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.tools.t3dmesh.StaticMeshTest;

import java.io.IOException;
import java.util.Objects;

/**
 * Testing t3d light
 */
public class T3DLightTest {

    @Test
    void testLightConversionU1toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);

        final T3DLight u1Light = (T3DLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DLight.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue1/U1-Light.t3d")).getPath());
        u1Light.convertScaleAndToT3D(2d);
    }

    @Test
    void testLightConversionU1toUT3() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT3);

        final T3DLight u1Light = (T3DLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DLight.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue1/U1-Light.t3d")).getPath());
        System.out.println(u1Light.convertScaleAndToT3D(2d));
    }

    @Test
    void testSpotLightConversionU1toUT3() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT3);

        final T3DLight u1Light = (T3DLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DLight.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue1/U1-SpotLight.t3d")).getPath());
        System.out.println(u1Light.convertScaleAndToT3D(2d));
    }

    @Test
    void testSpotLightConversionU1toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);

        final T3DLight u1Light = (T3DLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DLight.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue1/U1-SpotLight.t3d")).getPath());
        System.out.println(u1Light.convertScaleAndToT3D(2d));
    }

    @Test
    void testLightConversionUT2004toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT4);

        final T3DLight ut2004Light = (T3DLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DLight.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue2/UT2004-Light.t3d")).getPath());
        System.out.println(ut2004Light.convertScaleAndToT3D(2d));
    }


    @Test
    void testLightConversionUT3toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT3, UTGames.UTGame.UT4);
        mc.setConvertSounds(false);

        final T3DLight ut3Light = (T3DLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DLight.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue3/UT3-Light.t3d")).getPath());
        ut3Light.convertScaleAndToT3D(2d);
    }

    @Test
    void testLightWhiteColorConversion() throws IOException, ReflectiveOperationException {
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT3);
        mc.setConvertSounds(false);

        final T3DLight ut3Light = (T3DLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DLight.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue2/UT2004-WhiteLight.t3d")).getPath());
        System.out.println(ut3Light.convertScaleAndToT3D(2d));
    }
}
