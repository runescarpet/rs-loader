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

package net.fabricmc.loader.impl.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.Person;

final class V0ModMetadata extends AbstractModMetadata implements LoaderModMetadata {
	private static final Mixins EMPTY_MIXINS = new Mixins(Collections.emptyList());
	// Required
	private final String id;
	private Version version;

	// Optional (Environment)
	private Collection<ModDependency> dependencies;
	private final String languageAdapter = "net.fabricmc.loader.language.JavaLanguageAdapter"; // TODO: Constants class?
	private final Mixins mixins;
	private final String initializer;
	private final Collection<String> initializers;

	// Optional (metadata)
	private final String name;
	private final String description;
	private final Collection<Person> authors;
	private final Collection<Person> contributors;
	private final ContactInformation links;
	private final String license;

	V0ModMetadata(String id, Version version, Collection<ModDependency> dependencies, Mixins mixins, String initializer, Collection<String> initializers,
			String name, String description, Collection<Person> authors, Collection<Person> contributors, ContactInformation links, String license) {
		this.id = id;
		this.version = version;
		this.dependencies = Collections.unmodifiableCollection(dependencies);

		if (mixins == null) {
			this.mixins = V0ModMetadata.EMPTY_MIXINS;
		} else {
			this.mixins = mixins;
		}

		this.initializer = initializer;
		this.initializers = Collections.unmodifiableCollection(initializers);
		this.name = name;

		if (description == null) {
			this.description = "";
		} else {
			this.description = description;
		}

		this.authors = Collections.unmodifiableCollection(authors);
		this.contributors = Collections.unmodifiableCollection(contributors);
		this.links = links;
		this.license = license;
	}

	@Override
	public int getSchemaVersion() {
		return 0;
	}

	@Override
	public String getType() {
		return TYPE_FABRIC_MOD;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public Collection<String> getProvides() {
		return Collections.emptyList();
	}

	@Override
	public Version getVersion() {
		return this.version;
	}

	@Override
	public void setVersion(Version version) {
		this.version = version;
	}

	@Override
	public Collection<ModDependency> getDependencies() {
		return dependencies;
	}

	@Override
	public void setDependencies(Collection<ModDependency> dependencies) {
		this.dependencies = Collections.unmodifiableCollection(dependencies);
	}

	// General metadata

	@Override
	public String getName() {
		if (this.name != null && this.name.isEmpty()) {
			return this.id;
		}

		return this.name;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public Collection<Person> getAuthors() {
		return this.authors;
	}

	@Override
	public Collection<Person> getContributors() {
		return this.contributors;
	}

	@Override
	public ContactInformation getContact() {
		return this.links;
	}

	@Override
	public Collection<String> getLicense() {
		return Collections.singleton(this.license);
	}

	@Override
	public Optional<String> getIconPath(int size) {
		// honor Mod Menu's de-facto standard
		return Optional.of("assets/" + getId() + "/icon.png");
	}

	@Override
	public String getOldStyleLanguageAdapter() {
		return this.languageAdapter;
	}

	@Override
	public Map<String, CustomValue> getCustomValues() {
		return Collections.emptyMap();
	}

	@Override
	public boolean containsCustomValue(String key) {
		return false;
	}

	@Override
	public CustomValue getCustomValue(String key) {
		return null;
	}

	// Internals

	@Override
	public Map<String, String> getLanguageAdapterDefinitions() {
		return Collections.emptyMap();
	}

	@Override
	public Collection<NestedJarEntry> getJars() {
		return Collections.emptyList();
	}

	@Override
	public void emitFormatWarnings() { }

	@Override
	public Collection<String> getMixinConfigs() {
		return new ArrayList<>(this.mixins.common);
	}

	@Override
	public String getAccessWidener() {
		return null; // intentional null
	}

	static final class Mixins {
		final Collection<String> common;;

		Mixins(Collection<String> common) {
			this.common = Collections.unmodifiableCollection(common);
		}
	}
}
