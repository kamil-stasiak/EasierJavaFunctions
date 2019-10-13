import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import io.vavr.collection.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import utils.FunctionalInterfaceUtils;

public class SimpleFoldingBuilder extends FoldingBuilderEx {

    // TODO move to enum, add to settings
    private String paramToPresentableText(FunctionalStyleParameter param) {
        String params = param.getParameters()
                .map(p -> p.getType() + " " + p.getName())
                .intersperse(", ")
                .fold("", String::concat);
        return param.getReturnType() + " " + param.getName() + "(" + params + ")";
    }

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        return Stream.ofAll(PsiTreeUtil.findChildrenOfType(root, PsiParameter.class))
                .filter(FunctionalInterfaceUtils::isFunctionalInterface)
                .flatMap(FunctionalStyleParameter::of)
                // TODO add TextRange
                .map(param -> new ParameterFoldingDescriptor(param.getPsiParameter(), paramToPresentableText(param)))
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
