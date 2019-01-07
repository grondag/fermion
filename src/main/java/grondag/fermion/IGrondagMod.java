package grondag.fermion;

import org.apache.logging.log4j.Logger;

import net.minecraft.util.Identifier;

public interface IGrondagMod {
    public String modID();

    /**
     * Puts mod ID and . in front of whatever is passed in
     */
    public default String prefixName(String name) {
        return String.format("%s.%s", this.modID(), name.toLowerCase());
    }

    public default String prefixResource(String name) {
        return String.format("%s:%s", this.modID(), name.toLowerCase());
    }

    public default Identifier resource(String name) {
        return new Identifier(prefixResource(name));
    }

    public Logger getLog();

    public default void error(String message, Object o1, Object o2) {
        this.getLog().error(message, o1, o2);
    }

    public default void error(String message, Throwable t) {
        this.getLog().error(message, t);
    }

    public default void error(String message) {
        this.getLog().error(message);
    }

    public default void debug(String message, Object... args) {
        this.getLog().debug(String.format(message, args));
    }

    public default void debug(String message) {
        this.getLog().debug(message);
    }

    public default void info(String message, Object... args) {
        this.getLog().info(String.format(message, args));
    }

    public default void info(String message) {
        this.getLog().info(message);
    }

    public default void warn(String message, Object... args) {
        this.getLog().warn(String.format(message, args));
    }

    public default void warn(String message) {
        this.getLog().warn(message);
    }
}
