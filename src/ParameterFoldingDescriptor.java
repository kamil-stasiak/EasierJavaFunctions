import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.psi.PsiParameter;
import utils.ParametersUtils;

public class ParameterFoldingDescriptor extends FoldingDescriptor {

    private final String text;

    public ParameterFoldingDescriptor(PsiParameter psiParameter, String text) {
        super(psiParameter.getNode(), ParametersUtils.getTextRange(psiParameter),
                FoldingGroup.newGroup(String.valueOf(psiParameter.hashCode())));
        this.text = text;
    }

    @Override
    public String getPlaceholderText() {
        return text;
    }
}
