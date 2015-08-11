package org.xtx.ut4converter.t3d;

import java.util.ArrayList;
import java.util.List;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.ucore.ue4.BodyInstance;
import org.xtx.ut4converter.ucore.ue4.BodyInstance.ECollisionResponse;

/**
 * Based on properties of UT4 Assault mod v1.6.1
 * Only UT99 support for the moment
 * @author XtremeXp
 *
 */
public class T3DASObjective extends T3DSound {

	/**
	 * If critical means this objective has to be completed to finish the map
	 * else this is a secondary/optional objective
	 */
	boolean isCritical = true;
	
	/**
	 * Means this objective is completed by touching it
	 * UT99: Default is false
	 * UT4 MOD: default is true
	 */
	boolean byTouch;
	
	/**
	 * Means this objective is completed by destroying it
	 * UT99: Default is true
	 * UT4 MOD: default is false
	 */
	boolean byDamage = true;
	
	/**
	 * Objective health
	 */
	Double objectiveHealth = 100d;
	
	
	/**
	 * Objective description
	 */
	String objectiveDesc;
	
	/**
	 * Message displayed to players once objective completed
	 */
	String completedText;
	
	/**
	 * UT4 AS Mod - Objective order
	 * Linked with defense priority
	 */
	int order = 0;
	
	/**
	 * UT99 - The lower the value is the further the objective is (generally 0 means last objective)
	 */
	int defensePriority;
	
	/**
	 * UT4 AS Mod - Objective text displayed in hud (i guess ...)
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
			objectiveListText = objectiveDesc;
		}
		
		// CompletedText="Test"
		else if(line.startsWith("CompletedText")){
			completedText = T3DUtils.getString(line);
		}
		
		// DefensePriority=0 (0 = latest objective
		else if(line.startsWith("DefensePriority")){
			defensePriority = T3DUtils.getInteger(line);
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
		
		// Pawn Property - Default is 100 (UT99)
		else if(line.startsWith("Health=")){
			objectiveHealth = T3DUtils.getDouble(line);
		}
		
		else {
            return super.analyseT3DData(line);
        }
        
        return true;
	}
	
	/**
	 * Default pawn health.
	 * Used for objectives that can be damaged
	 */
	final double DEFAULT_PAWN_HEALTH = 100;
	
	/**
	 * Default message for objective completed in UT99
	 */
	final String DEFAULT_COMPLETED_OBJ_UT99 = "was destroyed!";
	
	public String toString(){
		
		sbf.append(IDT).append("Begin Actor Class=UTASObjective_C Name=").append(name).append("\n");
        sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"DefaultSceneRoot\"\n");
        writeEndObject();
        
        // Need to add collision component else objective would not trigger at all
        // TODO check if needed if byDamage = false
        sbf.append(IDT).append("\tBegin Object Class=CapsuleComponent Name=\"Capsule\"\n");
        sbf.append(IDT).append("\t\tBegin Object Class=BodySetup Name=\"BodySetup_4546\"\n");
        sbf.append(IDT).append("\t\tEnd Object\n");
        writeEndObject();
        
        sbf.append(IDT).append("\tBegin Object Name=\"DefaultSceneRoot\"\n");
        writeLocRotAndScale();
        writeEndObject();

        sbf.append(IDT).append("\tBegin Object Name=\"Capsule\"\n");
        
        sbf.append(IDT).append("\tBegin Object Name=\"BodySetup_4546\"\n");
        sbf.append(IDT).append("\t\tCollisionTraceFlag=CTF_UseSimpleAsComplex\n");
        writeEndObject();
        
        sbf.append(IDT).append("\t\tCapsuleHalfHeight=").append(collisionHeight).append("\n");
        sbf.append(IDT).append("\t\tCapsuleRadius=").append(collisionRadius).append("\n");
        BodyInstance bi = new BodyInstance();
        bi.setCollisionResponse(ECollisionResponse.ECR_Block);
        sbf.append(IDT).append("\t");
        bi.toT3d(sbf);
        sbf.append("\n");
        
        sbf.append(IDT).append("\t\tAttachParent=DefaultSceneRoot\n");
        sbf.append(IDT).append("\t\tCreationMethod=Instance\n");
        writeEndObject();
        
        if(objectiveDesc != null){
        	sbf.append(IDT).append("\tObjectiveDesc=\"").append(objectiveDesc).append("\"\n");
        }
        
        if(completedText != null){
        	sbf.append(IDT).append("\tCompletedText=\"").append(completedText).append("\"\n");
        }
        
        if(byDamage){
	        if(objectiveHealth != null){
	        	sbf.append(IDT).append("\tObjectiveHealth=").append(objectiveHealth).append("\n");
	        } 
	        else {
	        	sbf.append(IDT).append("\tObjectiveHealth=").append(DEFAULT_PAWN_HEALTH).append("\n");
	        }
        }
        

        sbf.append(IDT).append("\tbyTouch=").append(byTouch).append("\n");
        sbf.append(IDT).append("\tbyDamage=").append(byDamage).append("\n");
        sbf.append(IDT).append("\tisCritical=").append(isCritical).append("\n");
        sbf.append(IDT).append("\tOrder=").append(order).append("\n");
        
        sbf.append(IDT).append("\tRootComponent=DefaultSceneRoot\n");
        
        if(objectiveListText != null){
        	sbf.append(IDT).append("\tObjectiveListText=\"").append(objectiveListText).append("\"\n");
        }
        
        sbf.append(IDT).append("\tInstanceComponents(0)=CapsuleComponent'Capsule'\n");
        
        writeEndActor();
        
        return sbf.toString();
	}
	
	@Override
    public void convert(){
        
		if(completedText == null){
			completedText = DEFAULT_COMPLETED_OBJ_UT99;
		}
		
		completedText = objectiveDesc + " " + completedText;
		
		// TODO get default values for UT99 fortstandards ...
		if(collisionRadius == null){
			collisionRadius = 48d;
		}
		
		if(collisionHeight == null){
			collisionHeight = 64d;
		}
		
		// TODO move out ('quick code')
        mapConverter.getT3dLvlConvertor().objectives.put(Integer.valueOf(this.defensePriority), this);
        
        super.convert();
    }
	
}
