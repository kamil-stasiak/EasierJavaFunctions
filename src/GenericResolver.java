import com.intellij.codeInsight.ClassUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import utils.GenericsUtils;
import utils.ParametersUtils;

import java.util.Map;

public class GenericResolver {

    private final Map<String, String> genericsMap;
    private final PsiClass psiClass;
    private final PsiMethod abstractMethod;

    public GenericResolver(PsiType type) {
        this.genericsMap = GenericsUtils.getGenericMap(type);
        this.psiClass = PsiUtil.resolveClassInType(type);
        this.abstractMethod = ClassUtil.getAnyAbstractMethod(psiClass);
    }

    public String getParamsAsString() {
        return ParametersUtils.getParamsAsString(genericsMap, abstractMethod);
    }

    public List<OriginalParameter> getParameterList() {
        return Stream.of(abstractMethod.getParameterList().getParameters())
                .map(param -> new OriginalParameter(param.getName(),
                        genericsMap.getOrDefault(param.getType().getPresentableText(), param.getType().getPresentableText()),
                        param.getType().getPresentableText()))
                .toList();
    }

    public String getReturnType() {
        return getReturnType(genericsMap, abstractMethod.getReturnType().getPresentableText());
    }

    private static String getReturnType(Map<String, String> genericsMap, String presentableText) {
        return genericsMap.getOrDefault(presentableText, presentableText);
    }
}
