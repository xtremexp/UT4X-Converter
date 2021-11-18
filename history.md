- 1.0.0 - 22/02/2020
    * ut3: added support for terrain conversion
    * unreal 2: added support for terrain conversion
    * ut2004: enhanced terrain conversion with alpha layers conversion
    * ut2004, u2, ut3: fixed 3d scale for movers with 3d scale != 1
    * ut2004, ut3: added support for WeaponLocker and UTWeaponLocker
    * install: program is now bundled as an Windows installer containing Java Runtime Environnement (JRE) which makes the filesize significantly larger while embedding Java 13 runtime binaries.
    * tech: migrated from Java 8 to Java 13 version
    * tech: migrated to Java FX 13
    * all: smoothing group information is now correctly converted for staticmeshes
    * all: added support for bHidden property
    * all: fixed rare case where resources where not converted from map package
    * ui: xnview is no longer needed for conversion

- 0.9.5 - 04/01/2020
    * ut2003/4, ut3: added support for CameraActor, SpectatorCam, AttractCamera
    * ut2003/4, ut3: handled no collision properties for staticmeshes
    * ut3: fixed light intensity of all lights (some locations were ever too dark or too bright)

- 0.9.4 - 02/01/2020
    * all: added support for semi-solid and non-solid brushes (e.g: water sheet, ...)
    * u1/ut99: fixed bad lift destination / rotation for movers with multi positions

- 0.9.3 - 12/12/2019
    * Fix: skip sheet brushes to be converted causing BSP holes

- 0.9.2 - 08/12/2019
    * Hotfix: fixed no resources being identified / exported with newer version of UModel.

- 0.9.1 - 25/08/2018
    * all: fixed some unconverted resources

