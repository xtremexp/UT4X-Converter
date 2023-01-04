/*
 * UT Converter © 2023 by Thomas 'WinterIsComing/XtremeXp' P. is licensed under Attribution-NonCommercial-ShareAlike 4.0 International. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

/*
 * UT Converter © 2023 by Thomas 'WinterIsComing/XtremeXp' P. is licensed under Attribution-NonCommercial-ShareAlike 4.0 International. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package org.xtx.ut4converter.ucore;

/**
 * List all unreal engines
 */
public enum UnrealEngine {

    /**
     *
     */
    NONE("None", 0), // Used for java objects sometimes

    /**
     *
     */
    UE1("Unreal Engine 1", 1),

    /**
     *
     */
    UE2("Unreal Engine 2", 2),

    /**
     *
     */
    UE3("Unreal Engine 3", 3),

    /**
     *
     */
    UE4("Unreal Engine 4", 4);

    private final String name;

    /**
     *
     */
    public final int version;

    UnrealEngine(String name, int version) {
        this.name = name;
        this.version = version;

    }

    /**
     *
     * @return Name of unreal engine
     */
    @Override
    public String toString() {
        return name;
    }
}