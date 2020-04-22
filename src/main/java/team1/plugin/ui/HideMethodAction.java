package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import org.jetbrains.annotations.NotNull;
import team1.plugin.utils.EditorUtils;
import team1.plugin.utils.TextUtils;

import java.util.*;
import java.util.stream.Collectors;

public class HideMethodAction extends CommonAction {

    /**
     * isRefactorable adaptor for tab.
     *
     * @param e  AnActionEvent from actionPerformed
     * @return true if it could be refactored
     */
    @Override
    protected boolean isRefactorable(AnActionEvent e) {

        return _isRefactorable(EditorUtils.getPsiFile(e));
    }

    /**
     * isRefactorable adaptor for IntentionAction.
     *
     * @param project the project in which the availability is checked.
     * @param editor  the editor in which the intention will be invoked.
     * @param file    the file open in the editor.
     * @return true if refactoring action is available
     */
    @Override
    protected boolean isRefactorable(@NotNull Project project, Editor editor, PsiFile file) {
        return _isRefactorable(file);
    }

    /**
     * @param psiFile
     * @return true if refactoring action is available
     */
    boolean _isRefactorable(PsiFile psiFile) {
        if (psiFile == null) {
            return false;
        }

        return !getRefactorableMethods(psiFile).isEmpty();
    }


    /**
     *Search the overall code and return a set of methods
     * that are not referenced in the outer class
     *
     * @param psiFile
     * @return Set of refactorable PsiMethods
     */
    private Set<PsiMethod> getRefactorableMethods(PsiFile psiFile) {
        PsiClass[] classes = ((PsiJavaFileImpl) psiFile).getClasses();

        Map<PsiMethodCallExpression, PsiClass> callMap = new HashMap<>();
        Map<PsiMethod, PsiClass> methodMap = new HashMap<>();

        for (PsiClass aClass : classes) {
            List<PsiMethodCallExpression> methodCallExpressions = TextUtils.findMethodCallExpressions(aClass);

            methodCallExpressions.forEach(methodCallExpression -> callMap.put(methodCallExpression, aClass));

            Arrays.stream(aClass.getMethods()).forEach(method -> methodMap.put(method, aClass));
        }


        for (PsiMethodCallExpression methodCallExpression : callMap.keySet()) {
            PsiMethod method = methodCallExpression.resolveMethod();

            if (methodMap.get(method) != callMap.get(methodCallExpression)) {
                methodMap.remove(method);
            }
        }

        Set<PsiMethod> methodSet = methodMap.keySet().stream().filter(method -> !method.getModifierList().hasModifierProperty("private"))
                .collect(Collectors.toSet());

        return methodSet;
    }


    /**
     * runRefactoring adaptor for tab.
     * @param e AnActionEvent from actionPerformed
     */
    @Override
    protected void runRefactoring(AnActionEvent e) {
        PsiFile psiFile = EditorUtils.getPsiFile(e);
        runRefactoring(psiFile);
    }

    /**
     *  runRefactoring adaptor for IntentionAction.
     *
     * @param project the project in which the intention is invoked.
     * @param editor  the editor in which the intention is invoked.
     * @param file    the file open in the editor.
     */
    @Override
    protected void runRefactoring(@NotNull Project project, Editor editor, PsiFile file) {
        runRefactoring(file);
    }


    @Override
    public String getRefactorName() {
        return "Hide Method";
    }

    /**
     *  Actual Implementation of runrefactoring, get refactorable method sets
     *  from getRefactorableMethod() and change modifierProperty to private
     * @param file
     */
    public void runRefactoring(PsiFile file) {
        Set<PsiMethod> methodSet = getRefactorableMethods(file);

        for (PsiMethod method : methodSet) {
            WriteCommandAction.runWriteCommandAction(method.getProject(), () -> {
                method.getModifierList().setModifierProperty("private", true);
            });
        }
    }
}