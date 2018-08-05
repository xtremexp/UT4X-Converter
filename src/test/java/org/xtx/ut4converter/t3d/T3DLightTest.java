package org.xtx.ut4converter.t3d;


import org.junit.Before;
import org.junit.Test;
import org.xtx.ut4converter.UTGames;

/**
 * Testing t3d light
 */
class T3DLightTest extends T3DActorBaseTest {

    @Before
    private void setUp(){
        super.setUp(UTGames.UTGame.UT99);
    }


    @Test
    public void testIsSpotLight(){
        // t3dClass.equals(UE4_LightActor.SpotLight.name()) || lightEffect == UE12_LightEffect.LE_Spotlight || lightEffect == UE12_LightEffect.LE_StaticSpot
        //				|| lightEffect == UE12_LightEffect.LE_Spotlight2 || lightEffect == UE12_LightEffect.LE_SquareSpotlight;
        final T3DLight light = new T3DLight(mc, T3DLight.UE4_LightActor.SpotLight.name());
        //Assert.assertTrue(light.is);
    }
}