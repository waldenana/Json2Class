package com.softdream.intellij.plugin.ui;

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
import com.intellij.ui.ScreenUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.softdream.intellij.plugin.utils.DialogsFactory;
import com.softdream.intellij.plugin.utils.JsonFormat;
import com.softdream.intellij.plugin.utils.JsonParser;
import com.softdream.intellij.plugin.utils.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

public class JsonViewDialog extends DialogWrapper {
    private static final String NAME_SERIALIZABLE = "serializable";
    private JPanel contentPane;
    private JTextField textField1;
    private JPanel codePane;
    private JCheckBox serializableCheckBox;
    private JCheckBox parcelableCheckBox;
    private Project mProject;
    private PsiFile mFile;
    private JsonParser parser;
    private boolean bSupportJson = true;
    private FileEditor mEditor;
    private final AbstractAction myApplyAction = new AbstractAction("Format") {
        @Override
        public void actionPerformed(ActionEvent event) {
            onFormat();
        }
    };

    public JsonViewDialog(Project project, final PsiDirectory file) {
        super(project);
        parser = new JsonParser(project, file);
        mProject = project;
        myApplyAction.setEnabled(true);
        myApplyAction.putValue(Action.NAME, "&Format");
        Language language = Language.findLanguageByID("JSON");
        if (language == null) {
            language = Language.findLanguageByID("TEXT");
            bSupportJson = false;
        }
        mFile = PsiFileFactory.getInstance(mProject).createFileFromText("d.json", language, "");
        FileEditor editor = FileEditorProviderManager.getInstance().getProviders(mProject, mFile.getVirtualFile())[0].createEditor(mProject, mFile.getVirtualFile());
        Settings settings = Settings.getInstance(project);
        GridConstraints constraints = new GridConstraints();
        constraints.setAnchor(GridConstraints.ANCHOR_WEST);
        constraints.setVSizePolicy(GridConstraints.ALIGN_FILL);
        constraints.setColumn(0);
        constraints.setFill(GridConstraints.ALIGN_FILL);
        constraints.setHSizePolicy(GridConstraints.SIZEPOLICY_FIXED);
        codePane.add(editor.getComponent(), constraints);
        Rectangle size = ScreenUtil.getMainScreenBounds();
        codePane.setSize((int) (size.width * 0.75), (int) (size.height * 0.75));
        init();
        serializableCheckBox.setSelected(settings.isSerializable());
        parcelableCheckBox.setSelected(settings.isParcelable());
        mEditor = editor;
        parcelableCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (parcelableCheckBox.isSelected())
                    serializableCheckBox.setSelected(false);
            }
        });
        serializableCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (serializableCheckBox.isSelected())
                    parcelableCheckBox.setSelected(false);
            }
        });
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        final Action close = getOKAction();
        close.putValue(Action.NAME, "&Ok");
        if (bSupportJson)
            return new Action[]{myApplyAction, close};
        else
            return new Action[]{close};
    }

    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "Json2ClassWin";
    }

    private void onFormat() {
        if (!bSupportJson) {
            try {
                mFile.getVirtualFile().setBinaryContent(JsonFormat.formatJson(mFile.getText(), "   ").getBytes());
                mFile.getVirtualFile().getFileSystem().refresh(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
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
        if (onOK())
            super.doOKAction();
    }

    @Override
    protected void dispose() {
        mEditor.dispose();
        super.dispose();
    }

    private boolean onOK() {
        if (StringUtil.isEmpty(textField1.getText())) {
            DialogsFactory.showMissingSourcePathDialog(mProject);
            return false;
        }
        Settings.getInstance(mProject).setSerializable(serializableCheckBox.isSelected());
        Settings.getInstance(mProject).setParcelable(parcelableCheckBox.isSelected());
        PsiType type = null;
        try {
            parser.setSerializable(serializableCheckBox.isSelected());
            parser.setParcelable(parcelableCheckBox.isSelected());
            type = parser.formatJsonReader(PsiDocumentManager.getInstance(mProject).getDocument(mFile).getText(), textField1.getText());
        } catch (Exception e) {
            DialogsFactory.showErrorDialog(mProject, e.getMessage());
        }
        return type != null;
    }

}
