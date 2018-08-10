package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;

public class T3DMusicEvent extends T3DActor {

    private Boolean bAffectAllPlayers = Boolean.TRUE;

    private Boolean bOnceOnly;

    private Boolean bSilence;

    private Short cdTrack;

    private UPackageRessource song;

    private Short songSection;

    private MTran transition = MTran.MTRAN_Fade;

    /**
     * How fast music transition is done
     */
    enum MTran {
        MTRAN_None, MTRAN_Instant, MTRAN_Segue, MTRAN_Fade, MTRAN_FastFade, MTRAN_SlowFade
    }

    public T3DMusicEvent(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);
    }

    @Override
    public boolean analyseT3DData(String line) {
        if (line.startsWith("bAffectAllPlayers=")) {
            this.bAffectAllPlayers = T3DUtils.getBoolean(line);
        }
        else if (line.startsWith("bOnceOnly=")) {
            this.bOnceOnly = T3DUtils.getBoolean(line);
        }
        else if (line.startsWith("bSilence=")) {
            this.bSilence = T3DUtils.getBoolean(line);
        }
        else if (line.startsWith("CdTrack=")) {
            this.cdTrack = T3DUtils.getShort(line);
        }
        else if (line.startsWith("SongSection=")) {
            this.songSection = T3DUtils.getShort(line);
        }
        else if (line.startsWith("Song=")) {
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

        sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"DefaultSceneRoot\"\n");
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tBegin Object Name=\"DefaultSceneRoot\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tDefaultSceneRoot=DefaultSceneRoot\n");


        if(bAffectAllPlayers != null){
            sbf.append(IDT).append("\tbAffectAllPlayers=").append(bAffectAllPlayers).append("\n");
        }

        if(bOnceOnly != null){
            sbf.append(IDT).append("\tbOnceOnly=").append(bOnceOnly).append("\n");
        }

        if(bSilence != null){
            sbf.append(IDT).append("\tbSilence=").append(bSilence).append("\n");
        }

        if(cdTrack != null){
            sbf.append(IDT).append("\tCdTrack=").append(cdTrack).append("\n");
        }

        if(song != null){
            sbf.append(IDT).append("\tSong=SoundCue'").append(song.getConvertedName(mapConverter)).append("'\n");

            // duplicate property because in most case won't know the song because of "section" property
            // TODO maybe modify to add _<songSection> to Song property ?? (this would mean convert .s3m/.xm, and extract
            // wave by section
            sbf.append(IDT).append("\tSongOriginal=\"").append(song.getFullName()).append("\"\n");
        }

        if(songSection != null){
            sbf.append(IDT).append("\tSongSection=").append(songSection).append("\n");
        }

        if(transition != null){
            sbf.append(IDT).append("\tTransition=NewEnumerator").append(transition.ordinal()).append("\n");
        }

        sbf.append(IDT).append("\tRootComponent=DefaultSceneRoot\n");

        writeEndActor();

        return super.toString();
    }
}
