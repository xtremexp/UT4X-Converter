# UT Converter Readme file

## Description

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/dec0f1a5176748a29195c99d7862339f)](https://app.codacy.com/gh/xtremexp/UT4X-Converter/dashboard)


- Version: 1.2.1
- Release date: 09/01/2023
- Author: Thomas 'XtremeXp/WinterIsComing' P.
- Download: [GitHub - Releases](https://github.com/xtremexp/UT4X-Converter/releases)
- Source Code: [GitHub](https://github.com/xtremexp/UT4X-Converter)



UT Converter is a program that helps converting maps from Unreal (Tournament) previous games
to UT3 (2007) and Unreal Tournament 4 (2015).
It might work from non unreal/ut games, but it's not tested/supported at all.



## UT games supported

Here are the different conversion possible :

- Source Game -> Dest Game (Conversion quality)
- U1/UT99/Duke Nukem Forever (2001) -> UT3/UT4 (Good)
- UT2003/2004 -> UT4 (Good)
- Unreal 2/UT3/UDK -> UT4 (Medium)
- X -> Unreal Engine 5 (Not Supported)

Notes:
- All: Custom scripts and blueprints are not converted
- All: Shader materials are not converted
- UT3: Since there is no actor possible for conversion of ZoneInfo, converted level might appear too dark sometimes
- U1/UT99 : Meshes (Unreal Engine 1/2) are not converted
- U2: Staticmeshes have bad UV
- UT3/UDK: Music is not converted
- UTx->UT4: Movers are replaced with the only UT4 mover actor, the lift, which may not suit for some kind of movers (such as doors, switches, ...)


---

## Requirements

- Windows 7/8/10/11 - 64 Bit
- UT3 or UT4 editor for output converted map
- Any Unreal (Tournament) game for input map
- Some minimum skills with unreal engine editors

## Installation and quick start

------------------------------

- Install program by clicking on .exe file
- Launch program (.exe file)
- Go to Settings and set the unreal games folders
- Convert your map in Menu: File -> Unreal (Tournament) -> Convert Map to UT4/UT3 ...

Program Update
------------------------------
- Uninstall program in windows applications configuration panel
- Install new version (.exe file)

## How to convert a map?
Check wiki page at: [UT Converter Wiki](https://github.com/xtremexp/UT4X-Converter/wiki)


## License

See license.txt file.

## Security

-  Program only access to the internet to check for updates via github api (can be disabled in settings)
-  No information is being sent over the internet
-  For conversion, program creates files either in your system temp folder or the program folder (/Documents/UT4X-Converter)

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

--Notes--:
-  UI(.fxml files) can be easily modified with [Scene Builder editor](https://gluonhq.com/products/scene-builder/)
-  ExtractTextures.exe and UtxAnalyser.exe programs have been compiled using modified source code from "UT Package Delphi Unit" by Antonio Corbero
  - Download and install [Delphi Comunity Edition](https://www.embarcadero.com/products/delphi/starter)
  - Download and extract [UT Package Delphi Unit](https://www.acordero.org/projects/unreal-tournament-package-delphi-library/)
  - Add the /src/delphi/-.dpr files where you installed UT Package Delphi Unit
  - Open ExtractTextures.dpr or UtxAnalyser.dpr with the IDE

## External programs used
These programs are being used during conversion process.

| Library / Program                     | Description                                       | Author                                                                | Website                                         |
|---------------------------------------|---------------------------------------------------|-----------------------------------------------------------------------| ----------------------------------------------- |
| Texture Toolkit<br/>(g16convert.exe)  | Unreal terrain bitmap conversion                  | Alex Stewart                                                          | http://www.foogod.com/UEdTexKit/                |
| UModel  (umodel_64.exe)               | Unreal package extractor                          | Konstantin Nosov                                                      | http://www.gildor.org/en/projects/umodel        |
| SoundExchange (sox.exe)               | Sound wave conversion                             | Multiple                                                              | https://sox.sourceforge.net/                                             |
| UTXAnalyser.exe / ExtractTextures.exe | Texture info / Texture extract<br/>(for Unreal 2) | Me / Modified code of UT Package Delphi Unit by <br/>Antonio Acordero | https://www.acordero.org/projects/unreal-tournament-package-delphi-library/                                                                         |



---

## History
- 1.2.1 - 09/01/2023
  - ut99->ut3/ut4: custom textures are now converted
  - ui: Added UT Converter logo icon + welcome unreal games screenshots
  - ui: conversion settings page is now less confusing
  - ui: changed style to dark theme
  - u1/ut99->ut3/ut4: much more textures got correct alignment now (still WIP)
  - ui: removed auto launch browser to wiki after conversion (replaced with visible link at bottom)
  - ui: decreased log level for unsupported material type (less log spam for ue3 converted maps)
  - Legal: Set licence to Attribution-NonCommercial-ShareAlike 4.0
  - Legal: Added license user agreement for install
  - all: reverted default texture export format to .tga (some textures in .png were washed out)
  - all: fixed regression default volume sound factor was 2 instead of 1
  - udk->ut4: fixed regression conversion non working
  - ut3->ut4: fixed regression bug AmbientSound actor radius conversion error
  - tech: UserConfig.json is now ApplicationConfig.json with format changed containing some game config (user will need to set again the game paths at next start in ui)

- 1.2.0 - 03/01/2023
  - Added support for Duke Nukem Forever 2001 (Experimental)
  - ui: map scale factor can now be customized
  - u1/ut99->ut3: interpactors (movers) now collide_all by default
  - u1/ut99->ut3: sheet brushes causing BSP holes are now converted into semi-solid brushes (will remove many bsp holes)
  - ut99->ut3/ut4: fixed convertor crash when converting SpectatorCam (Assault maps)
  - dependencies: now umodel is embedded in program
  - all: better lightning radius
  - ui: now opens wiki github page at end of conversion
  - ui: now program checks for updates at startup (can be desactivated in settings)
  - all: if actor filter is on and contains brush actor, the big additive brush will still be added


See history.md for complete history
