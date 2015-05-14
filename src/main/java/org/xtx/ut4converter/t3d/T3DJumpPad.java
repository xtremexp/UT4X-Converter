/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import javax.vecmath.Vector3d;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UTGame;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 * Checked: U1, U2, UT99, UT2004, UT3
 * Tested: UT99
 * 
 * For testing:
 * UT99: DM-Cybrosis][
 * UT3: VCTF-Necropolis
 * 
 * TODO convert properly JumpVelocity to JumpTarget
 * @author XtremeXp
 */
public class T3DJumpPad extends T3DSound {

    
    /**
     * UT99 for actor class "Kicker" with "KickVelocity"
     * UT2004 for actor class "xKicker"
     * Unreal 2 for actor class "U2Kicker"
     * UT3 for actor class "UT_JumpPad"
     */
    Vector3d jumpVelocity;
    
    /**
     * UT99 for actor class "Jumper"
     */
    Double jumpZ;
    
    /**
     * UE4/UT4
     */
    Vector3d jumpTarget;
    

    /**
     * UT3:
     * JumpSound=SoundCue'A_Gameplay.JumpPad.Cue.A_Gameplay_JumpPad_Activate_Cue'
     * 
     * UT4:
     * JumpSound=SoundCue'/Game/RestrictedAssets/Audio/Gameplay/A_Gameplay_JumpPadJump01_Cue.A_Gameplay_JumpPadJump01_Cue'
     */
    UPackageRessource jumpSound;

    /**
     * UT3/UT4
     */
    Double jumpTime = 1.5d;
    
    public T3DJumpPad(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);
        
        if(mc.toUT4()){
            offsetZLocation = -40.5d;
        }
    }
    
    @Override
    public boolean analyseT3DData(String line) {
        
        if(line.contains("JumpTime")){
            jumpTime = T3DUtils.getDouble(line);
        }
        
        else if(line.contains("JumpZ")){
            jumpZ = T3DUtils.getDouble(line);
        }
        
        else if(line.contains("KickVelocity") || line.contains("JumpVelocity")){
            jumpVelocity = T3DUtils.getVector3d(line, 0d);
        }
        
        // UT3: JumpSound, U2: KickSound
        else if(line.contains("JumpSound") || line.contains("KickSound")){
            jumpSound = mapConverter.getUPackageRessource(line.split("\\'")[1], T3DRessource.Type.SOUND);
        }

        return super.analyseT3DData(line);
    }
    
    
    @Override
    public void scale(Double newScale){
        
        if(jumpVelocity != null){
            jumpVelocity.scale(newScale);
        }
        
        if(jumpZ != null){
            jumpZ *= newScale;
        }
        
        super.scale(newScale);
    }
    
    @Override
    public void convert(){
        
        if(mapConverter.getOutputGame() == UTGame.UT4){
            
            if(jumpZ != null){
                jumpTarget = new Vector3d(0d, 0d, jumpZ);
            } else {
            
                // TODO convert properly velocity to target
                if(jumpVelocity != null){
                    jumpTarget = jumpVelocity;
                } else {
                    jumpTarget = new Vector3d(0d, 0d, 250d);
                }
            }
        }
        
        super.convert();
    }
    
    @Override
    public String toString(){
        
        if(mapConverter.toUT4()){
            sbf.append(IDT).append("Begin Actor Class=BaseJumpPad_C name=").append(name).append("\n");
            sbf.append(IDT).append("\tBegin Object Name=\"SceneComponent\"\n");
            writeLocRotAndScale();
            sbf.append(IDT).append("\tEnd Object\n");

             //JumpPadColor=(R=0.064384,G=0.231086,B=0.520000,A=1.000000)
            sbf.append(IDT).append("\tSceneRoot=SceneComponent\n");
            
            if(jumpSound != null){
                sbf.append(IDT).append("\t\tJumpSound=SoundCue'").append(jumpSound.getConvertedName(mapConverter)).append("'\n");
            }

            if(jumpTarget != null){
                sbf.append(IDT).append("JumpTarget=(X=").append(jumpTarget.x).append(",Y=").append(jumpTarget.y).append(",Z=").append(jumpTarget.z).append(")\n");
            }

            if(jumpTime != null){
                sbf.append(IDT).append("\tJumpTime=").append(jumpTime).append("\n");
            }

            sbf.append(IDT).append("\tRootComponent=SceneComponent\n");
            writeEndActor();
        }
        
        return super.toString();
    }
    
    
}
