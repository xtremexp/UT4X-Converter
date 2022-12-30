package org.xtx.ut4converter.t3d;

import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.xtx.ut4converter.export.UCCExporterTest.T3D_EXPORT_FOLDER;


public class T3DLevelConvertorTest {


    /**
     * Converts all .t3d UT99 map file.
     * Need to use UCCExportTest.testExportAllUT99Levels prior to execute this test
     *
     * @throws IOException
     */
    @Test
    void testT3dConvertU1() throws IOException {
        testT3dConvertForGame(T3D_EXPORT_FOLDER + "\\t3d\\" + UTGames.UTGame.U1.shortName, UTGames.UTGame.U1);
    }

    /**
     * Converts all .t3d UT99 map file.
     * Need to use UCCExportTest.testExportAllUT99Levels prior to execute this test
     *
     * @throws IOException
     */
    @Test
    void testT3dConvertUT99() throws IOException {
        testT3dConvertForGame(T3D_EXPORT_FOLDER + "\\t3d\\" + UTGames.UTGame.UT99.shortName, UTGames.UTGame.UT99);
    }

    /**
     * Converts all .t3d Unreal 2 map file.
     * Need to use UCCExportTest.testExportAllUT99Levels prior to execute this test
     *
     * @throws IOException
     */
    @Test
    void testT3dConvertU2() throws IOException {
        testT3dConvertForGame(T3D_EXPORT_FOLDER + "\\t3d\\" + UTGames.UTGame.U2.shortName, UTGames.UTGame.U2);
    }

    /**
     * Convert all .t3d ut2004 map files.
     * Allows detect some errors with converter for parsing data.
     *
     * @throws IOException
     */
    @Test
    void testT3dConvertUT2003() throws IOException {
        testT3dConvertForGame(T3D_EXPORT_FOLDER + "\\t3d\\" + UTGames.UTGame.UT2003.shortName, UTGames.UTGame.UT2003);
    }

    /**
     * Convert all .t3d ut2004 map files.
     * Allows detect some errors with converter for parsing data.
     *
     * @throws IOException
     */
    @Test
    void testT3dConvertUT2004() throws IOException {
        testT3dConvertForGame(T3D_EXPORT_FOLDER + "\\t3d\\" + UTGames.UTGame.UT2004.shortName, UTGames.UTGame.UT2004);
    }



    /**
     * @param t3dFilesFolder
     * @param inputGame
     * @throws IOException
     */
    private void testT3dConvertForGame(final String t3dFilesFolder, UTGames.UTGame inputGame) throws IOException {
        final MapConverter mc = T3DTestUtils.getMapConverterInstance(inputGame, UTGames.UTGame.UT4);
        mc.setConvertTextures(false);
        mc.setConvertMusic(false);
        mc.setConvertSounds(false);
        mc.setConvertStaticMeshes(false);

        final List<Path> t3dFiles = Files.list(Paths.get(t3dFilesFolder)).collect(Collectors.toList());

        int idx = 0;

        for (final Path t3dPath : t3dFiles) {
            final File tempFile = File.createTempFile(t3dPath.toFile().getName(), "temp");

            try {
                final T3DLevelConvertor lc = new T3DLevelConvertor(t3dPath.toFile(), tempFile, mc);
                lc.setNoUi(true);
                lc.readConvertAndWrite();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed conversion of " + t3dPath.toFile().getName() + ":" + e.getMessage());
            } finally {
                tempFile.delete();
            }

            System.out.println(idx + "/" + t3dFiles.size());
            idx++;
        }
    }

}