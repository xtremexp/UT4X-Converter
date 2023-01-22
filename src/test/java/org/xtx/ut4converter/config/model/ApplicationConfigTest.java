/*
 * UT Converter © 2023 by Thomas 'WinterIsComing/XtremeXp' P. is licensed under Attribution-NonCommercial-ShareAlike 4.0 International. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package org.xtx.ut4converter.config.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xtx.ut4converter.config.ApplicationConfig;
import org.xtx.ut4converter.config.ConversionSettings;
import org.xtx.ut4converter.config.GameConversionConfig;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Test reading/writing ApplicationConfig.json file
 */
class ApplicationConfigTest {

    /**
     * Default application config test file
     */
    static final File DEFAULT_APP_CONFIG = new File(ApplicationConfigTest.class.getResource("/json/DefaultApplicationConfig.json").getPath());

    /**
     * Application config test file
     */
    static final File USER_APP_CONFIG = new File(ApplicationConfigTest.class.getResource("/json/ApplicationConfig.json").getPath());

    /**
     * Test loading ApplicationConfig.json should return the right object
     *
     * @throws IOException Exception throw when reading file
     */
    @Test
    void testLoad() throws IOException {

        final ApplicationConfig appConfig = ApplicationConfig.loadFromConfigFile(DEFAULT_APP_CONFIG);

        Assertions.assertEquals(1, appConfig.getVersion());
        Assertions.assertEquals(true, appConfig.getIsFirstRun());
        Assertions.assertEquals(10, appConfig.getGames().size());
        final UnrealGame uGame = appConfig.getGames().get(0);

        Assertions.assertEquals("Unreal 1", uGame.getName());
        Assertions.assertNull(uGame.getPath());
        // using contains for linux junit tests
        // 'expected xxx but was: </home/runner/work/UT4X-Converter/UT4X-Converter/C:\Program Files (x86)\Steam\steamapps\common\Unreal Gold>'
        Assertions.assertTrue(uGame.getSuggestedPath().getAbsolutePath().contains("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Unreal Gold"));
        Assertions.assertFalse(uGame.isUseTexDb());
        Assertions.assertFalse(uGame.getIsCustom());
        Assertions.assertEquals(1, uGame.getUeVersion());
        Assertions.assertEquals("/Maps", uGame.getMapFolder());
        Assertions.assertEquals("unr", uGame.getMapExt());
        Assertions.assertEquals("utx", uGame.getTexExt());
        Assertions.assertEquals("/System/ucc.exe", uGame.getPkgExtractorPath());
        Assertions.assertEquals("uax", uGame.getSoundExt());
        Assertions.assertEquals(2, uGame.getConvertsTo().size());
        Assertions.assertEquals(Arrays.asList("UT3", "UT4"), uGame.getConvertsTo().stream().map(GameConversionConfig::getGameId).collect(Collectors.toList()));
    }

    /**
     * Test merging user app config into reference app config
     *
     * @throws IOException Exception throw when reading .json file
     */
    @Test
    void createOrUpdate() throws IOException {

        final ApplicationConfig updatedConfig = ApplicationConfig.loadFromConfigFile(USER_APP_CONFIG);
        updatedConfig.mergeWithDefaultConfig(ApplicationConfig.loadFromConfigFile(DEFAULT_APP_CONFIG));
        Assertions.assertNotNull(updatedConfig);

        // User config had no check for updates, so updated config should also have
        Assertions.assertFalse(updatedConfig.isCheckForUpdates());

        // Updated config should have the new unreal game (ut3) from default app
        Assertions.assertEquals(11, updatedConfig.getGames().size());

        // Custom Game from user config was added
        Assertions.assertTrue(updatedConfig.getGames().stream().anyMatch(g -> g.getShortName().equals("CG")));

        // Unreal 1 install path was retrieved from original user config file
        final UnrealGame u1Game = updatedConfig.getGames().stream().filter(g -> g.getShortName().equals("U1")).findFirst().orElse(null);
        Assertions.assertNotNull(u1Game);
        Assertions.assertTrue(u1Game.getSuggestedPath().getAbsolutePath().contains("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Unreal Gold"));
    }

    @Test
    void testAddConversion() {
        final ApplicationConfig appConfig = new ApplicationConfig();

        final ConversionSettings cs = new ConversionSettings();
        appConfig.addRecentConversion(cs);

        Assertions.assertEquals(1, appConfig.getRecentConversions().size());

        // same conversion added, should not add same conversion
        appConfig.addRecentConversion(cs);
        Assertions.assertEquals(1, appConfig.getRecentConversions().size());

        final ConversionSettings cs2 = new ConversionSettings();
        cs2.setInputMap(new File("c://mymap.unr"));
        appConfig.addRecentConversion(cs2);
        Assertions.assertEquals(2, appConfig.getRecentConversions().size());

        for (int i = 0; i <= 10; i++) {
            final ConversionSettings csx = new ConversionSettings();
            csx.setInputMap(new File("c://mymap.unr" + i));
            appConfig.addRecentConversion(csx);
        }

        // recent conversion history limited to 8
        Assertions.assertEquals(8, appConfig.getRecentConversions().size());
    }

}