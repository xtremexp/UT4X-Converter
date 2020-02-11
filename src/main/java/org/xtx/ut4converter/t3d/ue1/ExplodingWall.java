package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DRessource;

public class ExplodingWall extends  Effects {


    public ExplodingWall(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);


        registerSimpleProperty("ExplosionSize", Float.class, 200);
        registerSimpleProperty("ExplosionDimensions", Float.class, 120f);
        registerSimpleProperty("WallParticleSize", Float.class, 1f);
        registerSimpleProperty("WoodParticleSize", Float.class, 1f);
        registerSimpleProperty("GlassParticleSize", Float.class, 1f);
        registerSimpleProperty("NumWallChunks", Float.class, 10);
        registerSimpleProperty("NumWoodChunks", Float.class, 3);
        registerSimpleProperty("Health", Integer.class, 0);
        registerSimpleProperty("bTranslucentGlass", Boolean.class, 0);
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

    @Override
    public String toString() {
        return writeSimpleActor("UBExplodingWall_C");
    }

}
