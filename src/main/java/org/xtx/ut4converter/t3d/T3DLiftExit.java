/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

/**
 * Checked: UT99, U1, U2, UT2004, UT3
 * 
 * UT99: DM-Cybrosis][
 * UT4: DM-Vortex
 * @author XtremeXp
 */
public class T3DLiftExit extends T3DSound {

    T3DMover linkedLift;
    
    Boolean bLiftJump;
    
    /**
     * UT4
     * UT3: bExitOnly
     */
    Boolean bLiftExit;
    
    /**
     * UT99
     */
    String liftTag;
    
    public T3DLiftExit(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);
    }
    
    @Override
    public boolean analyseT3DData(String line) {
        
        // UE1 -> "LiftTag=lastone"
        if(line.contains("LiftTag=")){
            liftTag = line.split("\\=")[1];
        }
        
        // UT3: MyLiftCenter=LiftCenter'LiftCenter_5'
        else if(line.contains("MyLiftCenter")){
            //liftTag = line.split("\\'")[1];
            // UT4 does not have UTLift_Center actor
        }
        
        // UT3
        else if(line.contains("bExitOnly=")){
            bLiftExit = Boolean.getBoolean(line.split("\\=")[1]);
        }
        else if(line.contains("LiftTag=")){
            liftTag = line.split("\\=")[1];
        }
        else {
            return super.analyseT3DData(line);
        }
        
        return true;
    }
    
    @Override
    public String toString(){
        sbf.append(IDT).append("Begin Actor Class=UTLiftExit Name=").append(name).append("\n");
        
        sbf.append(IDT).append("\tBegin Object Class=BillboardComponent Name=\"Icon\" Archetype=BillboardComponent'/Script/UnrealTournament.Default__UTLiftExit:Icon'\n");
        sbf.append(IDT).append("\tEnd Object\n");
        
        sbf.append(IDT).append("\tBegin Object Name=\"Icon\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");
        
        sbf.append(IDT).append("\tIcon=Icon\n");
        
        if(bLiftJump != null){
            sbf.append(IDT).append("\n" + "\tbLiftJump=").append(bLiftJump);
        }
        
        if(bLiftExit != null){
            sbf.append(IDT).append("\n" + "\tbLiftExit=").append(bLiftExit);
        }
        
        if(linkedLift != null){
            sbf.append(IDT).append("\tMyLift='").append(linkedLift.name).append("'\n");
        }
         
        sbf.append(IDT).append("\tRootComponent=Icon\n");
        writeEndActor();
        
        return super.toString();
    }
    
    @Override
    public void convert(){
        
        // we need to retrieve the linked lift
        // at this stage the T3D level converter
        // may not have yet parsed data of linked lift
        if(linkedTo == null || linkedTo.isEmpty()){
            T3DLevelConvertor tlc = mapConverter.getT3dLvlConvertor();
            
            for(T3DActor actor : tlc.convertedActors){
                
                // Note in previous UTs lifttag could be link to actor not necessarly movers (e.g: SpecialEvents)
                if(actor instanceof T3DMover){
                    
                    if(actor.tag != null && this.liftTag != null && this.liftTag.equals(actor.tag)){
                        this.linkedTo.add(actor);
                        actor.linkedTo.add(this);
                        linkedLift = (T3DMover) actor;
                        break;
                    }
                }
            }
        }
        
        super.convert();
    }
}
