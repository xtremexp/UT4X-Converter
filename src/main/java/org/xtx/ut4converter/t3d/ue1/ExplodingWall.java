package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DRessource;

public class ExplodingWall extends  Effects {


    public ExplodingWall(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);


        registerSimpleProperty("ExplosionSize", Float.class);
        registerSimpleProperty("ExplosionDimensions", Float.class);
        registerSimpleProperty("WallParticleSize", Float.class);
        registerSimpleProperty("WoodParticleSize", Float.class);
        registerSimpleProperty("GlassParticleSize", Float.class);
        registerSimpleProperty("NumWallChunks", Float.class);
        registerSimpleProperty("NumWoodChunks", Float.class);
        registerSimpleProperty("Health", Integer.class);
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
        return writeSimpleActor("UBExplodingWall_C");
    }

}
