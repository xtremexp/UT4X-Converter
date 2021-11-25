# UT4X Converter Readme file

## Description

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/dec0f1a5176748a29195c99d7862339f)](https://app.codacy.com/manual/xtremexp/UT4X-Converter?utm_source=github.com&utm_medium=referral&utm_content=xtremexp/UT4X-Converter&utm_campaign=Badge_Grade_Settings)


* Version: 1.0.3
* Release date: 25/11/2021
* Author: Thomas 'XtremeXp/WinterIsComing' P.
* Download: [UT Forums](https://www.epicgames.com/unrealtournament/forums/unreal-tournament-development/ut-development-level-design/9285)
* Source Code: [GitHub-UT4X Converter](https://github.com/xtremexp/UT4X-Converter)

  

UT4X Converter is a program that helps converting maps from Unreal (Tournament) previous games
to Unreal Tournament 4.



## UT games supported

* Unreal 1
* Unreal 2
* Unreal Tournament (1999)
* Unreal Tournament 2003/2004
* Unreal Tournament 3
* UDK

Event if all unreal games are supported, since program is in early stages, conversion quality for unreal tournament 2003/2004 and unreal tournament 3 might be pretty bad.

If you want to convert UT games to Unreal Tournament 3, download and use UT3 Converter program instead of UT4 Converter.

## What is converted

Overall conversion quality for games.

|                | Brushes | Lightning | Sounds | Textures | Meshes            | Static Meshes | Terrain | Base Pickups | Overall |
| -------------- | ------- | --------- | ------ | -------- | ----------------- | ------------- | ------- | :----------: | :-----: |
| Unreal 1       | ++      | ++        | ++     | ++       | Not supported yet | N/A           | N/A     |      ++      |  GOOD   |
| Unreal 2       | ++      | +         | ++     | +        | N/A               | + (2)         | +       |      -       | MEDIUM  |
| UT99           | ++      | ++        | ++     | ++ (1)   | Not supported yet | N/A           | N/A     |      ++      |  GOOD   |
| UT2003/ UT2004 | ++      | ++        | ++     | +        | N/A               | ++            | +       |      +       |  GOOD   |
| UT3            | ++      | +         | +      | -        | N/A               | +             | ++      |      +       | MEDIUM  |
| UDK            | ++      | +         | +      | +        | N/A               | +             | ++      |      +       | MEDIUM  |

Notes:

1: Custom textures not supported (due to .t3d files not having package information)

2: Unreal 2 staticmeshes have bad UV texturing

## What is NOT converted / limitations

- Flag bases/Teleporters do not import correctly if you do not have loaded previously a map
  containing these actors (UE4-side bug)
- Level may appear too dark sometimes. In this case add a "PostProcessVolume", "Unbound" it
  and set a cube ambient map texture.
- Movers are replaced with lift actor which may not suit for all kind of movers (such as doors, switches, ...)
  You may use the "Door" actor or "Matinee" actor to fit with your needs.

| Game     | #1                                     | #2                                                     |
| -------- | -------------------------------------- | ------------------------------------------------------ |
| Any      | Shader materials not converted (1)     | Custom scripts and pickups                             |
| Unreal 1 | Meshes / models not converted          | Bad texture alignment when texture originally rotated. |
| Unreal 2 | Bad texture alignment for staticmeshes |                                                        |
| UT99     | Meshes / models not converted          | Bad texture alignment when texture originally rotated. |
| UT2003/4 |                                        |                                                        |
| UT3      | Music not converted                    |                                                        |
| UDK      | Music not converted                    |                                                        |

1: Shader materials replaced with original material if possible which can lead to some scaling material issues on surfaces.



---

## Requirements

* Windows 7/8/10/11 - 64 Bit Operating System
* Unreal Tournament 4 (2015) Editor
* One of the following previous UT games (or UDK):
  * Unreal 1
  * Unreal 2
  * Unreal Tournament
  * Unreal Tournament 2003
  * Unreal Tournament 2004
  * Unreal Tournament 3
* UModel Unreal Engine package extractor: http://www.gildor.org/en/projects/umodel

Installation and first start
------------------------------

* Install umodel http://www.gildor.org/en/projects/umodel
* Decompress the ZIP archive to any folder of your choice.
* Double-click on UT4-Converter-<version>.jar, the program should be launching.

Go to Settings and set the unreal games folders as well as umodel program paths


Detailed explanations about conversion is always displayed after each end of map conversion

in program.

## How to convert a map?

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

<u>In UT4 Converter:</u>

- Launch UT4 Converter
- Go to "Convert -> <Unreal Tournament X/Unreal X> Map"
- In the conversion settings, press "Select" and choose the map
- for ut3 only: select the .t3d file you previously created manually (see For UT3 maps only section)
- Press ok and wait while the conversion is running (it might takes several minutes)

<u>In UT4 Editor:</u>

- Open the UT4 Editor
- Create new level ("File -> New level ...") and select "Empty level"

- Import sounds
  - Browse to /Maps/WIP/<MapName>-UT99/U1 path using the internal browser
  - Click on "Import" and select converted sound files from <UT4ConverterPath>/Converted/<MapName>/Sounds
  - After import, right click on all sounds imported in editor and choose "create cue"
  - Make sounds loop if needed (double click on it and set "looping" to true). Generally sounds with name containing "loop" word are loop sounds
  (e.g:  "AmbOutside_Looping_waves4")

- Import Textures
  - Browse to /Maps/WIP/<MapName>-UT99/U1 path using the internal browser
  - Click on "Import" and select converted sound files from <UT4ConverterPath>/Converted/<MapName>/Textures
  - After import, right click on all texture in editor and choose "create material"

- Import StaticMeshes
  - Browse to /Maps/WIP/<MapName>-UT99/U1 path using the internal browser
  - Click on "Import" and select converted staticmeshes .obj files from <UT4ConverterPath>/Converted/<MapName>/StaticMeshes

- Import converted map
  - Open the file <UT4ConverterPath>/Converted/<MapName>/<MapName>-<X>.t3d with an advanced text editor (like notepad++)
  - Select all text (Ctrl+A) and copy(Ctrl+C)/paste(Ctrl+V) it into the editor
- Convert flat brushes that will cause bsp holes reported by the converter to staticmeshes ("Create Staticmesh" button)
- Rebuid Geometry ("Build" -> "Build Geometry")


Note:

- If after conversion your map got some bsp holes, remove all
  "sheet" brushes (e.g: flat lava/water surface, they are generally colored in "green" color in UT/U1 editor).
   The converter does remove most of them, but not all of them yet.



## License

See license.txt file.

---

## How to build and run latest version ?

- Download and install OpenJDK17: https://jdk.java.net/17/ 

- Run with maven command : "mvn clean javafx:run"


Optional (UI editing with [IntelliJ IDEA](https://www.jetbrains.com/idea/) (not-free)) :

- Install "Java FX Scene Builder 2": http://www.oracle.com/technetwork/java/javase/downloads/javafxscenebuilder-1x-archive-2199384.html
- Select project then "File" -> "Project structure..." -> "Artifacts"
- Click on "+" and add "Java FX application" artifact
- Select UI file (.fxml) right click -> "Open in SceneBuilder"



### With [Netbeans IDE](https://netbeans.org/downloads/) (free, embedded UI editing tool):

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



### With [Eclipse IDE](https://www.eclipse.org/ide/) (free):

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

## External programs used
These programs are used during conversion process.

| Library / Program                         | Description                        | Author                                    | Website                                         |
| ----------------------------------------- | ---------------------------------- | ----------------------------------------- | ----------------------------------------------- |
| Texture Toolkit                           | Unreal terrain bitmap conversion   | Alex Stewart                              | http://www.foogod.com/UEdTexKit/                |
| UModel <u>(not embedded with program)</u> | Unreal package extractor           | Konstantin Nosov                          | http://www.gildor.org/en/projects/umodel        |



---

## History
- 1.0.3 - 25/11/2021
 * all: textures are now exported by default to .png (better quality and alpha layer) instead of .tga
 * all: flat brushes (water, fire torches, ...) that were causing bsp holes are no longer deleted but converted instead into semi-solid brushes
 * all: mover brushes are now converted to semi-solid brushes instead of solid brushes to prevent potential bsp holes
 * all: added extra scale options for conversion
 * ut3: now correctly handling "SoundSlots" property
 * ut3: fixed some resources not exported from map package
 * all: fixed lift destination for non moving movers
 * all: fixed some potential resources not exported
 * ut99: fixed bad export package name for textures in map package causing no texture match in UT4 import and bad scaling
 * ui: fixed some alignment issues, added github project link in menu
 * ci: fixed github build
 * tech: migrated from Java 13 to Java 17

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

See history.md for complete history
