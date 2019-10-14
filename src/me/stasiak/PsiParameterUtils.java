package me.stasiak;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiTypeElement;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

class PsiParameterUtils {
    static Option<PsiTypeElement> toPsiTypeElement(PsiParameter psiParameter) {
        return Option.of(psiParameter)
                .map(PsiElement::getChildren)
                .toStream()
                .flatMap(Stream::of)
                .find(e -> e instanceof PsiTypeElement)
                .map(e -> (PsiTypeElement) e);
    }
}
