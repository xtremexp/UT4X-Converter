/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import javax.vecmath.Vector3d;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.tools.Geometry;

/**
 * Base class for teleporters
 * @author XtremeXp
 */
public class T3DTeleporter extends T3DSound {

    /**
     * Unreal 1 property refering to 
     */
    String url;
    
    public T3DTeleporter(MapConverter mc) {
        super(mc);
        isLinked = true;
    }
    
    @Override
    public boolean analyseT3DData(String line) {
        
        // UE1 -> "URL="hepburn2"
        if(line.contains("URL=")){
            url = line.split("\\=")[1];
        } 
        else {
            return super.analyseT3DData(line);
        }
        
        return true;
    }
    
    @Override
    public String toString(){
        
        // only write if we have data about linked teleporter
        if(mapConverter.getOutputGame() == UTGames.UTGame.UT4 && linkedTo != null && !linkedTo.isEmpty()){
            
            sbf.append(IDT).append("Begin Actor Class=BP_Teleporter_New_C Name=").append(name).append("\n");
            sbf.append(IDT).append("\tBegin Object Name=\"TriggerBox\"\n");
            
            // to reset rotation of teleporter else teleporter destination invalid is rotation set
            // TODO maybe make rotation the translation vector instead of doing this
            this.rotation = null;
            writeLocRotAndScale();
            sbf.append(IDT).append("\tEnd Object\n");
            sbf.append(IDT).append("\tTriggerBox=TriggerBox\n");
            
            // Note UT4 only support teleporting to one possible location
            // unlike U1/UT99/? do support multiple destinations
            T3DTeleporter linkedTel = (T3DTeleporter) linkedTo.get(0);
            Vector3d t = Geometry.sub(linkedTel.location, this.location);
            linkedTel.rotation = null;
            
            sbf.append(IDT).append("\tTeleportTarget=(Translation=(X=").append(t.x).append(",Y=").append(t.y).append(",Z=").append(t.z).append("))\n");
            sbf.append(IDT).append("\tRootComponent=TriggerBox\n");
            writeEndActor();
            
            linkedTo.clear(); // needs to remove linked teleporter or else loop on writting linked teleporter
            return sbf.toString()+linkedTel.toString();
        } else {
            return "";
        }
    }
    
    @Override
    public void convert(){
        
        // we need to retrieve the linked teleporters
        // at this stage the T3D level converter
        // may not have yet parsed data of destination teleporter
        if(linkedTo == null || linkedTo.isEmpty()){
            T3DLevelConvertor tlc = mapConverter.getT3dLvlConvertor();
            
            for(T3DActor actor : tlc.convertedActors){
                if(actor instanceof T3DTeleporter){
                    
                    if(actor.tag != null && this.url != null && this.url.equals("\""+actor.tag+"\"")){
                        this.linkedTo.add(actor);
                        actor.linkedTo.add(this);
                        break;
                    }
                }
            }
        }
        
        super.convert();
    }
}
