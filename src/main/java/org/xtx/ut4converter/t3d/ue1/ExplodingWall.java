package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DRessource;

public class ExplodingWall extends  Effects {


    public ExplodingWall(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("ExplosionSize", Float.class, 120f);
        registerSimpleProperty("ExplosionDimensions", Float.class, 200f);
        registerSimpleProperty("WallParticleSize", Float.class, 1f);
        registerSimpleProperty("WoodParticleSize", Float.class, 1f);
        registerSimpleProperty("GlassParticleSize", Float.class, 1f);
        registerSimpleProperty("NumGlassChunks", Integer.class, 0);
        registerSimpleProperty("NumWallChunks", Integer.class, 10);
        registerSimpleProperty("NumWoodChunks", Integer.class, 3);
        registerSimpleProperty("Health", Integer.class, 0);
        registerSimpleProperty("bTranslucentGlass", Boolean.class);
        registerSimpleArrayProperty("ActivatedBy", String.class);
        registerSimplePropertyRessource("BreakingSound", T3DRessource.Type.SOUND);
        registerSimplePropertyRessource("WallTexture", T3DRessource.Type.TEXTURE);
        registerSimplePropertyRessource("WoodTexture", T3DRessource.Type.TEXTURE);
        registerSimplePropertyRessource("GlassTexture", T3DRessource.Type.TEXTURE);
    }

    @Override
    public void convert() {
        super.convert();
        this.t3dClass = "UBExplodingWall_C";
    }

    public String toT3d() {
        return writeSimpleActor("UExplodingWall_C", "BillboardComponent", "Billboard", "UExplodingWall_C'/Game/UEActors/UExplodingWall.Default__UExplodingWall_C'");
    }
}
