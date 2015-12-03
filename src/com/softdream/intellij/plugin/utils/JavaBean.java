package com.softdream.intellij.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.siyeh.ig.fixes.SerialVersionUIDBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zewei on 2015-03-13.
 */
public class JavaBean {
    private HashMap<String, PsiType> field = new HashMap<String, PsiType>();

    private boolean mSerializable = false;

    public void setSerializable(boolean mSerializable) {
        this.mSerializable = mSerializable;
    }

    public void addField(String name, PsiType classType) {
        field.put(name, classType);
    }

    public PsiClass generCode(PsiElementFactory factory, PsiClass javaFile) {
        if (mSerializable) {
            try {
                PsiJavaCodeReferenceElement implementsReference =
                        factory.createReferenceFromText(CommonClassNames.JAVA_IO_SERIALIZABLE, javaFile);
                javaFile.getImplementsList().add(implementsReference);
                createSerializable(factory, javaFile);
            } catch (Exception e) {

            }
        }
        Iterator<Map.Entry<String, PsiType>> iterable = field.entrySet().iterator();
        while (iterable.hasNext()) {
            Map.Entry<String, PsiType> var = iterable.next();
            javaFile.add(factory.createField(var.getKey(), var.getValue()));
            PsiMethod builderMethod = createGetMethod(factory, javaFile, var.getKey(), var.getValue());
            javaFile.add(builderMethod);

            builderMethod = createSetMethod(factory, javaFile, var.getKey(), var.getValue());
            javaFile.add(builderMethod);
        }
        return javaFile;

    }

    private void createSerializable(PsiElementFactory factory, PsiClass file) {
        final long serialVersionUID =
                SerialVersionUIDBuilder.computeDefaultSUID(file);
        final PsiField field =
                factory.createFieldFromText("private static final long serialVersionUID = " +
                        serialVersionUID + "L;", file);
        file.add(field);
    }

    private PsiMethod createGetMethod(PsiElementFactory factory, PsiClass file, String name, PsiType type) {
        PsiMethod builderMethod = factory.createMethod("get" + JsonParser.captureName(name), type);
        StringBuilder assignBuilder = new StringBuilder()
                .append("return this.").append(name).append(";\n");
        PsiStatement statement = factory.createStatementFromText(assignBuilder.toString(), file);
        builderMethod.getBody().add(statement);
        return builderMethod;
    }

    private PsiMethod createSetMethod(PsiElementFactory factory, PsiClass file, String name, PsiType type) {
        PsiMethod builderMethod = factory.createMethod("set" + JsonParser.captureName(name), PsiType.VOID);
        PsiParameter parameter = factory.createParameter(name, type);
        builderMethod.getParameterList().add(parameter);
        StringBuilder assignBuilder = new StringBuilder()
                .append("this.").append(name).append(" = ").append(name).append(";\n");
        PsiStatement statement = factory.createStatementFromText(assignBuilder.toString(), file);
        builderMethod.getBody().add(statement);
        return builderMethod;
    }

}
