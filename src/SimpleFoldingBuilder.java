import com.intellij.codeInsight.ClassUtil;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.PsiTypeElementImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/*
  TODO
  - remove empty methods
  - move methods invocations to buildFoldRegions (+ more filters)
  - move methods declaration to utils class
  - ignore final and annotations
  - add style settings (kotlin, java, scala)

 */
// java:
//     boolean validator(String t)
// scala:
//     validator: (t: String) => boolean
//     validator: (String) => boolean
// kotlin:
//     validator: (String) -> String
public class SimpleFoldingBuilder extends FoldingBuilderEx {

    private void testowa(String a1, Function<String, String> fun1, Predicate<String> pred1, String a2) {

    }

    private void abc(Predicate<String> pred1) {
    }

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        return Stream.ofAll(PsiTreeUtil.findChildrenOfType(root, PsiParameter.class))
                .filter(SimpleFoldingBuilder::isFunctionalInterface)
                .map(this::createFoldingDescriptor)
                .toJavaArray(FoldingDescriptor.class);
    }

    @NotNull
    private FoldingDescriptor createFoldingDescriptor(PsiParameter parameter) {
        return new FoldingDescriptor(parameter.getNode(), textRange(parameter), FoldingGroup.newGroup(String.valueOf(parameter.hashCode()))) {

            @Nullable
            @Override
            public String getPlaceholderText() {
                PsiElement parameterType = Stream.of(parameter.getChildren()).find(e -> e instanceof PsiTypeElement).get();
                PsiType type = ((PsiTypeElementImpl) parameterType).getType();

                Map<PsiTypeParameter, PsiType> genericsMap = genericsMap(parameterType);
                Map<String, String> stringStringGenericsMap = genericsMap.entrySet().stream()
                        .map(e -> new AbstractMap.SimpleEntry<>(e.getKey().getName(), e.getValue().getPresentableText()))
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

                PsiClass psiClass = PsiUtil.resolveClassInType(type);
                PsiMethod anyAbstractMethod = ClassUtil.getAnyAbstractMethod(psiClass);

                String stringParams = Stream.of(anyAbstractMethod.getParameterList().getParameters())
                        .map(param -> new AbstractMap.SimpleEntry<>(param.getName(), param.getType().getPresentableText()))
                        .map(param -> new AbstractMap.SimpleEntry<>(param.getKey(), stringStringGenericsMap.getOrDefault(param.getValue(), param.getValue())))
                        .map(param -> param.getValue() + " " + param.getKey())
                        .intersperse(", ")
                        .fold("", String::concat);

                String returnType = getReturnType(stringStringGenericsMap, anyAbstractMethod.getReturnType().getPresentableText());
                String name = parameter.getName();


                // IMPORTANT: keys can come with no values, so a test for null is needed
                // IMPORTANT: Convert embedded \n to backslash n, so that the string will look like it has LF embedded
                // in it and embedded " to escaped "
                return returnType + " " + name + "(" + stringParams + ")";
            }
        };
    }

    private static String getReturnType(Map<String, String> stringStringGenericsMap, String presentableText) {
        return stringStringGenericsMap.getOrDefault(presentableText, presentableText);
    }

    @NotNull
    private static Map<PsiTypeParameter, PsiType> genericsMap(PsiElement typeElement) {
        return (((PsiClassReferenceType) ((PsiTypeElement) typeElement).getType()))
                .resolveGenerics()
                .getSubstitutor()
                .getSubstitutionMap();
    }

    @NotNull
    private TextRange textRange(PsiParameter psiParam) {
        // Tutaj trzeba skipnąć adnotacje i final
        return new TextRange(psiParam.getTextRange().getStartOffset(), psiParam.getTextRange().getEndOffset());
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

    private static boolean isFunctionalInterface(PsiParameter parameter) {
        return Stream.of(parameter.getChildren())
                .find(e -> e instanceof PsiTypeElement)
                .map(e -> (PsiTypeElementImpl) e)
                .map(PsiTypeElementImpl::getInnermostComponentReferenceElement)
                .map(PsiJavaCodeReferenceElement::getQualifiedName)
                .map(qualifiedName -> JavaPsiFacade.getInstance(parameter.getProject()).findClass(qualifiedName, parameter.getType().getResolveScope()))
                .map(PsiModifierListOwner::getAnnotations)
                .toStream()
                .flatMap(Stream::of)
                .map(PsiElement::getText)
                .exists(e -> e.equals("@java.lang.FunctionalInterface"));
    }
}
