package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.AfterEach;


public abstract class T3DActorBaseTest {


    protected String convT3d;


    /**
     * TODO only print if local environnment
     */
    @AfterEach
    void printConvT3d(){
        System.out.println(convT3d);
    }
}
