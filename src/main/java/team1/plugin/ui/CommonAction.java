package team1.plugin.ui;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import team1.plugin.utils.DialogUtils;

public abstract class CommonAction extends AnAction implements IntentionAction {
    /**
     * Updates the state of the action.
     *
     * @param e Carries information on the invocation place and data available
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        try {
            e.getPresentation().setEnabled(isRefactorable(e));
        } catch (Exception ignored){
        }
    }

    /**
     * Implement this method to provide your action handler.
     *
     * @param e Carries information on the invocation place
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            if (!isRefactorable(e)) {
                DialogUtils.showErrorMessage(e.getProject(), getRefactorName(), getTemplatePresentation().getDescription());
                return;
            }
            runRefactoring(e);
        } catch (Exception ignored) {
        }
    }

    /**
     * Check whether this action has code smell.
     *
     * @param e AnActionEvent from actionPerformed
     * @return {true} if it has code smell, {false} otherwise
     */
    abstract protected boolean isRefactorable(AnActionEvent e);

    /**
     * Check whether this action has code smell at a caret offset in file.
     *
     * @param project the project in which the availability is checked.
     * @param editor  the editor in which the intention will be invoked.
     * @param file    the file open in the editor.
     * @return true if the refactoring is available, false otherwise.
     */
    abstract protected boolean isRefactorable(@NotNull Project project, Editor editor, PsiFile file);

    /**
     * Run this refactoring logic
     *
     * @param e AnActionEvent from actionPerformed
     */
    abstract protected void runRefactoring(AnActionEvent e);

    /**
     * Run refactoring.
     *
     * @param project the project in which the intention is invoked.
     * @param editor  the editor in which the intention is invoked.
     * @param file    the file open in the editor.
     */
    abstract protected void runRefactoring(@NotNull Project project, Editor editor, PsiFile file);

    /**
     * Returns text to be shown in the list of available actions, if this action
     * is available.
     *
     * @return the text to show in the intention popup.
     * @see #isAvailable(Project, Editor, PsiFile)
     */
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Team1Refactor: " + getRefactorName();
    }

    /**
     * get refactoring name
     *
     * @return the refactoring name text
     */
    public abstract String getRefactorName();

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    /**
     * Checks whether this intention is available at a caret offset in file.
     * If this method returns true, a light bulb for this intention is shown.
     *
     * @param project the project in which the availability is checked.
     * @param editor  the editor in which the intention will be invoked.
     * @param file    the file open in the editor.
     * @return true if the intention is available, false otherwise.
     */
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        try {
            return isRefactorable(project, editor, file);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Called when user invokes intention. This method is called inside command.
     * If {@link #startInWriteAction()} returns true, this method is also called
     * inside write action.
     *
     * @param project the project in which the intention is invoked.
     * @param editor  the editor in which the intention is invoked.
     * @param file    the file open in the editor.
     */
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        try {
            runRefactoring(project, editor, file);
        } catch (Exception ignored) {
            DialogUtils.showErrorMessage(project, getRefactorName(), "Fail to run - " + getText());
        }
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
