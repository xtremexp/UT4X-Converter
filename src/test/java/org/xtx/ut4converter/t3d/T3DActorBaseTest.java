package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

public abstract class T3DActorBaseTest {


    protected MapConverter mc;

    public void setUp(final UTGames.UTGame inputGame){
        this.mc = new MapConverter(inputGame, UTGames.UTGame.UT4);
    }
}
