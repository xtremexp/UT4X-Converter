package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;

import java.util.LinkedList;
import java.util.List;

public class T3DDynamicAmbientSound extends T3DKeyPoint {

    private final List<UPackageRessource> sounds;


    public T3DDynamicAmbientSound(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        sounds = new LinkedList<>();

        registerSimpleProperty("bDontRepeat", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("bInitiallyOn", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("maxRecheckTime", Float.class, 10f);
        registerSimpleProperty("minRecheckTime", Float.class, 5f);
        registerSimpleProperty("playProbability", Float.class, 0.6f);
    }

    @Override
    public boolean analyseT3DData(String line) {
        if (line.startsWith("Sounds(")) {
            sounds.add(mapConverter.getUPackageRessource(line.split("'")[1], T3DRessource.Type.SOUND));
        } else {
            return super.analyseT3DData(line);
        }

        return true;
    }

    @Override
    public void convert() {

        if(!sounds.isEmpty() && mapConverter.convertSounds()) {
            for(final UPackageRessource sound : sounds) {
                sound.export(UTPackageExtractor.getExtractor(mapConverter, sound));
            }
        }

        super.convert();
    }

    public String toT3d() {

        sbf.append(IDT).append("Begin Actor Class=UBDynamicAmbientSound_C  \n");

        writeAudioComponent();

        sbf.append(IDT).append("\tAudioComponent=AudioComponent0\n");
        sbf.append(IDT).append("\tRootComponent=AudioComponent0\n");

        // Sounds(0)=SoundCue'/Game/UB/Blueprints/Sounds/TransA3_Cue.TransA3_Cue'
        int idx = 0;

        for(final UPackageRessource sound : sounds){
            sbf.append(IDT).append("Sounds(").append(idx).append(")=SoundCue'").append(sound.getConvertedName(mapConverter)).append("'\n");
            idx ++;
        }

        writeSimpleProperties();
        writeEndActor();

        return super.toString();
    }
}
