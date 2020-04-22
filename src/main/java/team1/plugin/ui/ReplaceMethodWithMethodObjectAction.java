package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiElementFactoryImpl;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import team1.plugin.utils.DialogUtils;
import team1.plugin.utils.EditorUtils;
import team1.plugin.utils.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplaceMethodWithMethodObjectAction extends CommonAction {
    /**
     * @param e AnActionEvent from actionPerformed
     * @return if the code can be refactored
     */
    @Override
    protected boolean isRefactorable(AnActionEvent e) {
        PsiFile file = EditorUtils.getPsiFile(e);
        PsiMethod method = EditorUtils.getFocusedMethod(file, e.getData(LangDataKeys.PSI_ELEMENT));

        return _isRefactorable(method);
    }

    /**
     * @param project the project in which the availability is checked.
     * @param editor  the editor in which the intention will be invoked.
     * @param file    the file open in the editor.
     * @return if it can be refactored
     */
    @Override
    protected boolean isRefactorable(@NotNull Project project, Editor editor, PsiFile file) {
        PsiElement focusElement = file.findElementAt(editor.getCaretModel().getOffset());
        return _isRefactorable(EditorUtils.getFocusedMethod(file, focusElement));
    }

    /**
     * @param method from isRefactorable
     * @return true if there are no reference to private or protected field or method in the method
     */
    public boolean _isRefactorable(PsiMethod method) {
        if (method == null) {
            return false;
        }

        if (method.getTextLength() < 100) {
            return false;
        }

        List<PsiReferenceExpression> references = TextUtils.findReferenceExpressions(method);
        for (PsiReferenceExpression reference : references) {
            PsiElement element = reference.resolve();

            if (element instanceof PsiField) {
                PsiField field = (PsiField) element;

                if ((field.getModifierList().hasModifierProperty(PsiModifier.PRIVATE)
                        || field.getModifierList().hasModifierProperty(PsiModifier.PROTECTED))) {
                    return false;
                }
            }
        }

        List<PsiMethodCallExpression> methodCalls = TextUtils.findMethodCallExpressions(method);

        return !methodCalls.stream().anyMatch(call ->
                call.resolveMethod().getModifierList().hasModifierProperty(PsiModifier.PRIVATE)
                        || call.resolveMethod().getModifierList().hasModifierProperty(PsiModifier.PROTECTED));
    }

    /**
     * @param e AnActionEvent from actionPerformed
     */
    @Override
    protected void runRefactoring(AnActionEvent e) {
        PsiFile file = EditorUtils.getPsiFile(e);
        PsiMethod method = EditorUtils.getFocusedMethod(file, e.getData(LangDataKeys.PSI_ELEMENT));
        String newName = DialogUtils.stringInputDialog(e.getProject(), "Name of the new class", method.getName());
        if (newName == null) {
            return;
        }
        if (newName.isEmpty()) {
            newName = "_" + method.getName();
        }
        _runRefactoring(file, method, newName);
    }

    /**
     * @param project the project in which the intention is invoked.
     * @param editor  the editor in which the intention is invoked.
     * @param file    the file open in the editor.
     */
    @Override
    protected void runRefactoring(@NotNull Project project, Editor editor, PsiFile file) {
        PsiElement focusElement = file.findElementAt(editor.getCaretModel().getOffset());
        PsiMethod method = EditorUtils.getFocusedMethod(file, focusElement);
        String newName = DialogUtils.stringInputDialog(project, "Name of the new class", method.getName());
        if (newName == null) {
            return;
        }
        if (newName.isEmpty()) {
            newName = "_" + method.getName();
        }
        _runRefactoring(file, method, newName);
    }

    /**
     * @return "Replace Method With Method Object"
     */
    @Override
    public String getRefactorName() {
        return "Replace Method With Method Object";
    }

    /**
     * Create new class with name, and extract algorithm to new method in the class.
     * In the original method, it make one object of the new class, and call "calculate" method
     * @param file from runRefactoring
     * @param method from runRefactoring
     * @param name from runRefactoring
     */
    public void _runRefactoring(PsiFile file, PsiMethod method, String name) {
        PsiCodeBlock codeBlock = method.getBody();
        PsiElementFactoryImpl factory = new PsiElementFactoryImpl((PsiManagerEx.getInstanceEx(file.getProject())));
        PsiClass newClass = factory.createClass(name);
        PsiMethod newConstructor = factory.createConstructor(name, newClass);
        newConstructor.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);

        PsiClass parentClass = (PsiClass) method.getParent();

        String NameOfOriginalClass = "_" + parentClass.getName().toLowerCase();

        WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                newConstructor.getParameterList()
                        .add(factory.createParameter(NameOfOriginalClass
                                , factory.createType(parentClass))));

        boolean isNeededToAddFieldOfOriginalClass = false;

        List<PsiReferenceExpression> references = TextUtils.findReferenceExpressions(method);

        for (PsiReferenceExpression reference : references) {
            PsiElement element = reference.resolve();

            if (element instanceof PsiField) {
                isNeededToAddFieldOfOriginalClass = true;
                break;
            } else if (element instanceof PsiMethod) {
                isNeededToAddFieldOfOriginalClass = true;
                break;
            }
        }

        if (isNeededToAddFieldOfOriginalClass) {
            PsiField fieldOfOriginalClass = factory.createField(NameOfOriginalClass, factory.createType(parentClass));
            WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                    newClass.add(fieldOfOriginalClass));
            PsiStatement assignmentOfOriginalClassCodeBlock = factory.createStatementFromText("this." + NameOfOriginalClass + " = " + NameOfOriginalClass + ";", null);
            WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                    newConstructor.getBody().add(assignmentOfOriginalClassCodeBlock));
        }


        method.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                super.visitLocalVariable(variable);
                PsiType variableType = variable.getType();
                String variableName = variable.getName();
                PsiField variableToField = factory.createField(variableName, variableType);
                WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () -> newClass.add(variableToField));
                PsiExpression initializer = variable.getInitializer();
                if (initializer != null) {
                    PsiStatement assignmentToField = factory.createStatementFromText("this." + variableName + " = " + initializer.getText() + ";", null);
                    WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                            newConstructor.getBody().add(assignmentToField));
                }
            }
        });

        List<PsiStatement> newComputeStatementList = new ArrayList<>(Arrays.asList(codeBlock.getStatements()));

        codeBlock.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitDeclarationStatement(PsiDeclarationStatement statement) {
                super.visitDeclarationStatement(statement);
                newComputeStatementList.remove(statement);
            }

            @Override
            public void visitCallExpression(PsiCallExpression callExpression) {
                super.visitCallExpression(callExpression);
                WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                        callExpression.replace(factory.createExpressionFromText("this." + NameOfOriginalClass + "." + callExpression.getText(), null)));
            }

            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                super.visitReferenceExpression(expression);
                PsiElement element = expression.resolve();

                if (element instanceof PsiField) {
                    WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                            expression.replace(factory.createExpressionFromText("this." + NameOfOriginalClass + "." + expression.getText(), null)));
                }
            }
        });

        WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                newClass.add(newConstructor));

        PsiMethod newCompute = factory.createMethod("compute", method.getReturnType(), newClass);

        for (PsiStatement stat : newComputeStatementList) {
            WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                    newCompute.getBody().add(stat));
        }

        codeBlock.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitComment(PsiComment comment) {
                super.visitComment(comment);
                newCompute.getBody().add(comment);
            }
        });

        WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                newCompute.getParameterList().replace(method.getParameterList()));

        WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                newClass.add(newCompute));

        PsiUtil.setModifierProperty(newClass, PsiModifier.PUBLIC, false);

        WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                file.addAfter(newClass, file.getLastChild()));

        String parameterListString = "";
        PsiParameter[] parameters = method.getParameterList().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            parameterListString += parameters[i].getName();

            if (i != parameters.length - 1)
                parameterListString += ", ";
        }

        PsiCodeBlock newReturnCodeBlock = factory.createCodeBlockFromText("{return new " + name + "(this).compute(" + parameterListString + ");}", null);

        WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                method.getBody().replace(newReturnCodeBlock));
    }
}
