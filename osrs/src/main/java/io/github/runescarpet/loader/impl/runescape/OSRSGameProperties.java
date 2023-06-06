package io.github.runescarpet.loader.impl.runescape;

import java.util.Map;

/**
 * Properties for an OSRS version.
 */
public class OSRSGameProperties {
		private final Map<String, String> properties;

		public OSRSGameProperties(Map<String, String> properties) {
				this.properties = properties;
		}

		public String getProperty(String key) {
				return this.properties.get(key);
		}
}
