package utils;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import io.vavr.collection.Stream;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Map;

public class ParametersUtils {
    public static String getParamsAsString(Map<String, String> genericsMap, PsiMethod abstractMethod) {
        return Stream.of(abstractMethod.getParameterList().getParameters())
                .map(param -> new AbstractMap.SimpleEntry<>(param.getName(), param.getType().getPresentableText()))
                .map(param -> new AbstractMap.SimpleEntry<>(param.getKey(), genericsMap.getOrDefault(param.getValue(), param.getValue())))
                .map(param -> param.getValue() + " " + param.getKey())
                .intersperse(", ")
                .fold("", String::concat);
    }

    public static TextRange getTextRange(PsiParameter psiParameter) {
        // TODO remove annotations and modifier
        return new TextRange(psiParameter.getTextRange().getStartOffset(), psiParameter.getTextRange().getEndOffset());
    }
}