- 0.9.0 - 19/08/2018
    * added support for UDK maps (conversion quality is like UT3 = medium)
    * u1/ut99: default scale is now 2.5X
    * u1/ut99: decreased light intensity from 60 to 35
    * ut2003/4: decreased light intensity from 80 to 70
    * ut2003/4: fixed staticmeshes skins not being correctly applied
    * ut2003/4: handled uv2Mode, uv2Skin (thanks MoxNix for reporting)
    * ut2003/4/ut3: fixed some cases where some textures from staticmeshes where not converted
    * ut2003/4: added support for LadderVolume, PressureVolume, SnipingVolue, ConvoyPhysicsVolume, xFallingVolume, IonCannonKillVolume,
      HitScanBlockingVolume, ASCriticalObjectiveVolume, LeavingBattleFieldVolume, xFallingVolume
    * ut2003/4: VolumeTrigger, MessageTrigger, CrowdTrigger, UseTrigger, MusicTrigger, RedirectionTrigger,
      GravityTrigger, MaterialTrigger, TriggeredCondition, ScriptedTrigger now converted to TriggerVolume
    * ut2003/4: fixed lift actor property in lift exit actor not being set
    * ut2003/4: added support for multiple skins (corona) with lights
    * all: fixed some teamplayerstarts being converted as playerstart
    * all: fixed crash on UT4 editor import if trigger had either radius or height set to 0
    * unreal 2: fixed no staticmeshes or textures being converted. Since umodel does not support the game
      the conversion quality of resources is not good yet (staticmeshes with bad roration and uv)
    * unreal 2: added support for Trigger TT_USE case
    * all: movers forced to movetime=0.1s if their movetime is 0s (else won't move at all)
    * all: tag property now being converted
    * all: brushes/movers with flat horizontal shape are now being converted back (need convert them to staticmeshes
      prior fisrt build)
    * ut3: now no longer loads lightmap when extracting textures from packages (=quicker conversion)
    * ui: updated welcome screen
    * u1/ut99: fixed mover using default sound if no sound was set originally
    * ui: added option for lightmap resolution (default 128 for UE1/UE2 else 32)
    * ui: progress bar now being update more often
    * ui: remove notification of unconverted actors

- 0.8.10 - 18/07/2018
    * all: fixed low quality texture
    * all: now tries to export with UCC with UModel fails to load package
    * ut2004: fixed no sound for jumppads
    * ut2004: changed default scale from 2.4 to 2.2

- 0.8.9 - 01/08/2017
    * u1, ut99: fixed some messed up brushes with PostScale property not being correctly parsed correctly.

- 0.8.8 - 30/07/2017
    * ut3: fixed sounds not being converted

- 0.8.7 - 29/07/2017
    * all: fixed embedded sounds and staticmeshes in level package not being converted
    * ut3: fixed critical crash when DefensePoint actor is being converted
    * all: now if some actor fails to be converted won't make stop other actors to be converted


- 0.8.6 - 28/07/2017
    * ut2003/ut2004: added conversion of WaterVolume, NewWeaponBase, NewHealthCharger
    * ut2003/ut2004: fixed conversion failure of trigger with liveproximity and humanplayerproximity trigger type
    * u2/ut2003/ut2004/ut3: fixed bad description/tuto for converting staticmeshes

- 0.8.5 - 27/07/2017
    * u1/u2/ut2003/ut2004: blender manual conversion one by one of each staticmeshes is no longer needed.
      Now only need to import all .obj converted staticmeshes within the UE4 editor

- 0.8.4 - 19/07/2017
    * u1/u2/ut2003/ut2004: invisible brushes are no longer being converted. This might slightly reduce some bsp holes cases.
    * ut99: fixed some textures not being converted with some specific packages (like with SGTech1.utx texture package)

- 0.8.3 - 15/04/2017
    * all: fixed some cases where a few brushes had some textures not converted properly

- 0.8.2 - 18/12/2016
    * all: fix partial conversion if a mover had ambient sound and opened sound set

- 0.8.1 - 13/11/2016
    * ut2003/4, ut3: fixed some staticmeshes with long material names could not be converted
    * ut3: fixed sniper weapon with weaponpickupfactory not converting properly
    * ut3: fixed UTSlimeVolume, UTLavalVolume not converting properly
    * ut3: reverted back sound volume to original, now handling logarithm distance model for sound radius
    * ut3: added support for UTDefensePoint
    * misc: now also logs unconverted actor class name in .log file

- 0.8.0 - 11/11/2016
    * ut2003/ut2004: now terrain heightmap is imported (texture, decolayers are NOT yet converted)
    * ut3: default scale factor back to 2.2
    * ut3: added staticmeshes overidden material supported
    * ut3: added conversion of DecalActor, UTKillZVolume, CullDistanceVolume, EnforcerAmmo, UTWeaponPickupFactory, HeightFog
    * ut3: sounds are now converted from .ogg to .wav so they can be imported into UT4 editor
    * ut3: fixed some brushes not being properly converted that was causing bsp holes (see part "How to convert map?" -> "for ut3 maps only" section)
    * ut3: fixed some staticmeshes not being correctly located (prepivot support)
    * ut3: fixed many pickups not correctly aligned with floor
    * ut3: fixed SkyLight with no color set
    * ut3: fixed directional lights rendering as spotlights
    * ut3: reduced by 90% default volume of AmbientSounds
    * u1: added support for WeaponPowerup, StringerAmmo
    * ut99/ut2004: added support for conversion of Domination gametype actors.
    * misc: won't stop converting map resources if one fails to convert properly now.
    * misc: conversion processing is now saved in a conversion.log file
    * ui: slightly improved visibility in conversion settings

- 0.7.5 - 23/10/2016
    * Changes instructions for import .t3d converted file since UnrealEngine editor 4.13
      can no longer directly import .t3d files

- 0.7.4 - 21/09/2016:
    * Fixed some classes not being converted (like pickups, weapons) when class name was lowercase
    * Fixed bad parsing of tag sometimes

- 0.7.3 - 18/06/2016:
    * Changed default scale from 2.2 to 2.5 to fit with playersize change in some previous UT4 patch
    * Added settings to only convert some manually specified classes
    * u1, u2, ut: fixed bad lift destination not being scaled with conversion map scale
    * u1, u2, ut: fixed bad converted sounds that was causing crash on play in editor sometimes
    * all: fixed some resources (mainly sounds) that were not being converted

- 0.7.2 - 14/10/2015:
    * u2/ut2003/ut2004: Fixed bad staticmesh scaling in level if "DrawScale" property set.

- 0.7.1 - 12/10/2015:
    * Fixed polygon for normal brushes with shader texture not replaced with diffuse texture

- 0.7 - 11/10/2015:
    * ut2003/ut2004: staticmeshes are now textured (note: shader textures are not fully converted,
      possibly replaced with diffuse texture)
    * ut2003/ut2004: lifts got their original staticmesh set now
      Note: you need to update original python script file 'io_import_scene_unreal_psa_psk.py'
      in C:\\Program Files\\Blender Foundation\\Blender\\<VERSION>\\scripts\\addons
      with this one (which provide fixes for .psk staticmesh import in blender)
      by Befzz from https://github.com/Befzz/blender3d_import_psk

- 0.6 - 27/09/2015:
    * u2, ut2003, ut2004, ut3: added option to export staticmeshes used in map
      (partial conversion, need to convert .psk file format to.fbx using 3d modeler (like Blender))
    * UI: added information about how to convert map after conversion
    * ut3: added TriggerVolume, DynamicTriggerVolume, Note, UTTeamPlayerStart, UTTeamWarfarePlayerStart,
      PointLightMovable, PointLightToggleable, SkyLightToggleable, SpotLightMovable, SpotLightToggleable,
      AmbientSoundSimple, AmbientSoundNonLoop, AmbientSoundSimpleToggleable
      actors for conversion
    * u2, ut2003, ut2004, ut3: fixed crash when staticmesh actor has no staticmesh set
    * ut3: added partial conversion of ReverbVolume, PhysicsVolume
    * ut3: fixed sometimes a crash when exporting level to t3d file
    * ut3: fixed AmbientSound conversion error
    * ut3: fixed SpotLight bad rotation if 3d scale is negative
    * ut3: fixed resources from map package never exported
    * ut2003, ut2004, ut3: now Trigger actor are replaced with CapsuleTrigger
    * ut2003, ut2004: SunLight converted to DirectionalLight
    * ut2003, ut2004, ut3: fixed lift destination (since new UT4 version automatically scales it with 3d scale of lift mesh)
    * ut2004: increased light brightness and radius
    * ut3: removed useless warnings for actors ModelComponent, ForcedReachSpec

- 0.5 - 05/09/2015:
    * [UT99] Added partial support for conversion of UT99 assault maps with UTA resurgence mod
      (https://trello.com/b/Jtvc23S1/uta-resurgence)
    * [UT99] Replaced SmokeGenerator with Blueprint_Effect_Smoke_C blueprint actor
    * Added 2.1875 scale factor in conversion settings
    * Fixed never-ending (stuck) conversion in some rare-cases
    * Fixed brush of movers no longer being converted (<actorname>_brush actors)
    * Fixed duplicated mover actors
    * Now need to create Cue assets for sounds to be working
      (after sound files import select them in UE4 Editor asset browser then "Create CUE").

- 0.4.1 - 07/06/2015:
    * Fixed untextured surfaces if texture coming from level package

- 0.4.0 - 06/06/2015:
    * Added support for Unreal Tournament 2003 / 2004, Unreal 2, Unreal Tournament 3
      (note Unreal 2/UT2003/UT2004/UT3 conversion is pretty bad for some maps
      since staticmesh conversion is not yet available)
    * Added conversion for textures (note: material textures are not yet converted)
    * Automatically extract music now
    * [UI] Added advanced conversion settings
    * Fixed sounds not being exported from map package
    * Fixed frozen UI on map conversion
    * Some other fixes but can't remember

- 0.3.0 - 08/05/2015:
    * Improved lightning (brightness, lightning type, ...)
    * Sound export and conversion
    * Added LiftExit, Jumppad and BlockAll for conversion
    * Converts Triggers with "CapsuleTrigger" volume
    * Better alignment of FlagBases
    * UI: improved log display
    * Fixed crash if UT4 converter has whitespace in one of his parent folders
    * Added / Activated support for Unreal 1
    * Auto-creates big additive brush to simulate level in subtract mode and lightning importance volume for level

- 0.2.1 - 19/04/2015:
    * Fixed movers not importing correctly in UT4 editor

- 0.2.0 - 18/04/2015:
    * [UI] Added conversion log display
    * Fixed conversion crash with some maps with incorrect name prefix
    * Added support for UT99 Binary maps (.unr) as input file
    * Improved mover conversion (moving good now)
    * Added LavaZone, SlimeZone, WaterZone conversion
    * Added missing weapons conversion for
      "FlakCannon", "AutoMag", "Enforcer","doubleenforcer",
      "ImpactHammer", "ASMD", "Rifle", "Minigun", "SuperShockRifle" (instagib)
    * Added teleporter conversion
    * Added conversion for powerup "invisibility", "ut_invisibility",
      "ut_stealth", "nalifruit" (replaced with normal health),
      "bandages" (replaced with healthvial), "PowerBelt"
    * Added conversion for ammo "Sludge", "EClip", "FlakShellAmmo", "ASMDAmmo",
      "RifleAmmo", "RifleRound", "RifleShell", "Minigun", "SuperShockRifle" (instagib)
    * Removed unecessary auto-created notes for unconverted actor
      for PathNodes, InventorySpot, TranslocDest

- 0.1.0 - 13/04/2015:
    * First version with basic brush, lights and pickups conversion