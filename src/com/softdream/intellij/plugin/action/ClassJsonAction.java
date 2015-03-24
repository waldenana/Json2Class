package com.softdream.intellij.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.softdream.intellij.plugin.ui.JsonViewDialog;

import java.awt.*;

/**
 * Created by zewei on 2015-03-17.
 */
public class ClassJsonAction extends AnAction {
    public void actionPerformed(final AnActionEvent e) {
       final VirtualFile file = e.getData(LangDataKeys.VIRTUAL_FILE);
        PsiDirectory directory = PsiManager.getInstance(e.getProject()).findDirectory(file);
        Dialog jsonD = new JsonViewDialog(e.getProject(), directory);
        jsonD.setSize(400 ,200);
        jsonD.setLocationRelativeTo(null);
        jsonD.setVisible(true);
    }
}
