===============================================================================
                       The Texture Toolkit for UnrealEd
                                 Version 1.0
===============================================================================

Welcome!  This package contains a set of tools to assist with importing and
exporting textures from UnrealEd, the editor for the Unreal series of video
game engines.

UnrealEd supports exporting and importing textures (images) in a variety of
formats, but it does have some limitations and a couple of bugs which can make
the process of working with UnrealEd textures in external graphics applications
substantially more difficult than it needs to be.  The utilities in this
package are an attempt to fill in some of the gaps left by UnrealEd's image
file support.

===============================================================================
=                             Included Utilities                              =
===============================================================================

=== TGAFIX ===

UnrealEd can export textures in several different formats.  Unfortunately, the
only format to which it can export which has the capability of including
alpha-channel information is the TGA (Targa) format.  This wouldn't be a big
problem, as most graphics applications support TGA fairly well, but even more
unfortunately, UnrealEd writes broken TGA files so that the resulting files
claim not to have an alpha channel even though they do.

Some graphics programs (wrongly) ignore the TGA file header and blithely assume
that there's an alpha channel and load the alpha data anyway, and if you happen
to use one of those graphics applications you will likely never notice this
bug, but correctly written programs pay attention to the file header and load
UnrealEd's TGA files ignoring the alpha information.  Thus, the one format
which can hold alpha information still doesn't work properly for exporting
textures with alpha channels to many other programs.

The 'tgafix' utility fixes the TGA files produced by UnrealEd to correctly
report their alpha channel, so standards-conforming graphics software can load
them properly.

=== G16CONVERT ===

UnrealEd has an internal "G16" (16-bit grayscale) image representation which it
uses for such things as terrain heightmaps.  It has the ability to import and
export these images from/to files, but only supports importing/exporting them
as 16-bit grayscale BMP files.

The problem is that there's no such thing as a 16-bit grayscale BMP file.  Such
images are not supported by the file format.  Not only does UnrealEd write the
data in a nonstandard format, but it writes it in a way that is
indistinguishable from real 16-bit color (RGB) BMP files, thus creating a BMP
file which cannot be loaded properly (as a grayscale image) by any
standards-conforming graphics program, even if it wanted to.

The 'g16convert' utility is designed to convert the (broken) G16-BMP files
created by UnrealEd into (standard) 16-bit grayscale TIFF images (why they
didn't just use TIFF in the first place I don't understand), and can also
convert them back again for easy importing back into UnrealEd after editing.

(Note that while many graphics applications can read 16-bit grayscale TIFF
files, most only work with 8-bit data internally, so editing these files in
many programs will lose much of the detail.  In order to edit these files and
retain their full detail, you will need a 16-bit capable graphics application.)

=== DDSCONVERT ===

UnrealEd internally supports the use of Microsoft DirectX Texture Compression
(DXTC) to significantly reduce the size of stored textures, and this is the way
most textures used in Unreal engine maps are stored.  UnrealEd also supports
exporting DXT textures to files, but will only write them in the DDS (Microsoft
DirectDraw Surface) file format.  Unfortunately, most other graphics
applications do not understand DDS files or DXT compression, making these
exported textures problematic to use with other programs.

The 'ddsconvert' utility will convert DXT1, DXT3, and DXT5 compressed images
(the only forms of DXTC supported by UnrealEd) from DDS files to uncompressed
images in the more widely accepted TGA format.  As TGA is also a supported
import format in UnrealEd, this also makes for an easy way to uncompress DXT
textures for storage as RGBA8 (which UnrealEd does not support directly).

Note that the 'ddsconvert' utility was mainly included for completeness, and to
provide a convenient GUI interface for this functionality which is consistent
with the other utilities in this package.  It should be sufficient for most
UnrealEd-related uses, but is still fairly simplistic.  It does not handle
DXT2/4 formats, it does not attempt to extract mipmaps, cubemaps, etc, from DDS
files, will only convert one-way from DDS to TGA, and so on.  Those looking for
more sophisticated or general-purpose tools might wish to look into Nvidia's
texture tools (http://developer.nvidia.com/object/nv_texture_tools.html), or
the Microsoft DirectX SDK
(http://msdn.microsoft.com/downloads/list/directx.asp), both of which have free
utilities for manipulating DDS/DXT information in more flexible ways.

===============================================================================
=                             Using the Programs                              =
===============================================================================

Each of the above programs can be used as a command-line utility for scripts or
those who simply prefer working that way.  Additionally, however, if you have
installed these utilities using the provided Windows installer package, you can
also perform all of the above functions directly from the Windows file browser.

To use any of these utilities from the Windows file browser, simply right-click
on a file of the appropriate type.  You will see the following new menu options
in the pop-up menu for the following file types:

  TGA  - Fix UnrealEd Export
  BMP  - Convert G16 to TIFF
  TIFF - Convert to G16
  DDS  - Convert to TGA

Choose the option you want and follow the prompts, and presto!  That's all
there is to it.

(For those curious about the EXE file names, there are two versions of each
program:  The command-line version is named <utility>.exe and will print all
output to the text console.  For each utility there is also a <utility>_w.exe
executable, which is the GUI version, intended to be invoked from the file
browser interface, and will interact with the user via Windows dialog boxes
instead of the text console.)

===============================================================================
=                                Uninstalling                                 =
===============================================================================

To uninstall this package, go to the Control Panels folder and double-click
"Add or Remove Programs".  Under that control panel, find the entry for "Texture
Toolkit for UnrealEd" and click the "Remove" button.

Alternately, go into the folder where you installed this toolkit and run the
"uninst-texkit.exe" program located there.

===============================================================================
=                                  Compiling                                  =
===============================================================================

Source code is provided for all of these utilities.  The binaries provided have
been compiled using GCC and GNU Make from the Cygwin package, all of which are
available for free download from http://www.cygwin.com/ .  The installer
package was creating using the Nullsoft Scriptable Install System, also free,
from http://nsis.sourceforge.net/ .

If you are using the above tools, you should be able to recompile all of the
executables in the package simply by going into the "src" directory and typing
'make'.  'make dist' will make the toplevel directory suitable for packaging up
as a ZIP file, and 'make installer' will make a Windows-executable installer
package.

If you opt to use other development tools to compile these utilities, I can't
help you a lot, as I probably don't have them.  These programs should all be
pretty simple, though, so they'll probably be pretty easy to compile with any
Windows compiler.  Note that you'll need to define "WIN32_GUI" to build the
"_w" (GUI) versions of things.

(Note, these packages should be reasonably cross-platform (although they do
assume a little-endian (Intel) machine architecture in some places), but
compilation has not currently been tested on any platforms other than Windows.
I wouldn't be surprised though if one could build them with little or no
tweaking under Linux, for example.  YMMV)

===============================================================================
=                                   License                                   =
===============================================================================

All of the programs contained in this package have been released into the
public domain by the author.  That means you can use them in any way for any
purpose whatsoever with no restrictions.  Have fun!

(There now, wasn't that some of the easiest legalese you've ever read?)

===============================================================================
=                            Credits and Changelog                            =
===============================================================================

Package Version: 1.0
Date:            December 4, 2004
Author:          Alex Stewart <alex@foogod.com>
URL:             http://www.foogod.com/UEdTexKit/

Changelog:

Version 1.0: December 4, 2004 - Alex Stewart <alex@foogod.com>
- Initial release
- Contains tgafix 1.0, g16convert 1.0, and ddsconvert 1.0

