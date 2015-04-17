/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3d;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

/**
 * A mover is a brush that moves in level.
 * @author XtremeXp
 */
public class T3DMover extends T3DBrush {

    String closedSound, closingSound, openedSound, openingSound, moveAmbientSound;

    /**
     * List of positions where mover moves
     * UTUE4: Lift Destination=(X=0.000000,Y=0.000000,Z=730.000000) (no support for several nav localisations unlike UE1/2)
     * UT99: SavedPos=(X=-12345.000000,Y=-12345.000000,Z=-12345.000000), 
     * Is relative position
     */
    List<Vector3d> savedPositions = new ArrayList<>();
    
    /**
     * List of saved rotations for each "move point"
     * U1: BaseRot=(Yaw=-49152)
     */
    List<Vector3d> savedRotations = new ArrayList<>();;
    
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
        
        // UE1 -> 'Closed Sound' (UE4)
        else if(line.contains("ClosedSound=")){
            closedSound = line.split("\\=")[1];
        }
        
        // UE1 -> 'Lift Destination' (UE12)
        else if(line.contains("SavedPos=")){
            savedPositions.add(T3DUtils.getVector3d(line.split("SavedPos")[1], 0D));
        }
        
        // UE1 -> 'Saved Positions' (UE12)
        else if(line.contains("SavedRot=")){
            savedRotations.add(T3DUtils.getVector3dRot(line.split("SavedRot")[1]));
        }

        // TODO check "KeyPos(1)=(Y=72.000000)" usage ? 
        
        else {
            return super.analyseT3DData(line);
        }
        
        return true;
    }
    
    @Override
    public void scale(Double newScale){
        
        for(Vector3d savedPosition : savedPositions){
            savedPosition.scale(newScale);
        }
        
        super.scale(newScale);
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString(){
        
        if(mapConverter.getOutputGame() == UTGames.UTGame.UT4){
            
            // Write the mover as Destination Lift
            sbf.append(IDT).append("Begin Actor Class=Generic_Lift_C Name=").append(name).append("\n");
            sbf.append(IDT).append("\tBegin Object Name=\"Scene1\"\n");
            writeLocRotAndScale();
            sbf.append(IDT).append("\tEnd Object\n");
            sbf.append(IDT).append("\tScene1=Scene\n");
            sbf.append(IDT).append("\tRootComponent=Scene1\n");
            
            if(moveTime != null){
                sbf.append(IDT).append("\tLift Time=").append(moveTime).append("\n");
            }
            

            if(!savedPositions.isEmpty()){
                // IN UT4, at this stage, movers can only go to one location
                // so we only take the first one
                Vector3d liftD = savedPositions.get(0);
                
                // Lift Destination in UT4 is relative position
                if(location != null){
                    liftD.x -= location.x;
                    liftD.y -= location.y;
                    liftD.z -= location.z;
                }
                
                sbf.append(IDT).append("Lift Destination=(X=").append(fmt(liftD.x)).append(",Y=").append(fmt(liftD.y)).append(",Z=").append(fmt(liftD.z)).append(")\n");
            }

            if(openingSound != null){
                sbf.append(IDT).append("OpenStartSound=SoundWave'").append(openingSound).append("'\n");
            }
            
            if(openedSound != null){
                sbf.append(IDT).append("OpenStopSound=SoundWave'").append(openingSound).append("'\n");
            }
            
            if(closingSound != null){
                sbf.append(IDT).append("CloseStartSound=SoundWave'").append(openingSound).append("'\n");
            }
            
            if(closedSound != null){
                sbf.append(IDT).append("CloseStopSound=SoundWave'").append(openingSound).append("'\n");
            }
            
            if(stayOpenTime != null){
                sbf.append(IDT).append("Wait at top time=").append(stayOpenTime).append("'\n");
            }
            
            if(delayTime != null){
                sbf.append(IDT).append("Retrigger Delay=").append(delayTime).append("'\n");
            }
            
            writeEndActor();
            
            // TODO write move (test) and sounds data
            
            // TODO for UT4 make converter from brush to .fbx Autodesk file and transform into StaticMesh
            // TODO for UT3 make converter from brush to .ase file and transform into StaticMesh
            // Write the mover as brush as well so we can convert it in staticmesh in UE4 Editor ...
            return super.toString();
        }
        // TODO write mover UT UE<=3
        else {
            return "";
        }
    }
    
}
