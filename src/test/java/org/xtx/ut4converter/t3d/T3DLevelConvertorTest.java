package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;


public class T3DLevelConvertorTest {

    /**
     * Convert t3d unreal 1 map file.
     * Allows to detect some errors with converter for parsing data.
     */
    @Test
    void testT3dConvertU1() throws IOException, URISyntaxException {
        testT3dConvertForGame(new File((Objects.requireNonNull(T3DLevelConvertorTest.class.getResource("/t3d/U1-SimpleLevel.t3d")).toURI())), UTGames.UTGame.UT2004);
    }


    /**
     * @param t3dUnconvertedLvl Input t3d level file
     * @param inputGame         Input game
     * @throws IOException Error reading t3d file
     */
    private void testT3dConvertForGame(final File t3dUnconvertedLvl, UTGames.UTGame inputGame) throws IOException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(inputGame, UTGames.UTGame.UT4);
        mc.setConvertTextures(false);
        mc.setConvertMusic(false);
        mc.setConvertSounds(false);
        mc.setConvertStaticMeshes(false);

        final File tempFile = File.createTempFile(t3dUnconvertedLvl.getName(), "temp");

        try {
            final T3DLevelConvertor lc = new T3DLevelConvertor(t3dUnconvertedLvl, tempFile, mc, true);
            lc.setNoUi(true);
            lc.readConvertAndWrite();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed conversion of " + t3dUnconvertedLvl.getName() + ":" + e.getMessage());
        } finally {
            tempFile.delete();
        }
    }
}