package org.xtx.ut4converter.t3d;

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class T3DUtilsTest {


    /**
     * Test parsing line to get array entry
     */
    @Test
    void testGetArrayEntry() {

        final Pair<Integer, String> result = T3DUtils.getArrayEntry("KeyRot(2)=(Yaw=-16384)");
        Assertions.assertEquals(Integer.valueOf(2), result.getKey());
        Assertions.assertEquals("(Yaw=-16384)", result.getValue());
    }


    /**
     * Test generating random GUID in Unreal Engine format (e.g: 6818E3CE496079535E50108034423FA2)
     */
    @Test
    void testRandomGuid() {

        final String result = T3DUtils.randomGuid();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(32, result.length());

        Assertions.assertNotEquals(T3DUtils.randomGuid(), result);
    }

}