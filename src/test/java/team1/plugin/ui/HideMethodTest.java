package team1.plugin.ui;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.TestUtils;
import team1.plugin.utils.TextUtils;
import team1.plugin.utils.TreeUtils;


public class HideMethodTest extends LightPlatformTestCase {
    public void testNullFile(){
        HideMethodAction action = new HideMethodAction();
        assertFalse(action._isRefactorable(null));
    }

    public void testEmptyCode(){
        String codeBlock = "";
        PsiFile file = TestUtils.getPsiFileFromString(codeBlock);

        HideMethodAction action = new HideMethodAction();
        assertFalse(action._isRefactorable(file));
    }

    public void testNoMethod(){
        String codeBlock = "class myClass{ int a; int b; }";
        PsiFile file = TestUtils.getPsiFileFromString(codeBlock);

        HideMethodAction action = new HideMethodAction();
        assertFalse(action._isRefactorable(file));
    }

    public void testMethodHasExternalReference(){
  /*      String codeBlock = "class myClass{ int a; static void b(){} } " +
                "class yourClass{ void a(){ myClass.b(); } }";*/

        String codeBlock = "class a {\n" +
                "    static void aF {}\n" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    void bF { a.aF() }\n" +
                "}";

        PsiFile file = TestUtils.getPsiFileFromString(codeBlock);
        HideMethodAction action = new HideMethodAction();
        assertFalse(action._isRefactorable(file));
    }

    public void testWithMethod(){
        String codeBlock = "class myClass{ int a; void b(){}}";
        PsiFile file = TestUtils.getPsiFileFromString(codeBlock);

        HideMethodAction action = new HideMethodAction();
        assertTrue(action._isRefactorable(file));
    }

    public void testRefactorOneMethod() {
        String codeBlock = "class a {\n" +
                "    public void aF(){ }\n" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    void bF(){ }\n" +
                "}";

        String ansBlock = "class a {\n" +
                "    private void aF(){ }\n" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    private void bF(){ }\n" +
                "}";

        PsiFile file = TestUtils.getPsiFileFromString(codeBlock);

        HideMethodAction action = new HideMethodAction();
        assertTrue(action._isRefactorable(file));

        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(file.getText(), ansBlock));
    }

    public void testRefactorAllMultipleMethods() {
        String codeBlock = "class a {\n" +
                "    public void aF(){ }\n" +
                "    void oneMore(){}" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    void bF(){ }\n" +
                "}";

        String ansBlock = "class a {\n" +
                "    private void aF(){ }\n" +
                "    private void oneMore(){}" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    private void bF(){ }\n" +
                "}";

        PsiFile file = TestUtils.getPsiFileFromString(codeBlock);

        HideMethodAction action = new HideMethodAction();
        assertTrue(action._isRefactorable(file));

        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(file.getText(), ansBlock));
    }

    public void testRefactorParticularMultipleMethod() {
        String codeBlock = "class a {\n" +
                "    public void aF(){ }\n" +
                "    static void oneMore(){}" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    void bF(){ a.oneMore()}\n" +
                "}";

        String ansBlock = "class a {\n" +
                "    private void aF(){ }\n" +
                "    static void oneMore(){}" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    private void bF(){ a.oneMore()}\n" +
                "}";

        PsiFile file = TestUtils.getPsiFileFromString(codeBlock);

        HideMethodAction action = new HideMethodAction();
        assertTrue(action._isRefactorable(file));

        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(file.getText(), ansBlock));
    }

    // Test for PSI Tree structure
    public void testTreeRefactorOneMethod() {
        String codeBlock = "class a {\n" +
                "    public void aF(){ }\n" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    void bF(){ }\n" +
                "}";

        String ansBlock = "class a {\n" +
                "    private void aF(){ }\n" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    private void bF(){ }\n" +
                "}";

        PsiFile file = TestUtils.getPsiFileFromString(codeBlock);

        HideMethodAction action = new HideMethodAction();
        assertTrue(action._isRefactorable(file));

        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), ansBlock, getProject()));
    }

    public void testTreeRefactorAllMultipleMethods() {
        String codeBlock = "class a {\n" +
                "    public void aF(){ }\n" +
                "    void oneMore(){}" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    void bF(){ }\n" +
                "}";

        String ansBlock = "class a {\n" +
                "    private void aF(){ }\n" +
                "    private void oneMore(){}" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    private void bF(){ }\n" +
                "}";

        PsiFile file = TestUtils.getPsiFileFromString(codeBlock);

        HideMethodAction action = new HideMethodAction();
        assertTrue(action._isRefactorable(file));

        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), ansBlock, getProject()));
    }

    public void testTreeRefactorParticularMultipleMethod() {
        String codeBlock = "class a {\n" +
                "    public void aF(){ }\n" +
                "    static void oneMore(){}" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    void bF(){ a.oneMore()}\n" +
                "}";

        String ansBlock = "class a {\n" +
                "    private void aF(){ }\n" +
                "    static void oneMore(){}" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    private void bF(){ a.oneMore()}\n" +
                "}";

        PsiFile file = TestUtils.getPsiFileFromString(codeBlock);

        HideMethodAction action = new HideMethodAction();
        assertTrue(action._isRefactorable(file));

        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), ansBlock, getProject()));
    }
}
