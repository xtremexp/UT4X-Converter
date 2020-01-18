package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.config.UserConfig;
import org.xtx.ut4converter.config.UserGameConfig;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class T3DBaseTestTool {

    /**
     *
     */
    private MapConverter mc;


    public T3DBaseTestTool(final UTGames.UTGame inputGame, final String mapFile) {
        initForInputGame(inputGame, mapFile);
    }

    protected void initForInputGame(final UTGames.UTGame inputGame, final String mapFile){
        mc = new MapConverter(inputGame, UTGames.UTGame.UT4);

        final UserConfig uc = loadUserConfig();
        mc.setUserConfig(uc);

        mc.setInMap(new File("C:\\Temp\\mymap.uxx"));

        if (mapFile != null) {
            mc.setInMap(new File(UTGames.getMapsFolder(uc.getGameConfigByGame(inputGame).getPath(), inputGame) + "/" + mapFile));
        } else {
            mc.setInMap(new File("C:\\Temp\\mymap.uxx")); // fake map file
        }

        mc.setT3dLvlConvertor(new T3DLevelConvertor(null, null, mc));
    }

    /**
     * Load user test config from application.properties file
     *
     * @return
     */
    private UserConfig loadUserConfig() {
        final UserConfig uc = new UserConfig();
        uc.setIsFirstRun(false);


        final List<UserGameConfig> userGameConfigs = new ArrayList<>();

        try (final InputStream input = this.getClass().getResourceAsStream("/application.properties")) {

            final Properties prop = new Properties();
            prop.load(input);

            uc.setNConvertPath(new File(prop.getProperty("umodel.path")));
            userGameConfigs.add(new UserGameConfig(UTGames.UTGame.U1, new File(prop.getProperty("u1.path"))));
            userGameConfigs.add(new UserGameConfig(UTGames.UTGame.U2, new File(prop.getProperty("u2.path"))));
            userGameConfigs.add(new UserGameConfig(UTGames.UTGame.UT2003, new File(prop.getProperty("ut2003.path"))));
            userGameConfigs.add(new UserGameConfig(UTGames.UTGame.UT2004, new File(prop.getProperty("ut2004.path"))));
            userGameConfigs.add(new UserGameConfig(UTGames.UTGame.UT3, new File(prop.getProperty("ut3.path"))));

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
    protected T3DActor parseFromT3d(final String inputClassName, final Class<? extends T3DActor> utActorClass, final String t3dFilePath) throws ReflectiveOperationException, IOException {

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

    public void setMapFile(final String inMap){
        mc.setInMap(new File(UTGames.getMapsFolder(mc.getUserConfig().getGameConfigByGame(mc.getInputGame()).getPath(), mc.getInputGame())  + "/" + inMap));
    }

    public MapConverter getMc() {
        return mc;
    }
}
