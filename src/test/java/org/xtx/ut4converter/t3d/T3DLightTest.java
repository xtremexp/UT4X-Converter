package org.xtx.ut4converter.t3d;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.xtx.ut4converter.UTGames;

import java.io.IOException;

/**
 * Testing t3d light
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class T3DLightTest extends T3DActorBaseTest {

    @BeforeAll
    void setUp() throws IOException {
        super.setUp(UTGames.UTGame.UT99);
    }


    @Test
    void testIsSpotLight(){
        // t3dClass.equals(UE4_LightActor.SpotLight.name()) || lightEffect == UE12_LightEffect.LE_Spotlight || lightEffect == UE12_LightEffect.LE_StaticSpot
        //				|| lightEffect == UE12_LightEffect.LE_Spotlight2 || lightEffect == UE12_LightEffect.LE_SquareSpotlight;
        final T3DLight light = new T3DLight(mc, T3DLight.UE4_LightActor.SpotLight.name());
        //Assert.assertTrue(light.is);
    }


    @Test
    void testLight() throws IOException {

        final T3DLight pureBlueLight = new T3DLight(T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT3), "Light");
        pureBlueLight.setHue(170f); // blue
        pureBlueLight.setSaturation(0f); // pure blue (no saturation)
        pureBlueLight.setBrightness(255f);
        pureBlueLight.convert();
    }


}
