package io.github.runescarpet.loader.impl.runescape;

import net.fabricmc.loader.impl.game.LibClassifier;

public enum OSRSLibrary implements LibClassifier.LibraryType {
		GAMEPACK("client.class");

		private final String[] paths;

		OSRSLibrary(String... paths) {
				this.paths = paths;
		}

		@Override
		public String[] getPaths() {
				return this.paths;
		}
}
