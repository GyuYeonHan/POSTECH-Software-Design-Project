package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.TreeUtils;

public class CollapseHierarchyActionTest extends LightPlatformTestCase{
    public PsiFile getPsiFileFromString(String codeBlock)
    {
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), codeBlock);

        return file;
    }

    public void testNullFile() {
        Project project = getProject();
        String codeBlock = "class aClass{}";

        PsiFile file = getPsiFileFromString(codeBlock);

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertFalse(action.isRefactorable(null, ((PsiJavaFileImpl) file).getClasses()[0]));
    }

    public void testNullClass() {
        Project project = getProject();
        String codeBlock = "class aClass{}";

        PsiFile file = getPsiFileFromString(codeBlock);

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertFalse(action.isRefactorable(file, null));
    }

    public void testNoParentClass() {
        Project project = getProject();
        String codeBlock = "class aClass{}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiClass aClass = ((PsiJavaFileImpl) file).getClasses()[0];

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertFalse(action.isRefactorable(file, aClass));
    }

    public void testTargetHasSubClass() {
        Project project = getProject();
        String codeBlock =
                "class parentClass {}" +
                "class aClass extends parentClass {}" +
                "class bClass extends aClass {}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiClass aClass = ((PsiJavaFileImpl) file).getClasses()[1];

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertFalse(action.isRefactorable(file, aClass));
    }

    public void testTargetHasOverriding() {
        Project project = getProject();
        String codeBlock =
                "class parentClass {void aFunc() {}}" +
                        "class aClass extends parentClass {void aFunc(){}}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiClass aClass = ((PsiJavaFileImpl) file).getClasses()[1];

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertFalse(action.isRefactorable(file, aClass));
    }

    public void testImplementsInterface() {
        Project project = getProject();
        String codeBlock =
                "interface someInterface {}" +
                        "class parentClass {}" +
                        "class aClass extends parentClass implements someInterface {}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiClass aClass = ((PsiJavaFileImpl) file).getClasses()[2];

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertFalse(action.isRefactorable(file, aClass));
    }

    public void testFieldNameConflict() {
        Project project = getProject();
        String codeBlock =
                "class parentClass {int a;}" +
                        "class aClass extends parentClass {String a;}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiClass aClass = ((PsiJavaFileImpl) file).getClasses()[1];

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertFalse(action.isRefactorable(file, aClass));
    }

    public void testConstructor() {
        Project project = getProject();
        String codeBlock =
                "class parentClass {int a;" +
                        "parentClass(){} }" +
                        "class aClass extends parentClass {" +
                        "String a;" +
                        "aClass(){ super()}" +
                        "}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiClass aClass = ((PsiJavaFileImpl) file).getClasses()[1];

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertFalse(action.isRefactorable(file, aClass));
    }

    public void testTooManyChanges() {
        Project project = getProject();
        String codeBlock =
                 "class SuperClass {\n" +
                 "    SuperClass(){}" +
                 "}\n" +
                 "\n" +
                 "class aClass extends SuperClass{\n" +
                 "    private String extra;\n" +
                 "    \n" +
                 "    public void aFunc(int id, String name){\n" +
                 "        ;\n" +
                 "    }\n" +
                 "\n" +
                 "    public void changeExtra(){\n" +
                 "        this.extra = extra.substring(1);\n" +
                 "    }\n" +
                 "}";

         PsiFile file = getPsiFileFromString(codeBlock);
         PsiClass aClass = ((PsiJavaFileImpl) file).getClasses()[1];

         CollapseHierarchyAction action = new CollapseHierarchyAction();
         assertFalse(action.isRefactorable(file, aClass));
    }

    public void testReturnItself() {
        Project project = getProject();
        String codeBlock =
                "class SuperClass {\n" +
                        "    SuperClass(){}" +
                        "}\n" +
                        "\n" +
                        "class aClass extends SuperClass{\n" +
                        "    private String extra;\n" +
                        "    \n" +
                        "    public aClass aFunc(int id, String name){\n" +
                        "        return new aClass();\n" +
                        "    }\n" +
                        "}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiClass aClass = ((PsiJavaFileImpl) file).getClasses()[1];

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertFalse(action.isRefactorable(file, aClass));
    }

    public void testRefactorable() {
        Project project = getProject();
        String codeBlock =
                "class SuperClass {\n" +
                        "    private int id;" +
                        "}\n" +
                        "\n" +
                        "class aClass extends SuperClass{\n" +
                        "    private String extra;\n" +
                        "\n" +
                        "    public void changeExtra(){\n" +
                        "        this.extra = extra.substring(1);\n" +
                        "    }\n" +
                        "}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiClass aClass = ((PsiJavaFileImpl) file).getClasses()[1];

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertTrue(action.isRefactorable(file, aClass));
    }

    //Run Refactoring

    public void testMergeField() {
        Project project = getProject();
        String codeBlock =
                "class SuperClass { public SuperClass() {}}" +
                        "class SubClass extends SuperClass { private String extra; }";

        String ansBlock = "class SuperClass { private String extra; public SuperClass() {}}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiClass aClass = ((PsiJavaFileImpl) file).getClasses()[1];

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertTrue(action.isRefactorable(file, aClass));

        action.runRefactoring(file, aClass);

        assertTrue(TreeUtils.codeEqual(file.getText(), ansBlock, project));
    }

    public void testMergeMethod() {
        Project project = getProject();
        String codeBlock =
                "class SuperClass { public SuperClass() {}}" +
                        "class SubClass extends SuperClass { private void extra() {}; }";

        String ansBlock = "class SuperClass { private void extra() {}; public SuperClass() {}}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiClass aClass = ((PsiJavaFileImpl) file).getClasses()[1];

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertTrue(action.isRefactorable(file, aClass));

        action.runRefactoring(file, aClass);

        assertTrue(TreeUtils.codeEqual(file.getText(), ansBlock, project));
    }

    public void testUpdateReference() {
        Project project = getProject();
        String codeBlock =
                "class SuperClass { public SuperClass() {}}" +
                        "class SubClass extends SuperClass { private String extra; }" +
                        "class aClass { void aF() { SubClass a = new SubClass(); }}";

        String ansBlock = "class SuperClass { private String extra; public SuperClass() {}}" +
                "class aClass { void aF() { SuperClass a = new SuperClass(); }}";



        PsiFile file = getPsiFileFromString(codeBlock);
        PsiClass aClass = ((PsiJavaFileImpl) file).getClasses()[1];

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertTrue(action.isRefactorable(file, aClass));

        action.runRefactoring(file, aClass);

        assertTrue(TreeUtils.codeEqual(file.getText(), ansBlock, project));
    }

    public void testUpdateSuper() {
        Project project = getProject();
        String codeBlock =
                "class SuperClass { int a; public SuperClass() {}}" +
                        "class SubClass extends SuperClass { void aF() { super.a = 3;} }";

        String ansBlock = "class SuperClass { int a; void aF() { this.a = 3;} public SuperClass() {}}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiClass aClass = ((PsiJavaFileImpl) file).getClasses()[1];

        CollapseHierarchyAction action = new CollapseHierarchyAction();
        assertTrue(action.isRefactorable(file, aClass));

        action.runRefactoring(file, aClass);

        assertTrue(TreeUtils.codeEqual(file.getText(), ansBlock, project));
    }
}
