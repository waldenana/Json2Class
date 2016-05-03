package com.softdream.intellij.plugin.parcelable;

import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * Created by zewei on 2016-04-30.
 */
public class ParcelableGenerator {
    public static final String CREATOR_NAME = "CREATOR";
    public static final String TYPE_PARCEL = "android.os.Parcelable";
    private ParcelableHelper parcelableHelper;
    private PsiElementFactory factory;
    private PsiClass javaFile;
    public ParcelableGenerator(PsiElementFactory factory, PsiClass javaFile){
        this.factory=factory;
        this.javaFile = javaFile;
        parcelableHelper = new ParcelableHelper(javaFile);
    }

    public void writeCreator(){
        generInterface();
        PsiField field = factory.createFieldFromText(generateStaticCreator(javaFile),javaFile);
        javaFile.add(field);
    }

    private void generInterface(){
        PsiJavaCodeReferenceElement implementsReference =
                factory.createReferenceElementByType(PsiType.getTypeByName(TYPE_PARCEL, javaFile.getProject(),GlobalSearchScope.allScope(javaFile.getProject())));
        javaFile.getImplementsList().add(implementsReference);
        javaFile.add(parcelableHelper.writeToParcel(factory));
        javaFile.add(parcelableHelper.describeContents(factory));
    }

    private String generateStaticCreator(PsiClass psiClass) {
        StringBuilder sb = new StringBuilder("public static final Creator<");

        String className = psiClass.getName();

        sb.append(className).append("> CREATOR = new Creator<").append(className).append(">(){")
                .append("@Override ")
                .append("public ").append(className).append(" createFromParcel(Parcel source) {")
                .append(parcelableHelper.readJava("source"))
                .append("}")
                .append("@Override ")
                .append("public ").append(className).append("[] newArray(int size) {")
                .append("return new ").append(className).append("[size];}")
                .append("};");
        return sb.toString();
    }



}
