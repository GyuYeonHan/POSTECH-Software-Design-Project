package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.TreeUtils;


public class ReplaceMethodWithMethodObjectTest extends LightPlatformTestCase {
    public PsiFile getPsiFileFromString(String codeBlock)
    {
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), codeBlock);

        return file;
    }

    public void testNoSelection(){
        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();
        assertFalse(action._isRefactorable(null));
    }

    public void testSmallMethod(){
        String codeBlock =  "public class Test { void aF(){ } }";
        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();
        assertFalse(action._isRefactorable(method));

    }

    public void testMethodCallingPrivateMethod(){
        String codeBlock =  "public class Test { " +
                "private void aF(){ } " +
                "public void b(){ " +
                "    int k=0;" +
                "    String s = \"fklshuu9iuifihyelkrejkhaKASDGTKJHYKIHKWALJGLOKHDFUITLAHWERsaefwekljfsadjkljk\";  " +
                "aF(); } }";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[1];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();
        assertFalse(action._isRefactorable(method));
    }

    public void testPublicMemberVariableIsRefactorableTrue(){
        String codeBlock =  "public class Test { " +
                "public int marshmallow; " +
                "public int b(){ " +
                "    int k=0;" +
                "    // Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~\n" +
                "    // Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~\n" +
                "    // Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~\n" +
                "    // Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~\n" +
                "    return k + marshmallow;" +
                " }" +
                "}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();
        assertTrue(action._isRefactorable(method));
    }

    public void testPrivateMemberVariableIsRefactorableFalse(){
        String codeBlock =  "public class Test { " +
                "private int marshmallow; " +
                "public int b(){ " +
                "    int k=0;" +
                "    // Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~\n" +
                "    // Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~\n" +
                "    // Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~\n" +
                "    // Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~\n" +
                "    return k + marshmallow;" +
                " }" +
                "}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();
        assertFalse(action._isRefactorable(method));
    }

    public void testProtectedMemberVariableIsRefactorableFalse(){
        String codeBlock =  "public class Test { " +
                "protected int marshmallow; " +
                "public int b(){ " +
                "    int k=0;" +
                "    // Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~\n" +
                "    // Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~\n" +
                "    // Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~\n" +
                "    // Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~ Let it go~\n" +
                "    return k + marshmallow;" +
                " }" +
                "}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();
        assertFalse(action._isRefactorable(method));
    }

    public void testWithPrivateMethodCall(){
        String codeBlock =  "class aClass {\n" +
                "    public int aF() {\n" +
                "        int baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab = 3;\n" +
                "        fantasticBaby();\n" +
                "        return baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab + fantasticBaby();\n" +
                "    }\n" +
                "\n" +
                "    private int fantasticBaby() {\n" +
                "        return 4;\n" +
                "    }\n" +
                "}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();

        assertFalse(action._isRefactorable(method));
    }

    public void testWithProtectedMethodCall(){
        String codeBlock =  "class aClass {\n" +
                "    public int aF() {\n" +
                "        int baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab = 3;\n" +
                "        fantasticBaby();\n" +
                "        return baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab + fantasticBaby();\n" +
                "    }\n" +
                "\n" +
                "    protected int fantasticBaby() {\n" +
                "        return 4;\n" +
                "    }\n" +
                "}";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();
        assertFalse(action._isRefactorable(method));
    }

    public void testLargeMethodRefactorable(){
        String codeBlock =  "public class Test { " +
                "void aF(){ } " +
                "public void b(){ " +
                "    int k=0;" +
                "    String s = \"fklshuu9iuifihyelkrejkhaKASDGTKJHYKIHKWALJGLOKHDFUITLAHWERsaefwekljfsadjkljk\";  " +
                "aF(); } }";

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[1];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();
        assertTrue(action._isRefactorable(method));
    }

    public void testMethodToClass(){
        String codeBlock =  "public class aClass { " +
                "public void aF(){ " +
                "//jghjhgszfjisgffabjszdfgwaeofiasfihawefuisafagweiofhsaifioasfhusadfiojhsadfoguasf\n" +
                "}}";

        String result="public class aClass { " +
                "public void aF(){" +
                "     return new bClass(this).compute();" +
                "}}" +
                "class bClass{" +
                "    public bClass(aClass _aclass){ }" +
                "    public void compute() { " +
                "    //jghjhgszfjisgffabjszdfgwaeofiasfihawefuisafagweiofhsaifioasfhusadfiojhsadfoguasf\n" +
                "}}" ;

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();
        assertTrue(action._isRefactorable(method));

        action._runRefactoring(file, method, "bClass");
        assertTrue(TreeUtils.codeEqual(file.getText(), result,file.getProject()));
    }




    public void testMethodToClassFalse(){
        String codeBlock =  "public class aClass { " +
                "public void aF(){ " +
                "//jghjhgszfjisgffabjszdfgwaeofiasfihawefuisafagweiofhsaifioasfhusadfiojhsadfoguasf\n" +
                "}}";

        String result="public class aClass { " +
                "public void aF(){" +
                "     return new bClass(this).compute();" +
                "}}" +
                "class fakeClass{" +
                "    public bClass(aClass _aclass){ }" +
                "    public void compute() { " +
                "    //jghjhgszfjisgffabjszdfgwaeofiasfihawefuisafagweiofhsaifioasfhusadfiojhsadfoguasf\n" +
                "}}" ;

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();
        assertTrue(action._isRefactorable(method));

        action._runRefactoring(file, method, "bClass");

        assertFalse(TreeUtils.codeEqual(file.getText(), result,file.getProject()));
    }

    public void testLocalVariableToFieldIsRefactorable(){
        String codeBlock =  "public class aClass { " +
                "public void aF(){ " +
                "int asauighiatyjkszfhuijksafhdjksahdfiwajekisaudfhzgbduiwauszojdfbhixzodwaifhjszofdhuieg;" +
                "}}";

        String result="public class aClass { " +
                "public void aF(){" +
                "     return new bClass(this).compute();" +
                "}}" +
                "class bClass{" +
                "    private int asauighiatyjkszfhuijksafhdjksahdfiwajekisaudfhzgbduiwauszojdfbhixzodwaifhjszofdhuieg;" +
                "    public bClass(aClass _aclass){ }" +
                "    public void compute() { " +
                "}}" ;

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();

        assertTrue(action._isRefactorable(method));
    }

    public void testLocalVariableToFieldRunRefactoring(){
        String codeBlock =  "public class aClass { " +
                "public void aF(){ " +
                "int asauighiatyjkszfhuijksafhdjksahdfiwajekisaudfhzgbduiwauszojdfbhixzodwaifhjszofdhuieg;" +
                "}}";

        String result="public class aClass { " +
                "public void aF(){" +
                "     return new bClass(this).compute();" +
                "}}" +
                "class bClass{" +
                "    private int asauighiatyjkszfhuijksafhdjksahdfiwajekisaudfhzgbduiwauszojdfbhixzodwaifhjszofdhuieg;" +
                "    public bClass(aClass _aclass){ }" +
                "    public void compute() { " +
                "}}" ;

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();

        action._runRefactoring(file, method, "bClass");
        assertTrue(TreeUtils.codeEqual(file.getText(), result,file.getProject()));
    }


    public void testMethodWithParameter(){
        String codeBlock =  "public class aClass { " +
                "public int aF(int k){" +
                "int baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab = 3;" +
                "return baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab+k;" +
                "}}";

        String result="public class aClass { " +
                "public int aF(int k){" +
                "     return new bClass(this).compute(k);" +
                "}}" +
                "class bClass{" +
                "    private int baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab;" +
                "    public bClass(aClass _aclass){ " +
                "      this.baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab = 3; }" +
                "    public int compute(int k) { " +
                "    return baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab+k; " +
                "}}" ;

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();

        action._runRefactoring(file, method, "bClass");

        assertTrue(TreeUtils.codeEqual(file.getText(), result,file.getProject()));
    }

    public void testMethodWithManyParameter(){
        String codeBlock =  "public class aClass { " +
                "public int aF(int k, int j){" +
                "int baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab = 3;" +
                "return baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab+k*j;" +
                "}}";

        String result="public class aClass { " +
                "public int aF(int k, int j){" +
                "     return new bClass(this).compute(k, j);" +
                "}}" +
                "class bClass{" +
                "    private int baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab;" +
                "    public bClass(aClass _aclass){ " +
                "      this.baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab = 3; }" +
                "    public int compute(int k, int j) { " +
                "    return baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab+k*j; " +
                "}}" ;

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();

        action._runRefactoring(file, method, "bClass");

        assertTrue(TreeUtils.codeEqual(file.getText(), result,file.getProject()));
    }

    public void testWithPublicMethodCall(){
        String codeBlock =  "class aClass {\n" +
                "    public int aF() {\n" +
                "        int baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab = 3;\n" +
                "        fantasticBaby();\n" +
                "        return baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab + fantasticBaby();\n" +
                "    }\n" +
                "\n" +
                "    public int fantasticBaby() {\n" +
                "        return 4;\n" +
                "    }\n" +
                "}";

        String result="class aClass {\n" +
                "    public int aF() {\n" +
                "        return new bClass(this).compute();\n" +
                "    }\n" +
                "    public int fantasticBaby() {\n" +
                "        return 4;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class bClass {\n" +
                "    private aClass _aclass; \n" +
                "    private int baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab;\n" +
                "\n" +
                "    public bClass(aClass _aclass) {\n" +
                "        this._aclass = _aclass;\n" +
                "        this.baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab = 3;\n" +
                "    }\n" +
                "\n" +
                "    public int compute() {\n"+
                "        this._aclass.fantasticBaby();\n" +
                "        return baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab + this._aclass.fantasticBaby();\n" +
                "    }\n" +
                "}" ;

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();

        action._runRefactoring(file, method, "bClass");

        assertTrue(TreeUtils.codeEqual(file.getText(), result,file.getProject()));
    }


    public void testWithPublicField(){
        String codeBlock =  "class aClass {\n" +
                "    public int fantasticBaby; \n" +
                "    public int aF() {\n" +
                "        int baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab = 3;\n" +
                "        return baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab + fantasticBaby;\n" +
                "    }\n" +
                "\n" +
                "}";

        String result="class aClass {\n" +
                "    public int fantasticBaby;" +
                "    public int aF() {\n" +
                "        return new bClass(this).compute();\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class bClass {\n" +
                "    private aClass _aclass; \n" +
                "    private int baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab;\n" +
                "\n" +
                "    public bClass(aClass _aclass) {\n" +
                "        this._aclass = _aclass;\n" +
                "        this.baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab = 3;\n" +
                "    }\n" +
                "\n" +
                "    public int compute() {\n"+
                "        return baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab + this._aclass.fantasticBaby;\n" +
                "    }\n" +
                "}" ;

        PsiFile file = getPsiFileFromString(codeBlock);
        PsiMethod method = ((PsiJavaFileImpl) file).getClasses()[0].getMethods()[0];

        ReplaceMethodWithMethodObjectAction action = new ReplaceMethodWithMethodObjectAction();

        action._runRefactoring(file, method, "bClass");

        assertTrue(TreeUtils.codeEqual(file.getText(), result,file.getProject()));
    }


}

