
# UT4 Converter Readme file

* Version: 0.9.1
* Release date: 25/08/2018
* Author: Thomas 'XtremeXp/WinterIsComing' P.
* Download latest released version at: [UT Forums](https://forums.unrealtournament.com/showthread.php?18198)
* Source Code: [GitHub-UT4X Converter](https://github.com/xtremexp/UT4X-Converter)
---

# Description

UT4 Converter helps converting maps from Unreal (Tournament) previous games
to Unreal Tournament 4.
---


# UT games supported
* Unreal 1
* Unreal 2
* Unreal Tournament (1999)
* Unreal Tournament 2003/2004
* Unreal Tournament 3
* UDK

Event if all unreal games are supported, since program is in early stages,
conversion quality for unreal tournament 2003/2004 and unreal tournament 3
might be pretty bad.

If you want to convert UT games to Unreal Tournament 3,
download and use UT3 Converter program instead of UT4 Converter.
---

# What is converted
Here is the conversion table for all ut games about what the program can convert.

|                | Brushes | Lightning | Sounds | Textures | Meshes | StaticMeshes | Terrain    | Base Pickups | Overall |
|----------------|---------|-----------|--------|----------|--------|--------------|------------|:------------:|:-------:|
| Unreal 1       |    ++   |     ++    |   ++   |    ++    |   No   |      N/A     |     N/A    |      ++      |   GOOD  |
| Unreal 2       |    ++   |     +     |   ++   |     +    |   N/A  |       -      |      No    |       -      |   BAD   |
| UT99           |    ++   |     ++    |   ++   |    ++    |   No   |      N/A     |     N/A    |      ++      |   GOOD  |
| UT2003/ UT2004 |    ++   |     ++    |   ++   |     +    |   N/A  |      ++      |Yes(partial)|       +      |   GOOD  |
| UT3            |    ++   |     +     |    +   |     -    |   N/A  |       +      |      No    |       +      |  MEDIUM |
| UDK            |    ++   |     +     |    +   |     +    |   N/A  |       +      |      No    |       +      |  MEDIUM |


# What is NOT converted
* Models
* Terrain (UT2004 has partial support only)
* Music (Unreal Tournament 3 only)
* All custom things (scripts and pickups)

---

# Requirements
* Windows 7/8/10 - 64 Bit Operating System
* Java 8 - https://www.java.com/fr/download/ (not yet compatible with Java 9+)
* Unreal Tournament 4 (2015) Editor
* One of the following previous UT games (or UDK):
  ° Unreal 1
  ° Unreal 2
  ° Unreal Tournament
  ° Unreal Tournament 2003
  ° Unreal Tournament 2004
  ° Unreal Tournament 3
* NConvert texture conversion program: http://www.xnview.com/en/nconvert/
* UModel Unreal resource program extractor: http://www.gildor.org/en/projects/umodel

Installation and first start
------------------------------
* Install nconvert http://www.xnview.com/en/nconvert/
* Install umodel http://www.gildor.org/en/projects/umodel
* Decompress the ZIP archive to any folder of your choice.
* Double-click on UT4-Converter-<version>.jar, the program should be launching.

If not, make sure you have installed latest Java technology version at www.java.com
and create some "run.bat" batch file with notepad that you will save in the
program folder with this command line:
"java -jar UT4-Converter-<version>.jar" (will force launching this file associating it
as java program)
Go to Settings and set the unreal games folders as well as umodel and nconvert program paths


Detailed explanations about conversion is always displayed after each end of map conversion
in program.
---

# How to convert map?

Make sure you are allowed to convert the original map (if you are not the original author for example)

For UT3/UDK maps only:
- Open a text editor (notepad for example)
- Open map with the UT3 or UDK editor.
- In menu go to "Edit" -> "Select all"
- Copy all data stuff ("Edit" -> "Copy" or Ctrl+C)
- Paste data in your text editor and save your file with a .t3d file extension

Note: this .t3d file is needed because the "ut3/udk.com batchexport" command to export
.ut3 level to .t3d file messes up the brush order unlike the copy/paste from editor.

---
# In UT4 Converter:
- Launch UT4 Converter
- Go to "Convert -> <Unreal Tournament X/Unreal X> Map"
- In the conversion settings, press "Select" and choose the map
- for ut3 only: select the .t3d file you previously created manually (see For UT3 maps only section)
- Press ok and wait while the conversion is running (it might takes several minutes)

# In UT4 Editor:
- Open the UT4 Editor
- Create new level ("File -> New level ...") and select "Empty level"

- Import sounds
-- Browse to /Maps/WIP/<MapName>-UT99/U1 path using the internal browser
-- Click on "Import" and select converted sound files from <UT4ConverterPath>/Converted/<MapName>/Sounds
-- After import, right click on all sounds imported in editor and choose "create cue"
-- Make sounds loop if needed (double click on it and set "looping" to true). Generally sounds with name containing "loop" word are loop sounds
(e.g:  "AmbOutside_Looping_waves4")

- Import Textures
-- Browse to /Maps/WIP/<MapName>-UT99/U1 path using the internal browser
-- Click on "Import" and select converted sound files from <UT4ConverterPath>/Converted/<MapName>/Textures
-- After import, right click on all texture in editor and choose "create material"

- Import StaticMeshes
-- Browse to /Maps/WIP/<MapName>-UT99/U1 path using the internal browser
-- Click on "Import" and select converted staticmeshes .obj files from <UT4ConverterPath>/Converted/<MapName>/StaticMeshes

- Import converted map
-- Open the file <UT4ConverterPath>/Converted/<MapName>/<MapName>-<X>.t3d with an advanced text editor (like notepad++)
-- Select all text (Ctrl+A) and copy(Ctrl+C)/paste(Ctrl+V) it into the editor
- Convert flat brushes that will cause bsp holes reported by the converter to staticmeshes ("Create Staticmesh" button)
- Rebuid Geometry ("Build" -> "Build Geometry")


Note:
- If after conversion your map got some bsp holes, remove all
"sheet" brushes (e.g: flat lava/water surface, they are generally colored in "green" color in UT/U1 editor).
 The converter does remove most of them, but not all of them yet.

---
# Limitations / Issues / Troubleshooting
- [UT99] Some textures may not be correctly aligned
- Flag bases/Teleporters do not import correctly if you do not have loaded previously a map
containing these actors (UE4-side bug)
- Level may appear too dark sometimes. In this case add a "PostProcessVolume", "Unbound" it
and set a cube ambient map texture.
- Extraction of sound resources only works with Windows
- Movers are replaced with lift actor which may not suit for all kind of movers (such as doors, switches, ...)
You may use the "Door" actor or "Matinee" actor (not auto-converted yet)
- Since Unreal Engine 4 does not support sheet brushes (e.g: flat lava/water surface, ...) , and as the converter
does not remove all of them, some "bsp holes" may appear (in that case try to find out all these brushes
and remove them until bsp holes disapear)

# License
- No commercial use allowed
- You can edit/share source code as well as binaries
always keeping original author credits

---
MAKE SURE YOU GOT AUTHORISATION OR ARE ALLOWED TO CONVERT MAP BEFORE DOING IT.
YOU ARE THE ONLY ONE RESPONSIBLE FOR ANY COPYRIGHT INFRINGMENT RELATIVE TO
CONVERTED MAP.

---

# How to build and run latest version ?
- Download and install latest Java 8 JDK: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
Then you will need to install one of these IDE.
- Note: UT4X converter cannot be built with JDK 9 for the moment.
---

# With [IntelliJ IDEA](https://www.jetbrains.com/idea/) (not-free):
## Retrieve project
- File -> New -> Project from Version Control -> Git
- Set "Git Repository URL" to: "https://github.com/xtremexp/UT4X-Converter"
- Click on "Clone"

## Build and Run
- Menu: "Run" -> "Edit configuration"
- Click on "+" then select "Application"
- Set "org.xtx.ut4converter.MainApp" as "Main class"
- Save "Apply"/"OK" and go to menu "Run" -> "Run.."

## Package .jar project
- Menu: "Run" -> "Edit configuration"
- Click on "+" then select "Maven"
- Set "Command line" parameter to "clean install -e"
- Save "Apply"/"OK" and go to menu "Run" -> "Run.."
- The .jar file can be found here "C:\Users\<USERNAME>\.m2\repository\org\xtx\UT4X-Converter\<VERSION>\UT4X-Converter-<VERSION>.jar"

---
## Optional (UI editing):
- Install "Java FX Scene Builder 2": http://www.oracle.com/technetwork/java/javase/downloads/javafxscenebuilder-1x-archive-2199384.html
- Select project then "File" -> "Project structure..." -> "Artifacts"
- Click on "+" and add "Java FX application" artifact
- Select UI file (.fxml) right click -> "Open in SceneBuilder"

---

# With [Netbeans IDE](https://netbeans.org/downloads/) (free, embedded UI editing tool):
- Download and install Netbeans IDE: https://netbeans.org/downloads/ ("Java SE" version works but you can download full version)
- Open Netbeans and in Menu, go to "Team -> Git -> Clone.."
- Set "Repository URL" = https://github.com/xtremexp/UT4Converter
- Click "Next" and select "Master" to get latest code (you may select other branches if you want to)
- Click "Finish" and wait while project is being imported.
Project will be saved by default in C:\Documents and Settings\<username>\My Documents\NetbeansProject\UT4Converter
- Right-Click on project and press "Run". At first time it might take several minutes to download required libraries.
Note:
Once project has been imported to keep the project updated:
Right click on project -> Team -> Pull
---

# With [Eclipse IDE](https://www.eclipse.org/ide/) (free):
- Download and install "Java IDE" version of Eclipse: https://www.eclipse.org/ide/
- Open "Eclipse" Right-click in "Package Explorer" view then "New -> Project..", "Maven" -> "check out Maven projects from SCM"
- Click on "m2e marketplace" link
- Install "m2e-git" connect and click on finish.
- Restart again Eclipse for changes to apply and go back to "New -> Project..", "Maven" -> "check out Maven projects from SCM"
- Click "Next"
- Set "SCM Url" to: "https://github.com/xtremexp/UT4X-Converter.git"
- Click "Finish"
- Right click on "UT4-Converter" project then "Run as" -> "Java application"
Note:
Once project has been imported to keep the project updated:
Right click on project -> "Git" -> "Remote" -> "Pull"
---

# Libraries / Technologies used
------------------------------
- Java 8 - http://www.java.com
- Java Vecmath library - 1.5.2 - https://java.net/projects/vecmath
- Java Image IO - 1.4.0 - https://github.com/jai-imageio/jai-imageio-core
- Apache commons IO - 2.6 - https://commons.apache.org/proper/commons-io/
- Texture Toolkit for UnrealEd by Alex Stewart - 1.0 - http://www.foogod.com/UEdTexKit/
- Java Targa image reader by Rob Grzywinski / Reality Interactive, Inc.
- Sound Exchange (Sox) - 14.4.1 - http://sox.sourceforge.net/

Converter interfaces with these external tools:
- Nconvert by Pierre-E Gougelet  - http://www.xnview.com/en/nconvert/
- UModel by Konstantin Nosov - http://www.gildor.org/en/projects/umodel
---

# History
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
...

- 0.1.0 - 13/04/2015: 
  * First version with basic brush, lights and pickups conversion

