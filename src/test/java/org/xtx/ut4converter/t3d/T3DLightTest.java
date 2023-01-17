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
    void testLight() throws IOException {

        final T3DLight pureBlueLight = new T3DLight(T3DTestUtils.getMapConverterInstance(UTGames.UTGame.U1, UTGames.UTGame.UT3), "Light");
        pureBlueLight.setHue(170f); // blue
        pureBlueLight.setSaturation(0f); // pure blue (no saturation)
        pureBlueLight.setBrightness(255f);
        pureBlueLight.convert();
    }
}
