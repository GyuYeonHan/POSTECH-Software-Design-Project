package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import team1.plugin.utils.DialogUtils;
import team1.plugin.utils.EditorUtils;
import team1.plugin.utils.TextUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RemoveAssignmentsToParametersAction extends CommonAction {
    /**
     * isRefactorable adaptor for tab.
     *
     * @param e AnActionEvent from actionPerformed
     * @return true if refactoring is available.
     */
    @Override
    protected boolean isRefactorable(AnActionEvent e) {
        PsiElement psiElem = e.getData(LangDataKeys.PSI_ELEMENT);
        PsiFile psiFile = EditorUtils.getPsiFile(e);
        PsiMethod focusMethod = EditorUtils.getFocusedMethod(psiFile, psiElem);
        return _isRefactorable(psiFile, focusMethod);
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
        PsiElement focusElement = file.findElementAt(editor.getCaretModel().getOffset());
        return _isRefactorable(file, EditorUtils.getFocusedMethod(file, focusElement));
    }

    /**
     * Main routine of isRefactorable.
     *
     * @param psiFile File context.
     * @param method Target method.
     * @return true if refactoring is available.
     */
    public boolean _isRefactorable(PsiFile psiFile, PsiMethod method) {
        if(psiFile==null||method==null){
            return false;
        }

        Map<PsiParameter, List<PsiAssignmentExpression>> assignedParameters = getParameterAssignments(method);

        return !assignedParameters.isEmpty();
    }

    /**
     * runRefactoring adaptor for tab.
     *
     * @param e AnActionEvent from actionPerformed
     */
    @Override
    protected void runRefactoring(AnActionEvent e) {
        PsiElement psiElem = e.getData(LangDataKeys.PSI_ELEMENT);
        PsiFile psiFile = EditorUtils.getPsiFile(e);

        PsiMethod focusedMethod = EditorUtils.getFocusedMethod(psiFile,psiElem);
        _runRefactoring(psiFile, focusedMethod);
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
        PsiElement focusElement = file.findElementAt(editor.getCaretModel().getOffset());

        _runRefactoring(file, EditorUtils.getFocusedMethod(file, focusElement));
    }

    /**
     * GUI wrapper for runRefactoring.
     * Get user inputs for new names with dialog, then runRefactoring.
     *
     * @param file File context.
     * @param method Target method.
     */
    private void _runRefactoring(PsiFile file, PsiMethod method)
    {
        Map<PsiParameter, List<PsiAssignmentExpression>> parameters=getParameterAssignments(method);
        Map<PsiAssignmentExpression, String> newNames = getNewNames(getUsedIdentifiers(method),
                parameters.values().stream().flatMap(list -> list.stream()).collect(Collectors.toSet()));

        _runRefactoring(parameters, newNames);
    }

    @Override
    public String getRefactorName() {
        return "Remove Assignments To Parameters";
    }

    /**
     * Main runRefactoring routine.
     *
     * @param parameters
     * @param newNames
     */
    public void _runRefactoring(Map<PsiParameter, List<PsiAssignmentExpression>> parameters, Map<PsiAssignmentExpression, String> newNames) {
       parameters.forEach((parameter, expressions) ->
       {
           Collections.reverse(expressions);
           for (PsiAssignmentExpression expression : expressions)
           {
               PsiElementFactory elementFactory = ServiceManager.getService(parameter.getProject(), PsiElementFactory.class);
               PsiDeclarationStatement declarationStatement = elementFactory.createVariableDeclarationStatement(
                       newNames.get(expression), expression.getLExpression().getType(), expression.getRExpression());

               PsiElement replacedAssignmentStatement = WriteCommandAction.runWriteCommandAction(parameter.getProject(),
                       (Computable<PsiElement>) () -> expression.getParent().replace(declarationStatement));

               for (PsiElement mover = replacedAssignmentStatement.getNextSibling()
                    ; mover != null
                    ; mover = mover.getNextSibling())
               {
                   List<PsiIdentifier> identifierList = TextUtils.findIdentifiers(mover);

                   for (PsiIdentifier identifier : identifierList) {
                       if (identifier.getText().equals(parameter.getName()))
                       {
                           PsiIdentifier newIdentifier = elementFactory.createIdentifier(newNames.get(expression));
                           WriteCommandAction.runWriteCommandAction(parameter.getProject(),
                                   (Computable<PsiElement>) () -> identifier.replace(newIdentifier));
                       }
                   }
               }
           }
       });
    }

    /**
     * Finds assignments to parameters.
     *
     * @param method Target method.
     * @return Map of <Parameter, PsiAssignmentExpression to the parameter>.
     */
    public Map<PsiParameter, List<PsiAssignmentExpression>> getParameterAssignments(PsiMethod method){
        Map<PsiParameter, List<PsiAssignmentExpression>> result
                = new HashMap<PsiParameter, List<PsiAssignmentExpression>>();

        List<PsiAssignmentExpression> assignmentExpressions=TextUtils.findAssignmentExpressions(method);
        PsiParameterList parameters = method.getParameterList();

        for (PsiParameter parameter : parameters.getParameters()) {
            List<PsiAssignmentExpression> changeList = new ArrayList<>();

            List<PsiAssignmentExpression> assignmentList = assignmentExpressions.stream()
                    .filter(exp -> exp.getLExpression().getLastChild().getText()
                            .equals(parameter.getName()))
                    .collect(Collectors.toList());

            if (!assignmentList.isEmpty())
            {
                changeList.add(assignmentList.get(0));
            }

            if (!changeList.isEmpty())
            {
                result.put(parameter, changeList);
            }
        }

        return result;
    }

    /**
     * Find all used identifiers in the method
     *
     * @param method Target method.
     * @return Set of used methods.
     */
    public Set<String> getUsedIdentifiers(PsiMethod method)
    {
        Set <String> result =
                TextUtils.findIdentifiers(method).stream()
                        .map(identifier -> identifier.getText())
                        .collect(Collectors.toSet());

        return result;
    }

    /**
     * Popup dialog to input new names to be replace with.
     *
     * @param invalidIdentifiers Set of invalid name identifiers.
     * @param assignmentExpressions Set of assignments expressions.
     * @return Map of <Assignment expression, New identifier to replace with>.
     */
    public Map<PsiAssignmentExpression, String> getNewNames(
            Set<String> invalidIdentifiers,
            Set<PsiAssignmentExpression> assignmentExpressions){
        Map<PsiAssignmentExpression, String> newNames = new HashMap();


        assignmentExpressions.forEach(assignmentExpression ->
        {
            String input;

            while(true)
            {
                input = DialogUtils.stringInputDialog(assignmentExpression.getProject(),
                                "Type new name to refactor",assignmentExpression.getText());
                if (newNames.containsValue(input) || invalidIdentifiers.contains(input))
                {
                    DialogUtils.showErrorMessage(assignmentExpression.getProject(), getRefactorName(), "Same name cannot be used");
                }
                else
                {
                    break;
                }
            }
            newNames.put(assignmentExpression, input);
        });

        return newNames;
    }
}
