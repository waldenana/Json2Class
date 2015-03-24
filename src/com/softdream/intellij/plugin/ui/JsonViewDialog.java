package com.softdream.intellij.plugin.ui;

import com.intellij.ide.structureView.StructureView;
import com.intellij.json.JsonLanguage;
import com.intellij.lang.LanguageStructureViewBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.softdream.intellij.plugin.utils.DialogsFactory;
import com.softdream.intellij.plugin.utils.JsonParser;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class JsonViewDialog extends JDialog {
    private JPanel contentPane;
    private JTextField textField1;
    private JButton buttonOK;
    private JPanel codePane;
    private JButton buttonFormat;
    private Project mProject;
    private PsiFile mFile;
    private JsonParser parser;

    public JsonViewDialog(Project project,PsiDirectory file) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        parser = new JsonParser(project,file);
        mProject = project;
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonFormat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onFormat();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//
        mFile = PsiFileFactory.getInstance(mProject).createFileFromText("d.json",JsonLanguage.INSTANCE,"{}");
        FileEditor editor = FileEditorProviderManager.getInstance().getProviders(mProject,mFile.getVirtualFile())[0].createEditor(mProject, mFile.getVirtualFile());

        GridConstraints constraints = new GridConstraints();
        constraints.setAnchor(GridConstraints.ANCHOR_WEST);
        constraints.setVSizePolicy(GridConstraints.ALIGN_FILL);
        constraints.setColumn(0);
        constraints.setFill(GridConstraints.ALIGN_FILL);
        constraints.setHSizePolicy(GridConstraints.SIZEPOLICY_FIXED);
            codePane.add(editor.getComponent(), constraints);
    }

    private void onFormat() {
        ApplicationManager.getApplication().invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        new WriteCommandAction(mProject) {
                            @Override
                            protected void run(@NotNull Result result) throws Throwable {
                                CodeStyleManager.getInstance(mProject).reformat(mFile, false);
                            }
                        }.execute();
                    }
                }
        );
    }

    private void onOK() {
// add your code here
        if (StringUtil.isEmpty(textField1.getText())) {
            DialogsFactory.showMissingSourcePathDialog(mProject);
            return;
        }
        PsiType type = null;
        try {
            type = parser.formatJsonReader(PsiDocumentManager.getInstance(mProject).getDocument(mFile).getText(),textField1.getText());
        } catch (Exception e) {
            DialogsFactory.showErrorDialog(mProject,e.getMessage());
        }
        if (type != null)
            dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

}
