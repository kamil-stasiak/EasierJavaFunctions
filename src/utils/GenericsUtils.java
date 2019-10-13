package utils;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import io.vavr.control.Option;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GenericsUtils {
    public static Map<String, String> getGenericMap(PsiType type) {
        return Option.of(type)
                .map(psiType -> (PsiClassReferenceType) psiType)
                .map(PsiClassReferenceType::resolveGenerics)
                .map(JavaResolveResult::getSubstitutor)
                .map(PsiSubstitutor::getSubstitutionMap)
                .toStream()
                .flatMap(Map::entrySet)
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey().getName(), e.getValue().getPresentableText()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

}
