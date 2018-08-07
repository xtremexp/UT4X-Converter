package org.xtx.ut4converter.t3d;

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


    public T3DSimpleProperty(final String propertyName, final Object propertyClass) {
        this.propertyName = propertyName;
        this.propertyClass = propertyClass;
    }

    public T3DSimpleProperty(final String propertyName, final Object propertyClass, final Object defaultValue) {
        this.propertyName = propertyName;
        this.propertyClass = propertyClass;
        this.defaultValue = defaultValue;
    }

    public boolean readPropertyFromT3dLine(final String line){

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
        }

        return this.propertyValue != null;
    }

    public void writeProperty(final StringBuilder sbf){

        if(this.propertyValue != null) {
            sbf.append("\t").append(propertyName).append("=");

            if (this.propertyClass == String.class) {
                sbf.append("\"").append(propertyValue).append("\"\n");
            } else {
                sbf.append(propertyValue).append("\n");
            }
        }
    }
}
