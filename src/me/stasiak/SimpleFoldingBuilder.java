package me.stasiak;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import io.vavr.collection.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.stasiak.FunctionStyle.SCALA;
import static me.stasiak.ParametersUtils.getTextRange;

class SimpleFoldingBuilder extends FoldingBuilderEx {

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        return Stream.ofAll(PsiTreeUtil.findChildrenOfType(root, PsiParameter.class))
                .filter(FunctionalInterfaceUtils::isFunctionalInterface)
                .flatMap(psiParameter -> FoldingUtils.parameterToDescriptorInput(psiParameter, SCALA.getFunction()))
                .map(tuple -> new ParameterFoldingDescriptor(tuple._1, tuple._2, getTextRange(tuple._2)))
                .toJavaArray(ParameterFoldingDescriptor.class);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }
}
