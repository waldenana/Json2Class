package com.softdream.intellij.plugin.ui;

import com.intellij.json.JsonLanguage;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.softdream.intellij.plugin.utils.DialogsFactory;
import com.softdream.intellij.plugin.utils.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class JsonViewDialog extends DialogWrapper {
    private JPanel contentPane;
    private JTextField textField1;
    private JPanel codePane;
    private Project mProject;
    private PsiFile mFile;
    private JsonParser parser;
    private final AbstractAction myApplyAction = new AbstractAction("Format") {
        @Override
        public void actionPerformed(ActionEvent event) {
            onFormat();
        }
    };

    public JsonViewDialog(Project project,PsiDirectory file) {
        super(project);
        parser = new JsonParser(project,file);
        mProject = project;
        myApplyAction.setEnabled(true);
        myApplyAction.putValue(Action.NAME,"&Format");
        Language language = Language.findLanguageByID("JSON");
        if (language == null)
            language = Language.findLanguageByID("TEXT");
        mFile = PsiFileFactory.getInstance(mProject).createFileFromText("d.json",language ,"");
        FileEditor editor = FileEditorProviderManager.getInstance().getProviders(mProject,mFile.getVirtualFile())[0].createEditor(mProject, mFile.getVirtualFile());

        GridConstraints constraints = new GridConstraints();
        constraints.setAnchor(GridConstraints.ANCHOR_WEST);
        constraints.setVSizePolicy(GridConstraints.ALIGN_FILL);
        constraints.setColumn(0);
        constraints.setFill(GridConstraints.ALIGN_FILL);
        constraints.setHSizePolicy(GridConstraints.SIZEPOLICY_FIXED);
            codePane.add(editor.getComponent(), constraints);
        init();

    }
    @NotNull
    @Override
    protected Action[] createActions() {
        final Action close = getOKAction();
        close.putValue(Action.NAME, "&Ok");
        return new Action[]{myApplyAction,close};
    }

    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "Json2ClassWin";
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

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    @Override
    protected void doOKAction() {
        if(onOK())
        super.doOKAction();
    }

    private boolean onOK() {
        if (StringUtil.isEmpty(textField1.getText())) {
            DialogsFactory.showMissingSourcePathDialog(mProject);
            return false;
        }
        PsiType type = null;
        try {
            type = parser.formatJsonReader(PsiDocumentManager.getInstance(mProject).getDocument(mFile).getText(),textField1.getText());
        } catch (Exception e) {
            DialogsFactory.showErrorDialog(mProject,e.getMessage());
        }
        return type != null;
    }

}
