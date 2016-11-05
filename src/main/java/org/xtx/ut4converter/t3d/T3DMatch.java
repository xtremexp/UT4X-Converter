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

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UTGame;

/**
 * Base class to match or replace "basic" actor such as pickups, weapons and so
 * on. TODO use some xml file
 * 
 * @author XtremeXp
 */
public class T3DMatch {

	MapConverter mapConverter;

	/**
	 * Conversion property. Must always be checked when using 1.0x scaling! TODO
	 * move to other class TODO enum for other conversion property
	 */
	public static String Z_OFFSET = "Z_OFFSET";

	/**
	 * Offset with "Z" location for weapons to fit with floor. It's import that
	 */
	private final double UE1_UT4_WP_ZOFFSET = 26d;

	private final double UT2004_UT4_WP_ZOFFSET = 30d;

	/**
	 * Root component type for UT4 actor. Used when writting converted actor for
	 * UT4
	 */
	public static enum UE4_RCType {

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

		SPHERE("SphereComponent", "Sphere"),

		ICON("Icon", "Icon"),

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

		UE4_RCType(String name, String alias) {
			this.name = name;
			this.alias = alias;
		}
	}

	/**
	 * @param inputGame
	 *
	 */
	public T3DMatch(MapConverter mapConverter) {
		list = new ArrayList<>();
		this.mapConverter = mapConverter;
		initialise();
	}

