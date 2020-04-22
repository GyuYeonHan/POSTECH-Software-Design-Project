package team1.plugin.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.codeStyle.CodeStyleManagerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditorUtils {
    /**
     * Returns the instance of a text editor from AnActionEvent.
     *
     * @param e Carries information on the invocation place and data available
     * @return the editor data instance.
     */
    private static Editor getEditor(@NotNull AnActionEvent e) {
        return e.getData(CommonDataKeys.EDITOR);
    }

    /**
     * Returns the document edited or viewed in the editor.
     *
     * @param e Carries information on the invocation place and data available
     * @return the document instance.
     */
    private static Document getDocument(@NotNull AnActionEvent e) {
        return getEditor(e).getDocument();
    }

    /**
     * Returns the PSI file for the specified document.
     *
     * @param e Carries information on the invocation place and data available
     * @return the PSI file instance.
     */
    @Nullable
    public static PsiFile getPsiFile(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return null;
        }
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        return documentManager.getPsiFile(EditorUtils.getDocument(e));
    }

    /**
     * Returns the PSI Directory from the AnActionEvent.
     *
     * @param e Carries information on the invocation place and data available
     * @return the PSI Directory instance.
     */
    public static PsiDirectory getPsiDirectory(@NotNull AnActionEvent e) {
        return getPsiDirectory(e.getProject(), getEditor(e));
    }

    /**
     * Returns the PSI Directory.
     *
     * @param project the project.
     * @param editor  the editor.
     * @return the PSI Directory instance.
     */
    public static PsiDirectory getPsiDirectory(Project project, Editor editor) {
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        return documentManager.getPsiFile(editor.getDocument()).getContainingDirectory();
    }

    /**
     * Returns the focused PSI Method.
     *
     * @param psiFile the PSI File.
     * @param element the PSI Element.
     * @return the focused PSI Method instance.
     */
    @Nullable
    public static PsiMethod getFocusedMethod(PsiFile psiFile, PsiElement element) {
        PsiElement mover = element;
        while (mover != null && mover != psiFile) {
            if (mover instanceof PsiMethod) {
                return (PsiMethod) mover;
            }
            mover = mover.getParent();
        }
        return null;
    }

    /**
     * Returns the focused PSI Class.
     *
     * @param psiFile the PSI File.
     * @param element the PSI Element.
     * @return the focused PSI Class instance.
     */
    @Nullable
    public static PsiClass getFocusedClass(PsiFile psiFile, PsiElement element) {
        PsiElement mover = element;
        while (mover != null && mover != psiFile) {
            if (mover instanceof PsiClass) {
                return (PsiClass) mover;
            }
            mover = mover.getParent();
        }
        return null;
    }

    /**
     * Determining whether selected text
     *
     * @param e AnActionEvent from actionPerformed
     * @return {true} if text is selected, {false} otherwise
     */
    public static boolean hasSelectedText(@NotNull AnActionEvent e) {
        Editor editor = getEditor(e);
        if (editor == null) {
            return false;
        }

        return editor.getSelectionModel().hasSelection();
    }

    /**
     * Determining whether selected text
     *
     * @param e AnActionEvent from actionPerformed
     * @return {String} selected string when text is selected, {null} otherwise
     */
    public static String getSelectedText(@NotNull AnActionEvent e) {
        Editor editor = getEditor(e);
        if (editor == null) {
            return null;
        }

        return editor.getSelectionModel().getSelectedText();
    }

    /**
     * Replace selected text to new text
     *
     * @param e AnActionEvent from actionPerformed
     * @param newText replace text
     */
    public static void replaceSelectedText(@NotNull AnActionEvent e, String newText) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        Document document = editor.getDocument();

        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();

        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(start, end, newText)
        );

        primaryCaret.removeSelection();
    }

    /**
     * Replace psi element to new psi element
     *
     * @param initial initial PSI element
     * @param replacement replace PSI element
     * @param e AnActionEvent from actionPerformed
     */
    public static void replacePsiElement(PsiElement initial, PsiElement replacement, @NotNull AnActionEvent e) {
        Project project = e.getProject();
        ApplicationManager.getApplication().invokeLater(() ->
                WriteCommandAction.runWriteCommandAction(project, () ->
                {
                    initial.replace(replacement);
                }));
    }

    /**
     * Replace psi element to new psi element from project
     *
     * @param initial initial PSI element
     * @param replacement replace PSI element
     * @param project IntelliJ project object
     */
    public static void replacePsiElement(PsiElement initial, PsiElement replacement, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            initial.getParent().addBefore(replacement, initial);
            initial.delete();
        });
    }

    /**
     * Replace psi element to new psi element from file
     *
     * @param initial initial PSI element
     * @param replacement replace PSI element
     * @param file Psi File
     */
    public static void replacePsiElement(PsiElement initial, PsiElement replacement, @NotNull PsiFile file) {
        replacePsiElement(initial, replacement, file.getProject());
    }

    /**
     * Replace AssignmentExpression
     *
     * @param initial initial PsiAssignmentExpression
     * @param newAssign new Psi variable
     * @param newDeclare new Psi DeclarationStatement
     * @param project IntelliJ project object
     */
    public static void replaceAssignWithDeclare(PsiAssignmentExpression initial, PsiVariable newAssign,
                                                PsiDeclarationStatement newDeclare, @NotNull Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElement parent = initial.getParent();

            parent.getParent().addBefore(newDeclare, parent);
            newDeclare.add(newAssign);
            parent.addBefore(newDeclare, initial);

            initial.delete();
        });
    }

    /**
     * Auto indentation for file
     *
     * @param file Psi file
     */
    public static void autoIndentation(PsiFile file) {
        WriteCommandAction.runWriteCommandAction(file.getProject(), () -> {
            CodeStyleManagerImpl c = new CodeStyleManagerImpl(file.getProject());
            c.reformat(file);
        });
    }
}
