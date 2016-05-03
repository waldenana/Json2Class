package com.softdream.intellij.plugin.parcelable.factor;

import com.intellij.psi.PsiField;

/**
 * Created by zewei on 2016-05-03.
 */
public class BooleanParcelFactor extends PrimitiveParcelFactor {

    public BooleanParcelFactor(PsiField field) {
        super(field);
    }

    @Override
    public String writeValue(String parcel) {
        StringBuilder sb = new StringBuilder();
        sb.append(parcel)
                .append(".write");
        if (!isArray) {
            sb.append("Byte(")
                    .append(field.getName()).append(" ? (byte) 1 : (byte) 0);");
            return sb.toString();
        }
        sb.append(typeName).append("Array(this.")
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
                .append(".read");
        if (!isArray) {
            sb.append("Byte() != 0;");
            return sb.toString();
        }
        sb.append(typeName);
        sb.append("Array();");
        return sb.toString();
    }
}
