package me.stasiak;

import com.intellij.codeInsight.ClassUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

import java.util.Map;
import java.util.function.Function;

import static io.vavr.API.For;

class FoldingUtils {
    static Option<Tuple2<String, PsiParameter>> parameterToDescriptorInput(PsiParameter psiParameter, Function<PresentableFunctionDto, String> paramToText) {
        Option<PsiType> type = PsiParameterUtils
                .toPsiTypeElement(psiParameter)
                .map(PsiTypeElement::getType);

        Map<String, String> map = type.map(GenericsUtils::getGenericMap).get();
        Option<PsiMethod> abstractMethod = type
                .map(PsiUtil::resolveClassInType)
                .map(ClassUtil::getAnyAbstractMethod);

        List<PresentableParameter> parameters = abstractMethod
                .map(PsiMethod::getParameterList)
                .map(PsiParameterList::getParameters)
                .toStream()
                .flatMap(Stream::of)
                .map(param -> getPresentableParameter(map, param))
                .toList();

        return For(
                Option.of(psiParameter.getName()),
                getReturnType(map, abstractMethod))
                .yield((name, returnType) -> new Tuple2<>(
                        paramToText.apply(new PresentableFunctionDto(name, parameters, returnType)),
                        psiParameter))
                .toOption();
    }

    private static Option<String> getReturnType(Map<String, String> map, Option<PsiMethod> abstractMethod) {
        return abstractMethod
                .map(PsiMethod::getReturnType)
                .map(PsiType::getPresentableText)
                .map(text -> getOrIdentity(map, text));
    }

    private static PresentableParameter getPresentableParameter(Map<String, String> map, PsiParameter param) {
        return new PresentableParameter(
                param.getName(),
                getOrIdentity(map, param.getType().getPresentableText()),
                param.getType().getPresentableText());
    }

    private static String getOrIdentity(Map<String, String> map, String text) {
        return map.getOrDefault(text, text);
    }
}

