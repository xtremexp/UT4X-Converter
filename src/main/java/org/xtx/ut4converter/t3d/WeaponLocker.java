package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

import java.util.ArrayList;
import java.util.List;

public class WeaponLocker extends T3DActor {

    /**
     * Original weapon classes or
     */
    private List<String> weapons = new ArrayList<>();


    public WeaponLocker(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);
    }

    @Override
    public boolean analyseT3DData(String line) {

        // Weapons(0)=(WeaponClass=Class'UTGame.UTWeap_LinkGun')
        // Weapons(1)=(WeaponClass=Class'UTGame.UTWeap_RocketLauncher')
        if (line.startsWith("Weapons(")) {
            weapons.add(line.split("'")[1]);
        }

        return super.analyseT3DData(line);
    }

    @Override
    public void convert() {


        final List<String> newWpClass = new ArrayList<>();

        // convert old weapon class to UT4 weapon class
        for (final String weaponClass : weapons) {
            //extract method
            String newWeaponClass = performCheck(weaponClass);
            if (newWeaponClass != null) {
                newWpClass.add(newWeaponClass);
            }
        }

        // replace with UT4 weapon classes
        weapons = newWpClass;

        super.convert();
    }

    private String performCheck(String weaponClass) {
        String newWeaponCLass = null;
        switch (weaponClass) {
            // UT3 and UT2004classes
            case "UTGame.UTWeap_LinkGun", "XWeapons.LinkGun" ->
                    newWeaponCLass = "/Game/RestrictedAssets/Weapons/LinkGun/BP_LinkGun.BP_LinkGun_C";
            case "UTGame.UTWeap_RocketLauncher", "XWeapons.RocketLauncher" ->
                    newWeaponCLass = "/Game/RestrictedAssets/Weapons/RocketLauncher/BP_RocketLauncher.BP_RocketLauncher_C";
            case "UTGameContent.UTWeap_BioRifle_Content", "XWeapons.BioRifle" ->
                    newWeaponCLass = "/Game/RestrictedAssets/Weapons/BioRifle/BP_BioRifle.BP_BioRifle_C";
            case "UTGame.UTWeap_FlakCannon", "XWeapons.FlakCannon" ->
                    newWeaponCLass = "/Game/RestrictedAssets/Weapons/Flak/BP_FlakCannon.BP_FlakCannon_C";
            case "UTGameContent.UTWeap_Redeemer_Content", "XWeapons.Redeemer" ->
                    newWeaponCLass = "/Game/RestrictedAssets/Weapons/Redeemer/BP_Redeemer.BP_Redeemer_C";
            case "UTGame.UTWeap_ShockRifle", "XWeapons.ShockRifle" ->
                    newWeaponCLass = "/Game/RestrictedAssets/Weapons/ShockRifle/ShockRifle.ShockRifle_C";
            case "UTGame.UTWeap_SniperRifle", "XWeapons.SniperRifle" ->
                    newWeaponCLass = "/Game/RestrictedAssets/Weapons/Sniper/BP_Sniper.BP_Sniper_C";
            case "UTGame.UTWeap_Stinger", "XWeapons.Minigun" ->
                    newWeaponCLass = "/Game/RestrictedAssets/Weapons/Minigun/BP_Minigun.BP_Minigun_C";
            case "XWeapons.SuperShockRifle" ->
                    newWeaponCLass = "/Game/RestrictedAssets/Weapons/ShockRifle/BP_InstagibRifle.BP_InstagibRifle_C";
            case "XWeapons.ShieldGun" ->
                    newWeaponCLass = "/Game/RestrictedAssets/Weapons/ImpactHammer/BP_ImpactHammer.BP_ImpactHammer_C";
            default -> {
            }
        }
        return newWeaponCLass;
    }


    public String toT3d() {


        sbf.append(IDT).append("Begin Actor Class=BP_WeaponLocker_C Name=").append(name).append(" Archetype=BP_WeaponLocker_C'/Game/RestrictedAssets/Pickups/BP_WeaponLocker.Default__BP_WeaponLocker_C'\n");

        sbf.append(IDT).append(IDT).append("Begin Object Name=\"Capsule\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append(IDT).append("End Object\n");

        int idx = 0;

        for (final String wpClass : weapons) {
            // WeaponList(0)=(WeaponType=BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/LinkGun/BP_LinkGun.BP_LinkGun_C')
            sbf.append(IDT).append(IDT).append("WeaponList(").append(idx).append(")=(WeaponType=BlueprintGeneratedClass'").append(wpClass).append("')\n");
            idx++;
        }

        sbf.append(IDT).append(IDT).append("RootComponent=Capsule\n");
        writeEndActor();

        return super.toString();
    }

}
