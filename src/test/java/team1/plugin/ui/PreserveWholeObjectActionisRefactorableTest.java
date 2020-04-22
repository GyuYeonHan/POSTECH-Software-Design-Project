package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.testFramework.LightPlatformTestCase;

public class PreserveWholeObjectActionisRefactorableTest extends LightPlatformTestCase {
    public void testNothingDeclaration() {
        String s = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        boolean withinPlan = plan.withinRange();\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        PreserveWholeObjectAction action = new PreserveWholeObjectAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testOneObjectDeclaration() {
        String s = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        int low = daysTempRange.getLow();\n" +
                "        boolean withinPlan = plan.withinRange(low);\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        PreserveWholeObjectAction action = new PreserveWholeObjectAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testTwoObjectDeclaration() {
        String s = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        int low = daysTempRange.getLow();\n" +
                "        int high = daysTempRange.getHigh();\n" +
                "        boolean withinPlan = plan.withinRange(low, high);\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        PreserveWholeObjectAction action = new PreserveWholeObjectAction();
        assertTrue(action.isRefactorable(file));
    }

    public void testTwoIntDeclarationFalse() {
        String s = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        int low = 1;\n" +
                "        int high = 2;\n" +
                "        boolean withinPlan = plan.withinRange(low, high);\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        PreserveWholeObjectAction action = new PreserveWholeObjectAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testOneIntOneObjectDeclaration() {
        String s = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        int lowest = 1;\n" +
                "        int high = daysTempRange.getHigh();\n" +
                "        boolean withinPlan = plan.withinRange(high, lowest);\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        PreserveWholeObjectAction action = new PreserveWholeObjectAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testTwoIntTwoObjectDeclaration() {
        String s = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        int lowest = 1;\n" +
                "        int highest = 2;\n" +
                "        int low = daysTempRange.getLow();\n" +
                "        int high = daysTempRange.getHigh();\n" +
                "        boolean withinPlan = plan.withinRange(low, high, lowest, highest);\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        PreserveWholeObjectAction action = new PreserveWholeObjectAction();
        assertTrue(action.isRefactorable(file));
    }
}
