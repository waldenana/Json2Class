package com.softdream.intellij.plugin.utils;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

/**
 * Created by zewei on 2015-03-11.
 */
public class FileHelper {

    public static VirtualFile createOrFindFile(Project project, String fileName, String folderPath) throws IOException {
        VirtualFile folder = createFolderIfNotExist(project, folderPath);
        return folder.findOrCreateChildData(project, fileName);
    }

    public static VirtualFile setFileContent(Project project, VirtualFile createdFile, String code) throws IOException {
        createdFile.setBinaryContent(code.getBytes());
        openFileInEditor(project, createdFile);
        return createdFile;
    }

    private static void openFileInEditor(Project project, VirtualFile fileWithGeneratedCode) {
        FileEditorManager.getInstance(project).openFile(fileWithGeneratedCode, true);
    }

    public static VirtualFile createFolderIfNotExist(Project project, String folder) throws IOException {
        VirtualFile directory = project.getBaseDir();
        String[] folders = folder.split("/");
        for (String childFolder : folders) {
            VirtualFile childDirectory = directory.findChild(childFolder);
            if (childDirectory != null && childDirectory.isDirectory()) {
                directory = childDirectory;
            } else {
                directory = directory.createChildDirectory(project, childFolder);
            }
        }
        return directory;
    }



}
