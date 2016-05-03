package com.softdream.intellij.plugin.parcelable.factor;

import com.intellij.psi.PsiField;

/**
 * Created by zewei on 2016-05-03.
 */
public class PrimitiveParcelFactor extends ParcelFactor {
    protected String typeName;

    public PrimitiveParcelFactor(PsiField field) {
        super(field);
    }

    @Override
    public String writeValue(String parcel) {
        StringBuilder sb = new StringBuilder();
        sb.append(parcel)
                .append(".write")
                .append(typeName);
        if (isArray)
            sb.append("Array");
        sb.append("(this.")
                .append(field.getName())
                .append(");");
        return sb.toString();
    }

    @Override
    public String readValue(String parcel) {
        StringBuilder sb = new StringBuilder("var.");
        sb.append(field.getName())
                .append("=")
                .append(parcel)
                .append(".read")
                .append(typeName);
        if (isArray)
            sb.append("Array");
        sb.append("();");
        return sb.toString();
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
