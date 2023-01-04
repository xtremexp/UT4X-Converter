package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.config.model.ApplicationConfig;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.IOException;

public abstract class T3DActorBaseTest {


    protected MapConverter mc;

    void setUp(final UTGames.UTGame inputGame) throws IOException {
        final UnrealGame ueGameIn = ApplicationConfig.getBaseGames().stream().filter(g -> g.getShortName().equals(inputGame)).findFirst().orElse(null);
        final UnrealGame ut4GameOut = ApplicationConfig.getBaseGames().stream().filter(g -> g.getShortName().equals("UT4")).findFirst().orElse(null);
        this.mc = new MapConverter(ueGameIn, ut4GameOut);
    }
}
