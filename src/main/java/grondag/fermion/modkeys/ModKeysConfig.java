/*******************************************************************************
 * Copyright 2020 grondag
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
package grondag.fermion.modkeys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;

import grondag.fermion.Fermion;
import grondag.fermion.modkeys.impl.ModKeysAccess;

public class ModKeysConfig {
	public enum Option {
		SHIFT("shift", ModKeysAccess.SHIFT),
		CONTROL("control", ModKeysAccess.CONTROL),
		ALT("alt", ModKeysAccess.ALT),
		SUPER("menu", ModKeysAccess.SUPER);

		public final String key;
		public final int flag;

		Option(String key, int flag) {
			this.flag = flag;
			this.key = key;
		}
	}

	private static final String PRIMARY = "primary";
	private static final String SECONDARY = "secondary";
	private static final String TERTIARY = "tertiary";

	private ModKeysConfig() {}

	private static Option primary;
	private static Option secondary;
	private static Option tertiary;

	public static Option primary() {
		return primary;
	}

	public static Option secondary() {
		return secondary;
	}

	public static Option tertiary() {
		return tertiary;
	}

	public static void saveOptions(Option primaryIn, Option secondaryIn, Option tertiaryIn) {
		if(primaryIn != null) {
			primary = primaryIn;
		}

		if(secondaryIn != null) {
			secondary = secondaryIn;
		}

		if(tertiaryIn != null) {
			tertiary = tertiaryIn;
		}

		final File configFile = getFile();
		final Properties properties = getProperties(configFile);

		properties.put(PRIMARY, primary.key);
		properties.put(SECONDARY, secondary.key);
		properties.put(TERTIARY, tertiary.key);

		saveProperties(configFile, properties);
	}

	static {
		final File configFile = getFile();
		final Properties properties = getProperties(configFile);

		final String primaryStr = properties.computeIfAbsent(PRIMARY, (a) -> Option.CONTROL.key).toString().toLowerCase(Locale.ROOT);
		primary = getOption(primaryStr);

		if(primary == null) {
			primary = Option.CONTROL;
			properties.put(PRIMARY, Option.CONTROL.key);
		}

		final String secondaryStr = properties.computeIfAbsent(SECONDARY, (a) -> Option.ALT.key).toString().toLowerCase(Locale.ROOT);
		secondary = getOption(secondaryStr);

		if(secondary == null) {
			secondary = Option.ALT;
			properties.put(SECONDARY, Option.ALT.key);
		}

		final String tertiaryStr = properties.computeIfAbsent(TERTIARY, (a) -> Option.SUPER.key).toString().toLowerCase(Locale.ROOT);
		tertiary = getOption(tertiaryStr);

		if(tertiary == null) {
			tertiary = Option.SUPER;
			properties.put(TERTIARY, Option.SUPER.key);
		}

		saveProperties(configFile, properties);
	}

	static Option getOption(String keyName) {
		if(keyName.equals(Option.CONTROL.key)) {
			return Option.CONTROL;
		} else if(keyName.equals(Option.ALT.key)) {
			return Option.ALT;
		} else if(keyName.equals(Option.SHIFT.key)) {
			return Option.SHIFT;
		} else if(keyName.equals(Option.SUPER.key)) {
			return Option.SUPER;
		} else {
			return null;
		}
	}

	private static File getFile() {
		final File configDir = FabricLoader.getInstance().getConfigDirectory();
		if (!configDir.exists()) {
			Fermion.LOG.warn("[Fermion ModKeys] Could not access configuration directory: " + configDir.getAbsolutePath());
		}

		return new File(configDir, "modkeys.properties");
	}

	private static Properties getProperties(File configFile) {
		final Properties properties = new Properties();

		if (configFile.exists()) {
			try (FileInputStream stream = new FileInputStream(configFile)) {
				properties.load(stream);
			} catch (final IOException e) {
				Fermion.LOG.warn("[Fermion ModKeys] Could not read property file '" + configFile.getAbsolutePath() + "'", e);
			}
		}

		return properties;
	}

	private static void saveProperties(File configFile, Properties properties) {
		try (FileOutputStream stream = new FileOutputStream(configFile)) {
			properties.store(stream, "Fermion ModKeys properties file");
		} catch (final IOException e) {
			Fermion.LOG.warn("[Fermion ModKeys] Could not store property file '" + configFile.getAbsolutePath() + "'", e);
		}
	}

	static void init() {
		// NOOP - loads
	}
}
