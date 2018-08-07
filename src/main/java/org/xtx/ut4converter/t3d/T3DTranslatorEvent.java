package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;

public class T3DTranslatorEvent extends T3DActor {

    private String altMessage;

    private Boolean bTriggerAltMessage;

    private Boolean bTriggerOnceOnly;

    private String hint;

    private String mHintMessage;

    private String mNewMessage;

    private String mTransMessage;

    private String message;

    private final String defaultNewMessageSound = "UnrealShare.Pickups.TransA3";

    private UPackageRessource newMessageSound;

    private Float reTriggerDelay;


    public T3DTranslatorEvent(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);
    }

    @Override
    public boolean analyseT3DData(String line) {
        if (line.startsWith("AltMessage=")) {
            this.altMessage = T3DUtils.getString(line);
        }
        else if (line.startsWith("bTriggerAltMessage=")) {
            this.bTriggerAltMessage = T3DUtils.getBoolean(line);
        }
        else if (line.startsWith("bTriggerOnceOnly=")) {
            this.bTriggerOnceOnly = T3DUtils.getBoolean(line);
        }
        else if (line.startsWith("Hint=")) {
            this.hint = T3DUtils.getString(line);
        }
        else if (line.startsWith("M_NewMessage=")) {
            this.mNewMessage = T3DUtils.getString(line);
        }
        else if (line.startsWith("M_TransMessage=")) {
            this.mTransMessage = T3DUtils.getString(line);
        }
        else if (line.startsWith("M_HintMessage=")) {
            this.mHintMessage = T3DUtils.getString(line);
        }
        else if (line.startsWith("Message=")) {
            this.message = T3DUtils.getString(line);
        }
        else if (line.startsWith("NewMessageSound=")) {
            this.newMessageSound = mapConverter.getUPackageRessource(line.split("\'")[1], T3DRessource.Type.SOUND);
        }
        else if (line.startsWith("ReTriggerDelay=")) {
            this.reTriggerDelay = T3DUtils.getFloat(line);
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
            newMessageSound = mapConverter.getUPackageRessource(defaultNewMessageSound, T3DRessource.Type.SOUND);
        }

        if (mapConverter.convertSounds()) {
            newMessageSound.export(UTPackageExtractor.getExtractor(mapConverter, newMessageSound));
        }

        super.convert();
    }

    @Override
    public String toString() {

        sbf.append(IDT).append("Begin Actor Class=TranslatorEvent_C \n");

        sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"DefaultSceneRoot\"\n");
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tBegin Object Name=\"DefaultSceneRoot\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tDefaultSceneRoot=DefaultSceneRoot\n");


        if(altMessage != null){
            sbf.append(IDT).append("\tAltMessage=").append(altMessage).append("\n");
        }


        if(bTriggerAltMessage != null){
            sbf.append(IDT).append("\tbTriggerAltMessage=").append(bTriggerAltMessage).append("\n");
        }

        if(bTriggerOnceOnly != null){
            sbf.append(IDT).append("\tbTriggerOnceOnly=").append(bTriggerOnceOnly).append("\n");
        }

        if(hint != null){
            sbf.append(IDT).append("\tHint=\"").append(hint).append("\"\n");
        }

        if(mHintMessage != null){
            sbf.append(IDT).append("\tM_HintMessage=\"").append(mHintMessage).append("\"\n");
        }

        if(mNewMessage != null){
            sbf.append(IDT).append("\tM_NewMessage=\"").append(mNewMessage).append("\"\n");
        }

        if(mTransMessage != null){
            sbf.append(IDT).append("\tM_TransMessage=\"").append(mTransMessage).append("\"\n");
        }

        if(mHintMessage != null){
            sbf.append(IDT).append("\tM_HintMessage=\"").append(mHintMessage).append("\"\n");
        }

        if(newMessageSound != null){
            sbf.append(IDT).append("\tNewMessageSound=SoundCue'").append(newMessageSound.getConvertedName(mapConverter)).append("'\n");
        }

        if(message != null){
            sbf.append(IDT).append("\tMessage=\"").append(message).append("\"\n");
        }

        if(reTriggerDelay != null){
            sbf.append(IDT).append("\tReTriggerDelay=").append(reTriggerDelay).append("\n");
        }

        if(collisionRadius != null){
            sbf.append(IDT).append("\tCollisionRadius=").append(collisionRadius).append("\n");
        }

        if(collisionHeight != null){
            sbf.append(IDT).append("\tCollisionHeight=").append(collisionHeight).append("\n");
        }

        sbf.append(IDT).append("\tRootComponent=DefaultSceneRoot\n");

        writeEndActor();

        return super.toString();
    }
}
