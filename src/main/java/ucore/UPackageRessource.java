/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucore;

/**
 *
 * @author XtremeXp
 */
public class UPackageRessource {

    UPackage packageFile;
    
    /**
     * Name of package Basically is filename without file extension (e.g:
     * AmbAncient.Looping.Stower51
     */
    public String packageName;

    /**
     * Group of ressource (optional)
     */
    public String group;

    /**
     * Name of ressource
     */
    public String name;

    /**
     *
     * @param fullName Full package ressource name (e.g:
     * "AmbAncient.Looping.Stower51"
     */
    public UPackageRessource(String fullName) {

        String s[] = fullName.split("\\.");

        // TODO handle brush polygon texture info
        // which only have "name" info
        packageName = s[0];
        name = s[s.length - 1];

        if (s.length == 3) {
            group = s[2];
        }
    }
}
