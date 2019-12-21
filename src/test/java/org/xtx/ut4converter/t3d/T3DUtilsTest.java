package org.xtx.ut4converter.t3d;

import org.apache.commons.math3.util.Pair;
import org.junit.Assert;
import org.junit.Test;


public class T3DUtilsTest {


    /**
     * Test parsing line to get array entry
     */
    @Test
    public void testGetArrayEntry() {

        final Pair<Integer, String> result = T3DUtils.getArrayEntry("KeyRot(2)=(Yaw=-16384)");
        Assert.assertEquals(Integer.valueOf(2), result.getKey());
        Assert.assertEquals("(Yaw=-16384)", result.getValue());
    }

}