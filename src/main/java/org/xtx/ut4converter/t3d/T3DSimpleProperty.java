package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 *
 */
public class T3DSimpleProperty {

    private final String propertyName;

    private final Object propertyClass;

    /**
     * If true then this property is scalable.
     */
    private boolean scalable;

    private Object defaultValue;

    private Object propertyValue;

    /**
     *
     */
    private T3DRessource.Type ressourceType;


    public T3DSimpleProperty(final String propertyName, final Object propertyClass, final Object defaultValue) {
        this.propertyName = propertyName;
        this.propertyClass = propertyClass;
        this.defaultValue = defaultValue;
    }

    public T3DSimpleProperty(final String propertyName, final T3DRessource.Type ressourceType) {
        this.propertyName = propertyName;
        this.propertyClass = UPackageRessource.class;
        this.defaultValue = null;
    }

    public boolean readPropertyFromT3dLine(final String line, final MapConverter mapConverter){

        // property ever parsed before
        if(this.propertyValue != null){
            return false;
        }

        if(line.toLowerCase().startsWith(this.propertyName.toLowerCase() + "=")){
            if(propertyClass == String.class){
                this.propertyValue = T3DUtils.getString(line);
            }
            else if(propertyClass == Short.class){
                this.propertyValue = T3DUtils.getShort(line);
            }
            else if(propertyClass == Float.class){
                this.propertyValue = T3DUtils.getFloat(line);
            }
            else if(propertyClass == Boolean.class){
                this.propertyValue = T3DUtils.getBoolean(line);
            }
            else if(propertyClass == Integer.class){
                this.propertyValue = T3DUtils.getInteger(line);
            }
            else if(propertyClass == Double.class){
                this.propertyValue = T3DUtils.getDouble(line);
            }
            else if(propertyClass == UPackageRessource.class){
                this.propertyValue = T3DUtils.getUPackageRessource(mapConverter, line, this.ressourceType);
            }
        }

        return this.propertyValue != null;
    }

    void writeProperty(final StringBuilder sbf, final MapConverter mapConverter){

        if(this.propertyValue != null) {
            sbf.append("\t").append(propertyName).append("=");

            if (this.propertyClass == String.class) {
                sbf.append("\"").append(propertyValue).append("\"\n");
            } else if (this.propertyClass == UPackageRessource.class) {

                if(ressourceType == T3DRessource.Type.SOUND) {
                    sbf.append("SoundCue'").append(((UPackageRessource) propertyValue).getConvertedName(mapConverter)).append("'\n");
                } else if(ressourceType == T3DRessource.Type.STATICMESH) {
                    sbf.append("StaticMesh'").append(((UPackageRessource) propertyValue).getConvertedName(mapConverter)).append("'\n");
                } else if(ressourceType == T3DRessource.Type.TEXTURE) {
                    sbf.append("Material'").append(((UPackageRessource) propertyValue).getConvertedName(mapConverter)).append("'\n");
                }
            } else {
                sbf.append(propertyValue).append("\n");
            }
        }
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

    public T3DRessource.Type getRessourceType() {
        return ressourceType;
    }

    public Object getPropertyClass() {
        return propertyClass;
    }
}
