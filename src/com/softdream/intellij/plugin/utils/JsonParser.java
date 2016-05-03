package com.softdream.intellij.plugin.utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by zewei on 2015-03-11.
 */
public class JsonParser {
    private Project mProject;
    private PsiDirectory directory;
    private PsiElementFactory psiElementFactory;
    private boolean mSerializable=false;
    private boolean mParcelable=false;
    public JsonParser(Project project,PsiDirectory fileDirectory){
        mProject = project;
        directory= fileDirectory;
        psiElementFactory = JavaPsiFacade.getInstance(mProject).getElementFactory();
    }

    public void setSerializable(boolean mSerializable){
        this.mSerializable = mSerializable;
    }


    public void setParcelable(boolean parcelable) {
        this.mParcelable = parcelable;
    }
    public  PsiType formatJsonReader(String text,String name){
        text = removeComment(text);
        return formatJsonReader(new JSONObject(text),name);
    }

    public  PsiType formatJsonReader(JSONObject jsonReaderEx,String name) {
        final JavaBean bean = new JavaBean();
        Iterator<String> names=jsonReaderEx.keys();
        while (names.hasNext()){
            String nodeName = names.next();
            bean.addField(nodeName,nextType(jsonReaderEx.get(nodeName),captureName(name)+captureName(nodeName)));
        }
        final PsiClass psiClass =psiElementFactory.createClass(captureName(name));

        new WriteCommandAction.Simple<String>(mProject,psiClass.getContainingFile()){

            @Override
            protected void run() throws Throwable {
                bean.setSerializable(mSerializable);
                bean.setParcelable(mParcelable);
                bean.generaCode(psiElementFactory,psiClass);
                directory.add(psiClass);
            }
        }.execute();
        return psiElementFactory.createType(psiClass);
    }

    public static String removeComment(String text) {
        return text.replaceAll("(?<!:)//.*|/\\*(\\s|.)*?\\*/", "");
    }

    private PsiType nextType( Object clas,String nodeName){
        Class fieldClass =clas.getClass();
        if (fieldClass == String.class){
            return PsiType.getJavaLangString(PsiManager.getInstance(mProject), GlobalSearchScope.allScope(mProject));
        }
        if (fieldClass == Double.class || fieldClass == double.class){
            return PsiType.DOUBLE;
        }
        if (fieldClass == Float.class || fieldClass == float.class)
            return PsiType.FLOAT;
        if (fieldClass == int.class || fieldClass == Integer.class)
            return PsiType.INT;
        if (fieldClass == Long.class || fieldClass == long.class)
            return PsiType.LONG;
        if (fieldClass == boolean.class || fieldClass == Boolean.class)
            return PsiType.BOOLEAN;
        if (clas instanceof JSONObject)
            return formatJsonReader((JSONObject) clas,nodeName);
        if (clas instanceof JSONArray) {
            PsiType objtype = nextType( ((JSONArray) clas).get(0), nodeName);
            return objtype.createArrayType();
        }
        return PsiType.getJavaLangObject(PsiManager.getInstance(mProject), GlobalSearchScope.allScope(mProject));
    }

    public static String captureName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }
}
