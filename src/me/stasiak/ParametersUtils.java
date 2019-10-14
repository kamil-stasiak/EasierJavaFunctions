package me.stasiak;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiParameter;

import static me.stasiak.PsiParameterUtils.toPsiTypeElement;

class ParametersUtils {
    static TextRange getTextRange(PsiParameter psiParameter) {
        return new TextRange(toPsiTypeElement(psiParameter).get().getTextRange().getStartOffset(), psiParameter.getTextRange().getEndOffset());
    }
}
