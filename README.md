
UT4 Converter Readme file
------------------------------

- Version: 0.2.0 (in progress)
- Author: XtremeXp
- Release Date: 13/04/2015
- Download 0.1.0 version at: http://utforums.epicgames.com/showthread.php?t=588848
- Source Code: https://github.com/xtremexp/UT4Converter (license undetermined yet)


Description
------------------------------
UT4 Converter helps converting
Unreal Tournament (1999) maps to Unreal Tournament (2015)

UT games supported
------------------------------
UT4 Converter only convert the following game for the moment:
- Unreal Tournament (1999) -> Unreal Tournament (2015)

If you want to convert UT games to Unreal Tournament 3,
please download and use UT3 Converter program instead of UT4 Converter.

What is converted
------------------------------
- Brushes (mostly good but textures alignement may not be correct sometimes)
- Movers (partial)
- Pick-ups (most of them)
- Lightning (color good but brightness a bit 'high', also note that "ZoneInfo" actors from UT99
are not converted)

What is NOT converted
------------------------------
- Model brushes
- Textures
- Sounds
- Music
- All custom scripts
- Some special actors such as TriggerEvent, ...

Requirements
------------------------------

Operating System:
Windows 7 / Vista / 8 - 64 Bit

Unreal Tournament (1999) game

Java:
- Minimum Version required: 1.8.0_40
- Installation: www.java.com 
	
Installation and first start
------------------------------
Decompress the ZIP archive to any folder of your choice.
Double-click on UT4-Converter-0.1.0.jar, the program should be launching.

If not, make sure you have installed latest Java tecknology version at www.java.com
and create some "run.bat" batch file with notepad that you will save in the 
program folder with this commande line:
"java -jar UT4-Converter-0.1.0.jar" (will force launching this file associating it 
as java program)

How to convert map?
------------------------------
Make sure you are allowed to convert the original map (if you are not the original author for example)

In UT99 Editor:
- Open the Unreal Tournament (1999) Editor
- Open the map you want to convert 
(note if it does not seems to load (on Windows Vista/7/8/+), copy map to some folder without spaces and re-open)
- Export and save map to Unreal Text map using menu "File -> Export"

In UT4 Converter:
- Launch UT4 Converter
- Go to "Convert -> UT99 Map"
- Select the .t3d map file you have just saved before and press OK.

In UT4 Editor:
- Open the UT4 Editor
- Create new level ("File -> New level ...") and select "Empty level"
- Create an additive brush to simulate subtractive level: (Unreal Engine 4 does not support levels in "Subtract" mode)
-- Go to "Modes" panel (left top of screen)
-- Go to "BSP"
-- Drag and drop a "Box" in the map
- Import converted map
-- "File -> Import ..."
-- Select the file and click on OK to import the map
- Re-size the additive brush so the entire level fits in the brush
- Rebuid Geometry ("Build" -> "Build Geometry")

Optional:
- Select all surfaces of the brush in additive mode added manually (select one surface + press "Ctrl+B"
Since we don't need to have accurate lightmap of this huge brush because
its surfaces will never be shown to player, change "LightMap Resolution" from 32 to 1024.
This operation will decrease a lot filezise of map!

Note:
- For some maps you may have to use subtractive brushes before importing level.
This depends mainly on layout of map (outdoor map such as CTF-Face and so on ...)
- If after conversion your maps got lot of bsp holes, remove all
"sheet" brushes (e.g: flat lava/water surface). The converter does remove most of them,
but not all of them.


Limitations / Issues
------------------------------
- Bug with Ctf flagbases not being converted

Copyright / License
------------------------------
License to be determined / set. Just feel free to browse code or/and 
contribute !

MAKE SURE YOU GOT AUTHORISATION OR ARE ALLOWED TO CONVERT MAP BEFORE DOING IT.
YOU ARE THE ONLY ONE RESPONSIBLE FOR ANY COPYRIGHT INFRINGMENT RELATIVE TO 
CONVERTED MAP.

How to build and run latest version ?
------------------------------
- Download and install Java 8 JDK (Java Developement Kit)
- Download and install Netbeans IDE: https://netbeans.org/downloads/ ("Java SE" version works but you can download full version)
- Open Netbeans and in Menu, go to "Team -> Git -> Clone.."
- Set "Repository URL" = https://github.com/xtremexp/UT4Converter
- Click "Next" and select "Master" to get latest code (you may select other branches if you want to)
- Click "Finish" and wait while project is being imported.
Project will be saved by default in C:\Documents and Settings\<username>\My Documents\NetbeansProject\UT4Converter
- Right-Click on project and press "Run". At first time it might take several minutes to download required librairies.

Note:
Since it's a maven project, importing project with Eclipse IDE should work 
but it has not been yet tested.

Librairies / Technologies used
------------------------------
- Java 1.8.0_40
- Java 3D Vecmath - For handling brush data - https://vecmath.dev.java.net/

History
------------------------------
- 0.2 (in progress): 
--[UI] Added conversion log display
--[UT99->UT4] Fixed conversion crash with some maps with incorrect name prefix
[UT99->UT4] Added support for UT99 Binary maps (.unr) as input file
[UT99->UT4] Improved mover conversion (movement)
[UT99->UT4] Added missing weapons conversion for
"FlakCannon", "AutoMag", "Enforcer","doubleenforcer",
"ImpactHammer", "ASMD", "Rifle", "Minigun", "SuperShockRifle" (instagib)
[UT99->UT4] Removed unecessary auto-created notes for unconverted actor
for PathNodes, InventorySpot
...

- 0.1.0 - 13/04/2015: 
[UT99->UT4] First version with basic brush, lights and pickups conversion

