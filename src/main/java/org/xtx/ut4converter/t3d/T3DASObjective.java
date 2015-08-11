package org.xtx.ut4converter.t3d;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.xtx.ut4converter.MapConverter;

/**
 * Based on properties of UT4 Assault mod v1.6.1
 * Only UT99 support for the moment
 * @author XtremeXp
 *
 */
public class T3DASObjective extends T3DSound {

	/**
	 * Tells this objective
	 */
	boolean isCritical = true;
	
	/**
	 * Means this objective is completed by touching it
	 */
	boolean byTouch = true;
	
	/**
	 * Means this objective is completed by destroying it
	 */
	boolean byDamage = false;
	
	/**
	 * Objective health
	 */
	BigDecimal objectiveHealth = BigDecimal.ZERO;
	
	
	/**
	 * Objective description
	 */
	String objectiveDesc;
	
	/**
	 * Message displayed to players once objective completed
	 */
	String completedText;
	
	/**
	 * Objective order
	 */
	int order = 0;
	
	/**
	 * UT4 AsMod - Objective text displayed in hud (i guess ...)
	 */
	String objectiveListText;
	
	// properties not handled by UT4 mod yet
	
	/**
	 * UT99 - For each step triggers this event
	 */
	List<String> damageEvent;
	
	/**
	 * UT99 - For each damage level triggers events defined in "damageEvent"
	 */
	List<String> damageEventThreshold;
	
	/**
	 * Tells whether this objective should 'flash' until completion
	 */
	boolean bFlashing;
	
	
	/**
	 * UT99 - End cam tag
	 */
	String endCamTag;
	
	public T3DASObjective(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
		// TODO Auto-generated constructor stub
	}

	@Override
    public boolean analyseT3DData(String line) {
		
		// FortName="The front doors"
		if(line.startsWith("FortName")){
			objectiveDesc = T3DUtils.getString(line);
		}
		
		// CompletedText="Test"
		else if(line.startsWith("CompletedText")){
			completedText = T3DUtils.getString(line);
		}
		
		// DefensePriority=0 (0 = latest objective)
		else if(line.startsWith("DefensePriority")){
			order = 10000 - T3DUtils.getInteger(line);
		}
		
		else if(line.startsWith("DestroyedMessage")){
			completedText = T3DUtils.getString(line);
		}
		
		else if(line.startsWith("DamageEvent(")){
			
			if(damageEvent == null){
				damageEvent = new ArrayList<>();
			}
			
			damageEvent.add(T3DUtils.getString(line));
		}
		
		else if(line.startsWith("DamageEventThreshold(")){
			
			if(damageEventThreshold == null){
				damageEventThreshold = new ArrayList<>();
			}
			
			damageEventThreshold.add(T3DUtils.getString(line));
		}
		
		else if(line.startsWith("bTriggerOnly")){
			byTouch = T3DUtils.getBoolean(line);
			byDamage = !byTouch;
		}
		
		// not handled yet by UT4 mod
		else if(line.startsWith("bFlashing")){
			bFlashing = T3DUtils.getBoolean(line);
		}
		
		// not handled yet by UT4 mod
		else if(line.startsWith("EndCamTag")){
			endCamTag = T3DUtils.getString(line);
		}
		
		// dunno how to handle that (means order = 10000 ?)
		else if(line.startsWith("bFinalFort")){
			
		}
		
		else {
            return super.analyseT3DData(line);
        }
        
        return true;
	}
	
	public String toString(){
		
		sbf.append(IDT).append("Begin Actor Class=UTASObjective_C Name=").append(name).append("\n");
        sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"DefaultSceneRoot\"\n");
        sbf.append(IDT).append("\tEnd Object\n");
        

        sbf.append(IDT).append("\tBegin Object Name=\"DefaultSceneRoot\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");
        
        if(objectiveDesc != null){
        	sbf.append(IDT).append("\tObjectiveDesc=").append(objectiveDesc).append("\n");
        }
        
        if(completedText != null){
        	sbf.append(IDT).append("\tCompletedText=").append(completedText).append("\n");
        }
        
        if(objectiveHealth != null){
        	sbf.append(IDT).append("\tObjectiveHealth=").append(objectiveHealth).append("\n");
        }
        
        sbf.append(IDT).append("\tbyTouch=").append(byTouch).append("\n");
        sbf.append(IDT).append("\tbyDamage=").append(byDamage).append("\n");
        sbf.append(IDT).append("\tisCritical=").append(isCritical).append("\n");
        sbf.append(IDT).append("\tOrder=").append(order).append("\n");
        
        sbf.append(IDT).append("\tRootComponent=DefaultSceneRoot\n");
        
        if(objectiveListText != null){
        	sbf.append(IDT).append("\tObjectiveListText=").append(objectiveListText).append("\n");
        }
        
        writeEndActor();
        
        return sbf.toString();
	}
}
