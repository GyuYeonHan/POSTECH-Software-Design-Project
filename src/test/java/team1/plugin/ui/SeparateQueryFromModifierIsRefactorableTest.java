package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.TextUtils;
import team1.plugin.utils.TreeUtils;

public class SeparateQueryFromModifierIsRefactorableTest  extends LightPlatformTestCase {

    public void testBlank(){
        String s1 = "";
        String s2 = "";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SeparateQueryFromModifierAction action = new SeparateQueryFromModifierAction();
        assertFalse(action.isRefactorable(file));
        assertTrue(TextUtils.codeEqual(file.getText(), s2));
    }

    public void testShortGetterSetter(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public int setIntAndgetInt(int i){\n" +
                "        set_variable_int = i;\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "}";
        String s2 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public int setIntAndgetInt(int i) {\n" +
                "        setterOfSetIntAndgetInt(i);\n" +
                "        return getterOfSetIntAndgetInt();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetIntAndgetInt(int i) {\n" +
                "        set_variable_int = i;\n" +
                "    }\n" +
                "\n" +
                "    private int getterOfSetIntAndgetInt() {\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SeparateQueryFromModifierAction action = new SeparateQueryFromModifierAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(file.getText(), s2));
    }

    public void testMultipleGetterSetter(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public int setIntAndgetInt(int i){\n" +
                "        set_variable_int = i;\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setIntAndgetBoolean(int i){\n" +
                "        set_variable_int = i;\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}";
        String s2 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public int setIntAndgetInt(int i) {\n" +
                "        setterOfSetIntAndgetInt(i);\n" +
                "        return getterOfSetIntAndgetInt();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetIntAndgetInt(int i) {\n" +
                "        set_variable_int = i;\n" +
                "    }\n" +
                "\n" +
                "    private int getterOfSetIntAndgetInt() {\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setIntAndgetBoolean(int i) {\n" +
                "        setterOfSetIntAndgetBoolean(i);\n" +
                "        return getterOfSetIntAndgetBoolean();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetIntAndgetBoolean(int i) {\n" +
                "        set_variable_int = i;\n" +
                "    }\n" +
                "\n" +
                "    private boolean getterOfSetIntAndgetBoolean() {\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SeparateQueryFromModifierAction action = new SeparateQueryFromModifierAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(file.getText(), s2));
    }

    public void testComplexCase(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public void emptyMethod(){}\n" +
                "\n" +
                "    public int setIntAndgetInt(int i){\n" +
                "        set_variable_int = i;\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setIntAndgetBoolean(int i){\n" +
                "        set_variable_int = i;\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "    public int setBooleanAndgetInt(boolean b){\n" +
                "        set_variable_boolean = b;\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setBooleanAndgetBoolean(boolean b){\n" +
                "        set_variable_boolean = b;\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "    public int setBooleanIntAndgetInt(int i, boolean b){\n" +
                "        set_variable_int = i;\n" +
                "        set_variable_boolean = b;\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setBooleanIntAndgetBoolean(int i, boolean b){\n" +
                "        set_variable_int = i;\n" +
                "        set_variable_boolean = b;\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}";
        String s2 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public void emptyMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public int setIntAndgetInt(int i) {\n" +
                "        setterOfSetIntAndgetInt(i);\n" +
                "        return getterOfSetIntAndgetInt();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetIntAndgetInt(int i) {\n" +
                "        set_variable_int = i;\n" +
                "    }\n" +
                "\n" +
                "    private int getterOfSetIntAndgetInt() {\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setIntAndgetBoolean(int i) {\n" +
                "        setterOfSetIntAndgetBoolean(i);\n" +
                "        return getterOfSetIntAndgetBoolean();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetIntAndgetBoolean(int i) {\n" +
                "        set_variable_int = i;\n" +
                "    }\n" +
                "\n" +
                "    private boolean getterOfSetIntAndgetBoolean() {\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "    public int setBooleanAndgetInt(boolean b) {\n" +
                "        setterOfSetBooleanAndgetInt(b);\n" +
                "        return getterOfSetBooleanAndgetInt();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetBooleanAndgetInt(boolean b) {\n" +
                "        set_variable_boolean = b;\n" +
                "    }\n" +
                "\n" +
                "    private int getterOfSetBooleanAndgetInt() {\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setBooleanAndgetBoolean(boolean b) {\n" +
                "        setterOfSetBooleanAndgetBoolean(b);\n" +
                "        return getterOfSetBooleanAndgetBoolean();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetBooleanAndgetBoolean(boolean b) {\n" +
                "        set_variable_boolean = b;\n" +
                "    }\n" +
                "\n" +
                "    private boolean getterOfSetBooleanAndgetBoolean() {\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "    public int setBooleanIntAndgetInt(int i, boolean b) {\n" +
                "        setterOfSetBooleanIntAndgetInt(i, b);\n" +
                "        return getterOfSetBooleanIntAndgetInt();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetBooleanIntAndgetInt(int i, boolean b) {\n" +
                "        set_variable_int = i;\n" +
                "        set_variable_boolean = b;\n" +
                "    }\n" +
                "\n" +
                "    private int getterOfSetBooleanIntAndgetInt() {\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setBooleanIntAndgetBoolean(int i, boolean b) {\n" +
                "        setterOfSetBooleanIntAndgetBoolean(i, b);\n" +
                "        return getterOfSetBooleanIntAndgetBoolean();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetBooleanIntAndgetBoolean(int i, boolean b) {\n" +
                "        set_variable_int = i;\n" +
                "        set_variable_boolean = b;\n" +
                "    }\n" +
                "\n" +
                "    private boolean getterOfSetBooleanIntAndgetBoolean() {\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SeparateQueryFromModifierAction action = new SeparateQueryFromModifierAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(file.getText(), s2));
    }

    public void testNoSetter(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public int setBooleanAndgetInt(boolean b){\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "    \n" +
                "}";

        String s2 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public int setBooleanAndgetInt(boolean b){\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "    \n" +
                "}";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SeparateQueryFromModifierAction action = new SeparateQueryFromModifierAction();
        assertFalse(action.isRefactorable(file));
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(file.getText(), s2));
    }

    public void testNoGetter(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public void setBooleanAndgetInt(boolean b){\n" +
                "        set_variable_boolean = b;\n" +
                "    }\n" +
                "    \n" +
                "}";
        String s2 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public void setBooleanAndgetInt(boolean b){\n" +
                "        set_variable_boolean = b;\n" +
                "    }\n" +
                "    \n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SeparateQueryFromModifierAction action = new SeparateQueryFromModifierAction();
        assertFalse(action.isRefactorable(file));
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
        SeparateQueryFromModifierAction action = new SeparateQueryFromModifierAction();
        assertFalse(action.isRefactorable(file));
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }

    public void testTreeShortGetterSetter(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public int setIntAndgetInt(int i){\n" +
                "        set_variable_int = i;\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "}";
        String s2 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public int setIntAndgetInt(int i) {\n" +
                "        setterOfSetIntAndgetInt(i);\n" +
                "        return getterOfSetIntAndgetInt();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetIntAndgetInt(int i) {\n" +
                "        set_variable_int = i;\n" +
                "    }\n" +
                "\n" +
                "    private int getterOfSetIntAndgetInt() {\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SeparateQueryFromModifierAction action = new SeparateQueryFromModifierAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }

    public void testTreeMultipleGetterSetter(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public int setIntAndgetInt(int i){\n" +
                "        set_variable_int = i;\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setIntAndgetBoolean(int i){\n" +
                "        set_variable_int = i;\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}";
        String s2 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public int setIntAndgetInt(int i) {\n" +
                "        setterOfSetIntAndgetInt(i);\n" +
                "        return getterOfSetIntAndgetInt();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetIntAndgetInt(int i) {\n" +
                "        set_variable_int = i;\n" +
                "    }\n" +
                "\n" +
                "    private int getterOfSetIntAndgetInt() {\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setIntAndgetBoolean(int i) {\n" +
                "        setterOfSetIntAndgetBoolean(i);\n" +
                "        return getterOfSetIntAndgetBoolean();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetIntAndgetBoolean(int i) {\n" +
                "        set_variable_int = i;\n" +
                "    }\n" +
                "\n" +
                "    private boolean getterOfSetIntAndgetBoolean() {\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SeparateQueryFromModifierAction action = new SeparateQueryFromModifierAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }

    public void testTreeComplexCase(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public void emptyMethod(){}\n" +
                "\n" +
                "    public int setIntAndgetInt(int i){\n" +
                "        set_variable_int = i;\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setIntAndgetBoolean(int i){\n" +
                "        set_variable_int = i;\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "    public int setBooleanAndgetInt(boolean b){\n" +
                "        set_variable_boolean = b;\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setBooleanAndgetBoolean(boolean b){\n" +
                "        set_variable_boolean = b;\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "    public int setBooleanIntAndgetInt(int i, boolean b){\n" +
                "        set_variable_int = i;\n" +
                "        set_variable_boolean = b;\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setBooleanIntAndgetBoolean(int i, boolean b){\n" +
                "        set_variable_int = i;\n" +
                "        set_variable_boolean = b;\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}";
        String s2 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public void emptyMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public int setIntAndgetInt(int i) {\n" +
                "        setterOfSetIntAndgetInt(i);\n" +
                "        return getterOfSetIntAndgetInt();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetIntAndgetInt(int i) {\n" +
                "        set_variable_int = i;\n" +
                "    }\n" +
                "\n" +
                "    private int getterOfSetIntAndgetInt() {\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setIntAndgetBoolean(int i) {\n" +
                "        setterOfSetIntAndgetBoolean(i);\n" +
                "        return getterOfSetIntAndgetBoolean();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetIntAndgetBoolean(int i) {\n" +
                "        set_variable_int = i;\n" +
                "    }\n" +
                "\n" +
                "    private boolean getterOfSetIntAndgetBoolean() {\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "    public int setBooleanAndgetInt(boolean b) {\n" +
                "        setterOfSetBooleanAndgetInt(b);\n" +
                "        return getterOfSetBooleanAndgetInt();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetBooleanAndgetInt(boolean b) {\n" +
                "        set_variable_boolean = b;\n" +
                "    }\n" +
                "\n" +
                "    private int getterOfSetBooleanAndgetInt() {\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setBooleanAndgetBoolean(boolean b) {\n" +
                "        setterOfSetBooleanAndgetBoolean(b);\n" +
                "        return getterOfSetBooleanAndgetBoolean();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetBooleanAndgetBoolean(boolean b) {\n" +
                "        set_variable_boolean = b;\n" +
                "    }\n" +
                "\n" +
                "    private boolean getterOfSetBooleanAndgetBoolean() {\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "    public int setBooleanIntAndgetInt(int i, boolean b) {\n" +
                "        setterOfSetBooleanIntAndgetInt(i, b);\n" +
                "        return getterOfSetBooleanIntAndgetInt();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetBooleanIntAndgetInt(int i, boolean b) {\n" +
                "        set_variable_int = i;\n" +
                "        set_variable_boolean = b;\n" +
                "    }\n" +
                "\n" +
                "    private int getterOfSetBooleanIntAndgetInt() {\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "\n" +
                "    public boolean setBooleanIntAndgetBoolean(int i, boolean b) {\n" +
                "        setterOfSetBooleanIntAndgetBoolean(i, b);\n" +
                "        return getterOfSetBooleanIntAndgetBoolean();\n" +
                "    }\n" +
                "\n" +
                "    private void setterOfSetBooleanIntAndgetBoolean(int i, boolean b) {\n" +
                "        set_variable_int = i;\n" +
                "        set_variable_boolean = b;\n" +
                "    }\n" +
                "\n" +
                "    private boolean getterOfSetBooleanIntAndgetBoolean() {\n" +
                "        return get_variable_boolean;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SeparateQueryFromModifierAction action = new SeparateQueryFromModifierAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }

    public void testTreeNoSetter(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public int setBooleanAndgetInt(boolean b){\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "    \n" +
                "}";

        String s2 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public int setBooleanAndgetInt(boolean b){\n" +
                "        return get_variable_int;\n" +
                "    }\n" +
                "    \n" +
                "}";

        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SeparateQueryFromModifierAction action = new SeparateQueryFromModifierAction();
        assertFalse(action.isRefactorable(file));
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }

    public void testTreeNoGetter(){
        String s1 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public void setBooleanAndgetInt(boolean b){\n" +
                "        set_variable_boolean = b;\n" +
                "    }\n" +
                "    \n" +
                "}";
        String s2 = "public class Test {\n" +
                "\n" +
                "    private int set_variable_int;\n" +
                "    private boolean set_variable_boolean;\n" +
                "    private int get_variable_int;\n" +
                "    private boolean get_variable_boolean;\n" +
                "\n" +
                "    public void setBooleanAndgetInt(boolean b){\n" +
                "        set_variable_boolean = b;\n" +
                "    }\n" +
                "    \n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        SeparateQueryFromModifierAction action = new SeparateQueryFromModifierAction();
        assertFalse(action.isRefactorable(file));
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }

}
