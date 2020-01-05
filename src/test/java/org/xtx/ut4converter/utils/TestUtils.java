package org.xtx.ut4converter.utils;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.t3d.T3DActor;
import org.xtx.ut4converter.t3d.T3DLevelConvertor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TestUtils {


    /**
     * Utility fonction, given an input game, read a .t3d file for a specific actor and parse it to build the actor
     *
     * @param inputGame      E.G: Unreal Tournament 3
     * @param inputClassName
     * @param utActorClass
     * @param t3dFilePath    "/src/test/resources/t3d/***.t3d
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static T3DActor parseFromT3d(final UTGames.UTGame inputGame, final String inputClassName, final Class<? extends T3DActor> utActorClass, final String t3dFilePath) throws ReflectiveOperationException, IOException {

        final MapConverter mc = new MapConverter(inputGame, UTGames.UTGame.UT4);
        mc.setT3dLvlConvertor(new T3DLevelConvertor(null, null, mc));

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
}
