package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Assertions;
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
        final T3DLevelConvertor levelConvertor =  testT3dConvertForGame(new File((Objects.requireNonNull(T3DLevelConvertorTest.class.getResource("/t3d/U1-SimpleLevel.t3d")).toURI())), UTGames.UTGame.UT2004);
        Assertions.assertNotNull(levelConvertor);
        Assertions.assertEquals(6, levelConvertor.getConvertedActors().size());
    }

    /**
     * Test reading utf-16 encoded t3d level files
     *
     * @throws IOException        Error reading file
     * @throws URISyntaxException Error getting t3d from ressource
     */
    @Test
    void testT3dConvertUTF16() throws IOException, URISyntaxException {
        final T3DLevelConvertor lc = testT3dConvertForGame(new File((Objects.requireNonNull(T3DLevelConvertorTest.class.getResource("/t3d/ue1/DNF-UTF16.t3d")).toURI())), UTGames.UTGame.DNF);
        Assertions.assertNotNull(lc);
        Assertions.assertTrue(lc.getUnconvertedProperties().containsKey("LevelInfo"));
    }


    /**
     * @param t3dUnconvertedLvl Input t3d level file
     * @param inputGame         Input game
     * @throws IOException Error reading t3d file
     */
    private T3DLevelConvertor testT3dConvertForGame(final File t3dUnconvertedLvl, UTGames.UTGame inputGame) throws IOException {

        final MapConverter mc = T3DTestUtils.getMapConverterInstance(inputGame, UTGames.UTGame.UT4);

        final File tempFile = File.createTempFile(t3dUnconvertedLvl.getName(), "temp");
        T3DLevelConvertor lc = null;
                
        try {
            lc = new T3DLevelConvertor(t3dUnconvertedLvl, tempFile, mc, true);
            lc.setNoUi(true);
            lc.readConvertAndWrite();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed conversion of " + t3dUnconvertedLvl.getName() + ":" + e.getMessage());
        } finally {
            tempFile.delete();
        }
        
        return lc;
    }
}