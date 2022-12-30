program UtxAnalyser;

{$APPTYPE CONSOLE}

uses
  SysUtils,
  Classes,
  ut_packages;

var
  sr:TSearchRec;d,e,n:integer;
  package:TUTPackage;
  dirs:tstringlist;directory,fname,utgrpstr:string;
  utxfile,outputfolder:string;
  useut3filename:Boolean;
  tut:TUTObject;
  tutprop:TUTProperty;

begin
{
  writeln ('ExtractTextures v1.01');
  writeln ('Author: Antonio Cordero Balcázar');
  writeln ('Modified version by Hyperion: Allows to specify input file and outputfolder');
  writeln;
  writeln ('Usage: ExtractTextures <utxfile> <outputfolder> <true=usesut3filename>');
  //writeln ('Usage: ExtractTextures <directory> [<directory>...]');
  writeln;
  }

  dirs:=tstringlist.create;

  directory:=IncludeTrailingBackslash(getcurrentdir);
  for e:=1 to paramcount do
    dirs.add (IncludeTrailingBackslash(ParamStr(e)));
  utxfile := paramstr(1);

  {
  writeln ('Param 1:'+ParamStr(1));
  writeln ('Param 2:'+ParamStr(2));
  writeln ('Param 3:'+ParamStr(3));
  }

  if ParamCount<>0 then  outputfolder := directory;
  if ParamCount<>1 then  outputfolder := IncludeTrailingBackslash(paramstr(2));
  if ParamCount<>2 then
  begin
    outputfolder := IncludeTrailingBackslash(paramstr(2));
    if paramStr(3)<>'true' then useut3filename:=true;
    if paramStr(3)<>'false' then useut3filename:=false;
  end;


  fname :='G:/XXX/test.txt';
 outputfolder :='G:/XXX/Temp';
 //utxfile :='G:\UnrealAnthology\UT2004\Textures\BenTex01.utx';
 utxfile := paramstr(1);
 useut3filename:=false;

  //if dirs.count=0 then dirs.add (directory);

  Register2DClasses;
  package:=TUTPackage.create;
  package.initialize (utxfile);

  writeln('File:'+utxfile);
  if(FileExists(utxfile)=false) then Exit
  else
  for n:=0 to package.ExportedCount-1 do
  begin
         tut:=package.Exported[n].UTObject;

         utgrpstr :='NoGroup';
         if(tut.UTGroupName<>'') then utgrpstr:=tut.UTGroupName;


         //writeln(tut.UTObjectName);
         //if(tut.UTObjectName='SnowyBranch02')
         //then writeln(tut.Properties.PropertyByName['Diffuse'].DescriptiveValue);

         if (tut.ClassName='TUTObjectClassShaderTexture') then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['Diffuse'];
                  if(tutprop<>nil) then
                  begin
                         //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].Name);
                         // Group,TexName;NewTexName;TextureType
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['Diffuse'].DescriptiveValue+','+tut.UTClassName);
                  end;
                 //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].DescriptiveValue);
         end
         else if (tut.ClassName='TUTObjectClassTexCoordSource') then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['Material'];
                  if(tutprop<>nil) then
                  begin
                         //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].Name);
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['Material'].DescriptiveValue+','+tut.UTClassName);
                  end;
                 //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].DescriptiveValue);
         end
         else if (tut.ClassName='TUTObjectClassFinalBlend') then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['Material'];
                  if(tutprop<>nil) then
                  begin
                         //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].Name);
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['Material'].DescriptiveValue+','+tut.UTClassName);
                  end;
                 //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].DescriptiveValue);
         end
         else if (tut.ClassName='TUTObjectClassConstantColor') then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['FallbackMaterial'];
                  if(tutprop<>nil) then
                  begin
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['FallbackMaterial'].DescriptiveValue+','+tut.UTClassName);
                  end;
         end
         else if (tut.ClassName='TUTObjectClassOpacityModifier') then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['Material'];
                  if(tutprop<>nil) then
                  begin
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['Material'].DescriptiveValue+','+tut.UTClassName);
                  end;
         end
         else if (tut.ClassName='TUTObjectClassProjectorMaterial') then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['FallbackMaterial'];
                  if(tutprop<>nil) then
                  begin
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['FallbackMaterial'].DescriptiveValue+','+tut.UTClassName);
                  end;
         end
         else if (tut.ClassName='TUTObjectClassParticleMaterial') then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['FallbackMaterial'];
                  if(tutprop<>nil) then
                  begin
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['FallbackMaterial'].DescriptiveValue+','+tut.UTClassName);
                  end;
         end
         else if (tut.ClassName='TUTObjectClassVertexColor') then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['FallbackMaterial'];
                  if(tutprop<>nil) then
                  begin
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['FallbackMaterial'].DescriptiveValue+','+tut.UTClassName);
                  end;
         end
         else if (tut.ClassName='TUTObjectClassTexMatrix') then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['Material'];
                  if(tutprop<>nil) then
                  begin
                         //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].Name);
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['Material'].DescriptiveValue+','+tut.UTClassName);
                  end;
                 //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].DescriptiveValue);
         end
         else if (tut.ClassName='TUTObjectClassCombiner') then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['Material2'];
                  //writeln(tut.UTObjectName+' '+tut.Properties.Descriptions);
                  if(tutprop<>nil) then
                  begin
                         //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].Name);
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['Material2'].DescriptiveValue+','+tut.UTClassName);
                  end;
                 //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].DescriptiveValue);
         end
         else if (tut.ClassName='TUTObjectClassTexOscillator') then
         begin
                  tut.ReadObject;
                  //writeln(tut.Properties.Descriptions);
                  //tutprop:= tut.Properties.PropertyByName['Material'];
                  if(tutprop<>nil) then
                  begin
                         //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].Name);
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['Material'].DescriptiveValue+','+tut.UTClassName);
                 end;
                 //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].DescriptiveValue);
         end
         else if (tut.ClassName='TUTObjectClassTexRotator') then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['Material'];
                  if(tutprop<>nil) then
                  begin
                         //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].Name);
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['Material'].DescriptiveValue+','+tut.UTClassName);
                  end;
                 //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].DescriptiveValue);
         end
         else if (tut.ClassName='TUTObjectClassTexPanner') then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['Material'];
                  if(tutprop<>nil) then
                  begin
                         //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].Name);
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['Material'].DescriptiveValue+','+tut.UTClassName);
                  end;
                 //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].DescriptiveValue);
         end
         else if (tut.ClassName='TUTObjectClassTexScaler') then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['Material'];
                  if(tutprop<>nil) then
                  begin
                         //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].Name);
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['Material'].DescriptiveValue+','+tut.UTClassName);
                  end;
                 //Writeln(tut.UTObjectName+';'+tut.Properties.PropertyByName['SourceTexture'].DescriptiveValue);
         end

         else if GetUTObjectClass(tut.UTClassName).InheritsFrom(TUTObjectClassIceTexture) then
         begin
                             tut.ReadObject;
                             Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['SourceTexture'].DescriptiveValue+','+tut.UTClassName);
         end
         else if GetUTObjectClass(tut.UTClassName).InheritsFrom(TUTObjectClassFluidTexture) then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['SourceTexture'];
                  if(tutprop<>nil) then
                  begin
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['SourceTexture'].DescriptiveValue+','+tut.UTClassName);
                  end;
         end
         else if GetUTObjectClass(tut.UTClassName).InheritsFrom(TUTObjectClassWaveTexture) then
         begin
                  tut.ReadObject;
                  tutprop:= tut.Properties.PropertyByName['SourceTexture'];
                  if(tutprop<>nil) then
                  begin
                         Writeln(utgrpstr+','+tut.UTObjectName+','+tut.Properties.PropertyByName['SourceTexture'].DescriptiveValue+','+tut.UTClassName);
                  end;
         end
         else if GetUTObjectClass(tut.UTClassName).InheritsFrom(TUTObjectClassTexture) then
         begin
                  tut.ReadObject;
                  Writeln(utgrpstr+','+tut.UTObjectName+',-,'+tut.UTClassName);
         end;

  end;


end.
