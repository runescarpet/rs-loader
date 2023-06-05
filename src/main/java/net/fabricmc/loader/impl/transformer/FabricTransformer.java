/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.loader.impl.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import net.fabricmc.accesswidener.AccessWidenerClassVisitor;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;

public final class FabricTransformer {
	public static byte[] transform(boolean isDevelopment, String name, byte[] bytes) {
		boolean isRunescapeClass = name.startsWith("com.jagex.oldscape") || name.indexOf('.') < 0;
		boolean transformAccess = isRunescapeClass && FabricLauncherBase.getLauncher().getMappingConfiguration().requiresPackageAccessHack();
		boolean applyAccessWidener = isRunescapeClass && FabricLoaderImpl.INSTANCE.getAccessWidener().getTargets().contains(name);

		if (!transformAccess && !applyAccessWidener) {
			return bytes;
		}

		ClassReader classReader = new ClassReader(bytes);
		ClassWriter classWriter = new ClassWriter(classReader, 0);
		ClassVisitor visitor = classWriter;

		if (applyAccessWidener) {
			visitor = AccessWidenerClassVisitor.createClassVisitor(FabricLoaderImpl.ASM_VERSION, visitor, FabricLoaderImpl.INSTANCE.getAccessWidener());
		}

		if (transformAccess) {
			visitor = new PackageAccessFixer(FabricLoaderImpl.ASM_VERSION, visitor);
		}

		classReader.accept(visitor, 0);
		return classWriter.toByteArray();
	}
}
