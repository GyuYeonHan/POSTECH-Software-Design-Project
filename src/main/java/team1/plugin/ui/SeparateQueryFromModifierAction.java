package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiExpressionStatementImpl;
import com.intellij.psi.impl.source.tree.java.PsiReturnStatementImpl;
import org.jetbrains.annotations.NotNull;
import team1.plugin.utils.EditorUtils;
import team1.plugin.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class SeparateQueryFromModifierAction extends CommonAction {

    List<PsiMethod> refactorable_methods;

    /**
     * Check whether this action has code smell.
     *
     * @param e Carries information on the invocation place and data available
     */
    @Override
    protected boolean isRefactorable(AnActionEvent e) {
        return isRefactorable(EditorUtils.getPsiFile(e));
    }

    /**
     * Check whether this action has code smell.
     *
     * @param project the project in which the availability is checked.
     * @param editor  the editor in which the intention will be invoked.
     * @param file    the file open in the editor.
     * @return
     */
    @Override
    protected boolean isRefactorable(@NotNull Project project, Editor editor, PsiFile file) {
        return isRefactorable(file);
    }

    /**
     * Check whether this action has code smell.
     *
     * @param file the file open in the editor.
     */
    public boolean isRefactorable(PsiFile file) {
        refactorable_methods = new ArrayList<PsiMethod>();
        for (PsiMethod method : TextUtils.findMethods(file)) {
            if (!PsiPrimitiveType.VOID.equals(method.getReturnType())){
                if (isRefactorableMethod(method))
                    refactorable_methods.add(method);
            }
        }
        // if there are no refactorable_methods, return false.
        return !refactorable_methods.isEmpty();
    }

    /**
     * Check whether this action has code smell.
     *
     * @param method the method open in the editor.
     */
    private boolean isRefactorableMethod(PsiMethod method) {
        ArrayList<PsiAssignmentExpression> assignmentExpressions = new ArrayList<>();
        method.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                super.visitAssignmentExpression(expression);
                assignmentExpressions.add(expression);
            }
        });
        // if there are no assignments, this method is not refactorable so return false.
        return !assignmentExpressions.isEmpty();
    }

    /**
     * Run this refactoring logic
     *
     * @param e AnActionEvent from actionPerformed
     */
    @Override
    protected void runRefactoring(AnActionEvent e) {
        runRefactoring(EditorUtils.getPsiFile(e));
    }

    /**
     * Run refactoring.
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
        return "Separate Query From Modifier";
    }

    /**
     * Run this refactoring logic
     *
     * @param file    the file open in the editor.
     */
    public void runRefactoring(PsiFile file) {

        Project project = file.getProject();
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        //TODO select one method to refactor within the refactorable methods => MAYBE NEEDS UI...?
        for (PsiMethod method : refactorable_methods) {
            PsiMethod new_method = (PsiMethod) method.copy();
            String method_name_get = method.getName();
            String method_name = method_name_get.substring(0, 1).toUpperCase() + method.getName().substring(1);
            PsiType method_return_type = method.getReturnType();
            PsiParameterList method_parameters = method.getParameterList();

            // make new getter and setter as empty method
            PsiMethod getter_method = factory.createMethod("getterOf" + method_name, method_return_type);
            getter_method.getModifierList().setModifierProperty("private", true);

            PsiMethod setter_method = factory.createMethod("setterOf" + method_name, PsiPrimitiveType.VOID);
            setter_method.getModifierList().setModifierProperty("private", true);
            setter_method.getParameterList().replace(method_parameters);

            // fill each getter and setter's body
            for (PsiStatement statement : method.getBody().getStatements()) {
                if (statement instanceof PsiExpressionStatementImpl)
                    setter_method.getBody().add(statement);
                else if (statement instanceof PsiReturnStatementImpl)
                    getter_method.getBody().add(statement);
                else {
                    System.out.println("What's this code statement? type is " + statement.getClass());
                    System.out.println(statement.getText());
                }
            }

            // prepare to replace original method's body to call new getter and setter
            for (PsiStatement statement : new_method.getBody().getStatements())
                statement.delete();
            String getter_call_text = "return " + getter_method.getName() + "();";
            PsiStatement getter_call_statement = factory.createStatementFromText(getter_call_text, null);

            String setter_call_text = setter_method.getName() + "(";
            for (PsiParameter param : setter_method.getParameterList().getParameters())
                setter_call_text += param.getIdentifyingElement().getText() + ",";
            setter_call_text = setter_call_text.substring(0, setter_call_text.length() - 1) + ");";
            PsiStatement setter_call_statement = factory.createStatementFromText(setter_call_text, null);

            // replace and add new setter and getter
            new_method.getBody().add(setter_call_statement);
            new_method.getBody().add(getter_call_statement);

            new_method.add(TextUtils.newline(project));
            new_method.add(setter_method);
            new_method.add(TextUtils.newline(project));
            new_method.add(getter_method);
            EditorUtils.replacePsiElement(method, new_method, project);
            EditorUtils.autoIndentation(file);
        }
    }

}
