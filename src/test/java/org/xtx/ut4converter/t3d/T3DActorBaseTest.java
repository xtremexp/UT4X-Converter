package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.IOException;

public abstract class T3DActorBaseTest {


    protected MapConverter mc;

    void setUp(final UTGames.UTGame inputGame) throws IOException {
        this.mc = new MapConverter(inputGame, UTGames.UTGame.UT4);
    }
}
