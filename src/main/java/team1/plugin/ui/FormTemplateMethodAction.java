package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiElementFactoryImpl;
import com.intellij.psi.impl.PsiManagerEx;
import team1.plugin.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class FormTemplateMethodAction extends CommonAction {
    /**
     * @param e AnActionEvent from actionPerformed
     * @return true if the code can be refactored
     */
    @Override
    protected boolean isRefactorable(AnActionEvent e) {
        return isRefactorable(EditorUtils.getPsiFile(e));
    }

    /**
     * @param file to check which its code can be refactored
     * @return true if there are only one function whose name and return type is same by sibling classes, and their algorithm are similar.
     */
    public boolean isRefactorable(PsiFile file) {
        Set<MethodCollection> methodCollectionSet = MethodCollection.getMethodCollectionSetFromFile(file);
        if (methodCollectionSet.size() != 1) {
            return false;
        }

        MethodCollection methodCollection = methodCollectionSet.iterator().next();
        if (!methodCollection.isAllSameType()) {
            return false;
        }
        if (methodCollection.getMethods().size() < 2) {
            return false;
        }

        return methodCollection.hasSimilarAlgorithm();
    }

    /**
     * @param project the project in which the availability is checked.
     * @param editor  the editor in which the intention will be invoked.
     * @param file    the file open in the editor.
     * @return true if it can be refactored
     */
    @Override
    protected boolean isRefactorable(@NotNull Project project, Editor editor, PsiFile file) {
        //TODO implement this has code smell. {true} if it has code smell, {false} otherwise
        return isRefactorable(file);
    }

    /**
     * @param e AnActionEvent from actionPerformed
     */
    @Override
    protected void runRefactoring(AnActionEvent e) {
        runRefactoring(EditorUtils.getPsiFile(e));
    }

    /**
     * Add template method of local variable of child classes,
     * and create abstract method for parent class,
     * and extract method of same algorithm to parent class,
     * and delete child methods.
     * @param file which will be refactored
     */
    public void runRefactoring(PsiFile file) {
        MethodCollection methodCollection = MethodCollection.getMethodCollectionSetFromFile(file).iterator().next();

        for (PsiMethod method : methodCollection.getMethods()) {
            addMethodOfLocalVariable(methodCollection, (PsiClass) method.getParent(), file);
        }
        WriteCommandAction.runWriteCommandAction(file.getProject(), () ->
                methodCollection.getParent().getModifierList().setModifierProperty(PsiModifier.ABSTRACT, true));

        PsiMethod extractedMethod = extractMethod(methodCollection, file);
        WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                methodCollection.getParent().add(extractedMethod));
        deleteMethods(methodCollection, file);
    }

    /**
     * @param variableName Local variable name which will be extracted to template method
     * @return The name of template method
     */
    private String createMethodName(String variableName) {
        return "get" + variableName.substring(0, 1).toUpperCase() + variableName.substring(1) + "Amount";
    }

    /**
     * Add protected function implementation of child class which is from assignment to local variable
     * @param methodCollection from runRefactoring
     * @param psiClass child class
     * @param file from runRefactoring
     */
    private void addMethodOfLocalVariable(MethodCollection methodCollection, PsiClass psiClass, PsiFile file) {
        List<PsiStatement> sameStatementList = methodCollection.extractSameStatement();
        List<PsiStatement> differentStatementList = methodCollection.extractDifferentStatement(psiClass);
        PsiElementFactoryImpl factory = new PsiElementFactoryImpl((PsiManagerEx.getInstanceEx(file.getProject())));

        for (PsiStatement sameStatement : sameStatementList) {
            sameStatement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(PsiReferenceExpression expression) {
                    super.visitReferenceExpression(expression);
                    PsiElement element = expression.resolve();

                    String variableName;
                    PsiType variableType;
                    if (element instanceof PsiLocalVariable) {
                        variableName = ((PsiLocalVariable) element).getName();
                        variableType = ((PsiLocalVariable) element).getType();
                    } else if (element instanceof PsiField) {
                        variableName = ((PsiField) element).getName();
                        variableType = ((PsiField) element).getType();
                    } else {
                        return;
                    }

                    String methodName = createMethodName(variableName);
                    PsiClass superClass = methodCollection.getParent();
                    if (!hasMethod(superClass, methodName)) {
                        PsiMethod abstractMethod = factory.createMethod(methodName, variableType);
                        abstractMethod.getModifierList().setModifierProperty(PsiModifier.ABSTRACT, true);
                        abstractMethod.getModifierList().setModifierProperty(PsiModifier.PROTECTED, true);
                        WriteCommandAction.runWriteCommandAction(file.getProject(), () ->
                                abstractMethod.getBody().delete());
                        WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                                superClass.add(abstractMethod));

                    }


                    List<PsiStatement> methodBodyStatementList = new ArrayList<>();

                    if (!hasMethod(psiClass, methodName)) {
                        PsiMethod method = factory.createMethod(methodName, variableType);
                        for (PsiStatement differentStatement : differentStatementList) {
                            differentStatement.accept(new JavaRecursiveElementVisitor() {
                                @Override
                                public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                                    super.visitAssignmentExpression(expression);
                                    if (expression.getLExpression().getReference().resolve().getText().equals(element.getText())) {
                                        methodBodyStatementList.add(differentStatement);
                                    }
                                }

                                @Override
                                public void visitDeclarationStatement(PsiDeclarationStatement statement) {
                                    super.visitDeclarationStatement(statement);
                                    for (PsiElement declaredElement : statement.getDeclaredElements()) {
                                        if (declaredElement instanceof PsiVariable) {
                                            if (((PsiVariable) declaredElement).getName().equals(((PsiVariable) element).getName())) {
                                                methodBodyStatementList.add(differentStatement);
                                            }
                                        }
                                    }
                                }
                            });
                        }

                        for (PsiStatement methodBodyStatement : methodBodyStatementList) {
                            methodBodyStatement.accept(new JavaRecursiveElementVisitor() {
                                @Override
                                public void visitReferenceExpression(PsiReferenceExpression expression) {
                                    super.visitReferenceExpression(expression);
                                    PsiElement resolvedElement = expression.resolve();
                                    if (resolvedElement instanceof PsiLocalVariable && !(expression.getParent() instanceof PsiAssignmentExpression)) {
                                        PsiExpression newExpression = factory.createExpressionFromText(createMethodName(((PsiLocalVariable) resolvedElement).getName()) + "()", null);
                                        WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                                                expression.replace(newExpression));
                                    }
                                }

                                @Override
                                public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                                    super.visitAssignmentExpression(expression);
                                    expression.getRExpression().accept(new JavaRecursiveElementVisitor() {
                                        @Override
                                        public void visitReferenceExpression(PsiReferenceExpression expression) {
                                            super.visitReferenceExpression(expression);
                                            PsiElement resolvedElement = expression.resolve();
                                            if (resolvedElement instanceof PsiLocalVariable) {
                                                PsiExpression newExpression = factory.createExpressionFromText(createMethodName(((PsiLocalVariable) resolvedElement).getName()) + "()", null);
                                                WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                                                        expression.replace(newExpression));
                                            }
                                        }
                                    });
                                }
                            });
                            WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                                    method.getBody().add(methodBodyStatement));
                        }

                        method.getModifierList().setModifierProperty(PsiModifier.PROTECTED, true);
                        PsiStatement methodReturnStatement = factory.createStatementFromText("return " + variableName + ";", null);
                        WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                                method.getBody().add(methodReturnStatement));
                        WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                                psiClass.add(method));
                    }

                }

            });
        }

    }

    /**
     * @param methodCollection child methods to be deleted
     * @param file from runRefactoring
     */
    private void deleteMethods(MethodCollection methodCollection, PsiFile file) {
        for (PsiMethod method : methodCollection.getMethods()) {
            WriteCommandAction.runWriteCommandAction(file.getProject(), method::delete);
        }
    }

    /**
     * @param psiClass parent method
     * @param methodName name of method to check
     * @return true if parent has method of methodName
     */
    private Boolean hasMethod(PsiClass psiClass, String methodName) {
        return Arrays.stream(psiClass.getMethods()).anyMatch(method -> method.getName().equals(methodName));
    }

    /**
     * Extract same code of children method to parent method
     * @param methodCollection form runRefactoring
     * @param file from RunRefactoring
     * @return method of same code
     */
    private PsiMethod extractMethod(MethodCollection methodCollection, PsiFile file) {
        PsiElementFactoryImpl factory = new PsiElementFactoryImpl((PsiManagerEx.getInstanceEx(file.getProject())));
        PsiMethod extractedMethod = factory.createMethod(methodCollection.getMethodName(), methodCollection.getReturnType(), methodCollection.getParent());

        PsiMethod oneOfMethodCollection = methodCollection.getMethods().iterator().next();
        extractedMethod.getModifierList().replace(oneOfMethodCollection.getModifierList());

        for (PsiStatement sameStatement : methodCollection.extractSameStatement()) {
            PsiStatement newStatement = factory.createStatementFromText(sameStatement.getText(), extractedMethod.getContext());

            for (PsiReferenceExpression expression : TextUtils.findReferenceExpressions(newStatement)) {
                PsiElement resolvedElement = expression.resolve();
                if (!(expression.getParent() instanceof PsiMethodCallExpression) && !(resolvedElement instanceof PsiField)) {
                    PsiExpression newExpression = factory.createExpressionFromText(createMethodName(expression.getText()) + "()", null);
                    WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                            expression.replace(newExpression));
                }
            }

            WriteCommandAction.runWriteCommandAction(file.getProject(), (Computable<PsiElement>) () ->
                    extractedMethod.getBody().add(newStatement));
        }
        return extractedMethod;
    }


    /**
     * @param project the project in which the intention is invoked.
     * @param editor  the editor in which the intention is invoked.
     * @param file    the file open in the editor.
     */
    @Override
    protected void runRefactoring(@NotNull Project project, Editor editor, PsiFile file) {
        //TODO implement Run this refactoring logic
        runRefactoring(file);
    }

    /**
     * @return "Form Template Method"
     */
    @Override
    public String getRefactorName() {
        return "Form Template Method";
    }
}
