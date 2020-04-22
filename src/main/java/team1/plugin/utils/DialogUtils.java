package team1.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class DialogUtils {
    /**
     * show info message
     *
     * @param project IntelliJ project object
     * @param title   dialog title
     * @param message dialog message
     */
    public static void showInfoMessage(Project project, String title, String message) {
        String formattedTitle = StringUtil.defaultIfEmpty(title, "Info");
        Messages.showMessageDialog(project, message, formattedTitle, Messages.getInformationIcon());
    }

    /**
     * show error message
     *
     * @param project IntelliJ project object
     * @param title   dialog title
     * @param message dialog message
     */
    public static void showErrorMessage(Project project, String title, String message) {
        String formattedTitle = String.format("%s Error", title);
        Messages.showMessageDialog(project, message, formattedTitle, Messages.getErrorIcon());
    }

    /**
     * show input dialog
     *
     * @param project IntelliJ project object
     * @param title   dialog title
     * @param message dialog message
     * @return String if user submit input, null otherwise
     */
    public static String stringInputDialog(Project project, String title, String message) {
        return Messages.showInputDialog(project, message, title, Messages.getQuestionIcon());
    }
}
