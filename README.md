# UT Converter

[![Release](https://img.shields.io/github/v/release/xtremexp/UT4X-Converter)](https://github.com/xtremexp/UT4X-Converter/releases)
[![Java CI with Maven](https://github.com/xtremexp/UT4X-Converter/actions/workflows/maven.yml/badge.svg)](https://github.com/xtremexp/UT4X-Converter/actions/workflows/maven.yml)
[![License: CC BY-NC-SA 4.0](https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg)](LICENSE)

A desktop tool for converting maps from classic Unreal (Tournament) games to
**UT3 (2007)** and **Unreal Tournament 4 (2015)**.

> **Please note:** This tool is not fully automated — finishing a converted map
> usually requires some manual work in the Unreal Editor. Maps from non-Unreal
> games may convert successfully, but this is neither tested nor supported.

## Supported conversions

| Source game | Target game | Conversion quality |
|---|---|---|
| U1 / UT99 / Duke Nukem Forever (2001) | UT3 / UT4 | Good |
| UT2003 / UT2004 | UT4 | Good |
| Unreal Championship 2 / U2 / UT3 / UDK | UT4 | Medium |
| Any game | Unreal Engine 5 | Not supported |

Users have also reported successful conversions from Star Wars Republic Commando
and Stargate SG-1: The Alliance, although these are untested and unsupported.

### Known limitations

- Custom scripts, blueprints and shader materials are **not** converted.
- U1/UT99: meshes are not converted.
- U2: static meshes have incorrect UV mapping.
- UT3: converted levels may appear too dark, as there is no direct equivalent of the ZoneInfo actor.
- UT3/UDK: music is not converted.
- UT9x → UT4: all movers are converted to the UT4 lift actor, which may not behave correctly for doors or switches.

## Requirements

- Windows 7/8/10/11 (64-bit)
- The source Unreal (Tournament) game (for the input map) and the UT3/UT4 editor (for the output map)
- A basic familiarity with Unreal Engine editors

## Getting started

1. Download and install the latest [release](https://github.com/xtremexp/UT4X-Converter/releases) (`.exe`).
2. Launch the program and point it to your game folders in **Settings**.
3. Convert a map via **File → Unreal (Tournament) → Convert Map to UT4/UT3...**.

To update to a new version, uninstall the previous one first, then install the new release.

For a detailed, step-by-step conversion guide, see the
[UT Converter Wiki](https://github.com/xtremexp/UT4X-Converter/wiki).

## Building from source

You will need [git](https://gitforwindows.org/), [Maven](https://maven.apache.org/)
and [OpenJDK 25](https://jdk.java.net/25/).

```bash
git clone https://github.com/xtremexp/UT4X-Converter
cd UT4X-Converter

mvn clean javafx:run    # compile and run the UI
mvn clean install       # build the Windows installer (.exe), output in target/package
```

Notes:
- The UI (`.fxml` files) can be edited with [Scene Builder](https://gluonhq.com/products/scene-builder/).
- `ExtractTextures.exe` and `UtxAnalyser.exe` are Delphi tools, built from the
  sources in `src/delphi/` (based on the modified
  [UT Package Delphi Unit](https://www.acordero.org/projects/unreal-tournament-package-delphi-library/)
  by Antonio Acordero) using [Delphi Community Edition](https://www.embarcadero.com/products/delphi/starter).

## External tools

The following third-party tools are bundled with the application and invoked during conversion:

| Tool | Purpose | Author |
|---|---|---|
| [g16convert](http://www.foogod.com/UEdTexKit/) | Terrain bitmap conversion | Alex Stewart |
| [UModel](http://www.gildor.org/en/projects/umodel) | Unreal package extraction | Konstantin Nosov |
| [SoX](https://sox.sourceforge.net/) | Audio conversion | Multiple |
| UTXAnalyser / ExtractTextures | U2 texture inspection and extraction | Antonio Acordero (modified) |

## License

This project is licensed under the
[Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0)](LICENSE) license.

## History

A complete changelog is available in [history.md](history.md).
