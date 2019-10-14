package me.stasiak;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import io.vavr.collection.Stream;

class FunctionalInterfaceUtils {
    static boolean isFunctionalInterface(PsiParameter parameter) {
        return PsiParameterUtils.toPsiTypeElement(parameter)
                .map(PsiTypeElement::getType)
                .map(PsiUtil::resolveClassInType)
                .map(PsiModifierListOwner::getAnnotations)
                .toStream()
                .flatMap(Stream::of)
                .map(PsiElement::getText)
                .exists(e -> ("@" + FunctionalInterface.class.getCanonicalName()).equals(e));
    }
}
