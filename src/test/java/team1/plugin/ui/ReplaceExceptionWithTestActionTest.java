package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.TreeUtils;

public class ReplaceExceptionWithTestActionTest extends LightPlatformTestCase {

    /*
    * Test for Blank Code (should fail for isRefactorable)
    * */
    public void testBlankCodeIsRefactorable() {
        String s1 = "";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        ReplaceExceptionWithTestAction action = new ReplaceExceptionWithTestAction();
        assertFalse(action._isRefactorable(file));
    }

    /*
     * Test for Invalid Test Case (1)
     * - Multiple statements in try codeBlock
     * */
    public void testInvalidTestCase1() {
        String s1 = "public class Test {\n" +
                "  Integer[] values = new Integer[3];\n" +
                "\n" +
                "  int testIndexOutOfException(int number) {\n" +
                "    try {\n" +
                "      values[0] = 1;\n" +
                "      return values[number];\n" +
                "    } catch (ArrayIndexOutOfBoundsException e) {\n" +
                "      return 0;\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        ReplaceExceptionWithTestAction action = new ReplaceExceptionWithTestAction();
        assertFalse(action._isRefactorable(file));
    }

    /*
    * Test for Invalid Test Case (2)
    * - Exception object which is passed as a parameter is directly accessed in catch codeBlock.
    * */
    public void testInvalidTestCase2() {
        String s1 = "public class Test {\n" +
                "  Integer[] values = new Integer[3];\n" +
                "\n" +
                "  int testIndexOutOfException(int number) {\n" +
                "    try {\n" +
                "      return values[number];\n" +
                "    } catch (ArrayIndexOutOfBoundsException e) {\n" +
                "      System.out.println(e);\n" +
                "      return 0;\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        ReplaceExceptionWithTestAction action = new ReplaceExceptionWithTestAction();
        assertFalse(action._isRefactorable(file));
    }

    /*
     * Basic Test Case for ArrayIndexOutOfBoundsException
     * */
    public void testArrayIndexOutOfBoundsException() {
        String s1 = "public class Test {\n" +
                "  Integer[] values = new Integer[3];\n" +
                "\n" +
                "  int testIndexOutOfException(int number) {\n" +
                "    try {\n" +
                "      return values[number];\n" +
                "    } catch (ArrayIndexOutOfBoundsException e) {\n" +
                "      return 0;\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String s2 = "public class Test {\n" +
                "  Integer[] values = new Integer[3];\n" +
                "\n" +
                "  int testIndexOutOfException(int number) {\n" +
                "    if (number >= values.length) {\n" +
                "      return 0;}\n" +
                "    return values[number];\n" +
                "  }\n" +
                "} ";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText("test.java", StdFileTypes.JAVA.getLanguage(), s1);
        ReplaceExceptionWithTestAction action = new ReplaceExceptionWithTestAction();
        assertTrue(action._isRefactorable(file));
        action._runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(s2, file.getText(), mockProject));
    }

    /*
    * Catches ArrayIndexOutOfBoundsException, but there is NO array access in try codeBlock.
    * */
    public void testFakeArrayIndexOutOfBoundsException() {
        String s1 = "public class Test {\n" +
                "  Integer[] values = new Integer[3];\n" +
                "\n" +
                "  int testIndexOutOfException(int number) {\n" +
                "    try {\n" +
                "      System.out.println(number);\n" +
                "    } catch (ArrayIndexOutOfBoundsException e) {\n" +
                "      return 0;\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String s2 = "public class Test {\n" +
                "  Integer[] values = new Integer[3];\n" +
                "\n" +
                "  int testIndexOutOfException(int number) {\n" +
                "    try {\n" +
                "      System.out.println(number);\n" +
                "    } catch (ArrayIndexOutOfBoundsException e) {\n" +
                "      return 0;\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText("test.java", StdFileTypes.JAVA.getLanguage(), s1);
        ReplaceExceptionWithTestAction action = new ReplaceExceptionWithTestAction();
        assertTrue(action._isRefactorable(file));
        action._runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(s2, file.getText(), mockProject));
    }

    /*
     * Check null pointer exception for the simplest case.
     */

    public void testNullPointerExceptionSimpleReturn(){
        String s1 = "public class Test{\n" +
                "\n" +
                "    public boolean test(){\n" +
                "        try{\n" +
                "            return a->c;\n" +
                "        } catch(NullPointerException e){\n" +
                "            return false;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}";
        String s2 = "public class Test{\n" +
                "\n" +
                "    public boolean test() { \n" +
                "\n" +
                "        if (a == null) {\n" +
                "            return false;\n" +
                "        }\n" +
                "        else {\n" +
                "            return a -> c;\n" +
                "        }\n" +
                "        \n" +
                "    }\n" +
                "\n" +
                "}";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        ReplaceExceptionWithTestAction action = new ReplaceExceptionWithTestAction();
        assertTrue(action._isRefactorable(file));
        action._runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }

    /*
     * Check the non-refactorable case. Even though there is NullPointerException Handler,
     * it has more than two statements
     */

    public void testNullPointerExceptionNonRefactorable(){
        String s1 = "public class Test{\n" +
                "\n" +
                "    public boolean test(){\n" +
                "\n" +
                "        try{\n" +
                "            a;\n" +
                "            b;\n" +
                "        } catch(NullPointerException e){\n" +
                "            return false;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}";
        String s2 = "public class Test{\n" +
                "\n" +
                "    public boolean test(){\n" +
                "\n" +
                "        try{\n" +
                "            a;\n" +
                "            b;\n" +
                "        } catch(NullPointerException e){\n" +
                "            return false;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        ReplaceExceptionWithTestAction action = new ReplaceExceptionWithTestAction();
        assertFalse(action._isRefactorable(file));
        action._runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }

    /*
     * Check for the refactorable case. Even though it has multiple statements in catch block,
     * it is refactorable.
     */

    public void testNullPointerExceptionMultipleCatch(){
        String s1 = "public class Test{\n" +
                "\n" +
                "    public boolean test(){\n" +
                "\n" +
                "        try{\n" +
                "            a -> c;\n" +
                "        } catch(NullPointerException e){\n" +
                "            a;\n" +
                "            b;\n" +
                "            c;\n" +
                "            d;\n" +
                "            e;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}";
        String s2 = "public class Test{\n" +
                "\n" +
                "    public boolean test(){\n" +
                "\n" +
                "        if(a == null){\n" +
                "            a;\n" +
                "            b;\n" +
                "            c;\n" +
                "            d;\n" +
                "            e;\n" +
                "        }\n" +
                "        else{\n" +
                "            a -> c;\n" +
                "        }\n" +
                "        \n" +
                "    }\n" +
                "\n" +
                "}";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        ReplaceExceptionWithTestAction action = new ReplaceExceptionWithTestAction();
        assertTrue(action._isRefactorable(file));
        action._runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }

}
