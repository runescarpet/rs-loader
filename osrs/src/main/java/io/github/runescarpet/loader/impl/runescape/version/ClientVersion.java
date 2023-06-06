package io.github.runescarpet.loader.impl.runescape.version;

import net.fabricmc.loader.impl.util.LoaderUtil;
import net.fabricmc.loader.impl.util.SimpleClassPath;

import net.fabricmc.loader.impl.util.log.Log;

import net.fabricmc.loader.impl.util.log.LogCategory;

import org.objectweb.asm.ClassReader;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClientVersion {
		/**
		 * The version of the gamepack JAR.
		 */
		private final Integer rawGamepackVersion;

		/**
		 * The class file version used in the gamepack JAR.
		 */
		private final Integer classFileVersion;

		public ClientVersion(Integer gamepackVersion, Integer classFileVersion) {
				this.rawGamepackVersion = gamepackVersion;
				this.classFileVersion = classFileVersion;
		}

		public static ClientVersion fromJar(Path jar, String clientClass, Integer forcedVersion) throws IOException {
				Integer gamepackVersion = forcedVersion;
				Integer classFileVersion = null;

				try (SimpleClassPath cp = new SimpleClassPath(Collections.singletonList(jar))) {
						try (InputStream is = cp.getInputStream(LoaderUtil.getClassFileName(clientClass))) {
								if (is == null) {
										throw new RuntimeException("client class missing from jar?");
								}

								// Read the Java class version.
								DataInputStream dis = new DataInputStream(is);

								if (dis.readInt() == 0xCAFEBABE) {
										dis.readUnsignedShort();
										classFileVersion = dis.readUnsignedShort();
								}
						}

						if (forcedVersion != null) {
								try (InputStream is = cp.getInputStream(LoaderUtil.getClassFileName(clientClass))) {
										assert is != null;

										// Read gamepack version from client class.
										ClassReader reader = new ClassReader(is);
										VersionClassVisitor v = new VersionClassVisitor();
										reader.accept(v, 0);

										gamepackVersion = v.getVersion();
								}
						}
				}

				return new ClientVersion(gamepackVersion, classFileVersion);
		}

		public Integer getRawGamepackVersion() {
				return this.rawGamepackVersion;
		}

		public String getRawGamepackVersionString() {
				if (this.rawGamepackVersion == null) {
						return "unknown";
				}

				return String.valueOf(this.rawGamepackVersion);
		}

		public String getGamepackSemver() {
				if (this.rawGamepackVersion == null) {
						return "0.0.0-unknown";
				}

				return String.format("0.%d.0", this.rawGamepackVersion);
		}

		public Integer getClassFileVersion() {
				return this.classFileVersion;
		}

		private static int getRawGamepackVersionFromJar(File file) throws IOException {
				try (JarFile jar = new JarFile(file)) {
						for (Enumeration<JarEntry> it = jar.entries(); it.hasMoreElements();) {
								JarEntry entry = it.nextElement();

								if (!entry.getName().equals("client.class")) {
										continue;
								}

								InputStream in = jar.getInputStream(entry);

								ClassReader reader = new ClassReader(in);
								VersionClassVisitor v = new VersionClassVisitor();
								reader.accept(v, 0);
								return v.getVersion();
						}
				}

				return -1;
		}
}
