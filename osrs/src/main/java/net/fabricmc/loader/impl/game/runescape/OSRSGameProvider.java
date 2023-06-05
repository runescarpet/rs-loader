package net.fabricmc.loader.impl.game.runescape;

import net.fabricmc.loader.impl.game.GameProvider;

import java.util.Collection;

public class OSRSGameProvider implements GameProvider {
	@Override
	public String getGameId() {
		return "oldschool-runescape";
	}

	@Override
	public String getGameName() {
		return "Old School RuneScape";
	}

	@Override
	public String getRawGameVersion() {
		return null;
	}

	@Override
	public String getNormalizedGameVersion() {
		return null;
	}

	@Override
	public Collection<BuiltinMod> getBuiltinMods() {
		return null;
	}
}
