/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter;

/**
 * 
 * @author XtremeXp
 */
public class UTGameTypes {
    
    /**
     *
     */
    public enum GameType{

        /**
         *
         */
        ASSAULT("AS","Assault", true),

        /**
         *
         */
        CTF("CTF","Capture the flag", true),

        /**
         *
         */
        DM("DM","Death match", false),

        /**
         *
         */
        DUEL("DUEL","Death match", false),

        /**
         *
         */
        TDM("TDM","Team deathmatch", true);
        
        
        String prefix;
        String name;

        /**
         *
         */
        public boolean isTeamBased;
        
        GameType(String prefix, String name, boolean isTeamBased){
            this.prefix = prefix;
            this.isTeamBased = isTeamBased;
        }
    }
    
    /**
     *
     */
    public static final String GAMETYPE_DEATHMATCH = "DM";
    
    /**
     *
     */
    public static final String GAMETYPE_TEAM_DEATHMATCH = "TDM";
    
    /**
     *
     */
    public static final String GAMETYPE_DUEL = "DUEL";
    
    /**
     *
     */
    public static final String GAMETYPE_ASSAULT = "AS";
    
    /**
     *
     * @param mapName
     * @return
     */
    public static boolean isTeamBasedFromMapName(String mapName){
        
        if(mapName == null){
            return false;
        }
        
        if(mapName.contains("-")){
            String prefix = mapName.split("\\-")[0];
            GameType gameType = getGameType(prefix);
            
            if(gameType != null){
                return gameType.isTeamBased;
            }
        }
        
        return false;
    }
    
    public static GameType getGameType(String prefix){
        
        if(prefix == null){
            return null;
        }
        
        prefix = prefix.toUpperCase();
        
        switch (prefix) {
            case "DM":
                return GameType.DM;
            case "AS":
                return GameType.ASSAULT;
            case "CTF":
                return GameType.CTF;
            default:
                return null;
        }
    }
    
    /**
     * 
     * @param gameType
     * @return 
     */
    public static boolean isTeamBased(String gameType){
        
        switch (gameType){
            case GAMETYPE_DEATHMATCH:
                return false;
            case GAMETYPE_TEAM_DEATHMATCH:
                return true;
            case GAMETYPE_ASSAULT:
                return true;
            case GAMETYPE_DUEL:
                return false;
            default:
                return false;
        }
    }
}
