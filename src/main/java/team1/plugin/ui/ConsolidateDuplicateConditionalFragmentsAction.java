package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.codeStyle.CodeStyleManagerImpl;
import org.jetbrains.annotations.NotNull;
import team1.plugin.utils.EditorUtils;
import team1.plugin.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ConsolidateDuplicateConditionalFragmentsAction extends CommonAction {

    private List<PsiIfStatement> if_list;
    private PsiBlockStatement change_then_statement, change_else_statement;
    private PsiIfStatement change_if_statement;
    private String change_string;
    private PsiIfStatement new_if_statement;

    /**
     * isRefactorable adaptor for tab.
     *
     * @param e Event message.
     * @return true if refactoring is available.
     */
    @Override
    protected boolean isRefactorable(AnActionEvent e) {
        return isRefactorable(EditorUtils.getPsiFile(e));
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
     * Main routine of isRefactorable. Goes through if-statements, comparing branches
     * using string comparison in reverse.
     *
     * @param file File context of refactoring.
     * @return true if refactoring is available.
     */
    public boolean isRefactorable(PsiFile file) {
        for (PsiIfStatement psiIfStatement : TextUtils.findIfStatements(file)) {
            if (psiIfStatement.getThenBranch() == null || psiIfStatement.getElseBranch() == null) {
                continue;
            }
            String then_string = psiIfStatement.getThenBranch().getText();
            String else_string = psiIfStatement.getElseBranch().getText();

            if (equal_string(then_string, else_string, 0)) {
                change_then_statement = (PsiBlockStatement) psiIfStatement.getThenBranch();
                change_else_statement = (PsiBlockStatement) psiIfStatement.getElseBranch();
                change_if_statement = psiIfStatement;
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
        PsiFile psiFile = EditorUtils.getPsiFile(e);
        runRefactoring(psiFile);
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
     * Main routine of runRefactoring. Continuously consolidates duplicated conditional
     * fragments.
     *
     * @param file File context of refactoring.
     */
    public void runRefactoring(PsiFile file) {

        Project project = file.getProject();
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        while (isRefactorable(file)) {
            // Check whether then branch is empty.
            boolean is_empty_new_then_statement;
            // Check whether else branch is empty.
            boolean is_empty_new_else_statement;

            new_if_statement = (PsiIfStatement) change_if_statement.copy();
            PsiBlockStatement new_then_statement = (PsiBlockStatement) change_then_statement.copy();
            PsiBlockStatement new_else_statement = (PsiBlockStatement) change_else_statement.copy();

            // Delete the statement element in then statement list.
            PsiStatement[] then_list = new_then_statement.getCodeBlock().getStatements();
            then_list[then_list.length - 1].delete();

            is_empty_new_then_statement = new_then_statement.getCodeBlock().isEmpty();
            new_if_statement.setThenBranch(new_then_statement);

            // Delete the statement element in else statement list.
            PsiStatement[] else_list = new_else_statement.getCodeBlock().getStatements();
            else_list[else_list.length - 1].delete();
            new_if_statement.setElseBranch(new_else_statement);

            is_empty_new_else_statement = new_else_statement.getCodeBlock().isEmpty();

            PsiStatement added_string = factory.createStatementFromText(change_string, null);

            // Avoids creating a new branching statement if nothing exists in the body.
            if (is_empty_new_else_statement && is_empty_new_then_statement) {
                new_if_statement.delete();
                EditorUtils.replacePsiElement(change_if_statement, added_string, file);
                EditorUtils.autoIndentation(file);
                continue;
            } else {
                if (is_empty_new_else_statement) {
                    new_if_statement.add(TextUtils.newline(project));
                    new_if_statement.add(added_string);
                    new_if_statement.getElseBranch().delete();
                } else {
                    new_if_statement.setElseBranch(new_else_statement);
                    new_if_statement.add(TextUtils.newline(project));
                    new_if_statement.add(added_string);
                }
            }

            EditorUtils.replacePsiElement(change_if_statement, new_if_statement, file);
            EditorUtils.autoIndentation(file);
        }

        CodeStyleManagerImpl c = new CodeStyleManagerImpl(project);
        c.adjustLineIndent(file, 4);
    }

    /**
     * Determines whether two strings have the same character at a
     * certain index, starting from the end.
     *
     * @param a  the first string to compare.
     * @param b  the second string to compare.
     * @param i  index of strings to compare, counting from the back
     * @return true if the two strings at index i have the same character
     */
    private boolean equal_string(String a, String b, int i) {
        List<String> a_rev = reverse_parsing(a);
        List<String> b_rev = reverse_parsing(b);
        if (a_rev.get(i).equals(b_rev.get(i))) {
            if (a_rev.get(i).equals("}")) {
                return equal_string(a, b, i + 1);
            }
            change_string = a_rev.get(i);
            return true;
        }
        return false;
    }

    /**
     * @param a  the string to reverse and parse.
     * @return   the  list of strings of the input string reversed, with characters parsed out.
     */
    private List<String> reverse_parsing(String a) {
        String a_rev = new StringBuilder(a).reverse().toString();
        String[] a_rev_par = a_rev.split("\n");
        for (int i = 0; i < a_rev_par.length; i++) {
            a_rev_par[i] = new StringBuilder(a_rev_par[i]).reverse().toString();
            a_rev_par[i] = a_rev_par[i].replaceAll("\\s", "");
        }
        List<String> s = new ArrayList<>();
        for (String value : a_rev_par) {
            if (!value.isEmpty()) {
                s.add(value);
            }
        }
        return s;
    }

    @Override
    public String getRefactorName() {
        return "Consolidate Duplicate Conditional Fragments";
    }
}