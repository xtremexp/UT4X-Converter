package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.config.model.ApplicationConfig;
import org.xtx.ut4converter.config.model.UserConfig;
import org.xtx.ut4converter.config.model.UserGameConfig;
import org.xtx.ut4converter.ucore.UnrealGame;
import org.xtx.ut4converter.ui.ConversionSettingsController;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class T3DTestUtils {


    /**
     * Initialise
     * @param inGame
     * @return
     */
    public static MapConverter getMapConverterInstance(final UTGames.UTGame inGame, final UTGames.UTGame outGame) throws IOException {

        final UnrealGame inputGame = ApplicationConfig.getBaseGames().stream().filter(g -> g.getShortName().equals(inGame.shortName)).findFirst().orElse(null);
        final UnrealGame outputGame = ApplicationConfig.getBaseGames().stream().filter(g -> g.getShortName().equals(outGame.shortName)).findFirst().orElse(null);

        final MapConverter mc = new MapConverter(inputGame, outputGame);

        if (inputGame.getShortName().equals(UTGames.UTGame.U2.shortName)) {
            mc.setScale(ConversionSettingsController.DEFAULT_SCALE_UNREAL2_UE4);
        } else {
            mc.setScale(ConversionSettingsController.DEFAULT_SCALE_FACTOR_UE2_UE4);
        }


        mc.setInMap(new File("C:\\Temp\\mymap.uxx"));

        mc.setInMap(new File("C:\\Temp\\mymap.uxx")); // fake map file

        mc.setT3dLvlConvertor(new T3DLevelConvertor(null, null, mc, true));

        return mc;
    }

    /**
     * Load user test config from application.properties file
     *
     * @return
     */
    public static UserConfig loadUserConfig() {
        final UserConfig uc = new UserConfig();
        uc.setIsFirstRun(false);


        final List<UserGameConfig> userGameConfigs = new ArrayList<>();

        try (final InputStream input = T3DTestUtils.class.getResourceAsStream("/application.properties")) {

            final Properties prop = new Properties();
            prop.load(input);

            userGameConfigs.add(new UserGameConfig(UTGames.UTGame.U1.shortName, new File(prop.getProperty("u1.path"))));
            userGameConfigs.add(new UserGameConfig(UTGames.UTGame.U2.shortName, new File(prop.getProperty("u2.path"))));
            userGameConfigs.add(new UserGameConfig(UTGames.UTGame.UT2003.shortName, new File(prop.getProperty("ut2003.path"))));
            userGameConfigs.add(new UserGameConfig(UTGames.UTGame.UT2004.shortName, new File(prop.getProperty("ut2004.path"))));
            userGameConfigs.add(new UserGameConfig(UTGames.UTGame.UT3.shortName, new File(prop.getProperty("ut3.path"))));

            uc.setGame(userGameConfigs);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return uc;
    }

    /**
     * Utility fonction, given an input game, read a .t3d file for a specific actor and parse it to build the actor
     *
     * @param inputClassName
     * @param utActorClass
     * @param t3dFilePath    "/src/test/resources/t3d/***.t3d
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static T3DActor parseFromT3d(final MapConverter mc, final String inputClassName, final Class<? extends T3DActor> utActorClass, final String t3dFilePath) throws ReflectiveOperationException, IOException {

        final Constructor<? extends T3DActor> cons = utActorClass.getConstructor(MapConverter.class, String.class);
        final T3DActor uta = cons.newInstance(mc, inputClassName);

        // read the t3d file line by line
        try (final FileReader fr = new FileReader(t3dFilePath); final BufferedReader bfr = new BufferedReader(fr)) {

            String line;

            while ((line = bfr.readLine()) != null) {
                // apply trim as used by the t3dlevel convertor
                line = line.trim();
                uta.analyseT3DData(line);
            }
        }

        return uta;
    }

    public static void setMapFile(final MapConverter mc, final String inMap) throws IOException {
        mc.setInMap(new File(mc.getInputGame().getMapFolder() + "/" + inMap));
    }
}
