/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xtx.ut4converter.UTGames;

/**
 * Base class to match or replace properties
 * TODO use some xml file
 * @author XtremeXp
 */
public class T3DMatch {
    
    /**
     *
     */
    public static String FROM_UT99_Z_OFFSET = "FROM_UT99_Z_OFFSET";
    
    /**
     * Root component type for UT4 actor.
     * Used when writting converted actor for UT4
     */
    public static enum UE4_RCType{

        /**
         *
         */
        UNKNOWN("Unknown", "Unknown"),

        /**
         *
         */
        SCENE_COMP("SceneComponent", "DummyRoot"),

        /**
         *
         */
        CAPSULE("CapsuleComponent", "Capsule"),

        /**
         *
         */
        AUDIO("AudioComponent", "AudioComponent0");
        
        /**
         *
         */
        public String name;

        /**
         *
         */
        public String alias;
        
        UE4_RCType(String name, String alias){
            this.name = name;
            this.alias = alias;
        }
    }
    
    private void init(){
        list = new ArrayList<>();
        
        final String UT4_CLS_WPT = "WeaponBase_C";
        final String UT4_PROP_WPT = "WeaponType";
        final String UT4_PROP_IT = "InventoryType";
        
        // TODO use some proper xml file to set actor 'matches'
        
        // U1, U2, UT99, UT2003, UT2004, UT3, UT4 ...
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"HealthVial"}, null, null, null, new String[]{"Health_Small_C"})
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 24f}));
        
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"MedBox"}, null, null, null, new String[]{"Health_Medium_C"})
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 24f}));
        
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"HealthPack"}, null, null, null, new String[]{"Health_Large_C"})
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 24f}));
        
        // Weapons UT99 -> UT4
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"Eightball", "UT_Eightball"}, null, null, null, new String[]{UT4_CLS_WPT})
                .withUT4P(UT4_PROP_WPT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/RocketLauncher/BP_RocketLauncher.BP_RocketLauncher_C'")
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 26f}));
        
        // Weapons UT99 -> UT4
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"PulseGun"}, null, null, null, new String[]{UT4_CLS_WPT})
                .withUT4P(UT4_PROP_WPT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/LinkGun/BP_LinkGun.BP_LinkGun_C'")
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 26f}));
        
        // Weapons UT99 -> UT4
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"UT_FlakCannon"}, null, null, null, new String[]{UT4_CLS_WPT})
                .withUT4P(UT4_PROP_WPT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/Flak/BP_FlakCannon.BP_FlakCannon_C'")
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 26f}));
        
        // Weapons UT99 -> UT4
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"WarheadLauncher"}, null, null, null, new String[]{UT4_CLS_WPT})
                .withUT4P(UT4_PROP_WPT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/Redeemer/BP_Redeemer.BP_Redeemer_C'")
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 26f}));
        
        // Weapons UT99 -> UT4
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"ShockRifle"}, null, null, null, new String[]{UT4_CLS_WPT})
                .withUT4P(UT4_PROP_WPT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/ShockRifle/ShockRifle.ShockRifle_C'")
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 26f}));
        
        // Weapons UT99 -> UT4
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"ut_biorifle", "GESBioRifle"}, null, null, null, new String[]{UT4_CLS_WPT})
                .withUT4P(UT4_PROP_WPT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/BioRifle/BP_BioRifle.BP_BioRifle_C'")
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 26f}));
        
        // Weapons UT99 -> UT4
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"SniperRifle"}, null, null, null, new String[]{UT4_CLS_WPT})
                .withUT4P(UT4_PROP_WPT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/Sniper/BP_Sniper.BP_Sniper_C'")
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 26f}));
        
        // Weapons UT99 -> UT4
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"minigun2"}, null, null, null, new String[]{UT4_CLS_WPT})
                .withUT4P(UT4_PROP_WPT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/Minigun/BP_Minigun.BP_Minigun_C'")
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 26f}));
        
        // Weapons UT99 -> UT4
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"ThighPads"}, null, null, null, new String[]{"PowerupBase_C"})
                .withUT4P(UT4_PROP_IT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Pickups/Armor/Armor_ThighPads.Armor_ThighPads_C'")
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 8f}));
        
        
        // Armor2
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"Armor", "Armor2"}, null, null, null, new String[]{"PowerupBase_C"})
                .withUT4P(UT4_PROP_IT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Pickups/Armor/Armor_Chest.Armor_Chest_C'")
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 8f}));
        
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"UT_ShieldBelt"}, null, null, null, new String[]{"PowerupBase_C"})
                .withUT4P(UT4_PROP_IT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Pickups/Armor/Armor_ShieldBelt.Armor_ShieldBelt_C'")
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 8f}));
        
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"UT_Jumpboots"}, null, null, null, new String[]{"PowerupBase_C"})
                .withUT4P(UT4_PROP_IT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Pickups/Powerups/BP_JumpBoots.BP_JumpBoots_C'")
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 8f}));
        
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"UDamage"}, null, null, null, new String[]{"PowerupBase_C"})
                .withUT4P(UT4_PROP_IT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Pickups/Powerups/BP_UDamage.BP_UDamage_C'")
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 8f}));
        
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"BioAmmo"}, null, null, null, new String[]{"BioAmmoPickup_C"})
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 8f}));
        
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"Miniammo"}, null, null, null, new String[]{"MinigunAmmoPickup_C"})
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 8f}));
        
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"FlakAmmo", "FlakBox"}, null, null, null, new String[]{"FlakAmmoPickup_C"})
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 8f}));
        
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"RocketPack", "RocketCan"}, null, null, null, new String[]{"RocketAmmoPickup_C"})
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 8f}));
        
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"ShockCore"}, null, null, null, new String[]{"ShockAmmoPickup_C"})
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 8f}));
        
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"BulletBox"}, null, null, null, new String[]{"SniperAmmoPickup_C"})
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 8f}));
        
        list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[]{"PAmmo"}, null, null, null, new String[]{"LinkAmmoPickup_C"})
                .addConvP(new Object[]{FROM_UT99_Z_OFFSET, 8f}));
        

        // FIXME / NOT WORKING     
        list.add(iByGame(T3DPickup.class, UE4_RCType.SCENE_COMP.name, null, null, new String[]{"FlagBase"}, null, null, null, new String[]{"UTRedFlagBase_C"}));
        
        // UT99 Actor with class 'FlagBase' and property "Team" equals 1 = UTBlueFlagBase for UT4
        // FIXME / NOT WORKING 
        list.add(iByGame(T3DPickup.class, UE4_RCType.SCENE_COMP.name, null, null, new String[]{"FlagBase"}, null, null, null, new String[]{"UTBlueFlagBase_C"})
                .withUT99P("Team", "1"));

    }
     
    /**
     *
     */
    public static enum Type{

        /**
         *
         */
        UNKNOWN,

        /**
         *
         */
        WEAPON,

        /**
         *
         */
        HEALTH
    } 
    
    /**
     *
     */
    public class GlobalMatch {
        
        /**
         * Internal tag.
         * Might be used to set the same conversion property
         * for all globalmatches having this tag (e.g: z offset for all weapon pickups)
         */
        String tag;
        
        List<Match> matches;

        /**
         *
         * @param matches
         */
        public GlobalMatch(List<Match> matches) {
            this.matches = matches;
        }
        
        /**
         * Add property for UT4.
         * When any game will be converted to this actor
         * this property is needed
         * @param property
         * @param value
         * @return 
         */
        public GlobalMatch withUT4P(String property, String value){
            for(Match m : matches){
                if(m.game == UTGames.UTGame.UT4){
                    m.addP(property, value);
                }
            }
            
            return this;
        }
        
        /**
         * Add property for UT99
         * When any game will be converted to this actor
         * this property is needed
         * @param property
         * @param value
         * @return 
         */
        public GlobalMatch withUT99P(String property, String value){
            for(Match m : matches){
                if(m.game == UTGames.UTGame.UT99){
                    m.addP(property, value);
                }
            }
            
            return this;
        }
        
        /**
         *
         * @param property
         * @param value
         * @param game
         * @return
         */
        public GlobalMatch withPropEquals(String property, String value, UTGames.UTGame game){
            for(Match m : matches){
                if(m.game == game){
                    m.addP(property, value);
                }
            }
            
            return this;
        }
        
        /**
         *
         * @param keyValue
         * @return
         */
        public GlobalMatch addConvP(Object[] keyValue){
            for(Match m : matches){
                if(m.game == UTGames.UTGame.UT4){
                    m.convertProperties.put(keyValue[0].toString(), keyValue[1]);
                }
            }
            
            return this;
        }
    }
    
    /**
     *
     */
    public class Match {
        
        /**
         * 
         */
        UTGames.UTGame game = UTGames.UTGame.NONE;
        
        /**
         * Match available for this engine.
         * Might be useful for convert between games with same engine
         * or very close (eg.: UT3/UT4)
         */
        UTGames.UnrealEngine engine = UTGames.UnrealEngine.NONE;
        
        /**
         *
         */
        public List<String> actorClass = new ArrayList<>();
        
        /**
         * Will use this class for convert
         */
        public Class t3dClass;
        
        /**
         *
         */
        public Type type;
        
        /**
         * Properties that need to be written in t3d data for this actor.
         * Map of property, value
         * E.G: FolderPath="Pickups/Weapons" FolderPath, "Pickups/Weapons"
         */
        public Map<String, String> properties = new HashMap<>();
        
        /**
         * Internal properties used by converter
         * to set some other properties or other things
         * Like change height of actor to fit with output game and so on ...
         */
        public Map<String, Object> convertProperties = new HashMap<>();
        
        /**
         *
         * @param names
         * @param game
         * @param t3dClass
         */
        public Match(String[] names, UTGames.UTGame game, Class t3dClass){
            
            this.actorClass.addAll(Arrays.asList(names));
            this.game = game;
            this.engine = game.engine;
            this.t3dClass = t3dClass;
            this.type = Type.UNKNOWN;
        }
        
        /**
         *
         * @param names
         * @param game
         * @param t3dClass
         * @param convertProperty
         */
        public Match(String[] names, UTGames.UTGame game, Class t3dClass, String convertProperty){
            this.actorClass.addAll(Arrays.asList(names));
            this.game = game;
            this.engine = game.engine;
            this.t3dClass = t3dClass;
            this.convertProperties.put(convertProperty, null);
        }
        
        /**
         *
         * @param names
         * @param game
         */
        public Match(String[] names, UTGames.UTGame game){
            this.actorClass.addAll(Arrays.asList(names));
            this.game = game;
            this.engine = game.engine;
        }
        
        /**
         *
         * @param names
         * @param engine
         */
        public Match(String names, UTGames.UnrealEngine engine){
            this.actorClass.addAll(Arrays.asList(names));
            this.engine = engine;
        }
        
        /**
         *
         * @param property
         * @param value
         * @return
         */
        public Match addP(String property, String value){
            this.properties.put(property, value);
            
            return this;
        }
    }
    
    
    List<GlobalMatch> list;
    
    /**
     *
     */
    public T3DMatch(){
        list = new ArrayList<>();
        init();
    }
    
    private GlobalMatch iByGame(Class t3dClass, String convertProp, String[] u1Class, String[] u2Class, String[] ut99Class, String[] ut2003Class, String[] ut2004Class, String[] ut3Class, String[] ut4Class){
        
        List<Match> matches = new ArrayList<>();
        
        if(u1Class != null){
            matches.add(new Match(u1Class, UTGames.UTGame.U1, t3dClass, convertProp));
        }
        
        if(u2Class != null){
            matches.add(new Match(u2Class, UTGames.UTGame.U2, t3dClass, convertProp));
        }
        
        if(ut99Class != null){
            matches.add(new Match(ut99Class, UTGames.UTGame.UT99, t3dClass, convertProp));
        }
        
        if(ut2003Class != null){
            matches.add(new Match(ut2003Class, UTGames.UTGame.UT2003, t3dClass, convertProp));
        }
        
        if(ut2004Class != null){
            matches.add(new Match(ut2004Class, UTGames.UTGame.UT2004, t3dClass, convertProp));
        }
        
        if(ut3Class != null){
            matches.add(new Match(ut3Class, UTGames.UTGame.UT3, t3dClass, convertProp));
        }
        
        if(ut4Class != null){
            matches.add(new Match(ut4Class, UTGames.UTGame.UT4, t3dClass, convertProp));
        }
        
        return new GlobalMatch(matches);
    }

    
    /**
     * 
     * @param inputGame
     * @param outputGame
     * @return 
     */
    public HashMap<String, Match> getActorClassMatch(UTGames.UTGame inputGame, UTGames.UTGame outputGame){
        
        boolean goodList = true;
        List<String> inputClasses = new ArrayList<>();
        HashMap<String, Match> hm = new HashMap<>();

        for(GlobalMatch matchesForName : list ){
            
            for(Match matchForName : matchesForName.matches){
                if(matchForName.game == inputGame && matchForName.t3dClass != null){
                    goodList = true;
                    inputClasses = matchForName.actorClass;
                    break;
                }
            }
            
            if(goodList){
                for(Match matchForName : matchesForName.matches){
                    if(matchForName.game == outputGame){
                        
                        for(String inputClass : inputClasses){
                            hm.put(inputClass, matchForName);
                        }
                        break;
                    }
                }
            }
        }
        
        return hm;
    }
    
    /**
     * Tries to find out converted t3d actor inActorClass (if inActorClass has changed between ut games)
     * @param inActorClass
     * @param inputGame Input game map is being converted from
     * @param outputGame Output game map is being converted to
     * @param withT3dClass
     * @param inActorProps
     * @return 
     */
    public Match getMatchFor(String inActorClass, UTGames.UTGame inputGame, UTGames.UTGame outputGame, boolean withT3dClass, Map<String, String> inActorProps){
        
        Match m = null;
        

        List<GlobalMatch> goodMatches = new ArrayList<>();
        
        boolean superBreak = false;
        
        for(GlobalMatch matchesForName : list ){
            
            for(Match matchForName : matchesForName.matches){
                
                if(matchForName.game == inputGame && inActorClass != null && matchForName.actorClass.contains(inActorClass)){
                    
                    // Useful? Normally any actor has properties ...
                    if(inActorProps == null || inActorProps.isEmpty()){
                        break;
                    } else {
                        if(matchForName.properties == null || matchForName.properties.isEmpty()){
                            goodMatches.add(matchesForName);
                            break;
                        } else {
                            
                            // Current Actor properties match perfectly
                            // properties needed, this is the good one
                            for(String key : matchForName.properties.keySet()){
                                if(inActorProps.containsKey(key) && inActorProps.get(key).equals(matchForName.properties.get(key))){
                                    goodMatches.clear();
                                    goodMatches.add(matchesForName);
                                    superBreak = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            
            if(superBreak){
                break;
            }
        }
        
        for(GlobalMatch matchesForName : goodMatches ){
            for(Match matchForName : matchesForName.matches){
                if(matchForName.game == outputGame){
                    return matchForName;
                }
            }
        }
        
        
        
        return m;
    }
}
