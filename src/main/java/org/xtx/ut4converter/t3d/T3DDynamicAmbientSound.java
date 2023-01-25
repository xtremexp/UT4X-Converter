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

        registerSimpleProperty("bDontRepeat", Boolean.class);
        registerSimpleProperty("bInitiallyOn", Boolean.class);
        registerSimpleProperty("maxRecheckTime", Float.class);
        registerSimpleProperty("minRecheckTime", Float.class);
        registerSimpleProperty("playProbability", Float.class);
    }

    @Override
    public boolean analyseT3DData(String line) {
        // SoundNodeWave (UE3)
        if (line.startsWith("Sounds(") || line.startsWith("SoundSlots(")) {
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

    /**
     * UE4 only
     * @return
     */
    public String toT3d() {

        this.archetype = "UE1DynamicAmbientSound_C'/Game/UEActors/UE1DynamicAmbientSound.Default__UE1DynamicAmbientSound_C'";
        this.t3dClass = "UE1DynamicAmbientSound_C";

        final Component mainAudioComp = buildMainAudioComponent();

        // Sounds(0)=SoundCue'/Game/UB/Blueprints/Sounds/TransA3_Cue.TransA3_Cue'
        int idx = 0;

        for (final UPackageRessource sound : sounds) {
            this.addConvProperty("Sounds(" + idx + ")", "SoundCue'" + sound.getConvertedName() + "'");
            idx++;
        }

        this.addComponent(mainAudioComp);

        sbf.append(super.toT3dNew());

        return super.toString();
    }
}
