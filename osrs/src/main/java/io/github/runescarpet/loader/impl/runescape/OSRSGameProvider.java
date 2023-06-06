package io.github.runescarpet.loader.impl.runescape;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.github.runescarpet.loader.impl.runescape.applet.AppletFrame;
import io.github.runescarpet.loader.impl.runescape.version.ClientVersion;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ObjectShare;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.game.GameProviderHelper;
import net.fabricmc.loader.impl.game.LibClassifier;
import net.fabricmc.loader.impl.game.patch.GameTransformer;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata;
import net.fabricmc.loader.impl.metadata.ModDependencyImpl;
import net.fabricmc.loader.impl.util.Arguments;
import net.fabricmc.loader.impl.util.ExceptionUtil;
import net.fabricmc.loader.impl.util.SystemProperties;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;

/**
 * {@link GameProvider} implementation for OSRS.
 */
public class OSRSGameProvider implements GameProvider {
		private ClientVersion version;
		private Path gamepackJar;
		private Arguments arguments;
		private Collection<Path> validParentClassPath;
		private List<Path> miscLibraries;
		public String clientClass;
		public OSRSGameProperties gameProperties;
		private final GameTransformer transformer = new GameTransformer();

		@Override
		public String getGameId() {
				return "osrs";
		}

		@Override
		public String getGameName() {
				return "OldSchool RuneScape";
		}

		@Override
		public String getRawGameVersion() {
				return this.version.getRawGamepackVersionString();
		}

		@Override
		public String getNormalizedGameVersion() {
				return this.version.getGamepackSemver();
		}

		@Override
		public Collection<BuiltinMod> getBuiltinMods() {
				BuiltinModMetadata.Builder builder = new BuiltinModMetadata.Builder(this.getGameId(), this.getNormalizedGameVersion())
								.setName(this.getGameName());

				// Add Java dependency if we know the class file version.
				if (this.version.getClassFileVersion() != null) {
						int javaVersion = this.version.getClassFileVersion() - 44;

						try {
								builder.addDependency(new ModDependencyImpl(
												ModDependency.Kind.DEPENDS,
												"java",
												Collections.singletonList(String.format(">=%d", javaVersion))
								));
						} catch (VersionParsingException e) {
								throw new RuntimeException(e);
						}
				}

				BuiltinMod mod = new BuiltinMod(Collections.singletonList(this.gamepackJar), builder.build());
				return Collections.singletonList(mod);
		}

		@Override
		public String getEntrypoint() {
				return this.clientClass;
		}

		@Override
		public Path getLaunchDirectory() {
				// TODO system property / arg?
				return Paths.get(".");
		}

		@Override
		public boolean isObfuscated() {
				// The gamepack we get passed is already deobfuscated and remapped.
				// TODO is it?
				return false;
		}

		@Override
		public boolean requiresUrlClassLoader() {
				return false;
		}

		@Override
		public boolean isEnabled() {
				return System.getProperty(SystemProperties.SKIP_DEFAULT_PROVIDER) == null;
		}

		@Override
		public boolean locateGame(FabricLauncher launcher, String[] args) {
				this.arguments = new Arguments();
				this.arguments.parse(args);

				try {
						LibClassifier<OSRSLibrary> classifier = new LibClassifier<>(OSRSLibrary.class, this);

						// Pass JAR specified by system property to the classifier, if any.
						Path systemPropertyJar = GameProviderHelper.getGameJar();

						if (systemPropertyJar != null) {
								classifier.process(systemPropertyJar);
						}

						// As well as the launcher classpath.
						classifier.process(launcher.getClassPath());

						// Locate gamepack JAR.
						this.gamepackJar = classifier.getOrigin(OSRSLibrary.GAMEPACK);
						this.clientClass = classifier.getClassName(OSRSLibrary.GAMEPACK);

						if (this.gamepackJar == null || this.clientClass == null) {
								return false;
						}

						// Get version info from gamepack.
						Integer forcedVersion = null;
						String stringVersion = this.arguments.remove(Arguments.GAME_VERSION);

						if (stringVersion == null) {
								stringVersion = System.getProperty(SystemProperties.GAME_VERSION);
						}

						if (stringVersion != null && !stringVersion.isEmpty()) {
								try {
										forcedVersion = Integer.parseInt(stringVersion);
								} catch (NumberFormatException e) {
										Log.warn(LogCategory.GAME_PROVIDER, "Invalid forced version number provided: %s", stringVersion);
								}
						}

						this.version = ClientVersion.fromJar(this.gamepackJar, this.clientClass, forcedVersion);

						// Set stuff for loader init.
						this.validParentClassPath = classifier.getSystemLibraries();
						this.miscLibraries = classifier.getUnmatchedOrigins();
				} catch (IOException e) {
						throw ExceptionUtil.wrap(e);
				}

				// Expose JAR location in object share for convenience.
				ObjectShare share = FabricLoader.getInstance().getObjectShare();
				share.put("rs-loader:gamepackJar", this.gamepackJar);

				return true;
		}

		@Override
		public void initialize(FabricLauncher launcher) {
				launcher.setValidParentClassPath(this.validParentClassPath);
				this.transformer.locateEntrypoints(launcher, Collections.singletonList(this.gamepackJar));
		}

		@Override
		public GameTransformer getEntrypointTransformer() {
				return this.transformer;
		}

		@Override
		public void unlockClassPath(FabricLauncher launcher) {
				launcher.addToClassPath(this.gamepackJar);

				for (Path path: this.miscLibraries) {
						launcher.addToClassPath(path);
				}
		}

		@Override
		public void launch(ClassLoader loader) {
				// Launch applet.
				EventQueue.invokeLater(() -> {
						AppletFrame frame = new AppletFrame(this.getGameName());
						frame.launch(loader, this);
				});
		}

		@Override
		public Arguments getArguments() {
				return this.arguments;
		}

		@Override
		public String[] getLaunchArguments(boolean sanitize) {
				if (this.arguments == null) return new String[0];
				return this.arguments.toArray();
		}
}
