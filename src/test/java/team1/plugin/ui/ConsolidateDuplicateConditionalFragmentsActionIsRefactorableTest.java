package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.testFramework.LightPlatformTestCase;


public class ConsolidateDuplicateConditionalFragmentsActionIsRefactorableTest extends LightPlatformTestCase {

    public void testBlankCodeIsRefactor(){
        String s = "";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testSimpleIfStateIsRefactor(){
        String s = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int a = 3;\n" +
                "        if (true){\n" +
                "            a = 2;\n" +
                "        }else{\n" +
                "            a = 2;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}\n";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        assertTrue(action.isRefactorable(file));
    }

    public void testIfElseifElseStatementSimpleIsRefactorSuccess(){
        String s = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int a = 3;\n" +
                "        if (true){\n" +
                "            a = 2;\n" +
                "        }else if(true) {\n" +
                "            a = 4;\n" +
                "        }else{\n" +
                "            a = 4;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}\n";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        assertTrue(action.isRefactorable(file));
    }

    public void testIfElseifElseStatementSimpleIsRefactorFail(){
        String s = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int a = 3;\n" +
                "        if (true){\n" +
                "            a = 2;\n" +
                "        }else if(true) {\n" +
                "            a = 4;\n" +
                "        }else{\n" +
                "            a = 5;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}\n";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testComplexIfElseStatementIsRefactorFail(){
        String s = "public class Test {\n" +
                "\n" +
                "    public void test(){\n" +
                "\n" +
                "        int a = 3;\n" +
                "        int b = 5;\n" +
                "\n" +
                "        if (true){\n" +
                "\n" +
                "            b = 5;\n" +
                "            a = 2;\n" +
                "\n" +
                "        }else if(true) {\n" +
                "\n" +
                "            a = 3;\n" +
                "\n" +
                "            if (a == 4){\n" +
                "                b = 5;\n" +
                "                a = 2;\n" +
                "            } else {\n" +
                "                b = 6;\n" +
                "                b = 2;\n" +
                "                a = 2;\n" +
                "            }\n" +
                "\n" +
                "        }else{\n" +
                "\n" +
                "            a = 4;\n" +
                "            b = 2;\n" +
                "            a = 5;\n" +
                "\n" +
                "\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}\n";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        assertTrue(action.isRefactorable(file));
    }

    public void testBottomEqualIsRefactorable(){
        String s = "public class Test {\n" +
                "    public void test(){\n" +
                "        int a;\n" +
                "        if(true){\n" +
                "            a = 2;\n" +
                "        }\n" +
                "        else{\n" +
                "            if (true){\n" +
                "                a = 0;\n" +
                "            }\n" +
                "            else{\n" +
                "                a = 1;\n" +
                "            }\n" +
                "            a = 2;\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        assertTrue(action.isRefactorable(file));
    }

    public void testDifferentSizeIsRefactorable(){
        String s = "public class Test{\n" +
                "    public void test(){\n" +
                "        if(true){" +
                "   int a = 1; " +
                "};\n" +
                "        else;\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testDifferentSizeIsRefactorableAnother(){
        String s = "public class Test{\n" +
                "    public void test(){\n" +
                "        if(true);\n" +
                "        else{" +
                "   int a = 1; " +
                "}\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testBraceOnlyIsRefactorable(){
        String s = "public class Test{\n" +
                "    public void test(){\n" +
                "        if(true){\n" +
                "\n" +
                "            {\n" +
                "\n" +
                "                {\n" +
                "\n" +
                "\n" +
                "                }\n" +
                "\n" +
                "            }\n" +
                "\n" +
                "        }\n" +
                "        else{\n" +
                "\n" +
                "            {\n" +
                "\n" +
                "                {\n" +
                "\n" +
                "                    {\n" +
                "\n" +
                "\n" +
                "                        {\n" +
                "                            \n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ConsolidateDuplicateConditionalFragmentsAction action = new ConsolidateDuplicateConditionalFragmentsAction();
        assertFalse(action.isRefactorable(file));
    }

}
