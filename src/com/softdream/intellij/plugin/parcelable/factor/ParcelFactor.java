package com.softdream.intellij.plugin.parcelable.factor;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;

/**
 * Created by zewei on 2016-05-03.
 */
public abstract class ParcelFactor {

    protected PsiField field;
    protected boolean isArray = false;

    public ParcelFactor(PsiField field) {
        this.field = field;
        isArray = field.getType() instanceof PsiArrayType;
    }

    public abstract String writeValue(String parcel);

    public abstract String readValue(String parcel);

    public static ParcelFactor getFactor(PsiField field) {
        PsiType psiType = field.getType().getDeepComponentType();
        if (psiType instanceof PsiPrimitiveType || psiType.getCanonicalText().equals("java.lang.String")) {
            PrimitiveParcelFactor factor = psiType == PsiType.BOOLEAN ? new BooleanParcelFactor(field) : new PrimitiveParcelFactor(field);
            String typename = psiType.getPresentableText();
            factor.setTypeName(typename.substring(0, 1).toUpperCase() + typename.substring(1));
            return factor;
        }
        return new ObjectParcelFactor(field);
    }
}
