/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import java.util.List;
import javax.vecmath.Vector3d;
import org.xtx.ut4converter.MapConverter;

/**
 * A mover is a brush that moves in level.
 * @author XtremeXp
 */
public class T3DMover extends T3DBrush {

    String closedSound, closingSound, openedSound, openingSound, moveAmbientSound;

    /**
     * Where mover moves
     * UTUE4: Lift Destination=(X=0.000000,Y=0.000000,Z=730.000000) (no support for several nav localisations unlike UE1/2)
     * Is relative position
     */
    List<Vector3d> positions;
    
    /**
     * How long it takes for the mover to get to next position
     */
    Double moveTime;
    
    /**
     * How long the mover stay static in his final position before returning to home
     */
    Double stayOpenTime;
    
    
    /**
     * How long time the mover is available again after getting back to home position
     */
    Double delayTime;
    
    /**
     *
     * @param mc
     */
    public T3DMover(MapConverter mc) {
        super(mc);
    }
    
    @Override
    public boolean analyseT3DData(String line) {
        
        // UE1 -> 'Wait at top time' (UE4)
        if(line.contains("StayOpenTime")){
            stayOpenTime = T3DUtils.getDouble(line);
        } 
        
        // UE1 -> 'Lift Time' (UE4)
        else if(line.contains("MoveTime")){
            moveTime = T3DUtils.getDouble(line);
        } 
        
        // UE1 -> 'Retrigger Delay' (UE4)
        else if(line.contains("DelayTime")){
            delayTime = T3DUtils.getDouble(line);
        }
        
        // UE1 -> 'CloseStartSound' ? (UE4)
        else if(line.contains("ClosedSound=")){
            closedSound = line.split("\\=")[1];
        }
        
        // UE1 -> 'CloseStopSound' ? (UE4)
        else if(line.contains("ClosingSound=")){
            closingSound = line.split("\\=")[1];
        }
        
        // UE1 -> 'OpenStartSound' ? (UE4)
        else if(line.contains("OpeningSound=")){
            openingSound = line.split("\\=")[1];
        }
        
        // UE1 -> 'OpenStopSound' ? (UE4)
        else if(line.contains("OpenedSound=")){
            openedSound = line.split("\\=")[1];
        }
        
        // UE1 -> 'Retrigger Delay' (UE4)
        else if(line.contains("ClosedSound=")){
            closedSound = line.split("\\=")[1];
        }
        
        else {
            return super.analyseT3DData(line);
        }
        
        return true;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString(){
        
        if(mapConverter.toUnrealEngine4()){
            
            // Write the mover as Destination Lift
            sbf.append(IDT).append("Begin Actor Class=Generic_Lift_C Name=").append(name).append("\n");
            sbf.append(IDT).append("\tBegin Object Name=\"Scene1\"\n");
            writeLocRotAndScale();
            sbf.append(IDT).append("\tEnd Object\n");
            sbf.append(IDT).append("\tScene1=Scene\n");
            sbf.append(IDT).append("\tRootComponent=Scene1\n");
            writeEndActor();
            
            // TODO write move and sounds data
            
            // Write the mover as brush as well so we can convert it in staticmesh in UE4 Editor ...
            return super.toString();
        }
        
        return "";
    }
    
}
