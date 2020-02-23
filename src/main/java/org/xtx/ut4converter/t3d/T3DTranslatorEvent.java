package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;

public class T3DTranslatorEvent extends T3DActor {

    private final static String DEFAULT_NEW_MESSAGE_SOUND = "UnrealShare.Pickups.TransA3";

    private UPackageRessource newMessageSound;



    public T3DTranslatorEvent(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("AltMessage", String.class);
        registerSimpleProperty("bTriggerAltMessage", Boolean.class);
        registerSimpleProperty("bTriggerOnceOnly", Boolean.class);
        registerSimpleProperty("Hint", String.class);
        registerSimpleProperty("M_NewMessage", String.class, "New Translator Message");
        registerSimpleProperty("M_TransMessage", String.class, "Translator Message");
        registerSimpleProperty("M_HintMessage", String.class, "New hint message (press F3 to read).");
        registerSimpleProperty("Message", String.class);
        registerSimpleProperty("ReTriggerDelay", Float.class, 0.25f);
    }

    @Override
    public boolean analyseT3DData(String line) {
        if (line.startsWith("NewMessageSound=")) {
            this.newMessageSound = mapConverter.getUPackageRessource(line.split("'")[1], T3DRessource.Type.SOUND);
        }
        else {
            return super.analyseT3DData(line);
        }

        return true;
    }

    @Override
    public void convert() {

        // set default collision so it's being scaled up correctly
        if(collisionHeight == null || collisionHeight == 0d){
            collisionHeight = 40d;
        }

        if(collisionRadius == null || collisionRadius == 0d){
            collisionRadius = 40d;
        }

        if(newMessageSound == null){
            newMessageSound = mapConverter.getUPackageRessource(DEFAULT_NEW_MESSAGE_SOUND, T3DRessource.Type.SOUND);
        }

        if (mapConverter.convertSounds()) {
            newMessageSound.export(UTPackageExtractor.getExtractor(mapConverter, newMessageSound));
        }

        super.convert();
    }

    @Override
    public String toString() {

        sbf.append(IDT).append("Begin Actor Class=TranslatorEvent_C \n");

        sbf.append(IDT).append("\tBegin Object Class=BoxComponent Name=\"CollisionComp\"\n");
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tBegin Object Name=\"CollisionComp\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");


        writeSimpleProperties();

        if(newMessageSound != null){
            sbf.append(IDT).append("\tNewMessageSound=SoundCue'").append(newMessageSound.getConvertedName(mapConverter)).append("'\n");
        }

        if(collisionRadius != null){
            sbf.append(IDT).append("\tCollisionRadius=").append(collisionRadius).append("\n");
        }

        if(collisionHeight != null){
            sbf.append(IDT).append("\tCollisionHeight=").append(collisionHeight).append("\n");
        }

        sbf.append(IDT).append("\tCollisionComponent=CollisionComp\n");
        sbf.append(IDT).append("\tRootComponent=CollisionComp\n");

        writeEndActor();

        return super.toString();
    }
}
