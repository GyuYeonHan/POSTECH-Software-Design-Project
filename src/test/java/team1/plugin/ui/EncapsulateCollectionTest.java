package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.TreeUtils;

public class EncapsulateCollectionTest extends LightPlatformTestCase {
    public PsiFile getPsiFileFromString(String codeBlock)
    {
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), codeBlock);

        return file;
    }

    public void testNullFile(){
        EncapsulateCollectionAction action = new EncapsulateCollectionAction();
        assertFalse(action._isRefactorable(null));
    }

    public void testNoCollection(){
        String codeBlock = "import java.util.List;" +
                "class aClass { " +
                "int a; " +
                "void aF() {}}";
        PsiFile file = getPsiFileFromString(codeBlock);

        EncapsulateCollectionAction action = new EncapsulateCollectionAction();
        assertFalse(action._isRefactorable(((PsiJavaFileImpl) file).getClasses()[0]));
    }

    public void testCollection(){
        String codeBlock = "class aClass { " +
                "int a; " +
                "List<Integer> b;" +
                "void aF() {}}";
        PsiFile file = getPsiFileFromString(codeBlock);

        EncapsulateCollectionAction action = new EncapsulateCollectionAction();
        assertFalse(action._isRefactorable(((PsiJavaFileImpl) file).getClasses()[0]));
    }

    public void testCollectionWithGetter(){
        String codeBlock = "class aClass { " +
                "int a; " +
                "List<Integer> b;" +
                "List<Integer> aF() {" +
                "return b;}}";
        PsiFile file = getPsiFileFromString(codeBlock);

        EncapsulateCollectionAction action = new EncapsulateCollectionAction();
        assertFalse(action._isRefactorable(((PsiJavaFileImpl) file).getClasses()[0]));
    }

    public void testCollectionSetter(){
        String codeBlock = "class aClass { " +
                "int a; " +
                "List<Integer> b;" +
                "void setB(List<Integer> c){" +
                "b = c;}}";
        PsiFile file = getPsiFileFromString(codeBlock);

        EncapsulateCollectionAction action = new EncapsulateCollectionAction();
        assertFalse(action._isRefactorable(((PsiJavaFileImpl) file).getClasses()[0]));
    }

    public void testCollectionWithGetterAndSetter(){
        String codeBlock = "class aClass { " +
                "int a; " +
                "List<Integer> b;" +
                "List<Integer> aF() {" +
                "return b;}" +
                "void setB(List<Integer> c){" +
                "b = c;}}";
        PsiFile file = getPsiFileFromString(codeBlock);

        EncapsulateCollectionAction action = new EncapsulateCollectionAction();
        assertTrue(action._isRefactorable(((PsiJavaFileImpl) file).getClasses()[0]));
    }

    public void testCollectionWithConstructor(){
        String codeBlock = "class aClass { " +
                "int a; " +
                "List<Integer> b;" +
                "List<Integer> aF() {" +
                "return b;}" +
                "public aClass(List<Integer> c){" +
                "b = c;}}";
        PsiFile file = getPsiFileFromString(codeBlock);

        EncapsulateCollectionAction action = new EncapsulateCollectionAction();
        assertFalse(action._isRefactorable(((PsiJavaFileImpl) file).getClasses()[0]));
    }

    public void testRefactorList(){
        String codeBlock = "class aClass {\n" +
                "int a;\n" +
                "List<Integer> b;\n" +
                "List<Integer> getter() {\n" +
                "return b;}\n" +
                "void setter(List<Integer> c) {\n" +
                "b = c;}\n" +
                "}\n" +
                "\n" +
                "class bClass {\n" +
                "void run(){\n" +
                "aClass a = new aClass();\n" +
                "List<Integer> c = a.getter();\n" +
                "c.add(3);}\n" +
                "}\n";

        String ansBlock = "class aClass {\n" +
                "int a;\n" +
                "List<Integer> b;\n" +
                "List<Integer> getter() {\n" +
                "return Collections.unmodifiableList(b);\n" +
                "}\n" +
                "\n" +
                "public boolean addB(Integer input) {\n" +
                "return b.add(input);\n" +
                "}\n" +
                "\n" +
                "public boolean removeB(Integer input) {\n" +
                "return b.remove(input);\n" +
                "}\n" +
                "}\n" +
                "\n" +
                "class bClass {\n" +
                "void run(){\n" +
                "aClass a = new aClass();\n" +
                "List<Integer> c = a.getter();\n" +
                "a.addB(3);\n" +
                "}\n" +
                "}\n";

        PsiFile file = getPsiFileFromString(codeBlock);

        EncapsulateCollectionAction action = new EncapsulateCollectionAction();
        action.runRefactoring(file, ((PsiJavaFileImpl) file).getClasses()[0]);
        assertTrue(TreeUtils.codeEqual(file.getText(), ansBlock, file.getProject()));
    }

    public void testRefactorSet() {
        String codeBlock = "class aClass {\n" +
                "int a;\n" +
                "Set<Integer> b;\n" +
                "Set<Integer> getter() {\n" +
                "return b;}\n" +
                "void setter(Set<Integer> c) {\n" +
                "b = c;}\n" +
                "}\n" +
                "\n" +
                "class bClass {\n" +
                "void run(){\n" +
                "aClass a = new aClass();\n" +
                "Set<Integer> c = a.getter();\n" +
                "c.add(3);}\n" +
                "}\n";

        String ansBlock = "class aClass {\n" +
                "int a;\n" +
                "Set<Integer> b;\n" +
                "Set<Integer> getter() {\n" +
                "return Collections.unmodifiableSet(b);\n" +
                "}\n" +
                "\n" +
                "public boolean addB(Integer input) {\n" +
                "return b.add(input);\n" +
                "}\n" +
                "\n" +
                "public boolean removeB(Integer input) {\n" +
                "return b.remove(input);\n" +
                "}\n" +
                "}\n" +
                "\n" +
                "class bClass {\n" +
                "void run(){\n" +
                "aClass a = new aClass();\n" +
                "Set<Integer> c = a.getter();\n" +
                "a.addB(3);\n" +
                "}\n" +
                "}\n";

        PsiFile file = getPsiFileFromString(codeBlock);

        EncapsulateCollectionAction action = new EncapsulateCollectionAction();
        action.runRefactoring(file, ((PsiJavaFileImpl) file).getClasses()[0]);
        assertTrue(TreeUtils.codeEqual(file.getText(), ansBlock, file.getProject()));
    }
}
