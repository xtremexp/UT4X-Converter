# UT Converter Readme file

## Description

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/dec0f1a5176748a29195c99d7862339f)](https://app.codacy.com/manual/xtremexp/UT4X-Converter?utm_source=github.com&utm_medium=referral&utm_content=xtremexp/UT4X-Converter&utm_campaign=Badge_Grade_Settings)


* Version: 1.1.0
* Release date: DD/MM/2022
* Author: Thomas 'XtremeXp/WinterIsComing' P.
* Download: [GitHub - Releases](https://github.com/xtremexp/UT4X-Converter/releases)
* Source Code: [GitHub](https://github.com/xtremexp/UT4X-Converter)

  

UT Converter is a program that helps converting maps from Unreal (Tournament) previous games
to UT3 (2007) and Unreal Tournament 4 (2015).
It might work from non unreal/ut games, but it's not tested/supported at all.



## UT games supported

Here are the different games possible :

- Source Game -> Dest Game (Conversion quality)
- Unreal 1 -> UT3/UT4 (Good)
- Unreal 2 -> UT4 (Medium)
- Unreal Tournament (1999) -> UT3/UT4 (Good)
- Unreal Tournament 2003/2004 -> UT4 (Good)
- Unreal Tournament 3 -> UT4 (Medium)
- UDK -> UT4 (Medium)
- Unreal Engine 5 -> Not Supported

Notes:
* All: Custom scripts are blueprints are not converted
* All: Shader materials are not converted
* <UT3: Since there is no actor possible for conversion of ZoneInfo, converted level might appear too dark sometimes
* U1/UT99 : Meshes (Unreal Engine 1/2) are not converted
* UT99 : Custom textures are not converted
* U2: Staticmeshes have bad UV
* UT3/UDK: Music is not converted
* UTx->UT4: Movers are replaced with the only UT4 mover actor, the lift, which may not suit for some kind of movers (such as doors, switches, ...)


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
* Some minimum skills with unreal engine editors

## Installation and quick start
------------------------------

* Download umodel from http://www.gildor.org/en/projects/umodel
* Install program by clicking on .exe file
* Launch program (.exe file)
* Go to Settings and set the unreal games folders as well as the umodel.exe program path
* Convert your map in Menu: File -> Unreal (Tournament) -> Convert Map to UT4/UT3 ...


Program Update
------------------------------
* Uninstall program in windows applications configuration panel
* Install new version (.exe file)

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

<u>In UT Converter:</u>

- Launch UT Converter
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


**Note**:
- UE1/UE2: If after conversion your map got some bsp holes, remove all brushes with flat surfaces (such as torches)



## License

See license.txt file.

---

## Build and run from source code

- Install git
- Install maven
- Clone project (default master branch is the release branch and most stable, develop being used for on going developments)
> git clone https://github.com/xtremexp/UT4X-Converter
- Download and install OpenJDK17: https://jdk.java.net/17/
- Run with maven command :
> mvn clean javafx:run
- For packaging application use maven command (.exe install file will be generated in /target/package folder)
> mvn clean install

**Note**: UI(.fxml files) can be easily modified with Scene Builder editor (https://gluonhq.com/products/scene-builder/)

## External programs used
These programs are used during conversion process.

| Library / Program                         | Description                        | Author                                    | Website                                         |
| ----------------------------------------- | ---------------------------------- | ----------------------------------------- | ----------------------------------------------- |
| Texture Toolkit                           | Unreal terrain bitmap conversion   | Alex Stewart                              | http://www.foogod.com/UEdTexKit/                |
| UModel <u>(not embedded with program)</u> | Unreal package extractor           | Konstantin Nosov                          | http://www.gildor.org/en/projects/umodel        |



---

## History
- 1.1.0 - DD/MM/2022
  * Added Unreal 1/UT99 -> UT3 conversion
  * UE1/UE2: fixed no sound actor created from actors with sound properties (e.g Lights)
  * all: group property is now properly converted
  * u1: now checks if patch from oldunreal.com is installed prior to conversion (ucc.exe needed)
  * ui: renamed program from UT4X Converter to UT Converter since it helps converting not to UT4 only

See history.md for complete history
