package me.stasiak;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiParameter;

class ParameterFoldingDescriptor extends FoldingDescriptor {

    private final String text;

    ParameterFoldingDescriptor(String text, PsiParameter psiParameter, TextRange textRange) {
        super(psiParameter.getNode(), textRange, FoldingGroup.newGroup(String.valueOf(psiParameter.hashCode())));
        this.text = text;
    }

    @Override
    public String getPlaceholderText() {
        return text;
    }
}
