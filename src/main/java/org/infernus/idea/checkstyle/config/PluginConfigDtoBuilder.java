package org.infernus.idea.checkstyle.config;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.infernus.idea.checkstyle.CheckStylePlugin;
import org.infernus.idea.checkstyle.VersionListReader;
import org.infernus.idea.checkstyle.csapi.BundledConfig;
import org.infernus.idea.checkstyle.model.ConfigurationLocation;
import org.infernus.idea.checkstyle.model.ConfigurationLocationFactory;
import org.infernus.idea.checkstyle.model.ScanScope;
import org.infernus.idea.checkstyle.util.OS;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public final class PluginConfigDtoBuilder {
    private String checkstyleVersion;
    private ScanScope scanScope;
    private boolean suppressErrors;
    private boolean copyLibraries;
    private SortedSet<ConfigurationLocation> locations;
    private List<String> thirdPartyClasspath;
    private ConfigurationLocation activeLocation;
    private boolean scanBeforeCheckin;
    private String lastActivePluginVersion;

    private PluginConfigDtoBuilder(@NotNull final String checkstyleVersion,
                                   @NotNull final ScanScope scanScope,
                                   final boolean suppressErrors,
                                   final boolean copyLibraries,
                                   @NotNull final SortedSet<ConfigurationLocation> locations,
                                   @NotNull final List<String> thirdPartyClasspath,
                                   @Nullable final ConfigurationLocation activeLocation,
                                   final boolean scanBeforeCheckin,
                                   @Nullable final String lastActivePluginVersion) {
        this.checkstyleVersion = checkstyleVersion;
        this.scanScope = scanScope;
        this.suppressErrors = suppressErrors;
        this.copyLibraries = copyLibraries;
        this.locations = locations;
        this.thirdPartyClasspath = thirdPartyClasspath;
        this.activeLocation = activeLocation;
        this.scanBeforeCheckin = scanBeforeCheckin;
        this.lastActivePluginVersion = lastActivePluginVersion;
    }

    public static PluginConfigDtoBuilder defaultConfiguration(final Project project) {
        final String csDefaultVersion = new VersionListReader().getDefaultVersion();

        final SortedSet<ConfigurationLocation> defaultLocations = new TreeSet<>();
        defaultLocations.add(configurationLocationFactory(project).create(BundledConfig.SUN_CHECKS, project));
        defaultLocations.add(configurationLocationFactory(project).create(BundledConfig.GOOGLE_CHECKS, project));

        final boolean copyLibs = OS.isWindows();

        return new PluginConfigDtoBuilder(csDefaultVersion, ScanScope.getDefaultValue(), false,
                copyLibs, defaultLocations, Collections.emptyList(), null, false,
                CheckStylePlugin.currentPluginVersion());
    }

    public static PluginConfigDtoBuilder testInstance(final String checkstyleVersion) {
        return new PluginConfigDtoBuilder(checkstyleVersion, ScanScope.AllSources, false, false,
                Collections.emptySortedSet(), Collections.emptyList(), null, false, "aVersion");
    }

    public static PluginConfigDtoBuilder from(final PluginConfigDto source) {
        return new PluginConfigDtoBuilder(source.getCheckstyleVersion(),
                source.getScanScope(),
                source.isSuppressErrors(),
                source.isCopyLibs(),
                source.getLocations(),
                source.getThirdPartyClasspath(),
                source.getActiveLocation(),
                source.isScanBeforeCheckin(),
                source.getLastActivePluginVersion());
    }

    public PluginConfigDtoBuilder withCheckstyleVersion(@Nullable final String newCheckstyleVersion) {
        this.checkstyleVersion = newCheckstyleVersion;
        return this;
    }

    public PluginConfigDtoBuilder withActiveLocation(@Nullable final ConfigurationLocation newActiveLocation) {
        this.activeLocation = newActiveLocation;
        return this;
    }

    public PluginConfigDtoBuilder withSuppressErrors(final boolean newSuppressErrors) {
        this.suppressErrors = newSuppressErrors;
        return this;
    }

    public PluginConfigDtoBuilder withCopyLibraries(final boolean newCopyLibraries) {
        this.copyLibraries = newCopyLibraries;
        return this;
    }

    public PluginConfigDtoBuilder withScanBeforeCheckin(final boolean newScanBeforeCheckin) {
        this.scanBeforeCheckin = newScanBeforeCheckin;
        return this;
    }

    public PluginConfigDtoBuilder withLocations(final SortedSet<ConfigurationLocation> newLocations) {
        this.locations = newLocations;
        return this;
    }

    public PluginConfigDtoBuilder withThirdPartyClassPath(final List<String> newThirdPartyClassPath) {
        this.thirdPartyClasspath = newThirdPartyClassPath;
        return this;
    }

    public PluginConfigDtoBuilder withScanScope(@NotNull final ScanScope newScanScope) {
        this.scanScope = newScanScope;
        return this;
    }

    public PluginConfigDtoBuilder withLastActivePluginVersion(final String newLastActivePluginVersion) {
        this.lastActivePluginVersion = newLastActivePluginVersion;
        return this;
    }

    public PluginConfigDto build() {
        return new PluginConfigDto(checkstyleVersion, scanScope, suppressErrors, copyLibraries,
                locations, thirdPartyClasspath, activeLocation,
                scanBeforeCheckin, lastActivePluginVersion);
    }

    private static ConfigurationLocationFactory configurationLocationFactory(final Project project) {
        return ServiceManager.getService(project, ConfigurationLocationFactory.class);
    }
}
