package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.TreeUtils;

public class PreserveWholeObjectActionRunRefactoringTest extends LightPlatformTestCase {
    public void testTwoObjectRunRefactor(){
        String s = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        int low = daysTempRange.getLow();\n" +
                "        int high = daysTempRange.getHigh();\n" +
                "        boolean withinPlan = plan.withinRange(1, low, high);\n" +
                "    }\n" +
                "}";
        String ans = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        boolean withinPlan = plan.withinRange(1, daysTempRange);\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        PreserveWholeObjectAction action = new PreserveWholeObjectAction();
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), mockProject));
    }

    public void testThreeObjectRunRefactor(){
        String s = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        int low = daysTempRange.getLow();\n" +
                "        int middle = daysTempRange.getMiddle();\n" +
                "        int high = daysTempRange.getHigh();\n" +
                "        boolean withinPlan = plan.withinRange(low, middle, high);\n" +
                "    }\n" +
                "}";
        String ans = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        boolean withinPlan = plan.withinRange(daysTempRange);\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        PreserveWholeObjectAction action = new PreserveWholeObjectAction();
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), mockProject));
    }

    public void testThreeObjectTwoRefactorRunRefactor(){
        String s = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        int low = daysTempRange.getLow();\n" +
                "        int middle = daysTempRange.getMiddle();\n" +
                "        int high = daysTempRange.getHigh();\n" +
                "        boolean withinPlan = plan.withinRange(low, high);\n" +
                "    }\n" +
                "}";
        String ans = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        int middle = daysTempRange.getMiddle();\n" +
                "        boolean withinPlan = plan.withinRange(daysTempRange);\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        PreserveWholeObjectAction action = new PreserveWholeObjectAction();
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), mockProject));
    }

    public void testOneIntTwoObjectRunRefactor(){
        String s = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        int lowest = 1;\n" +
                "        int low = daysTempRange.getLow();\n" +
                "        int high = daysTempRange.getHigh();\n" +
                "        boolean withinPlan = plan.withinRange(lowest, low, high);\n" +
                "    }\n" +
                "}";
        String ans = "class Test {\n" +
                "    public void someMethod() {\n" +
                "        int lowest = 1;\n" +
                "        boolean withinPlan = plan.withinRange(lowest, daysTempRange);\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        PreserveWholeObjectAction action = new PreserveWholeObjectAction();
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), mockProject));
    }

}
