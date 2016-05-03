package com.softdream.intellij.plugin.parcelable;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiAnnotationImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.softdream.intellij.plugin.parcelable.factor.ParcelFactor;

import java.util.ArrayList;

/**
 * Created by zewei on 2016-05-03.
 */
public class ParcelableHelper {

    private PsiClass javaFile;
    private ArrayList<ParcelFactor> factors = new ArrayList<ParcelFactor>();

    public ParcelableHelper(PsiClass javaFile) {
        this.javaFile = javaFile;
       PsiField[] fields= javaFile.getAllFields();
        for (PsiField field : fields) {
            factors.add(ParcelFactor.getFactor(field));
        }
    }

    public String readJava(String parcel) {
        String className = javaFile.getName();
        StringBuilder sb = new StringBuilder()
                .append(className).append(" var=new ").append(className).append("();");
//                .append()
        for (ParcelFactor factor : factors) {
            sb.append(factor.readValue(parcel));
        }
        sb.append("return var;");
        return sb.toString();
    }

    public PsiMethod writeToParcel(PsiElementFactory factory) {

        PsiMethod builderMethod = factory.createMethod("writeToParcel" , PsiType.VOID);
        builderMethod.getModifierList().addAnnotation("Override");
        builderMethod.getParameterList().add(factory.createParameter("dest",PsiType.getTypeByName("android.os.Parcel",javaFile.getProject(), GlobalSearchScope.allScope(javaFile.getProject()))));
        builderMethod.getParameterList().add(factory.createParameter("flags",PsiType.INT));
        for (ParcelFactor factor : factors) {
            PsiStatement statement = factory.createStatementFromText(factor.writeValue("dest"), javaFile);
            builderMethod.getBody().add(statement);
        }
        return builderMethod;
    }

    public PsiMethod describeContents(PsiElementFactory factory){
        PsiMethod builderMethod = factory.createMethod("describeContents" , PsiType.INT);
        builderMethod.getModifierList().addAnnotation("Override");
        StringBuilder assignBuilder = new StringBuilder()
                .append("return 0;");
        PsiStatement statement = factory.createStatementFromText(assignBuilder.toString(), javaFile);
        builderMethod.getBody().add(statement);
        return builderMethod;
    }

}
