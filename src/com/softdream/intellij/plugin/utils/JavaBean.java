package com.softdream.intellij.plugin.utils;

import com.intellij.psi.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zewei on 2015-03-13.
 */
public class JavaBean {
    private HashMap<String,PsiType> field= new HashMap<String, PsiType>();
    public void addField(String name,PsiType classType){
        field.put(name,classType);
    }

    public PsiClass generCode(PsiElementFactory factory,PsiClass javaFile){
        Iterator<Map.Entry<String, PsiType>> iterable = field.entrySet().iterator();
        while (iterable.hasNext()){
            Map.Entry<String,PsiType> var =iterable.next();
            javaFile.add(factory.createField(var.getKey(),var.getValue()));
            PsiMethod builderMethod = createGetMethod(factory,javaFile,var.getKey(),var.getValue());
            javaFile.add(builderMethod);

            builderMethod = createSetMethod(factory, javaFile, var.getKey(), var.getValue());
            javaFile.add(builderMethod);
        }
        return javaFile;

    }

    private PsiMethod createGetMethod(PsiElementFactory factory,PsiClass file,String name,PsiType type){
        PsiMethod builderMethod =factory.createMethod("get" + JsonParser.captureName(name), type);
        StringBuilder assignBuilder = new StringBuilder()
                .append("return this.").append(name).append(";\n");
        PsiStatement statement =factory.createStatementFromText(assignBuilder.toString(), file);
        builderMethod.getBody().add(statement);
        return builderMethod;
    }

    private PsiMethod createSetMethod(PsiElementFactory factory,PsiClass file,String name,PsiType type){
        PsiMethod builderMethod =factory.createMethod("set" + JsonParser.captureName(name), PsiType.VOID);
        PsiParameter parameter = factory.createParameter(name, type);
        builderMethod.getParameterList().add(parameter);
        StringBuilder assignBuilder = new StringBuilder()
                .append("this.").append(name).append(" = ").append(name).append(";\n");
        PsiStatement statement =factory.createStatementFromText(assignBuilder.toString(),file);
        builderMethod.getBody().add(statement);
        return builderMethod;
    }

}
