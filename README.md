# UT Converter Readme file

## Description

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/dec0f1a5176748a29195c99d7862339f)](https://app.codacy.com/manual/xtremexp/UT4X-Converter?utm_source=github.com&utm_medium=referral&utm_content=xtremexp/UT4X-Converter&utm_campaign=Badge_Grade_Settings)


* Version: 1.1.1
* Release date: 22/12/2022
* Author: Thomas 'XtremeXp/WinterIsComing' P.
* Download: [GitHub - Releases](https://github.com/xtremexp/UT4X-Converter/releases)
* Source Code: [GitHub](https://github.com/xtremexp/UT4X-Converter)

  

UT Converter is a program that helps converting maps from Unreal (Tournament) previous games
to UT3 (2007) and Unreal Tournament 4 (2015).
It might work from non unreal/ut games, but it's not tested/supported at all.



## UT games supported

Here are the different conversion possible :

- Source Game -> Dest Game (Conversion quality)
- Unreal 1 -> UT3/UT4 (Good)
- Unreal 2 -> UT4 (Medium)
- Unreal Tournament (1999) -> UT3/UT4 (Good)
- Unreal Tournament 2003/2004 -> UT4 (Good)
- Unreal Tournament 3 -> UT4 (Medium)
- UDK -> UT4 (Medium)
- Unreal Engine 5 -> Not Supported

Notes:
* All: Custom scripts and blueprints are not converted
* All: Shader materials are not converted
* UT3: Since there is no actor possible for conversion of ZoneInfo, converted level might appear too dark sometimes
* U1/UT99 : Meshes (Unreal Engine 1/2) are not converted
* UT99 : Custom textures are not converted
* U2: Staticmeshes have bad UV
* UT3/UDK: Music is not converted
* UTx->UT4: Movers are replaced with the only UT4 mover actor, the lift, which may not suit for some kind of movers (such as doors, switches, ...)


---

## Requirements

* Windows 7/8/10/11 - 64 Bit
* UT3 or UT4 editor for output converted map
* Any Unreal (Tournament) game for input map
* Some minimum skills with unreal engine editors

## Installation and quick start

------------------------------

* Install program by clicking on .exe file
* Launch program (.exe file)
* Go to Settings and set the unreal games folders
* Convert your map in Menu: File -> Unreal (Tournament) -> Convert Map to UT4/UT3 ...

Program Update
------------------------------
* Uninstall program in windows applications configuration panel
* Install new version (.exe file)

## How to convert a map?
Check wiki page at: [UT Converter Wiki](https://github.com/xtremexp/UT4X-Converter/wiki)


## License

See license.txt file.

---

## Build and run from source code

- Install [git](https://gitforwindows.org/) 
- Install [maven](https://maven.apache.org/)
- Clone project using this command line:
> git clone https://github.com/xtremexp/UT4X-Converter
- Download and install [OpenJDK19](https://jdk.java.net/19/): 
- Build and run with maven command :
> mvn clean javafx:run
- Package application with maven command (.exe install file will be generated in <project_path>/target/package folder) :
> mvn clean install

**Note**: UI(.fxml files) can be easily modified with [Scene Builder editor](https://gluonhq.com/products/scene-builder/)

## External programs used
These programs are being used during conversion process.

| Library / Program                         | Description                        | Author                                    | Website                                         |
| ----------------------------------------- | ---------------------------------- | ----------------------------------------- | ----------------------------------------------- |
| Texture Toolkit                           | Unreal terrain bitmap conversion   | Alex Stewart                              | http://www.foogod.com/UEdTexKit/                |
| UModel <u>(not embedded with program)</u> | Unreal package extractor           | Konstantin Nosov                          | http://www.gildor.org/en/projects/umodel        |



---

## History
- 1.1.X
  * dependencies: now umodel is embedded in program
  
- 1.1.1 - 22/12/2022
  * u2/ut2003/4->UT4: fixed menu to convert to UT4 not available
  * tech: upgraded to latest java version (17->19)

- 1.1.0 - 21/12/2022
  * Added Unreal 1/UT99 -> UT3 conversion
  * UE1/UE2: fixed no sound actor created from actors with sound properties (e.g Lights)
  * all: group property is now properly converted
  * u1: now checks if patch from oldunreal.com is installed prior to conversion (ucc.exe needed)
  * ui: renamed program from UT4X Converter to UT Converter since it helps converting not to UT4 only
  * ui: added menu entry to github project wiki page

See history.md for complete history
