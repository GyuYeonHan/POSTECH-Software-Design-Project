package team1.plugin.ui;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.TextUtils;
import team1.plugin.utils.TreeUtils;

public class PullUpConstructorBodyActionTest extends LightPlatformTestCase {
    public void testBlank(){
        String s1 = "";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText("blank.java", StdFileTypes.JAVA.getLanguage(), s1);
        VirtualFile vFile = getSourceRoot();
        PsiDirectory directory = PsiDirectoryFactory.getInstance(mockProject).createDirectory(vFile);
        WriteCommandAction.runWriteCommandAction(mockProject, () -> {
            directory.add(file);
        });
        PullUpConstructorBodyAction action = new PullUpConstructorBodyAction();
        assertFalse(action.isRefactorable(directory));
    }

    public void testRefactoring(){
        String inputText1 = "public class Test {\n" +
                "\n" +
                "}\n" +
                "\n" +
                "public class Test1 extends Test {\n" +
                "    int name;\n" +
                "    int id;\n" +
                "    int age;\n" +
                "\n" +
                "    Test1(int name, int id, int age) {\n" +
                "        this.name = name;\n" +
                "        this.id = id;\n" +
                "        this.age = age;\n" +
                "    }\n" +
                "}";

        String inputText2 = "public class Test2 extends Test {\n" +
                "    int name;\n" +
                "    int id;\n" +
                "    int grade;\n" +
                "\n" +
                "    Test2(int name, int id, int grade) {\n" +
                "        this.name = name;\n" +
                "        this.id = id;\n" +
                "        this.grade = grade;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "public class Test3 extends Test1 {\n" +
                "    Test3() {\n" +
                "\n" +
                "    }\n" +
                "}";

        String answerText1 = "public class Test {\n" +
                "\n" +
                "    private int name;\n" +
                "    private int id;\n" +
                "\n" +
                "    public Test(int name, int id) {\n" +
                "        this.name = name;\n" +
                "        this.id = id;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "public class Test1 extends Test {\n" +
                "    int age;\n" +
                "\n" +
                "    Test1(int name, int id, int age) {\n" +
                "        super(name, id);\n" +
                "        this.age = age;\n" +
                "    }\n" +
                "}";
        String answerText2 = "public class Test2 extends Test {\n" +
                "    int grade;\n" +
                "\n" +
                "    Test2(int name, int id, int grade) {\n" +
                "        super(name, id);\n" +
                "        this.grade = grade;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "public class Test3 extends Test1 {\n" +
                "    Test3() {\n" +
                "\n" +
                "    }\n" +
                "}";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file1 = fac.createFileFromText("file1.java", StdFileTypes.JAVA.getLanguage(), inputText1);
        PsiFile file2 = fac.createFileFromText("file2.java", StdFileTypes.JAVA.getLanguage(), inputText2);

        VirtualFile vFile = getSourceRoot();
        PsiDirectory directory = PsiDirectoryFactory.getInstance(mockProject).createDirectory(vFile);
        WriteCommandAction.runWriteCommandAction(mockProject, () -> {
            directory.add(file1);
            directory.add(file2);
        });

        PullUpConstructorBodyAction action = new PullUpConstructorBodyAction();
        assertTrue(action.isRefactorable(directory));
        action.runRefactoring(directory);
        assertTrue(TextUtils.codeEqual(answerText1, directory.findFile("file1.java").getText()));
        assertTrue(TextUtils.codeEqual(answerText2, directory.findFile("file2.java").getText()));
    }

    // Test for PSI Tree structure
    public void testTreeRefactoring(){
        String inputText1 = "public class Test {\n" +
                "\n" +
                "}\n" +
                "\n" +
                "public class Test1 extends Test {\n" +
                "    int name;\n" +
                "    int id;\n" +
                "    int age;\n" +
                "\n" +
                "    Test1(int name, int id, int age) {\n" +
                "        this.name = name;\n" +
                "        this.id = id;\n" +
                "        this.age = age;\n" +
                "    }\n" +
                "}";

        String inputText2 = "public class Test2 extends Test {\n" +
                "    int name;\n" +
                "    int id;\n" +
                "    int grade;\n" +
                "\n" +
                "    Test2(int name, int id, int grade) {\n" +
                "        this.name = name;\n" +
                "        this.id = id;\n" +
                "        this.grade = grade;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "public class Test3 extends Test1 {\n" +
                "    Test3() {\n" +
                "\n" +
                "    }\n" +
                "}";

        String answerText1 = "public class Test {\n" +
                "\n" +
                "    private int name;\n" +
                "    private int id;\n" +
                "\n" +
                "    public Test(int name, int id) {\n" +
                "        this.name = name;\n" +
                "        this.id = id;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "public class Test1 extends Test {\n" +
                "    int age;\n" +
                "\n" +
                "    Test1(int name, int id, int age) {\n" +
                "        super(name, id);\n" +
                "        this.age = age;\n" +
                "    }\n" +
                "}";
        String answerText2 = "public class Test2 extends Test {\n" +
                "    int grade;\n" +
                "\n" +
                "    Test2(int name, int id, int grade) {\n" +
                "        super(name, id);\n" +
                "        this.grade = grade;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "public class Test3 extends Test1 {\n" +
                "    Test3() {\n" +
                "\n" +
                "    }\n" +
                "}";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file1 = fac.createFileFromText("file1.java", StdFileTypes.JAVA.getLanguage(), inputText1);
        PsiFile file2 = fac.createFileFromText("file2.java", StdFileTypes.JAVA.getLanguage(), inputText2);

        VirtualFile vFile = getSourceRoot();
        PsiDirectory directory = PsiDirectoryFactory.getInstance(mockProject).createDirectory(vFile);
        WriteCommandAction.runWriteCommandAction(mockProject, () -> {
            directory.add(file1);
            directory.add(file2);
        });

        PullUpConstructorBodyAction action = new PullUpConstructorBodyAction();
        assertTrue(action.isRefactorable(directory));
        action.runRefactoring(directory);
        assertTrue(TreeUtils.codeEqual(answerText1, directory.findFile("file1.java").getText(), getProject()));
        assertTrue(TreeUtils.codeEqual(answerText2, directory.findFile("file2.java").getText(), getProject()));
    }
}
