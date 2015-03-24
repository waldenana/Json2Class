package com.softdream.intellij.plugin.utils;

import com.intellij.ide.IdeBundle;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.UIUtil;
import com.softdream.intellij.plugin.ui.StringResources;

/**
 * Copyright 2014 Tomasz Morcinek. All rights reserved.
 */
public class DialogsFactory {


    public static void showMissingSourcePathDialog(Project project) {
        Messages.showErrorDialog(
                project,
                StringResources.MISSING_SOURCE_PATH_DIALOG_MESSAGE,
                StringResources.MISSING_SOURCE_PATH_DIALOG_TITLE
        );
    }

    public static void showErrorDialog(Project project,String message) {
        Messages.showErrorDialog(
                project,
                message,
                StringResources.MISSING_SOURCE_PATH_DIALOG_TITLE
        );
    }


}