	/**
	 * 
	 * 
	 * @param inputGame
	 *            Input game for converting
	 */
	private void initialise() {
		list = new ArrayList<>();

		// TODO use some proper xml file to set actor 'matches'
		// UT99->UT4, no match / check for:
		// DispersionPistol, QuadShot, Stinger, Razorjack, Chainsaw, ripper,
		// RazorAmmo
		// ShellBox / Clip (enforcer ammo)
		// Stinger Ammo, BladeHopper, SuperShockCore
		// Flare, FlashLight, SearchLight, ForceField
		// ScubaGear
		// AsbestosSuit, KevlarSuit, ToxinSuit
		// Shells (U1)

		// U1, U2, UT99, UT2003, UT2004, UT3, UT4 ...
		list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "Bandages" }, null, new String[] { "HealthVial", "Bandages" }, new String[] { "MiniHealthPack" },
				new String[] { "MiniHealthPack" }, new String[] { "UTPickupFactory_HealthVial" }, new String[] { "Health_Small_C" }).addConvP(UTGame.UT4, new Object[] { Z_OFFSET, 24d }));

		list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "Health", "UPakHealth", "RespawningHealth", "NaliFruit" }, new String[] { "ArtifactHealth", "HealthPickup" },
				new String[] { "MedBox", "NaliFruit" }, new String[] { "HealthCharger" }, new String[] { "HealthCharger" }, new String[] { "UTPickupFactory_MediumHealth" },
				new String[] { "Health_Medium_C" }).addConvP(UTGame.UT4, new Object[] { Z_OFFSET, 24d }));

		list.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "SuperHealth" }, null, new String[] { "HealthPack" }, new String[] { "SuperHealthCharger" },
				new String[] { "SuperHealthCharger" }, new String[] { "UTPickupFactory_SuperHealth" }, new String[] { "Health_Large_C" }).addConvP(UTGame.UT4, new Object[] { Z_OFFSET, 24d }));

		initialiseWeapons(mapConverter.getInputGame());

		initialiseAmmos();

		initialisePowerUps();

		list.add(iByGame(T3DPickup.class, UE4_RCType.SCENE_COMP.name, null, null, new String[] { "FlagBase" }, new String[] { "xRedFlagBase" }, new String[] { "xRedFlagBase" },
				new String[] { "UTCTFRedFlagBase" }, new String[] { "UTRedFlagBase_C" }).addConvP(UTGame.UT4, new Object[] { Z_OFFSET, -64d }));

		// UT99 Actor with class 'FlagBase' and property "Team" equals 1 =
		// UTBlueFlagBase for UT4
		list.add(iByGame(T3DPickup.class, UE4_RCType.SCENE_COMP.name, null, null, new String[] { "FlagBase" }, new String[] { "xBlueFlagBase" }, new String[] { "xBlueFlagBase" },
				new String[] { "UTCTFBlueFlagBase" }, new String[] { "UTBlueFlagBase_C" }).withP(UTGame.UT99, "Team", "1").addConvP(UTGame.UT4, new Object[] { Z_OFFSET, -64d }));

		// TODO improve t3d matcher to sync / convert properties "on the fly"

		list.add(iByGame(T3DPickup.class, UE4_RCType.ICON.name, null, null, new String[] { "DefensePoint" }, null, null, null, new String[] { "UTDefensePoint" }));

		// Blueprint_Effect_Smoke_C
		list.add(iByGame(T3DPickup.class, "P_Smoke", null, null, new String[] { "SmokeGenerator" }, null, null, null, new String[] { "Blueprint_Effect_Smoke_C" }));
	}

	/**
	 * Add actor matches for power ups
	 */
	private void initialisePowerUps() {

		final String UT4_PROP_IT = "InventoryType";
		final String UT4_CLS_PWRUP = "PowerupBase_C";

		List<GlobalMatch> gmPwrups = new ArrayList<>();

		// Items - ThighPads
		gmPwrups.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[] { "ThighPads" }, null, null, new String[] { "UTArmorPickup_ThighPads" }, new String[] { UT4_CLS_PWRUP })
				.withP(UTGame.UT4, UT4_PROP_IT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Pickups/Armor/Armor_ThighPads.Armor_ThighPads_C'"));

		// Items - Helmet
		gmPwrups.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, null, null, null, new String[] { "UTArmorPickup_Helmet" }, new String[] { UT4_CLS_PWRUP }).withP(UTGame.UT4,
				UT4_PROP_IT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Pickups/Armor/Armor_Helmet.Armor_Helmet_C'"));

		// PowerUp - Armor
		gmPwrups.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "Armor" }, new String[] { "ArtifactArmor" }, new String[] { "Armor", "Armor2" },
				new String[] { "ShieldCharger" }, new String[] { "ShieldCharger" }, new String[] { "UTArmorPickup_Vest" }, new String[] { UT4_CLS_PWRUP }).withP(UTGame.UT4, UT4_PROP_IT,
				"BlueprintGeneratedClass'/Game/RestrictedAssets/Pickups/Armor/Armor_Chest.Armor_Chest_C'"));

		// PowerUp - ShieldBelt
		gmPwrups.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "ShielBelt", "PowerShield" }, null, new String[] { "UT_ShieldBelt", "ShielBelt", "PowerBelt" },
				new String[] { "SuperShieldCharger" }, new String[] { "SuperShieldCharger" }, new String[] { "UTArmorPickup_ShieldBelt" }, new String[] { UT4_CLS_PWRUP }).withP(UTGame.UT4,
				UT4_PROP_IT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Pickups/Armor/Armor_ShieldBelt.Armor_ShieldBelt_C'"));

		// PowerUp - JumpBoots
		gmPwrups.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "JumpBoots" }, null, new String[] { "UT_Jumpboots", "JumpBoots" }, null, null,
				new String[] { "UTPickupFactory_JumpBoots" }, new String[] { UT4_CLS_PWRUP }).withP(UTGame.UT4, UT4_PROP_IT,
				"BlueprintGeneratedClass'/Game/RestrictedAssets/Pickups/Powerups/BP_JumpBoots.BP_JumpBoots_C'"));

		// PowerUp - Damage Amplifier
		gmPwrups.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "Amplifier", "WeaponPowerUp" }, null, new String[] { "UDamage", "Amplifier" }, new String[] { "UDamageCharger" },
				new String[] { "UDamageCharger" }, new String[] { "UTPickupFactory_UDamage" }, new String[] { UT4_CLS_PWRUP }).withP(UTGame.UT4, UT4_PROP_IT,
				"BlueprintGeneratedClass'/Game/RestrictedAssets/Pickups/Powerups/BP_UDamage.BP_UDamage_C'"));

		// PowerUp - Invisibility
		gmPwrups.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "Invisibility" }, new String[] { "ArtifactInvisibility" },
				new String[] { "Invisibility", "UT_Invisibility", "UT_Stealth" }, null, null, new String[] { "UTPickupFactory_Invisibility" }, new String[] { UT4_CLS_PWRUP }).withP(UTGame.UT4,
				UT4_PROP_IT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Pickups/Powerups/BP_Invis.BP_Invis_C'"));

		// PowerUp - Berserk
		gmPwrups.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, null, null, null, new String[] { "UTPickupFactory_Berserk" }, new String[] { UT4_CLS_PWRUP }).withP(UTGame.UT4,
				UT4_PROP_IT, "BlueprintGeneratedClass'/Game/RestrictedAssets/Pickups/Powerups/BP_Berserk.BP_Berserk_C'"));

		for (GlobalMatch gmPwrup : gmPwrups) {
			gmPwrup.addConvP(UTGame.UT4, new Object[] { Z_OFFSET, 8d });
		}

		list.addAll(gmPwrups);
	}

	/**
	 * Add matches for ammo picksups
	 */
	private void initialiseAmmos() {


		List<GlobalMatch> gmAmmos = new ArrayList<>();
		
		// Ammo - Enforcer
		gmAmmos.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, null, null,
				null, new String[] { "UTAmmo_EnforcerRifle" }, new String[] { "EnforcerAmmoPickup_C" }));

		// Ammo - BioAmmo
		gmAmmos.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "Sludge" }, null, new String[] { "BioAmmo", "Sludge" }, new String[] { "BioAmmoPickup" },
				new String[] { "BioAmmoPickup" }, new String[] { "UTAmmo_BioRifle_Content", "UTAmmo_BioRifle" }, new String[] { "BioAmmoPickup_C" }));

		// Ammo - Minigun
		gmAmmos.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "Clip", "StingerAmmo" }, null, new String[] { "Miniammo", "EClip" }, new String[] { "MinigunAmmoPickup" },
				new String[] { "MinigunAmmoPickup" }, new String[] { "UTAmmo_Stinger" }, new String[] { "MinigunAmmoPickup_C" }));

		// Ammo - Flak
		gmAmmos.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "FlakBox", "FlakShellAmmo" }, null, new String[] { "FlakAmmo", "FlakBox", "FlakShellAmmo" },
				new String[] { "FlakAmmoPickup" }, new String[] { "FlakAmmoPickup" }, new String[] { "UTAmmo_FlakCannon" }, new String[] { "FlakAmmoPickup_C" }));

		// Ammo - Rocket
		gmAmmos.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "RocketCan", "RLAmmo" }, new String[] { "ammoRocketLauncher" }, new String[] { "RocketPack", "RocketCan" },
				new String[] { "RocketAmmoPickup" }, new String[] { "RocketAmmoPickup" }, new String[] { "UTAmmo_RocketLauncher" }, new String[] { "RocketAmmoPickup_C" }));

		// Ammo - Shock
		gmAmmos.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "ASMDAmmo" }, null, new String[] { "ShockCore", "ASMDAmmo" }, new String[] { "ShockAmmoPickup" },
				new String[] { "ShockAmmoPickup" }, new String[] { "UTAmmo_ShockRifle" }, new String[] { "ShockAmmoPickup_C" }));

		// Ammo - Sniper
		gmAmmos.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, new String[] { "RifleAmmo", "RifleRound", "ShellBox" }, new String[] { "ammoSniperRifle" }, new String[] { "BulletBox", "RifleAmmo",
				"RifleRound", "RifleShell" }, new String[] { "SniperAmmoPickup" }, new String[] { "SniperAmmoPickup" }, new String[] { "UTAmmo_SniperRifle" }, new String[] { "SniperAmmoPickup_C" }));

		// Ammo - LinkGun
		gmAmmos.add(iByGame(T3DPickup.class, UE4_RCType.CAPSULE.name, null, null, new String[] { "PAmmo" }, new String[] { "LinkAmmoPickup" }, new String[] { "LinkAmmoPickup" },
				new String[] { "UTAmmo_LinkGun" }, new String[] { "LinkAmmoPickup_C" }));

		for (GlobalMatch gmAmmo : gmAmmos) {
			gmAmmo.addConvP(UTGame.UT4, new Object[] { Z_OFFSET, 8d });
		}

		list.addAll(gmAmmos);
	}

	/**
	 * TODO handle UT3 WeaponLocker and WeaponPickupFactory
	 * 
	 * @param inputGame
	 */
	private void initialiseWeapons(UTGame inputGame) {

		final String UT4_CLS_WPT = "WeaponBase_C";
		final String[] UT3_CLS_WPT = new String[] {"UTWeaponPickupFactory"};
		final String UT3_PCK_FAC_WP_PROP = "WeaponPickupClass";
		final String WEAPON_TYPE = "WeaponType";

		List<GlobalMatch> gmWeapons = new ArrayList<>();
		Class<? extends T3DActor> pickupCls = T3DPickup.class;
		//          WeaponPickupClass=Class'UTGame.UTWeap_RocketLauncher'

		
		// Weapons - Rocket Launcher
		gmWeapons.add(iByGame(pickupCls, UE4_RCType.CAPSULE.name, new String[] { "Eightball" }, new String[] { "weaponRocketLauncher", "weaponRocketLauncherGimp" },
				new String[] { "Eightball", "UT_Eightball" }, new String[] { "xWeaponBase" }, new String[] { "xWeaponBase" }, UT3_CLS_WPT, new String[] { UT4_CLS_WPT })
				.withP(UTGame.UT3, UT3_PCK_FAC_WP_PROP, "Class'UTGame.UTWeap_RocketLauncher'")
				.withP(UTGame.UT4, WEAPON_TYPE, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/RocketLauncher/BP_RocketLauncher.BP_RocketLauncher_C'")
				.withP(UTGame.UT2004, WEAPON_TYPE, "Class'XWeapons.RocketLauncher'").withP(UTGame.UT2003, WEAPON_TYPE, "Class'XWeapons.RocketLauncher'"));

		// Weapons - Link Gun
		gmWeapons.add(iByGame(pickupCls, UE4_RCType.CAPSULE.name, null, null, new String[] { "PulseGun" }, null, new String[] { "xWeaponBase" }, UT3_CLS_WPT, new String[] { UT4_CLS_WPT })
				.withP(UTGame.UT3, UT3_PCK_FAC_WP_PROP, "Class'UTGame.UTWeap_LinkGun'")
				.withP(UTGame.UT4, WEAPON_TYPE, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/LinkGun/BP_LinkGun.BP_LinkGun_C'").withP(UTGame.UT2004, WEAPON_TYPE, "Class'XWeapons.LinkGun'")
				.withP(UTGame.UT2003, WEAPON_TYPE, "Class'XWeapons.LinkGun'"));

		// Weapons - Flak Cannon
		gmWeapons.add(iByGame(pickupCls, UE4_RCType.CAPSULE.name, new String[] { "FlakCannon" }, null, new String[] { "UT_FlakCannon", "FlakCannon" }, new String[] { "xWeaponBase" },
				new String[] { "xWeaponBase" }, UT3_CLS_WPT, new String[] { UT4_CLS_WPT })
				.withP(UTGame.UT3, UT3_PCK_FAC_WP_PROP, "Class'UTGame.UTWeap_FlakCannon'")
				.withP(UTGame.UT4, WEAPON_TYPE, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/Flak/BP_FlakCannon.BP_FlakCannon_C'")
				.withP(UTGame.UT2004, WEAPON_TYPE, "Class'XWeapons.FlakCannon'").withP(UTGame.UT2003, WEAPON_TYPE, "Class'XWeapons.FlakCannon'"));

		// Weapons - Enforcer
		gmWeapons.add(iByGame(pickupCls, UE4_RCType.CAPSULE.name, new String[] { "AutoMag" }, new String[] { "weaponAssaultRifle" }, new String[] { "AutoMag", "enforcer", "doubleenforcer" },
				new String[] { "xWeaponBase" }, new String[] { "xWeaponBase" }, null, new String[] { UT4_CLS_WPT })
				.withP(UTGame.UT4, WEAPON_TYPE, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/Enforcer/Enforcer.Enforcer_C'")
				.withP(UTGame.UT2004, WEAPON_TYPE, "Class'XWeapons.AssaultRifle'").withP(UTGame.UT2003, WEAPON_TYPE, "Class'XWeapons.AssaultRifle'"));

		// Weapons - Impact Hammer
		gmWeapons.add(iByGame(pickupCls, UE4_RCType.CAPSULE.name, null, null, new String[] { "ImpactHammer" }, null, null, null, new String[] { UT4_CLS_WPT })
				.withP(UTGame.UT4, WEAPON_TYPE, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/ImpactHammer/BP_ImpactHammer.BP_ImpactHammer_C'")
				.withP(UTGame.UT2004, WEAPON_TYPE, "Class'XWeapons.ShieldGun'").withP(UTGame.UT2003, WEAPON_TYPE, "Class'XWeapons.ShieldGun'"));

		// Weapons - Redeemer
		gmWeapons.add(iByGame(pickupCls, UE4_RCType.CAPSULE.name, new String[] { "RocketLauncher" }, null, new String[] { "WarheadLauncher" }, null, new String[] { "xWeaponBase" }, UT3_CLS_WPT,
				new String[] { UT4_CLS_WPT }).withP(UTGame.UT4, WEAPON_TYPE, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/Redeemer/BP_Redeemer.BP_Redeemer_C'")
				.withP(UTGame.UT3, UT3_PCK_FAC_WP_PROP, "Class'UTGameContent.UTWeap_Redeemer_Content'")
				.withP(UTGame.UT2004, WEAPON_TYPE, "Class'XWeapons.Redeemer'").withP(UTGame.UT2003, WEAPON_TYPE, "Class'XWeapons.Redeemer'"));

		// Weapons - Shock Rifle
		gmWeapons.add(iByGame(pickupCls, UE4_RCType.CAPSULE.name, new String[] { "ASMD" }, null, new String[] { "ShockRifle", "ASMD" }, null, new String[] { "xWeaponBase" }, UT3_CLS_WPT,
				new String[] { UT4_CLS_WPT })
				.withP(UTGame.UT3, UT3_PCK_FAC_WP_PROP, "Class'UTGame.UTWeap_ShockRifle'")
				.withP(UTGame.UT4, WEAPON_TYPE, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/ShockRifle/ShockRifle.ShockRifle_C'")
				.withP(UTGame.UT2004, WEAPON_TYPE, "Class'XWeapons.ShockRifle'").withP(UTGame.UT2003, WEAPON_TYPE, "Class'XWeapons.ShockRifle'"));

		// Weapons - Bio Rifle
		gmWeapons.add(iByGame(pickupCls, UE4_RCType.CAPSULE.name, new String[] { "GESBioRifle" }, null, new String[] { "ut_biorifle", "GESBioRifle" }, null, new String[] { "xWeaponBase" }, UT3_CLS_WPT,
				new String[] { UT4_CLS_WPT })
				.withP(UTGame.UT3, UT3_PCK_FAC_WP_PROP, "Class'UTGameContent.UTWeap_Bio_Rifle_Content'")
				.withP(UTGame.UT4, WEAPON_TYPE, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/BioRifle/BP_BioRifle.BP_BioRifle_C'")
				.withP(UTGame.UT2004, WEAPON_TYPE, "Class'XWeapons.BioRifle'").withP(UTGame.UT2003, WEAPON_TYPE, "Class'XWeapons.BioRifle'"));

		// Weapons - Sniper
		gmWeapons.add(iByGame(pickupCls, UE4_RCType.CAPSULE.name, new String[] { "Rifle" }, new String[] { "weaponSniperRifle" }, new String[] { "SniperRifle", "Rifle" }, UT3_CLS_WPT,
				new String[] { "xWeaponBase" }, null, new String[] { UT4_CLS_WPT })
				.withP(UTGame.UT3, UT3_PCK_FAC_WP_PROP, "Class'UTGame.UTWeap_Sniper_Rifle'")
				.withP(UTGame.UT4, WEAPON_TYPE, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/Sniper/BP_Sniper.BP_Sniper_C'")
				.withP(UTGame.UT2004, WEAPON_TYPE, "Class'XWeapons.SniperRifle'").withP(UTGame.UT2003, WEAPON_TYPE, "Class'XWeapons.SniperRifle'"));
		
		// Weapons - Minigun
		gmWeapons.add(iByGame(pickupCls, UE4_RCType.CAPSULE.name, new String[] { "Minigun" }, null, new String[] { "minigun2", "Minigun" }, null, new String[] { "xWeaponBase" }, UT3_CLS_WPT,
				new String[] { UT4_CLS_WPT })
				.withP(UTGame.UT3, UT3_PCK_FAC_WP_PROP, "Class'UTGame.UTWeap_Stinger'")
				.withP(UTGame.UT4, WEAPON_TYPE, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/Minigun/BP_Minigun.BP_Minigun_C'")
				.withP(UTGame.UT2004, WEAPON_TYPE, "Class'XWeapons.Minigun'").withP(UTGame.UT2003, WEAPON_TYPE, "Class'XWeapons.Minigun'"));

		// Weapons - Instagib
		gmWeapons.add(iByGame(pickupCls, UE4_RCType.CAPSULE.name, null, null, new String[] { "SuperShockRifle" }, null, null, null, new String[] { UT4_CLS_WPT })
				.withP(UTGame.UT4, WEAPON_TYPE, "BlueprintGeneratedClass'/Game/RestrictedAssets/Weapons/ShockRifle/BP_InstagibRifle.BP_InstagibRifle_C'")
				.withP(UTGame.UT2004, WEAPON_TYPE, "Class'XWeapons.SuperShockRifle'").withP(UTGame.UT2003, WEAPON_TYPE, "Class'XWeapons.SuperShockRifle'"));

		for (GlobalMatch gmWp : gmWeapons) {
			// UT99 -> UT4 modify "z" location to fit with floor
			if (inputGame.engine == UTGames.UnrealEngine.UE1) {
				gmWp.addConvP(UTGame.UT4, new Object[] { Z_OFFSET, UE1_UT4_WP_ZOFFSET });
			} else if (inputGame == UTGame.UT2004 || inputGame == UTGame.UT2003) {
				gmWp.addConvP(UTGame.UT4, new Object[] { Z_OFFSET, UT2004_UT4_WP_ZOFFSET });
			}

		}

		list.addAll(gmWeapons);
	}

	/**
     *
     */
	public class GlobalMatch {

		/**
		 * Internal tag. Might be used to set the same conversion property for
		 * all globalmatches having this tag (e.g: z offset for all weapon
		 * pickups)
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
		 *
		 * @param property
		 * @param value
		 * @param game
		 * @return
		 */
		public GlobalMatch withP(UTGames.UTGame game, String property, String value) {

			for (Match m : matches) {
				if (m.game == game) {
					m.addP(property, value);
				}
			}

			return this;
		}

		/**
		 * Adds a conversion property for exemple increase or decrease offset
		 * "Z" location to fit with floor TODO add some inGame parameter
		 * 
		 * @param outGame
		 *            UT Game the actor will be converted to
		 * @param keyValue
		 * @return
		 */
		public GlobalMatch addConvP(UTGame outGame, Object[] keyValue) {

			for (Match m : matches) {
				if (m.game == outGame) {
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
		 * Match available for this engine. Might be useful for convert between
		 * games with same engine or very close (eg.: UT3/UT4)
		 */
		UTGames.UnrealEngine engine = UTGames.UnrealEngine.NONE;

		/**
         *
         */
		public List<String> actorClass = new ArrayList<>();

		/**
		 * Will use this class for convert
		 */
		public Class<? extends T3DActor> t3dClass;

		/**
		 * Properties that need to be written in t3d data for this actor. Map of
		 * property, value E.G: FolderPath="Pickups/Weapons" FolderPath,
		 * "Pickups/Weapons"
		 */
		public Map<String, String> properties = new HashMap<>();

		/**
		 * Internal properties used by converter to set some other properties or
		 * other things Like change height of actor to fit with output game and
		 * so on ...
		 */
		public Map<String, Object> convertProperties = new HashMap<>();

		/**
		 *
		 * @param names
		 * @param game
		 * @param t3dClass
		 */
		public Match(String[] names, UTGames.UTGame game, Class<? extends T3DActor> t3dClass) {

			this.actorClass.addAll(Arrays.asList(names));
			this.game = game;
			this.engine = game.engine;
			this.t3dClass = t3dClass;
		}

		/**
		 *
		 * @param names
		 * @param game
		 * @param t3dClass
		 * @param convertProperty
		 */
		public Match(String[] names, UTGames.UTGame game, Class<? extends T3DActor> t3dClass, String convertProperty) {
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
		public Match(String[] names, UTGames.UTGame game) {
			this.actorClass.addAll(Arrays.asList(names));
			this.game = game;
			this.engine = game.engine;
		}

		/**
		 *
		 * @param names
		 * @param engine
		 */
		public Match(String names, UTGames.UnrealEngine engine) {
			this.actorClass.addAll(Arrays.asList(names));
			this.engine = engine;
		}

		/**
		 *
		 * @param property
		 * @param value
		 * @return
		 */
		public Match addP(String property, String value) {
			this.properties.put(property, value);

			return this;
		}
	}

	List<GlobalMatch> list;

	/**
	 * 
	 * @param game
	 * @return
	 */
	private boolean isFromOrTo(UTGame game) {
		return mapConverter.getInputGame() == game || mapConverter.getOutputGame() == game;
	}

	private GlobalMatch iByGame(Class<? extends T3DActor> t3dClass, String convertProp, String[] u1Class, String[] u2Class, String[] ut99Class, String[] ut2003Class, String[] ut2004Class,
			String[] ut3Class, String[] ut4Class) {

		List<Match> matches = new ArrayList<>();

		if (u1Class != null && isFromOrTo(UTGames.UTGame.U1)) {
			matches.add(new Match(u1Class, UTGames.UTGame.U1, t3dClass, convertProp));
		}

		if (u2Class != null && isFromOrTo(UTGames.UTGame.U2)) {
			matches.add(new Match(u2Class, UTGames.UTGame.U2, t3dClass, convertProp));
		}

		if (ut99Class != null && isFromOrTo(UTGames.UTGame.UT99)) {
			matches.add(new Match(ut99Class, UTGames.UTGame.UT99, t3dClass, convertProp));
		}

		if (ut2003Class != null && isFromOrTo(UTGames.UTGame.UT2003)) {
			matches.add(new Match(ut2003Class, UTGames.UTGame.UT2003, t3dClass, convertProp));
		}

		if (ut2004Class != null && isFromOrTo(UTGames.UTGame.UT2004)) {
			matches.add(new Match(ut2004Class, UTGames.UTGame.UT2004, t3dClass, convertProp));
		}

		if (ut3Class != null && isFromOrTo(UTGames.UTGame.UT3)) {
			matches.add(new Match(ut3Class, UTGames.UTGame.UT3, t3dClass, convertProp));
		}

		if (ut4Class != null && isFromOrTo(UTGames.UTGame.UT4)) {
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
	public HashMap<String, Match> getActorClassMatch(UTGames.UTGame inputGame, UTGames.UTGame outputGame) {

		boolean goodList = true;
		List<String> inputClasses = new ArrayList<>();
		HashMap<String, Match> hm = new HashMap<>();

		for (GlobalMatch matchesForName : list) {

			for (Match matchForName : matchesForName.matches) {
				if (matchForName.game == inputGame && matchForName.t3dClass != null) {
					goodList = true;
					inputClasses = matchForName.actorClass;
					break;
				}
			}

			if (goodList) {
				for (Match matchForName : matchesForName.matches) {
					if (matchForName.game == outputGame) {

						for (String inputClass : inputClasses) {
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
	 * Tries to find out converted t3d actor inActorClass (if inActorClass has
	 * changed between ut games)
	 * 
	 * @param inActorClass
	 * @param inputGame
	 *            Input game map is being converted from
	 * @param outputGame
	 *            Output game map is being converted to
	 * @param withT3dClass
	 * @param inActorProps
	 * @return
	 */
	public Match getMatchFor(final String inActorClass, UTGames.UTGame inputGame, UTGames.UTGame outputGame, boolean withT3dClass, Map<String, String> inActorProps) {


		Match m = null;

		List<GlobalMatch> goodMatches = new ArrayList<>();

		boolean superBreak = false;

		for (GlobalMatch matchesForName : list) {

			for (Match matchForName : matchesForName.matches) {

				if (matchForName.game == inputGame && inActorClass != null) {

					// insensitive case class name check matching
					long matchCount = matchForName.actorClass.stream().filter(x -> {
						return x.toLowerCase().equals(inActorClass.toLowerCase());
					}).count();

					if (matchCount > 0) {
						// Useful? Normally any actor has properties ...
						if (inActorProps == null || inActorProps.isEmpty()) {
							break;
						} else {
							if (matchForName.properties == null || matchForName.properties.isEmpty()) {
								goodMatches.add(matchesForName);
								break;
							} else {

								// Current Actor properties match perfectly
								// properties needed, this is the good one
								for (String key : matchForName.properties.keySet()) {
									if (inActorProps.containsKey(key) && inActorProps.get(key).equals(matchForName.properties.get(key))) {
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
			}

			if (superBreak) {
				break;
			}
		}

		for (GlobalMatch matchesForName : goodMatches) {
			for (Match matchForName : matchesForName.matches) {
				if (matchForName.game == outputGame) {
					return matchForName;
				}
			}
		}

		return m;
	}
}
