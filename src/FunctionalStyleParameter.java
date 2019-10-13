import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.util.Objects.isNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FunctionalStyleParameter {

    private String name;
    private List<OriginalParameter> parameters;
    private String returnType;
    private PsiParameter psiParameter;

    public static Option<FunctionalStyleParameter> of(PsiParameter psiParameter) {
        // TODO ugly code :( refactor needed
        String name = psiParameter.getName();
        if (isNull(name)) {
            return Option.none();
        }

        PsiType type = Stream.of(psiParameter.getChildren())
                .find(e -> e instanceof PsiTypeElement)
                .map(e -> (PsiTypeElement) e)
                .map(PsiTypeElement::getType)
                .get();

        // TODO refactor!
        GenericResolver genericResolver = new GenericResolver(type);
        List<OriginalParameter> paramsList = genericResolver.getParameterList();
        if (isNull(paramsList)) {
            return Option.none();
        }

        String returnType = genericResolver.getReturnType();
        if (isNull(returnType)) {
            return Option.none();
        }

        return Option.of(new FunctionalStyleParameter(name, paramsList, returnType, psiParameter));
    }
}
