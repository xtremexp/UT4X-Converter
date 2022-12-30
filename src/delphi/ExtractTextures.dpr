program ExtractTextures;

{$APPTYPE CONSOLE}

uses
  SysUtils,
  Classes,
  ut_packages;

var
  sr:TSearchRec;d,e,n:integer;
  package:TUTPackage;
  dirs:tstringlist;directory,fname:string;
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

  if ParamCount=0 then
  begin
    Exit;
  end;
  if ParamCount=1 then
  begin
    utxfile:= paramstr(1);
    outputfolder := directory;
  end;
  if ParamCount=2 then
  begin
    utxfile:= paramstr(1);
    outputfolder := IncludeTrailingBackslash(paramstr(2));
    useut3filename:=false;
  end;
  if ParamCount=3 then
  begin
    utxfile:= paramstr(1);
    outputfolder := IncludeTrailingBackslash(paramstr(2));
    if paramStr(3)='true' then useut3filename:=true;
    if paramStr(3)='false' then useut3filename:=false;
  end;


  //if dirs.count=0 then dirs.add (directory);

  Register2DClasses;
  package:=TUTPackage.create;
  package.initialize (utxfile);



  //for d:=0 to dirs.count-1 do
    begin
      //writeln ('Searching directory for packages '+dirs[d]);
      //e:=findfirst (dirs[d]+'*.u*',faAnyFile-faDirectory-faVolumeId,sr);
      //while e=0 do
        begin
          package:=TUTPackage.create;
          try
            writeln ('Analyzing package '+utxfile+'...');
            //writeln ('Analyzing package '+dirs[d]+sr.name+'...');
            //package.initialize (dirs[d]+sr.name);
            package.initialize (utxfile);
            for n:=0 to package.ExportedCount-1 do
              begin
                if GetUTObjectClass(package.Exported[n].UTClassName).InheritsFrom(TUTObjectClassTexture) then
                  begin
                    package.Exported[n].UTObject.ReadObject;
                    fname:=outputfolder+changefileext(sr.name,'')+'\';
                    //fname:=directory+changefileext(sr.name,'')+'\';
                    //if package.Exported[n].UTGroupName<>'' then fname:=fname+package.Exported[n].UTGroupName+'\';
                    if useut3filename=true then
                    begin
                           if package.Exported[n].UTGroupName<>''  then
                           begin
                               fname:=fname+package.Exported[n].UTGroupName+'.'+package.Exported[n].UTObjectName+'.bmp';
                               write ('   Extracting texture '+package.Exported[n].UTGroupName+'.'+package.Exported[n].UTObjectName+'.bmp');
                           end;

                           if (Length(package.Exported[n].UTGroupName))=0   then
                           begin
                                fname:=fname+package.Exported[n].UTObjectName+'.bmp';
                                write ('   Extracting texture(2) '+package.Exported[n].UTObjectName+'.bmp');
                           end;

                    end;

                    if useut3filename=false then
                    begin
                    //writeln ('NO USE UT3');
                    fname:=fname+package.Exported[n].UTObjectName+'.bmp';
                    write ('   Extracting texture '+package.Exported[n].UTObjectName+'... ');
                    end;

                    forcedirectories(extractfilepath(fname));
                    try
                    //TUTObjectClassStaticMesh(package.Exported[n].UTObject).
                      TUTObjectClassTexture(package.Exported[n].UTObject).GoodMipMap[0].AsBitmap.savetofile (fname);
                      writeln (' OK');
                    except
                      on e:exception do
                        writeln (e.message);
                    end;
                    package.Exported[n].UTObject.ReleaseObject;
                  end;
              end;
          except
            on e:exception do
              writeln ('Error!:'+e.message);
          end;
          package.free;
          //e:=findnext(sr);
        end;
      findclose(sr);
    end;

  dirs.free;

end.
