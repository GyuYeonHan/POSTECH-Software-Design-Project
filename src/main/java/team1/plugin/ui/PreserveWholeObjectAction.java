package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import team1.plugin.utils.EditorUtils;

import java.util.*;

public class PreserveWholeObjectAction extends CommonAction {
    /**
     * @param e AnActionEvent from actionPerformed
     * @return true if refactoring action is available
     */
    @Override
    protected boolean isRefactorable(AnActionEvent e) {
        return isRefactorable(EditorUtils.getPsiFile(e));
    }

    /**
     * @param project the project in which the availability is checked.
     * @param editor  the editor in which the intention will be invoked.
     * @param file    the file open in the editor.
     * @return true if refactoring action is available
     */
    @Override
    protected boolean isRefactorable(@NotNull Project project, Editor editor, PsiFile file) {
        return isRefactorable(file);
    }

    /**
     * @param file
     * @return true if refactoring action is available
     */
    public boolean isRefactorable(PsiFile file) {
        Set<PsiReferenceExpression> paramSet = findReferenceExpression(file);

        List<String> refList = new ArrayList<>();
        file.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitDeclarationStatement(PsiDeclarationStatement decl) {
                super.visitDeclarationStatement(decl);
                PsiElement[] c = decl.getDeclaredElements();
                PsiLocalVariable var = ((PsiLocalVariable) c[0]);
                PsiExpression init = var.getInitializer();

                if (init != null) {
                    for (PsiReferenceExpression refExp : paramSet) {
                        if (Objects.equals(var.getName(), refExp.getText())) {
                            PsiElement iter;
                            for (iter = init.getFirstChild(); iter.getFirstChild() != null; iter = iter.getFirstChild()) {
                                //Nothing
                            }
                            PsiElement firstRef = iter.getParent();
                            refList.add(firstRef.getText());
                        }
                    }
                }
            }
        });

        List<String> countList = new ArrayList<>();
        for (String s : refList) {
            if (countList.contains(s)) {
                return true;
            } else {
                countList.add(s);
            }
        }

        return false;
    }

    /**
     * @param file
     * @return set of referenceExpression
     */
    private Set<PsiReferenceExpression> findReferenceExpression(@NotNull PsiFile file) {
        Set<PsiReferenceExpression> paramSet = new HashSet<>();

        file.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression call) {
                super.visitMethodCallExpression(call);

                PsiExpressionList paramList = (PsiExpressionList) call.getLastChild();
                for (PsiExpression exp : paramList.getExpressions()) {
                    if (exp instanceof PsiReferenceExpression) {
                        paramSet.add((PsiReferenceExpression) exp);
                    }
                }
            }
        });

        return paramSet;
    }

    /**
     * runRefactoring adaptor for Tab
     *
     * @param e AnActionEvent from actionPerformed
     */
    @Override
    protected void runRefactoring(AnActionEvent e) {
        runRefactoring(EditorUtils.getPsiFile(e));
    }

    /**
     * runRefactoring adaptor for IntentionAction
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
        return "Preserve Whole Object";
    }

    /**
     * Actual Implementation of runRefactoring
     * @param file
     */
    public void runRefactoring(PsiFile file) {
        Project project = file.getProject();
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        Set<PsiReferenceExpression> paramSet = findReferenceExpression(file);
        List<PsiDeclarationStatement> deletedDeclSet = new ArrayList<>();
        List<String> deletedStringList = new ArrayList<>();

        Set<String> refSet = new HashSet<>();
        file.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitDeclarationStatement(PsiDeclarationStatement decl) {
                super.visitDeclarationStatement(decl);
                PsiElement[] c = decl.getDeclaredElements();
                PsiLocalVariable var = ((PsiLocalVariable) c[0]);
                PsiExpression init = var.getInitializer();

                if (init instanceof PsiReferenceExpression || init instanceof PsiMethodCallExpression) {
                    for (PsiReferenceExpression refExp : paramSet) {
                        if (Objects.equals(var.getName(), refExp.getText())) {
                            PsiElement iter;
                            for (iter = init.getFirstChild(); iter.getFirstChild() != null; iter = iter.getFirstChild()) {
                                //Nothing
                            }
                            PsiElement firstRef = iter.getParent();
                            deletedDeclSet.add(decl);
                            deletedStringList.add(var.getName());
                            refSet.add(firstRef.getText());
                        }
                    }
                }
            }
        });

        for (PsiDeclarationStatement decl : deletedDeclSet) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                decl.delete();
            });
        }

        for (PsiReferenceExpression ref : paramSet) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                if (deletedStringList.contains(ref.getText())) {
                    PsiElement methodParamList = ref.getParent();
                    for (String s : refSet) {
                        PsiClass cls = factory.createClass(s);
                        PsiReferenceExpression newRef = factory.createReferenceExpression(cls);
                        methodParamList.add(newRef);
                        refSet.remove(s);
                    }
                    ref.delete();
                }
            });
        }

    }
}
