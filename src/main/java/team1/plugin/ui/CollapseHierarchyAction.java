package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;
import team1.plugin.utils.EditorUtils;
import team1.plugin.utils.TextUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class CollapseHierarchyAction extends CommonAction {
    static final int THRESHOLD = 2;

    /**
     * isRefactorable adaptor for tab.
     *
     * @param e Event message.
     * @return true if refactoring is available.
     */
   @Override
    protected boolean isRefactorable(AnActionEvent e) {
        PsiFile file = EditorUtils.getPsiFile(e);
        return isRefactorable(file, EditorUtils.getFocusedClass(file, e.getData(LangDataKeys.PSI_ELEMENT)));
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
        return isRefactorable(file, EditorUtils.getFocusedClass(file, focusElement));
    }

    /**
     * Main routine of isRefactorable.
     *
     * @param file File context of refactoring.
     * @param aClass Target class.
     * @return true if refactoring is available.
     */
    public boolean isRefactorable(PsiFile file, PsiClass aClass) {
        //Not null
        if (file == null || aClass == null) {
            return false;
        }

        //No parent class
        if (aClass.getSuperClass() == null) {
            return false;
        }

        //Has child class
        if (!TextUtils.findSubClasses(file, aClass).isEmpty()) {
            return false;
        }

        //Has overriding function
        if (!TextUtils.findOverridingMethods(aClass).isEmpty()) {
            return false;
        }

        //Extends interface
        if (aClass.getInterfaces().length != 0) {
            return false;
        }

        //Has field name conflicts
        if (findNameConflicts(aClass)) {
            return false;
        }

        //Has Abnormal Constructor
        if (aClass.getConstructors().length != 0) {
            return false;
        }

        //Returns self type
        if (findSelfReturns(aClass)) {
            return false;
        }

        //Calls super()
        if (findSuperCalls(aClass)) {
            return false;
        }

        //Too many changes
        if (countChanges(aClass) > THRESHOLD) {
            return false;
        }

        return true;
    }

    /**
     * Find super() calls in the class.
     *
     * @param aClass Target class.
     * @return true if there is any super() call.
     */
    private boolean findSuperCalls(PsiClass aClass) {
        return TextUtils.findReferenceExpressions(aClass)
                .stream()
                .anyMatch(reference -> Objects.equals(reference.getLastChild().getText(), "super"));
    }

    /**
     * Find field name conflicts between superclass and subclass.
     *
     * @param aClass Target subclass.
     * @return true if there is any name conflict.
     */
    private boolean findNameConflicts(PsiClass aClass) {
        Stream<PsiField> superFields = Arrays.stream(aClass.getSuperClass().getFields());

        return Arrays.stream(aClass.getFields())
                .anyMatch(field -> superFields.anyMatch(superField -> Objects.equals(superField.getName(), field.getName())));
    }

    /**
     * Find methods returns type of itself.
     *
     * @param aClass Target class.
     * @return true if there is any return of own type.
     */
    private boolean findSelfReturns(PsiClass aClass) {
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(aClass.getProject());

        PsiType type = factory.createType(aClass);

        return Arrays.stream(aClass.getMethods()).anyMatch(method -> Objects.equals(method.getReturnType(), type));
    }

    /**
     * Count methods and fields in the class.
     *
     * @param aClass Target class.
     * @return Number of newly declared methods and fields.
     */
    private int countChanges(PsiClass aClass) {
        return aClass.getFields().length + aClass.getMethods().length;
    }

    /**
     * runRefactoring adaptor for tab.
     *
     * @param e Event message.
     */
    @Override
    protected void runRefactoring(AnActionEvent e) {
        PsiFile file = EditorUtils.getPsiFile(e);

        runRefactoring(file, EditorUtils.getFocusedClass(file, e.getData(LangDataKeys.PSI_ELEMENT)));
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
        runRefactoring(file, EditorUtils.getFocusedClass(file, focusElement));
    }

    /**
     * Main routine of runRefactoring
     *
     * @param file File context of refactoring.
     * @param aClass Target class.
     */
    public void runRefactoring(PsiFile file, PsiClass aClass) {
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(file.getProject());

        PsiClass superClass = aClass.getSuperClass();
        PsiElement anchor;

        //Replace super with this
        //super.x
        for (PsiSuperExpression superExpression : TextUtils.findSuperExpression(aClass)) {
            WriteCommandAction.runWriteCommandAction(file.getProject(), () -> {
                superExpression.replace(factory.createExpressionFromText("this", null));
            });
        }

        //Move CodeBlock
        if (superClass.getFields().length > 0) {
            anchor = superClass.getFields()[superClass.getFields().length - 1];
        } else {
            anchor = superClass.getLBrace();
        }

        WriteCommandAction.runWriteCommandAction(file.getProject(), () -> {
            anchor.getParent().addRangeAfter(aClass.getLBrace().getNextSibling(),
                    aClass.getRBrace().getPrevSibling(), anchor);
        });

        //Redirect references
        List<PsiJavaCodeReferenceElement> references = TextUtils.findReferenceElements(file)
                .stream()
                .filter(element -> element.resolve() == aClass)
                .collect(Collectors.toList());

        for (PsiJavaCodeReferenceElement reference : references) {
            PsiIdentifier newIdentifier = factory.createIdentifier(superClass.getName());
            EditorUtils.replacePsiElement(reference.getFirstChild(), newIdentifier, file);
        }

        WriteCommandAction.runWriteCommandAction(file.getProject(), () -> {
            aClass.delete();
        });

    }

    @Override
    public String getRefactorName() {
        return "Collapse Hierarchy";
    }
}
