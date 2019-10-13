package utils;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiTypeElementImpl;
import io.vavr.collection.Stream;

public class FunctionalInterfaceUtils {
    public static boolean isFunctionalInterface(PsiParameter parameter) {
        return Stream.of(parameter.getChildren())
                .find(e -> e instanceof PsiTypeElement)
                .map(e -> (PsiTypeElementImpl) e)
                .map(PsiTypeElementImpl::getInnermostComponentReferenceElement)
                .map(PsiJavaCodeReferenceElement::getQualifiedName)
                // TODO resolve class in type
                .map(qualifiedName -> JavaPsiFacade.getInstance(parameter.getProject()).findClass(qualifiedName, parameter.getType().getResolveScope()))
                .map(PsiModifierListOwner::getAnnotations)
                .toStream()
                .flatMap(Stream::of)
                .map(PsiElement::getText)
                // TODO do something better than string comparison
                .exists(e -> e.equals("@java.lang.FunctionalInterface"));
    }
}
