
UT4 Converter Readme file
------------------------------

- Version: 0.7.1
- Author: XtremeXp
- Release Date: 12/10/2015
- Download latest released version at: https://forums.unrealtournament.com/showthread.php?18198
- Source Code: https://github.com/xtremexp/UT4Converter


Description
------------------------------
UT4 Converter helps converting maps from Unreal (Tournament) previous games
to Unreal Tournament 4.


UT games supported
------------------------------
- Unreal 1
- Unreal 2
- Unreal Tournament (1999)
- Unreal Tournament 2003/2004
- Unreal Tournament 3

Event if all unreal games are supported, since program is in early stages,
conversion quality for unreal tournament 2003/2004 and unreal tournament 3
might be pretty bad.

If you want to convert UT games to Unreal Tournament 3,
download and use UT3 Converter program instead of UT4 Converter.

What is converted
------------------------------
Here is the conversion table for all ut games about what the program can convert.

|                | Brushes | Lightning | Sounds | Textures | Meshes | StaticMeshes | Terrain    | Base Pickups | Overall |
|----------------|---------|-----------|--------|----------|--------|--------------|------------|:------------:|:-------:|
| Unreal 1       |    ++   |     ++    |   ++   |    ++    |   No   |      N/A     |     N/A    |      ++      |   GOOD  |
| Unreal 2       |    ++   |     ++    |   ++   |     +    |   N/A  |      ++      |      No    |       -      |  MEDIUM |
| UT99           |    ++   |     ++    |   ++   |    ++    |   No   |      N/A     |     N/A    |      ++      |   GOOD  |
| UT2003/ UT2004 |    ++   |     ++    |   ++   |     +    |   N/A  |      ++      |      No    |       +      |   GOOD  |
| UT3            |    ++   |     +     |    +   |    No    |   N/A  |       +      |      No    |       +      |   BAD   |


Note:
UT99/U1 Lightning: For Unreal Tournament (99) and Unreal 1, zoneinfo actor does not exist in UT4, since then
StaticMesh conversion is not yet operational. Since UT3 is nearly full of staticmeshes, conversion
is pretty bad. (same note for some ut2004 maps)

What is NOT converted
------------------------------
- Models
- Terrain
- Music (Unreal Tournament 3 only)
- All custom things (scripts and pickups)

Note: staticmesh conversion is not fully automatic.
You need to convert them manually using Blender software.
Converter will do the job to get the right references and textures applied.

Requirements
------------------------------
* Windows 7 / Vista / 8 - 64 Bit Operating System
* Java > 1.8.0_40 . www.java.com
* Unreal Tournament 4 (2015) Editor
* One of the following previous UT games:
  * Unreal 1
  * Unreal 2
  * Unreal Tournament
  * Unreal Tournament 2003
  * Unreal Tournament 2004
  * Unreal Tournament 3
* NConvert texture conversion program: http://www.xnview.com/en/nconvert/
* UModel Unreal resource program extractor: http://www.gildor.org/en/projects/umodel

Installation and first start
------------------------------
- Install nconvert http://www.xnview.com/en/nconvert/
- Install umodel http://www.gildor.org/en/projects/umodel
- Decompress the ZIP archive to any folder of your choice.
- Double-click on UT4-Converter-<version>.jar, the program should be launching.

If not, make sure you have installed latest Java technology version at www.java.com
and create some "run.bat" batch file with notepad that you will save in the 
program folder with this command line:
"java -jar UT4-Converter-<version>.jar" (will force launching this file associating it 
as java program)
Go to Settings and set the unreal games folders as well as umodel and nconvert program paths

- For staticmesh conversion:
  * Install Blender software (www.blender.org)
  * Download https://github.com/Befzz/blender3d_import_psk zip archive file
  * Overwrite io_import_scene_unreal_psa_psk.py python script file in blender folder:
  * C:\\Program Files\\Blender Foundation\\Blender\\<VERSION>\\scripts\\addons
  
Detailed explanations about conversion is always displayed after each end of map conversion 
in program.


How to convert map?
------------------------------
Make sure you are allowed to convert the original map (if you are not the original author for example)

In UT4 Converter:
- Launch UT4 Converter
- Go to "Convert -> <Unreal Tournament X/Unreal X> Map"
- In the conversion settings, press "Select" and choose the map
- Press ok and wait while the conversion is running (it might takes several minutes)

In UT4 Editor:
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

- Import converted map
-- In menu, go to "File -> Import ..."
-- Select the file <UT4ConverterPath>/Converted/<MapName>/Level/<MapName>-<X>/U1.t3d
-- click on OK to import the map
- Rebuid Geometry ("Build" -> "Build Geometry")



Note:
- If after conversion your map got some bsp holes, remove all
"sheet" brushes (e.g: flat lava/water surface, they are generally colored in "green" color in UT/U1 editor).
 The converter does remove most of them, but not all of them yet.


Limitations / Issues / Troubleshooting
------------------------------
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

License
------------------------------
- No commercial use allowed
- You can edit/share source code as well as binaries
always keeping original author credits


MAKE SURE YOU GOT AUTHORISATION OR ARE ALLOWED TO CONVERT MAP BEFORE DOING IT.
YOU ARE THE ONLY ONE RESPONSIBLE FOR ANY COPYRIGHT INFRINGMENT RELATIVE TO 
CONVERTED MAP.

How to build and run latest version ?
------------------------------
With Netbeans IDE:
- Download and install latest Java 8 JDK: http://www.oracle.com/technetwork/java/javase/downloads/index.html
- Download and install Netbeans IDE: https://netbeans.org/downloads/ ("Java SE" version works but you can download full version)
- Open Netbeans and in Menu, go to "Team -> Git -> Clone.."
- Set "Repository URL" = https://github.com/xtremexp/UT4Converter
- Click "Next" and select "Master" to get latest code (you may select other branches if you want to)
- Click "Finish" and wait while project is being imported.
Project will be saved by default in C:\Documents and Settings\<username>\My Documents\NetbeansProject\UT4Converter
- Right-Click on project and press "Run". At first time it might take several minutes to download required libraries.

With Eclipse IDE:
- Download and install latest Java 8 JDK: http://www.oracle.com/technetwork/java/javase/downloads/index.html
- Download and install "Eclipse for Java Developers": https://eclipse.org/downloads/
- Open "Eclipse" Right-click in "Package Explorer" view then "New -> Project..", "Maven" -> "check out Maven projects from SCM"
- Click on "m2e marketplace" link
- Install "m2e-git" connect and click on finish. 
- Restart again Eclipse for changes to apply and go back to "New -> Project..", "Maven" -> "check out Maven projects from SCM"
- Click "Next"
- Set "SCM Url" to: "https://github.com/xtremexp/UT4Converter.git"
- Click "Finish"
- Right click on "UT4-Converter" project then "Run as" -> "Java application"


Libraries / Technologies used
------------------------------
- Java 1.8.0_40 - http://www.java.com
- Java Vecmath library - 1.3.1 - https://java.net/projects/vecmath
- Texture Toolkit for UnrealEd by Alex Stewart - 1.0 - http://www.foogod.com/UEdTexKit/
- Java Targa image reader by Rob Grzywinski / Reality Interactive, Inc.

Converter interfaces with these external tools:
- Nconvert by Pierre-E Gougelet  - http://www.xnview.com/en/nconvert/
- UModel by Konstantin Nosov - http://www.gildor.org/en/projects/umodel

History
------------------------------
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

