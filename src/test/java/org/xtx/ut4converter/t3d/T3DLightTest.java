package org.xtx.ut4converter.t3d;


import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
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

        Assertions.assertEquals(29, u1Light.radius);

        Assertions.assertEquals(21, u1Light.hue);
        Assertions.assertEquals(84, u1Light.saturation);
        Assertions.assertEquals(208, u1Light.brightness);

        final String convT3d = u1Light.convertScaleAndToT3D(2d);
        System.out.println(convT3d);
        Assertions.assertTrue(convT3d.contains("Intensity=15.0"));
        Assertions.assertTrue(convT3d.contains("LightColor=(B=68,G=137,R=208,A=255)"));
        Assertions.assertTrue(convT3d.contains("bUseInverseSquaredFalloff=false"));
        Assertions.assertTrue(convT3d.contains("LightFalloffExponent=3.0"));
        Assertions.assertTrue(convT3d.contains("AttenuationRadius=2041.6")); // radius * 32 * 1.1 * {ScaleFactor}
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
    void testDirectionalLightConversionU1toUT3() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT3);

        final T3DLight u1Light = (T3DLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DLight.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue1/U1-DirectionalLight.t3d")).getPath());
        System.out.println(u1Light.convertScaleAndToT3D(2d));
    }

    @Test
    void testLightConversionUT2004toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT4);

        final T3DLight ut2004Light = (T3DLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DLight.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue2/UT2004-Light.t3d")).getPath());
        Assertions.assertEquals(100, ut2004Light.hue);
        Assertions.assertEquals(12, ut2004Light.radius);
        Assertions.assertEquals(250, ut2004Light.brightness);
        Assertions.assertEquals(48, ut2004Light.saturation);

        final String convT3d = ut2004Light.convertScaleAndToT3D(2d);

        Assertions.assertTrue(convT3d.contains("Begin Actor Class=PointLight"));
        Assertions.assertTrue(convT3d.contains("LightColor=(B=118,G=250,R=47,A=255)"));
        Assertions.assertTrue(convT3d.contains("Mobility=Static"));
    }


    @Test
    void testDefaultLightConversionUT3toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT3, UTGames.UTGame.UT4);
        mc.setConvertSounds(false);

        final T3DLight light = (T3DLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DLight.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue3/UT3-Light-Default.t3d")).getPath());

        // test default light values

        // default light is white
        Assertions.assertEquals(255, light.rgbColor.R);
        Assertions.assertEquals(255, light.rgbColor.G);
        Assertions.assertEquals(255, light.rgbColor.B);
        Assertions.assertEquals(0, light.rgbColor.A);

        // default radius 1024
        Assertions.assertEquals(1024, light.radius);

        final String convT3d = light.convertScaleAndToT3D(2d);
        System.out.println(convT3d);
        Assertions.assertTrue(convT3d.contains("LightColor=(B=255,G=255,R=255,A=0)"));

        // Radius = Radius * 1.25 * {ScaleFactor}
        Assertions.assertTrue(convT3d.contains("AttenuationRadius=2560.0"));
    }

    @Test
    void testDefaultLightConversionUT2k4ToUT3() throws IOException, ReflectiveOperationException {
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT2004, UTGames.UTGame.UT3);
        mc.setConvertSounds(false);

        final T3DLight light = (T3DLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DLight.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue2/UT2004-Light-Default.t3d")).getPath());

        // check default values
        Assertions.assertEquals(0, light.hue, "Default UE1/UE2 light hue is 0 (white).");
        Assertions.assertEquals(64, light.radius, "Default UE1/UE2 light radius is 64.");
        Assertions.assertEquals(64, light.brightness);
        Assertions.assertEquals(255, light.saturation);

        // Test default values
        final String convT3d = light.convertScaleAndToT3D(2d);
        System.out.println(convT3d);

        // Intensity always 1
        Assertions.assertTrue(convT3d.contains("Intensity=1.0"));

        // Fao always 1
        Assertions.assertTrue(convT3d.contains("FalloffExponent=3.0"));

        // test radius = radius * 32 * 1.1 * {ScaleFactor} = 2048 * 2.2
        Assertions.assertTrue(convT3d.contains("Radius=4505.6"));

        // test color converted is white
        Assertions.assertTrue(convT3d.contains("LightColor=(B=64,G=64,R=64,A=255)"));
    }

    /**
     * Test conversion of light Unreal 1 actor with sound properties to UT3
     * After conversion it should create both a Light actor and an AmbientSound actor
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing light actor
     */
    @Test
    void testLightSoundConversionUT1toUT3() throws IOException, ReflectiveOperationException {
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT3);
        mc.setConvertSounds(false);

        final T3DLight ut3Light = (T3DLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DLight.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue1/U1-LightSound.t3d")).getPath());

        // test had read ambient sound properties
        Assertions.assertEquals(24, ut3Light.soundMaxRadius);
        Assertions.assertEquals(255, ut3Light.soundVolume);
        Assertions.assertEquals(80, ut3Light.soundPitch);

        final String convT3d = ut3Light.convertScaleAndToT3D(2d);
        System.out.println(convT3d);


        // test has created both light and sound actors
        Assertions.assertTrue(convT3d.contains("Begin Actor Class=PointLight"));
        Assertions.assertTrue(convT3d.contains("Begin Actor Class=AmbientSoundSimple"));
    }


    /**
     * Test conversion of light Unreal 1 actor with sound properties to UT4
     * After conversion it should create both a Light actor and an AmbientSound actor
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing light actor
     */
    @Test
    void testLightSoundConversionUT1toUT4() throws IOException, ReflectiveOperationException {
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);
        mc.setConvertSounds(false);

        final T3DLight ut3Light = (T3DLight) T3DTestUtils.parseFromT3d(mc, "Light", T3DLight.class, Objects.requireNonNull(StaticMeshTest.class.getResource("/t3d/ue1/U1-LightSound.t3d")).getPath());

        final String convT3d = ut3Light.convertScaleAndToT3D(2d);

        // There must be only one point light component
        Assertions.assertEquals(2, StringUtils.countMatches(convT3d, "Begin Object Class=PointLightComponent")); // it's declared 2x

        // There must be only one audio component
        Assertions.assertEquals(2, StringUtils.countMatches(convT3d, "Begin Object Class=AudioComponent")); // it's declared 2x

        System.out.println(convT3d);
    }
}
