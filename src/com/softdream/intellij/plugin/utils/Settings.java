package com.softdream.intellij.plugin.utils;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;


@State(
        name = "Json2ClassSettings",
        storages = {
                @Storage(file = StoragePathMacros.PROJECT_FILE),
                @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/json2class_settings.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)
public class Settings implements PersistentStateComponent<Settings> {

    private boolean serializable;
    private boolean parcelable;

    public boolean isParcelable() {
        return parcelable;
    }

    public void setParcelable(boolean parcelable) {
        this.parcelable = parcelable;
    }


    public boolean isSerializable() {
        return serializable;
    }

    public void setSerializable(boolean sourcePath) {
        this.serializable = sourcePath;
    }

    public static Settings getInstance(Project project) {
        return ServiceManager.getService(project, Settings.class);
    }

    @Nullable
    @Override
    public Settings getState() {
        return this;
    }

    @Override
    public void loadState(Settings settings) {
        XmlSerializerUtil.copyBean(settings, this);
    }
}
