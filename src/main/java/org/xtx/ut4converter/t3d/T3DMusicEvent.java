package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;

public class T3DMusicEvent extends T3DActor {

    private UPackageRessource song;

    private MTran transition = MTran.MTRAN_Fade;

    /**
     * How fast music transition is done
     */
    enum MTran {
        MTRAN_None, MTRAN_Instant, MTRAN_Segue, MTRAN_Fade, MTRAN_FastFade, MTRAN_SlowFade
    }

    public T3DMusicEvent(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bAffectAllPlayers", Boolean.class, Boolean.TRUE);
        registerSimpleProperty("bOnceOnly", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("bSilence", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("CdTrack", Short.class, 255);
        registerSimpleProperty("SongSection", Short.class, 0);
    }

    @Override
    public boolean analyseT3DData(String line) {
        if (line.startsWith("Song=")) {
            this.song = mapConverter.getUPackageRessource(line.split("\'")[1], T3DRessource.Type.MUSIC);
        }
        else if (line.startsWith("Transition=")) {
            this.transition = MTran.valueOf(T3DUtils.getString(line));
        }

        else {
            return super.analyseT3DData(line);
        }

        return true;
    }

    @Override
    public void convert() {

        if(song != null) {
            song.export(UTPackageExtractor.getExtractor(mapConverter, song));
        }

        super.convert();
    }

    @Override
    public String toString() {

        sbf.append(IDT).append("Begin Actor Class=MusicEvent_C \n");

        sbf.append(IDT).append("\tBegin Object Class=AudioComponent Name=\"LevelMusic\"\n");
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tBegin Object Name=\"LevelMusic\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");


        if(song != null){
            sbf.append(IDT).append("\tSong=SoundCue'").append(song.getConvertedName(mapConverter)).append("'\n");

            // duplicate property because in most case won't know the song because of "section" property
            // TODO maybe modify to add _<songSection> to Song property ?? (this would mean convert .s3m/.xm, and extract
            // wave by section
            sbf.append(IDT).append("\tSongOriginal=\"").append(song.getFullName()).append("\"\n");
        }

        if(transition != null){
            sbf.append(IDT).append("\tTransition=NewEnumerator").append(transition.ordinal()).append("\n");
        }

        writeSimpleProperties();

        sbf.append(IDT).append("\tLevelMusic=LevelMusic\n");
        sbf.append(IDT).append("\tRootComponent=LevelMusic\n");

        writeEndActor();

        return super.toString();
    }
}
