package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 * Special event trigger in Unreal Engine 1 (Unreal 1)
 */
public class T3DSpecialEvent extends T3DActor {

    /**
     * Means will be broadcast to all other players
     */
    private Boolean bBroadcast;

    private Boolean bPlayerViewRot;

    private Float damage;

    private String damageString;

    private String damageType;

    private String message;

    private UPackageRessource sound;

    public T3DSpecialEvent(final MapConverter mc, final String t3dClass) {
        super(mc, t3dClass);
    }

    @Override
    public boolean analyseT3DData(String line) {
        if (line.startsWith("bBroadcast=")) {
            this.bBroadcast = T3DUtils.getBoolean(line);
        }
        else if (line.startsWith("bPlayerViewRot=")) {
            this.bBroadcast = T3DUtils.getBoolean(line);
        }
        else if (line.startsWith("Damage=")) {
            this.damage = T3DUtils.getFloat(line);
        }
        else if (line.startsWith("DamageString=")) {
            this.damageString = T3DUtils.getString(line);
        }
        else if (line.startsWith("DamageType=")) {
            this.damageType = T3DUtils.getString(line);
        }
        else if (line.startsWith("Message=")) {
            this.message = T3DUtils.getString(line);
        }
        else if (line.startsWith("Sound=")) {
            this.sound = mapConverter.getUPackageRessource(line.split("\'")[1], T3DRessource.Type.SOUND);
        }

        else {
            return super.analyseT3DData(line);
        }

        return true;
    }

    @Override
    public void convert() {
        if (mapConverter.convertSounds() && sound != null) {
            sound.export(UTPackageExtractor.getExtractor(mapConverter, sound));
        }

        super.convert();
    }

    @Override
    public String toString() {

        sbf.append(IDT).append("Begin Actor Class=Dispatcher_C \n");

        sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"DefaultSceneRoot\"\n");
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tBegin Object Name=\"DefaultSceneRoot\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tDefaultSceneRoot=DefaultSceneRoot\n");


        if(bBroadcast != null){
            sbf.append(IDT).append("\tbBroadcast=").append(bBroadcast).append("\n");
        }

        if(bPlayerViewRot != null){
            sbf.append(IDT).append("\tbPlayerViewRot=").append(bPlayerViewRot).append("\n");
        }

        if(damage != null){
            sbf.append(IDT).append("\tDamage=").append(damage).append("\n");
        }

        if(damageString != null){
            sbf.append(IDT).append("\tDamageString=").append(damageString).append("\n");
        }

        if(message != null){
            sbf.append(IDT).append("\tMessage=").append(message).append("\n");
        }

        if(sound != null){
            sbf.append(IDT).append("\tSound=SoundCue'").append(sound.getConvertedName(mapConverter)).append("'\n");
        }

        sbf.append(IDT).append("\tRootComponent=DefaultSceneRoot\n");

        writeEndActor();

        return super.toString();
    }
}
