package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.testFramework.LightPlatformTestCase;

public class ReplaceSubclassWithFieldsActionIsRefactorableTest extends LightPlatformTestCase {

    public void testBlankCodeIsRefactor() {
        String s = "";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testOneSubclassIsRefactor() {
        String s = "class Person {\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    };\n" +
                "}\n";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        assertTrue(action.isRefactorable(file));
    }

    public void testTwoSubclassIsRefactor() {
        String s = "class Person {\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    };\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    };\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        assertTrue(action.isRefactorable(file));
    }

    public void testTwoSubclassIsRefactorDiffrentTypeFalse() {
        String s = "class Person {\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    };\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    int getName() {\n" +
                "        return 3;\n" +
                "    };\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testTwoSubclassIsRefactorDifferentMethodNameFalse() {
        String s = "class Person {\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    };\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    int getAge() {\n" +
                "        return 42;\n" +
                "    };\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testAlreadyHasSameField() {
        String s = "class Person {\n" +
                "    private string apple;\n" +
                "    private string name;\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    };\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    };\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testNotConstantReturn() {
        String s = "class Person {\n" +
                "    private string name;\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName(String s) {\n" +
                "        return \"M\" + s;\n" +
                "    };\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    int getAge() {\n" +
                "        return 42;\n" +
                "    };\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        assertFalse(action.isRefactorable(file));
    }


    public void testEmptyMethodBodyFalse() {
        String s = "abstract class Person {\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "abstract class Male extends Person {\n" +
                "    abstract String getName();\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    int getAge() {\n" +
                "    };\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        assertFalse(action.isRefactorable(file));
    }


    public void testNoClassFalse() {
        String s = "void getCode() {}\n";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testNoSuperClass() {
        String s = "class Male{\n" +
                "    String getName() {\n" +
                "        return \"N\"}\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testTwoSubclassIsRefactorWithDifferentParent() {
        String s = "class Animal {\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Person {\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Pig extends Animal {\n" +
                "    String getName() {\n" +
                "        return \"PIG\";\n" +
                "    };\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    };\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        assertTrue(action.isRefactorable(file));
    }
}