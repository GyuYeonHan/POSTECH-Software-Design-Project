package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.testFramework.LightPlatformTestCase;
import org.junit.Test;
import team1.plugin.utils.TextUtils;
import team1.plugin.utils.TreeUtils;

public class ConsolidateDuplicateConditionalFragmentsActionRunRefactoringTest extends LightPlatformTestCase {


    @Test
    public void testCodeEqual(){
        String a = " AB c D";
        String b = " A B c D";
        assertTrue(TextUtils.codeEqual(a, b));
    }

    @Test
    public void testEnterCodeEqual(){
        String a = " AB \n c D";
        String b = " A B\n  c D";
        assertTrue(TextUtils.codeEqual(a, b));
    }

    @Test
    public void testEnterNotCodeEqual(){
        String a = " AB \n c D";
        String b = " A B c D";
        assertFalse(TextUtils.codeEqual(a, b));
    }

    @Test
    public void testEnterDiffCodeEqual(){
        String a = " AB \n c D";
        String b = " A B \n C D";
        assertFalse(TextUtils.codeEqual(a, b));
    }

    @Test
    public void testBlankCodeRunRefactor(){
        Project mockProject = getProject();
        String s = "";
        String ans = "";
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(ans, file.getText()));
    }

    @Test
    public void testSimpleCodeRunRefactor(){

        String s = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 2;\n" +
                "        if (true) {\n" +
                "            a = 2;\n" +
                "            a = 3;\n" +
                "            a = 4;\n" +
                "        }\n" +
                "        else{\n" +
                "            a = 3;\n" +
                "            a = 3;\n" +
                "            a = 4;\n" +
                "        }\n" +
                "        a = 2;\n" +
                "    }\n" +
                "}\n";
        String ans = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 2;\n" +
                "        if (true) {\n" +
                "            a = 2;\n" +
                "        } else {\n" +
                "            a = 3;\n" +
                "        }\n" +
                "        a = 3;\n" +
                "        a = 4;\n" +
                "        a = 2;\n" +
                "    }\n" +
                "}\n";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(ans, file.getText()));

    }


    public void testChainCodeRunRefactor(){

        String s = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 1;\n" +
                "        if (a == 4){\n" +
                "            a = 2;\n" +
                "            if (a == 3){\n" +
                "                a = 2;\n" +
                "                a = 4;\n" +
                "            }\n" +
                "            else{\n" +
                "                a = 2;\n" +
                "                a = 4;\n" +
                "            }\n" +
                "        }\n" +
                "        else{\n" +
                "            a = 2;\n" +
                "        }\n" +
                "    }\n" +
                "}\n";

        String ans1  = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 1;\n" +
                "        if (a == 4) {\n" +
                "            a = 2;\n" +
                "            a = 2;\n" +
                "            a = 4;\n" +
                "        } else {\n" +
                "            a = 2;\n" +
                "        }\n" +
                "    }\n" +
                "}\n";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(ans1, file.getText()));

    }


    public void testPartialMultipleRefactorRunRefactor(){

        String s = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 1;\n" +
                "\n" +
                "        if(true){\n" +
                "            a = 2;\n" +
                "            a = 3;\n" +
                "            \n" +
                "            if (true){\n" +
                "                a = 4;\n" +
                "                a = 5;\n" +
                "            }\n" +
                "            else{\n" +
                "                a = 4;\n" +
                "                a = 5;\n" +
                "            }\n" +
                "            \n" +
                "        }\n" +
                "        else{\n" +
                "            a = 2;\n" +
                "            a = 3;\n" +
                "            a = 4;\n" +
                "            a = 5;\n" +
                "        }\n" +
                "        \n" +
                "        if(true){\n" +
                "            a = 2;\n" +
                "        }\n" +
                "        else{ \n" +
                "            a = 2;\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "    }\n" +
                "}\n";

        String ans1 = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 1;\n" +
                "\n" +
                "        a = 2;\n" +
                "        a = 3;\n" +
                "        a = 4;\n" +
                "        a = 5;\n" +
                "\n" +
                "        a = 2;\n" +
                "\n" +
                "\n" +
                "    }\n" +
                "}";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        action.runRefactoring(file);
        System.out.println(file.getText());
        assertTrue(TextUtils.codeEqual(file.getText(), ans1));

    }

    public void testElseDisappearanceRunRefactor(){

        String s = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 2;\n" +
                "        if (true){\n" +
                "            a = 3;\n" +
                "            a = 2;\n" +
                "        }\n" +
                "        else{\n" +
                "            a = 2;\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "}\n";

        String ans1 = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 2;\n" +
                "        if (true) {\n" +
                "            a = 3;\n" +
                "        }\n" +
                "        a = 2;\n" +
                "\n" +
                "    }\n" +
                "}\n";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();

        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(ans1, file.getText()));

    }

    // Test for PSI tree structure

    @Test
    public void testTreeBlankCodeRunRefactor(){
        Project mockProject = getProject();
        String s = "";
        String ans = "";
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), getProject()));
    }

    @Test
    public void testTreeSimpleCodeRunRefactor(){

        String s = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 2;\n" +
                "        if (true) {\n" +
                "            a = 2;\n" +
                "            a = 3;\n" +
                "            a = 4;\n" +
                "        }\n" +
                "        else{\n" +
                "            a = 3;\n" +
                "            a = 3;\n" +
                "            a = 4;\n" +
                "        }\n" +
                "        a = 2;\n" +
                "    }\n" +
                "}\n";
        String ans = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 2;\n" +
                "        if (true) {\n" +
                "            a = 2;\n" +
                "        } else {\n" +
                "            a = 3;\n" +
                "        }\n" +
                "        a = 3;\n" +
                "        a = 4;\n" +
                "        a = 2;\n" +
                "    }\n" +
                "}\n";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), getProject()));

    }


    public void testTreeChainCodeRunRefactor(){

        String s = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 1;\n" +
                "        if (a == 4){\n" +
                "            a = 2;\n" +
                "            if (a == 3){\n" +
                "                a = 2;\n" +
                "                a = 4;\n" +
                "            }\n" +
                "            else{\n" +
                "                a = 2;\n" +
                "                a = 4;\n" +
                "            }\n" +
                "        }\n" +
                "        else{\n" +
                "            a = 2;\n" +
                "        }\n" +
                "    }\n" +
                "}\n";

        String ans1  = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 1;\n" +
                "        if (a == 4) {\n" +
                "            a = 2;\n" +
                "            a = 2;\n" +
                "            a = 4;\n" +
                "        } else {\n" +
                "            a = 2;\n" +
                "        }\n" +
                "    }\n" +
                "}\n";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans1, file.getText(), getProject()));

    }


    public void testTreePartialMultipleRefactorRunRefactor(){

        String s = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 1;\n" +
                "\n" +
                "        if(true){\n" +
                "            a = 2;\n" +
                "            a = 3;\n" +
                "            \n" +
                "            if (true){\n" +
                "                a = 4;\n" +
                "                a = 5;\n" +
                "            }\n" +
                "            else{\n" +
                "                a = 4;\n" +
                "                a = 5;\n" +
                "            }\n" +
                "            \n" +
                "        }\n" +
                "        else{\n" +
                "            a = 2;\n" +
                "            a = 3;\n" +
                "            a = 4;\n" +
                "            a = 5;\n" +
                "        }\n" +
                "        \n" +
                "        if(true){\n" +
                "            a = 2;\n" +
                "        }\n" +
                "        else{ \n" +
                "            a = 2;\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "    }\n" +
                "}\n";

        String ans1 = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 1;\n" +
                "\n" +
                "        a = 2;\n" +
                "        a = 3;\n" +
                "        a = 4;\n" +
                "        a = 5;\n" +
                "\n" +
                "        a = 2;\n" +
                "\n" +
                "\n" +
                "    }\n" +
                "}";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        action.runRefactoring(file);
        System.out.println(file.getText());
        assertTrue(TreeUtils.codeEqual(file.getText(), ans1, getProject()));

    }

    public void testTreeElseDisappearanceRunRefactor(){

        String s = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 2;\n" +
                "        if (true){\n" +
                "            a = 3;\n" +
                "            a = 2;\n" +
                "        }\n" +
                "        else{\n" +
                "            a = 2;\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "}\n";

        String ans1 = "public class Test {\n" +
                "    public void test() {\n" +
                "        int a = 2;\n" +
                "        if (true) {\n" +
                "            a = 3;\n" +
                "        }\n" +
                "        a = 2;\n" +
                "\n" +
                "    }\n" +
                "}\n";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();

        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans1, file.getText(), getProject()));

    }

}
