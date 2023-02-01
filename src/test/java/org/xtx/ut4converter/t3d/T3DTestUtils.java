package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.config.ApplicationConfig;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;

public class T3DTestUtils {


    public static MapConverter getMapConverterInstance(final UTGames.UTGame inGame, final UTGames.UTGame outGame) throws IOException {
        return getMapConverterInstance(inGame, outGame, null, false, false);
    }

    /**
     * Initialise a map converter instance from input and output game
     * @param inGame Input game
     * @param outGame Output game
     * @return Map converter instance
     */
    public static MapConverter getMapConverterInstance(final UTGames.UTGame inGame, final UTGames.UTGame outGame, final File uMap, boolean convertRessources, boolean useUserConfig) throws IOException {

        ApplicationConfig appConfig;

        if (useUserConfig) {
            appConfig = ApplicationConfig.loadApplicationConfig();
        } else {
            appConfig = ApplicationConfig.loadDefaultApplicationConfig();
        }

        final UnrealGame inputGame = appConfig.getGames().stream().filter(g -> g.getShortName().equals(inGame.shortName)).findFirst().orElse(null);
        final UnrealGame outputGame = appConfig.getGames().stream().filter(g -> g.getShortName().equals(outGame.shortName)).findFirst().orElse(null);

        final MapConverter mc = new MapConverter(inputGame, outputGame);


        if (uMap != null) {
            mc.setInMap(uMap);
        } else {
            mc.setInMap(new File("C:\\Temp\\mymap.uxx"));
            mc.setT3dLvlConvertor(new T3DLevelConvertor(null, null, mc, true));
        }

        mc.getConversionSettings().setConvertMusic(convertRessources);
        mc.getConversionSettings().setConvertTextures(convertRessources);
        mc.getConversionSettings().setConvertSounds(convertRessources);
        mc.getConversionSettings().setConvertStaticMeshes(convertRessources);

        mc.setNoUi(true);
        mc.isTestMode = true;

        return mc;
    }


    /**
     * Utility fonction, given an input game, read a .t3d file for a specific actor and parse it to build the actor
     *
     * @param inputClassName Input class name in original t3d file (e.g: 'Light')
     * @param utActorClass   Map converter actor class to convert to (e.g: 'T3DLight')
     * @param t3dFilePath    "/src/test/resources/t3d/***.t3d
     * @return T3D actor parsed
     * @throws ReflectiveOperationException Exception thrown if could not instantiate utActorClass
     * @throws IOException                  Exception thrown if could not read t3d file
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
                uta.preAnalyse(line);
            }
        }

        return uta;
    }

    public static void setMapFile(final MapConverter mc, final String inMap) {
        mc.setInMap(new File(mc.getInputGame().getMapFolder() + "/" + inMap));
    }
}
