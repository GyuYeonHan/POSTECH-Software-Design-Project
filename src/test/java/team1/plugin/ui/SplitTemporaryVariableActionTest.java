package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.TextUtils;
import team1.plugin.utils.TreeUtils;

public class SplitTemporaryVariableActionTest extends LightPlatformTestCase {

    public void testBlank(){
        String s1 = "";
        String s2 = "";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SplitTemporaryVariableAction action = new SplitTemporaryVariableAction();
        assertFalse(action.isRefactorable(file));
        assertTrue(TextUtils.codeEqual(file.getText(), s2));
    }

    public void testRefactoring(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int height = 3;\n" +
                "        int width = 4;\n" +
                "        int temp = height * width;\n" +
                "        System.out.println(temp);\n" +
                "        temp = temp * temp;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 2 * height * width;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 3 * height * width;\n" +
                "        System.out.println(temp);\n" +
                "    }\n" +
                "\n" +
                "}\n";

        String s2 = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int height = 3;\n" +
                "        int width = 4;\n" +
                "        int temp = height * width;\n" +
                "        System.out.println(temp);\n" +
                "        int temp1 = temp * temp;\n" +
                "        System.out.println(temp1);\n" +
                "        int temp2 = 2 * height * width;\n" +
                "        System.out.println(temp2);\n" +
                "        int temp3 = 3 * height * width;\n" +
                "        System.out.println(temp3);\n" +
                "    }\n" +
                "\n" +
                "}\n";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SplitTemporaryVariableAction action = new SplitTemporaryVariableAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        String outputText = file.getText();
        System.out.println(outputText);
        assertTrue(TextUtils.codeEqual(file.getText(), s2));
    }

    public void testAssignmentSign(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int height = 3;\n" +
                "        int width = 4;\n" +
                "        int temp = height * width;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 5 - temp * temp / temp + temp - temp;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 2 * height * width;\n" +
                "        temp += height * width;\n" +
                "    }\n" +
                "\n" +
                "}\n";

        String s2 = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int height = 3;\n" +
                "        int width = 4;\n" +
                "        int temp = height * width;\n" +
                "        System.out.println(temp);\n" +
                "        int temp1 = 5 - temp * temp / temp + temp - temp;\n" +
                "        System.out.println(temp1);\n" +
                "        int temp2 = 2 * height * width;\n" +
                "        temp2 += height * width;\n" +
                "    }\n" +
                "\n" +
                "}\n";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SplitTemporaryVariableAction action = new SplitTemporaryVariableAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(file.getText(), s2));
    }


    public void testReplaceChilds(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int height = 3;\n" +
                "        int width = 4;\n" +
                "        int temp = height * width;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 5 - temp * temp / temp + temp - temp;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 2 * height * width;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 3 * height * width;\n" +
                "        System.out.println(temp);\n" +
                "    }\n" +
                "\n" +
                "}\n";

        String s2 = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int height = 3;\n" +
                "        int width = 4;\n" +
                "        int temp = height * width;\n" +
                "        System.out.println(temp);\n" +
                "        int temp1 = 5 - temp * temp / temp + temp - temp;\n" +
                "        System.out.println(temp1);\n" +
                "        int temp2 = 2 * height * width;\n" +
                "        System.out.println(temp2);\n" +
                "        int temp3 = 3 * height * width;\n" +
                "        System.out.println(temp3);\n" +
                "    }\n" +
                "\n" +
                "}\n";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SplitTemporaryVariableAction action = new SplitTemporaryVariableAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(file.getText(), s2));
    }

    // Test for PSI Tree Structure
    public void testTreeBlank(){
        String s1 = "";
        String s2 = "";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SplitTemporaryVariableAction action = new SplitTemporaryVariableAction();
        assertFalse(action.isRefactorable(file));
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }

    public void testTreeRefactoring(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int height = 3;\n" +
                "        int width = 4;\n" +
                "        int temp = height * width;\n" +
                "        System.out.println(temp);\n" +
                "        temp = temp * temp;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 2 * height * width;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 3 * height * width;\n" +
                "        System.out.println(temp);\n" +
                "    }\n" +
                "\n" +
                "}\n";

        String s2 = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int height = 3;\n" +
                "        int width = 4;\n" +
                "        int temp = height * width;\n" +
                "        System.out.println(temp);\n" +
                "        int temp1 = temp * temp;\n" +
                "        System.out.println(temp1);\n" +
                "        int temp2 = 2 * height * width;\n" +
                "        System.out.println(temp2);\n" +
                "        int temp3 = 3 * height * width;\n" +
                "        System.out.println(temp3);\n" +
                "    }\n" +
                "\n" +
                "}\n";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SplitTemporaryVariableAction action = new SplitTemporaryVariableAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        String outputText = file.getText();
        System.out.println(outputText);
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }

    public void testTreeAssignmentSign(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int height = 3;\n" +
                "        int width = 4;\n" +
                "        int temp = height * width;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 5 - temp * temp / temp + temp - temp;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 2 * height * width;\n" +
                "        temp += height * width;\n" +
                "    }\n" +
                "\n" +
                "}\n";

        String s2 = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int height = 3;\n" +
                "        int width = 4;\n" +
                "        int temp = height * width;\n" +
                "        System.out.println(temp);\n" +
                "        int temp1 = 5 - temp * temp / temp + temp - temp;\n" +
                "        System.out.println(temp1);\n" +
                "        int temp2 = 2 * height * width;\n" +
                "        temp2 += height * width;\n" +
                "    }\n" +
                "\n" +
                "}\n";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SplitTemporaryVariableAction action = new SplitTemporaryVariableAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }


    public void testTreeReplaceChilds(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int height = 3;\n" +
                "        int width = 4;\n" +
                "        int temp = height * width;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 5 - temp * temp / temp + temp - temp;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 2 * height * width;\n" +
                "        System.out.println(temp);\n" +
                "        temp = 3 * height * width;\n" +
                "        System.out.println(temp);\n" +
                "    }\n" +
                "\n" +
                "}\n";

        String s2 = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int height = 3;\n" +
                "        int width = 4;\n" +
                "        int temp = height * width;\n" +
                "        System.out.println(temp);\n" +
                "        int temp1 = 5 - temp * temp / temp + temp - temp;\n" +
                "        System.out.println(temp1);\n" +
                "        int temp2 = 2 * height * width;\n" +
                "        System.out.println(temp2);\n" +
                "        int temp3 = 3 * height * width;\n" +
                "        System.out.println(temp3);\n" +
                "    }\n" +
                "\n" +
                "}\n";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SplitTemporaryVariableAction action = new SplitTemporaryVariableAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }
}
