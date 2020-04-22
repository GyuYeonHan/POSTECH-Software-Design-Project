package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import team1.plugin.utils.EditorUtils;
import team1.plugin.utils.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SplitTemporaryVariableAction extends CommonAction {
    /**
     * isRefactorable adaptor for tab.
     *
     * @param e Event message.
     * @return true if refactoring is available.
     */
    @Override
    protected boolean isRefactorable(AnActionEvent e) {
        PsiFile file = EditorUtils.getPsiFile(e);
        return isRefactorable(file);
    }

    /**
     * isRefactorable adaptor for IntentionAction.
     *
     * @param project the project in which the availability is checked.
     * @param editor  the editor in which the intention will be invoked.
     * @param file    the file open in the editor.
     * @return true if refactoring is available.
     */
    @Override
    protected boolean isRefactorable(@NotNull Project project, Editor editor, PsiFile file) {
        return isRefactorable(file);
    }

    /**
     * Main routine of isRefactorable.
     *
     * @param file File context of refactoring.
     * @return true if refactoring is available.
     */
    public boolean isRefactorable(PsiFile file) {
        ArrayList<String> variables = new ArrayList<>();

        List<PsiLocalVariable> variableList = TextUtils.findLocalVariables(file);
        for (PsiLocalVariable variable : variableList) {
            variables.add(variable.getName());
        }

        List<PsiAssignmentExpression> assignmentList = TextUtils.findAssignmentExpressions(file);
        for (PsiAssignmentExpression expression : assignmentList) {
            String variableName = expression.getLExpression().getText();
            if (expression.getOperationSign().getText().equals("=") && variables.contains(variableName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * runRefactoring adaptor for tab.
     *
     * @param e Event message.
     */
    @Override
    protected void runRefactoring(AnActionEvent e) {
        PsiFile file = EditorUtils.getPsiFile(e);
        runRefactoring(file);
    }

    /**
     * runRefactoring adaptor for IntentionAction.
     *
     * @param project the project in which the intention is invoked.
     * @param editor  the editor in which the intention is invoked.
     * @param file    the file open in the editor.
     */
    @Override
    protected void runRefactoring(@NotNull Project project, Editor editor, PsiFile file) {
        runRefactoring(file);
    }

    /**
     * Main routine of runRefactoring
     *
     * @param file File context of refactoring.
     */
    public void runRefactoring(PsiFile file) {
        Project project = file.getProject();
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(file.getProject());

        ArrayList<String> variables = new ArrayList<>();
        HashMap<String, PsiType> variableTypes = new HashMap<>();
        HashMap<String, ArrayList<Integer>> variableOffsets = new HashMap<>();

        // Stores the offset in the text where the variables are first declared, along with the variable types.
        List<PsiLocalVariable> variableList = TextUtils.findLocalVariables(file);
        for (PsiLocalVariable variable : variableList) {
            variables.add(variable.getName());
            variableTypes.put(variable.getName(), variable.getType());
            variableOffsets.put(variable.getName(), new ArrayList<>());
            variableOffsets.get(variable.getName()).add(variable.getTextRange().getEndOffset());
        }

        // Goes through assignments, splitting the local variable if it is a new assignment.
        List<PsiAssignmentExpression> assignmentList = TextUtils.findAssignmentExpressions(file);
        for (PsiAssignmentExpression expression : assignmentList) {
            String variableName = expression.getLExpression().getText();
            if (expression.getOperationSign().getText().equals("=") && variables.contains(variableName)) {
                // Used to make the new variable name
                int identifier = variableOffsets.get(variableName).size();
                String leftName = expression.getLExpression().getText();
                // Replaces right side of assignment due to nested occurrences of local variables.
                replaceAssignmentChilds(expression, leftName, identifier, project, factory);

                PsiLocalVariable localVariable = factory.createResourceVariable(leftName + identifier,
                        variableTypes.get(leftName), expression.getRExpression(), expression.getFirstChild());
                PsiDeclarationStatement declarationStatement = factory.createVariableDeclarationStatement(leftName + identifier,
                        variableTypes.get(leftName), expression.getRExpression());

                // Stores a modified offset, adjusting for the characters in the added type name.
                int lengthDifference = declarationStatement.getTextLength() - expression.getTextLength();
                variableOffsets.get(variableName).add(expression.getTextRange().getEndOffset() + lengthDifference);
                // Replaces assignment statement with local variable statement.
                EditorUtils.replaceAssignWithDeclare(expression, localVariable, declarationStatement, project);
            }
        }

        // Updates all other references of split variables with the new names
        List<PsiExpression> expressionList = TextUtils.findExpressions(file);
        for (PsiExpression expression : expressionList) {
            String varName = expression.getText();
            if (variables.contains(varName)) {
                ArrayList<Integer> assignmentOffsets = variableOffsets.get(varName);
                int currentOffset = expression.getTextRange().getStartOffset();

                int identifier = assignmentOffsets.size() - 1;
                for (Integer assignmentOffset : assignmentOffsets) {
                    if (currentOffset < assignmentOffset) {
                        identifier = assignmentOffsets.indexOf(assignmentOffset) - 1;
                        break;
                    }
                }
                if (identifier < 1) {
                    continue;
                }
                String newName = varName + identifier;
                PsiExpression referenceExpression = factory.createExpressionFromText(newName, null);
                EditorUtils.replacePsiElement(expression, referenceExpression, project);
            }
        }
    }

    /**
     * Replaces references to split variables that are nested in assignment statements with the new variable names.
     * Looks through the right side of assignment expressions for these cases.
     *
     * @param expression    the expression to replace the children of.
     * @param originalName  the original name of the split variable.
     * @param identifier    the number to concatenate to the end of the variable name for uniqueness.
     * @param project       the project in which the intention is invoked.
     * @param factory       the factory previously initialized.
     */
    private void replaceAssignmentChilds(PsiAssignmentExpression expression, String originalName, Integer identifier, Project project, PsiElementFactory factory) {
        for (PsiElement element : expression.getRExpression().getChildren()) {
            if (!(element instanceof PsiReferenceExpression)) {
                if (element instanceof PsiExpression) {
                    replaceChilds((PsiExpression) element, originalName, identifier, project, factory);
                }
            }
            if (element instanceof PsiExpression && element.getText().equals(originalName)) {
                String newName = expression.getLExpression().getText() + (identifier - 1 > 0 ? (identifier - 1) : "");
                PsiExpression referenceExpression = factory.createExpressionFromText(newName, element);
                EditorUtils.replacePsiElement(element, referenceExpression, project);
            }
        }
    }

    /**
     * Replaces references to split variables that are nested in assignment statements with the new variable names.
     * Looks through the children of the expression for the split variables.
     *
     * @param expression    the expression to replace the children of.
     * @param originalName  the original name of the split variable.
     * @param identifier    the number to concatenate to the end of the variable name for uniqueness.
     * @param project       the project in which the intention is invoked.
     * @param factory       the factory previously initialized.
     */
    private void replaceChilds(PsiExpression expression, String originalName, Integer identifier, Project project, PsiElementFactory factory) {
        for (PsiElement element : expression.getChildren()) {
            if (!(element instanceof PsiReferenceExpression)) {
                if (element instanceof PsiExpression) {
                    replaceChilds((PsiExpression) element, originalName, identifier, project, factory);
                }
            }
            if (element instanceof PsiExpression && element.getText().equals(originalName)) {
                String newName = originalName + (identifier - 1 > 0 ? (identifier - 1) : "");
                PsiExpression referenceExpression = factory.createExpressionFromText(newName, element);
                EditorUtils.replacePsiElement(element, referenceExpression, project);
            }
        }
    }

    @Override
    public String getRefactorName() {
        return "Split Temporary Variable";
    }
}
