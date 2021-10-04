package me.basiqueevangelist.ecstatic.api;

import org.gradle.api.provider.ListProperty;

public interface EcstaticExtension {
    ListProperty<String> getTargetedClasses();
}
