/*******************************************************************************
 * Copyright 2019 grondag
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

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
