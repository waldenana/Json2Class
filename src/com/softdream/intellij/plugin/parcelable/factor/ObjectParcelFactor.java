package com.softdream.intellij.plugin.parcelable.factor;

import com.intellij.psi.PsiField;

/**
 * Created by zewei on 2016-05-03.
 */
public class ObjectParcelFactor extends ParcelFactor {
    public ObjectParcelFactor(PsiField field) {
        super(field);
    }

    @Override
    public String writeValue(String parcel) {
        if (isArray)
            return parcel + ".writeTypedArray(this." + field.getName() + ", flags);";
        return parcel + ".writeParcelable(this." + field.getName() + ", flags);";
    }

    @Override
    public String readValue(String parcel) {
        if (isArray)
            return "var." + field.getName() + " = " + parcel + ".createTypedArray(" + field.getType().getDeepComponentType().getCanonicalText() + ".CREATOR);";
        return "var." + field.getName() + " = " + parcel + ".readParcelable(" + field.getType().getCanonicalText() + ".class.getClassLoader());";
    }
}
