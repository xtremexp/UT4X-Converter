package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.IOException;
import java.util.Objects;


class T3DSoundTest {

    /**
     * U1 sound -> UT4
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing as t3dsound
     */
    @Test
    void testAmbientSoundConversionU1toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);

        final T3DSound u1Sound = (T3DSound) T3DTestUtils.parseFromT3d(mc, "AmbientSound", T3DSound.class, Objects.requireNonNull(T3DSoundTest.class.getResource("/t3d/ue1/U1-AmbientSound.t3d")).getPath());

        // Parsing test
        Assertions.assertEquals(102, u1Sound.soundMaxRadius);
        Assertions.assertEquals(137, u1Sound.soundVolume);
        Assertions.assertEquals(66, u1Sound.soundPitch);
        Assertions.assertNotNull(u1Sound.ambientSound);
        Assertions.assertEquals("AmbOutside.Looping.wind23", u1Sound.ambientSound.getFullName(true));

        // Conversion test
        String convT3D = u1Sound.convertScaleAndToT3D(2d);
        System.out.println(convT3D);

        // Sound test - reference conversion
        Assertions.assertTrue(convT3D.contains("AmbOutside_Looping_wind23_Cue"));

        // Pitch test - "SoundPitch=66" (U1) -> "SoundPitch={value}/64=66/64" (UT4)
        Assertions.assertTrue(convT3D.contains("PitchMultiplier=1.03125"), "SoundPitch!={value}/64");

        // Volume test - "SoundVolume=137" (U1) -> "VolumeMultiplier={value}/128=137/128" (UT4)
        Assertions.assertTrue(convT3D.contains("VolumeMultiplier=1.0703125"), "VolumeMultiplier!={value}/128");

        // Radius test - "SoundRadius=102" (U1) -> FalloffDistance={value} * 24 * {ScaleFactor} (UT) = 102 * 24 * 2
        Assertions.assertTrue(convT3D.contains("bOverrideAttenuation=true"));
        Assertions.assertTrue(convT3D.contains("FalloffDistance=4896.0"), "FalloffDistance!={value} * 24 * {ScaleFactor}");

        // Actor generic
        // Drawscale test - "DrawScale=6.0" (U1) -> SpriteScale={value} * {ScaleFactor} = 6 * 2
        Assertions.assertTrue(convT3D.contains("SpriteScale=12.0"), "SpriteScale!={value} * {ScaleFactor}");

        // Location test - "(x,y,z)=(1000,2000,3000)" (U1) -> Location={value} * {ScaleFactor} = (1000,2000,3000) * 2
        Assertions.assertTrue(convT3D.contains("RelativeLocation=(X=2000.000000,Y=4000.000000,Z=6000.000000)"));
    }

    /**
     * U1 sound (default) -> UT4
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing as t3dsound
     */
    @Test
    void testDefaultAmbientSoundConversionU1toUT4() throws IOException, ReflectiveOperationException {
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT4);

        final T3DSound u1Sound = (T3DSound) T3DTestUtils.parseFromT3d(mc, "AmbientSound", T3DSound.class, Objects.requireNonNull(T3DSoundTest.class.getResource("/t3d/ue1/U1-AmbientSound-Default.t3d")).getPath());

        // Test has default UE1 sound values
        Assertions.assertEquals(64, u1Sound.soundMaxRadius);
        Assertions.assertEquals(190, u1Sound.soundVolume);
        Assertions.assertEquals(64, u1Sound.soundPitch);

        // Test conversion
        final String convT3D = u1Sound.convertScaleAndToT3D(2d);
        System.out.println(convT3D);

        // Volume test - Volume=190 (default U1) -> VolumeMultiplier=190/128
        Assertions.assertTrue(convT3D.contains("VolumeMultiplier=1.484375"), "VolumeMultiplier!=190/128");

        // Pitch test - no change
        Assertions.assertTrue(convT3D.contains("PitchMultiplier=1.0"));

        // Radius test - Radius=64 (default U1) -> FalloffDistance=64 * 24 * {ScaleFactor} (UT) = 1536 * 2
        Assertions.assertTrue(convT3D.contains("FalloffDistance=3072.0"), "FalloffDistance!=64 * 24 * {ScaleFactor}");
    }

    /**
     * U1 sound -> UT3
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing as t3dsound
     */
    @Test
    void testAmbientSoundConversionU1toUT3() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT3);

        final T3DSound u1Sound = (T3DSound) T3DTestUtils.parseFromT3d(mc, "AmbientSound", T3DSound.class, Objects.requireNonNull(T3DSoundTest.class.getResource("/t3d/ue1/U1-AmbientSound.t3d")).getPath());
        final String convT3D = u1Sound.convertScaleAndToT3D(2d);
        System.out.println(convT3D);

        Assertions.assertTrue(convT3D.contains("Wave=SoundNodeWave"));
        Assertions.assertTrue(convT3D.contains("mymap-U1.AmbOutside_Looping_wind23"));

        // Volume test
        Assertions.assertTrue(convT3D.contains("Begin Object Class=DistributionFloatUniform Name=DistributionVolume"));
        Assertions.assertTrue(convT3D.contains("Min=1.0703125"));
        Assertions.assertTrue(convT3D.contains("Min=1.0703125"));

        // Pitch test
        Assertions.assertTrue(convT3D.contains("Begin Object Class=DistributionFloatUniform Name=DistributionPitch"));
        Assertions.assertTrue(convT3D.contains("Min=1.03125"));
        Assertions.assertTrue(convT3D.contains("Min=1.03125"));

        // Radius test
        Assertions.assertTrue(convT3D.contains("Begin Object Class=DistributionFloatUniform Name=DistributionMaxRadius"));
        Assertions.assertTrue(convT3D.contains("Min=4896.0"));
        Assertions.assertTrue(convT3D.contains("Max=4896.0"));
    }

    /**
     * UT3 sound (default) -> UT4
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing as t3dsound
     */
    @Test
    void testDefaultAmbientSoundConversionUT3toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT3, UTGames.UTGame.UT4);
        final T3DSound ut3Sound = (T3DSound) T3DTestUtils.parseFromT3d(mc, "AmbientSound", T3DSound.class, Objects.requireNonNull(T3DSoundTest.class.getResource("/t3d/ue3/UT3-AmbientSound-Default.t3d")).getPath());

        Assertions.assertNotNull(ut3Sound.ambientSound);
        Assertions.assertEquals("DoorsMod_General_mdend50", ut3Sound.ambientSound.getFullName(false));

        // Test has default UT3 sound values
        Assertions.assertEquals(400, ut3Sound.soundMinRadius);
        Assertions.assertEquals(5000, ut3Sound.soundMaxRadius);
        Assertions.assertEquals(1, ut3Sound.soundVolume);
        Assertions.assertEquals(1, ut3Sound.soundPitch);

        final String convT3D = ut3Sound.convertScaleAndToT3D(2d);
        System.out.println(convT3D);

        Assertions.assertTrue(convT3D.contains("mymap_DoorsMod_General_mdend50_Cue"));
        Assertions.assertTrue(convT3D.contains("VolumeMultiplier=1.0"));
        Assertions.assertTrue(convT3D.contains("PitchMultiplier=1.0"));
    }

    /**
     * UT3 sound -> UT4
     *
     * @throws IOException                  Error reading t3d file
     * @throws ReflectiveOperationException Error instancing as t3dsound
     */
    @Test
    void testAmbientSoundConversionUT3toUT4() throws IOException, ReflectiveOperationException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(UTGames.UTGame.UT3, UTGames.UTGame.UT4);
        final T3DSound ut3Sound = (T3DSound) T3DTestUtils.parseFromT3d(mc, "AmbientSound", T3DSound.class, Objects.requireNonNull(T3DSoundTest.class.getResource("/t3d/ue3/UT3-AmbientSound.t3d")).getPath());

        Assertions.assertEquals(200, ut3Sound.soundMinRadius);
        Assertions.assertEquals(4000, ut3Sound.soundMaxRadius);
        Assertions.assertEquals(1.2, ut3Sound.soundVolume);
        Assertions.assertEquals(1.1, ut3Sound.soundPitch);

        final String convT3D = ut3Sound.convertScaleAndToT3D(2d);
        System.out.println(convT3D);

        Assertions.assertTrue(convT3D.contains("bOverrideAttenuation=true"));

        // minRadius -> ShapeExtent.x = minRadius * {ScaleFactor}
        Assertions.assertTrue(convT3D.contains("AttenuationShapeExtents=(X=400.0"));

        // maxRadius -> FalloffDistance = maxRadius * {ScaleFactor}
        Assertions.assertTrue(convT3D.contains("FalloffDistance=8000.0"));

        Assertions.assertTrue(convT3D.contains("PitchMultiplier=1.1"));
        Assertions.assertTrue(convT3D.contains("VolumeMultiplier=1.2"));
    }
}