package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import team1.plugin.utils.EditorUtils;
import team1.plugin.utils.MethodCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReplaceSubclassWithFieldsAction extends CommonAction {

    /**
     * isRefactorable adaptor for tab.
     *
     * @param e AnActionEvent from actionPerformed
     * @return true if refactoring is available.
     */
    @Override
    protected boolean isRefactorable(AnActionEvent e) {
        return isRefactorable(EditorUtils.getPsiFile(e));
    }

    /**
     * isRefactorable adaptor for IntentionAction
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
     * @param file
     * @return true if the children methods have the same return type, and all of them returns constant literal, and their are no more duplicate, and their new field does not have duplicated name
     */
    public boolean isRefactorable(PsiFile file) {
        Set<MethodCollection> methodCollectionSet = new HashSet<>();
        MethodCollection.setupCollection(methodCollectionSet, file);

        Map<PsiClass, Integer> parentClassDuplicatedCountMap = getCountMap(methodCollectionSet);


        for (MethodCollection methodCollection : methodCollectionSet) {
            String fieldName = methodCollection.getMethodName().substring(3, 4).toLowerCase() + methodCollection.getMethodName().substring(4);
            if (methodCollection.isAllSameType()
                    && methodCollection.isAllReturnConstant()
                    && parentClassDuplicatedCountMap.get(methodCollection.getParent()) == 1
                    && !methodCollection.hasField(fieldName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * runRefactoring adaptor for tab.
     *
     * @param e AnActionEvent from actionPerformed
     */
    @Override
    protected void runRefactoring(AnActionEvent e) {
        runRefactoring(EditorUtils.getPsiFile(e));
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
     * @return "Replace Subclass With Fields"
     */
    @Override
    public String getRefactorName() {
        return "Replace Subclass With Fields";
    }

    /**
     * Main routine of runRefactoring.
     *
     * @param file File to work on.
     */
    public void runRefactoring(PsiFile file) {
        Set<MethodCollection> methodCollectionSet = new HashSet<>();
        Project project = file.getProject();
        MethodCollection.setupCollection(methodCollectionSet, file);

        Map<PsiClass, Integer> parentClassDuplicatedCountMap = getCountMap(methodCollectionSet);

        for (MethodCollection methodCollection : methodCollectionSet) {
            if (!methodCollection.isAllSameType()
                    || !methodCollection.isAllReturnConstant()
                    || parentClassDuplicatedCountMap.get(methodCollection.getParent()) != 1) {
                continue;
            }

            WriteCommandAction.runWriteCommandAction(project, () -> {
                String fieldName = methodCollection.getMethodName().substring(3, 4).toLowerCase() + methodCollection.getMethodName().substring(4);

                PsiElementFactory factory = JavaPsiFacade.getElementFactory(file.getProject());
                PsiField field = factory.createField(fieldName, methodCollection.getReturnType());

                PsiClass parentClass = methodCollection.getParent();
                PsiType parentType = JavaPsiFacade.getInstance(project).getElementFactory().createType(parentClass);
                PsiTypeElement parentTypeElement = factory.createTypeElement(parentType);
                parentClass.add(field);

                if (methodCollection.getParent().getConstructors().length > 0) {
                    for (PsiMethod constructor : methodCollection.getParent().getConstructors()) {
                        PsiParameter parameter = factory.createParameter(fieldName, methodCollection.getReturnType());
                        PsiStatement assignment = factory.createStatementFromText("this." + fieldName + " = " + fieldName + ";", parentClass);
                        constructor.getParameterList().add(parameter);
                        constructor.getBody().add(assignment);
                    }
                } else {
                    PsiMethod constructor = factory.createConstructor(parentClass.getName());
                    PsiParameter parameter = factory.createParameter(fieldName, methodCollection.getReturnType());
                    PsiStatement assignment = factory.createStatementFromText("this." + fieldName + " = " + fieldName + ";", parentClass);
                    constructor.getParameterList().add(parameter);
                    constructor.getBody().add(assignment);
                    parentClass.add(constructor);
                }

                PsiMethod newMethod = factory.createMethod(methodCollection.getMethodName(), methodCollection.getReturnType());
                PsiStatement newStatement = factory.createStatementFromText("return " + fieldName + ";", parentClass);
                newMethod.getBody().add(newStatement);
                parentClass.add(newMethod);

                for (PsiMethod psiMethod : methodCollection.getMethods()) {
                    // TODO: replace new Statement

                    PsiType subclassType = JavaPsiFacade.getInstance(project).getElementFactory().createType((PsiClass) psiMethod.getParent());
                    PsiTypeElement subclassTypeElement = factory.createTypeElement(subclassType);

                    file.accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitDeclarationStatement(PsiDeclarationStatement statement) {
                            super.visitDeclarationStatement(statement);

                            for (PsiElement e : statement.getDeclaredElements()) {
                                if (e instanceof PsiLocalVariable) {
                                    PsiLocalVariable localVariable = (PsiLocalVariable) e;

                                    if (localVariable.getType().getCanonicalText().equals(subclassType.getCanonicalText())) {
                                        localVariable.getTypeElement().replace(parentTypeElement);
                                    }
                                }
                            }
                        }
                    });

                    file.accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitNewExpression(PsiNewExpression expression) {
                            if (expression.getType().getCanonicalText().equals(subclassType.getCanonicalText())) {
                                super.visitNewExpression(expression);
                                PsiExpressionList newExpressionArgumentList = expression.getArgumentList();
                                newExpressionArgumentList.add(getReturnConstantExpression(psiMethod));
                                PsiExpression candidate = factory.createExpressionFromText("new " + parentType.getCanonicalText() + newExpressionArgumentList.getText(), file);
                                expression.replace(candidate);
                            }
                        }
                    });

                    psiMethod.getParent().delete();
                }
            });
        }
    }

    /**
     * Count parent classes.
     *
     * @param methodCollectionSet
     * @return Map of <Parent class, # of methods in methodCollectionSet>.
     */
    private Map<PsiClass, Integer> getCountMap(Set<MethodCollection> methodCollectionSet) {
        Map<PsiClass, Integer> parentClassDuplicatedCountMap = new HashMap<>();

        for (MethodCollection methodCollection : methodCollectionSet) {
            if (parentClassDuplicatedCountMap.containsKey(methodCollection.getParent())) {
                parentClassDuplicatedCountMap.computeIfPresent(methodCollection.getParent(), (k, v) -> v + 1);
            } else {
                parentClassDuplicatedCountMap.put(methodCollection.getParent(), 1);
            }
        }

        return parentClassDuplicatedCountMap;
    }

    /**
     * Find constant expression returned in psiMethod.
     *
     * @param psiMethod
     * @return Constant expression psiMethod returns.
     */
    private PsiExpression getReturnConstantExpression(PsiMethod psiMethod) {
        final PsiExpression[] expression = new PsiExpression[1];
        psiMethod.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitReturnStatement(PsiReturnStatement statement) {
                super.visitReturnStatement(statement);
                expression[0] = statement.getReturnValue();
            }
        });

        return expression[0];
    }

}
