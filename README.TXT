
UT4 Converter Readme file
------------------------------

Version: 0.1.0
Author: XtremeXp
Release Date: 13/04/2015
Forum Topic (UT3 forums): http://utforums.epicgames.com/showthread.php?t=588848 (some topic might be created in UT4 forums)
Source Code: Available at https://github.com/xtremexp/UT4Converter (feel free to contribute!)


Description
------------------------------
UT4 Converter helps converting
Unreal Tournament (1999) maps to Unreal Tournament (2015)

UT games supported
------------------------------
UT4 Converter only convert the following game for the moment:
X) Unreal Tournament (1999) -> Unreal Tournament (2015)

If you want to convert UT games to Unreal Tournament 3,
please download and use UT3 Converter program instead of UT4 Converter.

What is converted
------------------------------
Brushes (mostly good but textures alignement may not be correct sometimes)
Movers (partial)
Pick-ups (most of them)
Lightning (color good but brightness a bit 'high', also note that "ZoneInfo" actors from UT99
are not converted)

What is NOT converted
------------------------------
Model brushes
Textures
Sounds
Music
All custom scripts
Some special actors such as TriggerEvent, ...

Requirements
------------------------------

Operating System:
Windows 7 / Vista / 8 - 64 Bit

Unreal Tournament (1999) game

Java:
X) Minimum Version required: 1.8.0_40
X) Installation: www.java.com 
	
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
X) Open the Unreal Tournament (1999) Editor
X) Open the map you want to convert 
(note if it does not seems to load (on Windows Vista/7/8/+), copy map to some folder without spaces and re-open)
X) Export and save map to Unreal Text map using menu "File -> Export"

In UT4 Converter:
X) Launch UT4 Converter
X) Go to "Convert -> UT99 Map"
X) Select the .t3d map file you have just saved before and press OK.

In UT4 Editor:
X) Open the UT4 Editor
X) Create new level ("File -> New level ...") and select "Empty level"
X) Create an additive brush to simulate subtractive level: (Unreal Engine 4 does not support levels in "Subtract" mode)
-- Go to "Modes" panel (left top of screen)
-- Go to "BSP"
-- Drag and drop a "Box" in the map
X) Import converted map
-- "File -> Import ..."
-- Select the file and click on OK to import the map
X) Rebuid Geometry ("Build" -> "Build Geometry")
X) Re-size the additive brush so the entire level fits in the brush

Optional:
Select all surfaces of subtractive brush (select one surface + press "Ctrl+B"
Change "LightMap Resolution" from 32 to 1024.
This will save a lot of filesize space for the map ! (and outside of that brush is never displayed to player)

Note:
For some maps you may have to use subtractive brushes before importing level.
This depends mainly on layout of map (outdoor map such as CTF-Face and so on ...)


Limitation/Issues/Known bugs
------------------------------
- Ctf flagpoint not converted

Copyright / License
------------------------------
License to be determined / set. Just feel free to browse code or/and 
contribute !

MAKE SURE YOU GOT AUTHORISATION OR ARE ALLOWED TO CONVERT MAP BEFORE DOING IT.
YOU ARE THE ONLY ONE RESPONSIBLE FOR ANY COPYRIGHT INFRINGMENT RELATIVE TO 
CONVERTED MAP.


Librairies / Technologies used
------------------------------
Java 1.8.0_40
Java 3D Vecmath - For handling brush data - https://vecmath.dev.java.net/

History
------------------------------
0.1.0: First version with basic brush, lights and pickup conversion for UT99 to UT4

