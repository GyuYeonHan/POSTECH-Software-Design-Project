package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.TextUtils;
import team1.plugin.utils.TreeUtils;


public class ReplaceSubclassWithFieldsActionRunRefactoringTest extends LightPlatformTestCase {

    public void testTwoStringClassRunRefactor(){
        String s = "class Person {\n" +
                "    void getCode() {}\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    }\n" +
                "}";
        String ans = "class Person {\n" +
                "    private String name;\n" +
                "\n" +
                "    public Person(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    void getCode() {}\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(ans, file.getText()));
    }

    public void testOneStringClassRunRefactor() {
        String s = "class Person {\n" +
                "    void getCode() {}\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    }\n" +
                "}\n";
        String ans = "class Person {\n" +
                "    private String name;\n" +
                "\n" +
                "    public Person(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    void getCode() {}\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(ans, file.getText()));
    }

    public void testTwoIntClassRunRefactor(){
        String s = "class Person {\n" +
                "    void getCode() {}\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    int getAge() {\n" +
                "        return 42;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    int getAge() {\n" +
                "        return 76;\n" +
                "    }\n" +
                "}";
        String ans = "class Person {\n" +
                "    private int age;\n" +
                "\n" +
                "    public Person(int age) {\n" +
                "        this.age = age;\n" +
                "    }\n" +
                "\n" +
                "    void getCode() {}\n" +
                "\n" +
                "    public int getAge() {\n" +
                "        return age;\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(ans, file.getText()));
    }

    public void testTwoStringClassRunRefactorWithReference(){
        String s = "class Person {\n" +
                "    void getCode() {}\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        Male male = new Male();\n" +
                "        Female female = new Female();\n" +
                "        male.getName();\n" +
                "        female.getName();\n" +
                "    }\n" +
                "}";
        String ans = "class Person {\n" +
                "    private String name;\n" +
                "\n" +
                "    public Person(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    void getCode() {}\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        Person male = new Person(\"M\");\n" +
                "        Person female = new Person(\"F\");\n" +
                "        male.getName();\n" +
                "        female.getName();\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(ans, file.getText()));
    }

    public void testTwoStringClassRunRefactorWithnoAssignmentDeclarationReference(){
        String s = "class Person {\n" +
                "    void getCode() {}\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        Male male;\n" +
                "        male = new Male();\n" +
                "        Female female;\n" +
                "        female = new Female();\n" +
                "        male.getName();\n" +
                "        female.getName();\n" +
                "    }\n" +
                "}";
        String ans = "class Person {\n" +
                "    private String name;\n" +
                "\n" +
                "    public Person(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    void getCode() {}\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        Person male;\n" +
                "        male = new Person(\"M\");\n" +
                "        Person female;\n" +
                "        female = new Person(\"F\");\n" +
                "        male.getName();\n" +
                "        female.getName();\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(ans, file.getText()));
    }

    public void testTwoStringClassRunRefactorWithAlreadyhasConstructor(){
        String s = "class Person {\n" +
                "    private int age;\n" +
                "    Person(int age) {\n" +
                "        this.age = age;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    Male(int age) {\n" +
                "        super(age);\n" +
                "    }\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    Female(int age) {\n" +
                "        super(age);\n" +
                "    }\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    };\n" +
                "}";
        String ans = "class Person {\n" +
                "    private int age;\n" +
                "    private String name;\n" +
                "\n" +
                "    Person(int age, String name) {\n" +
                "        this.age = age;\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(ans, file.getText()));
    }

    public void testRefactorFailedByDifferentType() {
        String s = "class Person {\n" +
                "    private string name;\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    int getName() {\n" +
                "        return 40;\n" +
                "    };\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    };\n" +
                "}";
        String ans = "class Person {\n" +
                "    private string name;\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    int getName() {\n" +
                "        return 40;\n" +
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
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(ans, file.getText()));
    }

    public void testRefactorFailedByReturnNotConstant() {
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
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    };\n" +
                "}";
        String ans = "class Person {\n" +
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
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    };\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(ans, file.getText()));
    }

    public void testRefactorFailedByDifferentNamedMethod() {
        String s = "class Person {\n" +
                "    private string name;\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    int getAge() {\n" +
                "        return 70;\n" +
                "    };\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    };\n" +
                "}";
        String ans = "class Person {\n" +
                "    private string name;\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    int getAge() {\n" +
                "        return 70;\n" +
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
        action.runRefactoring(file);
        assertTrue(TextUtils.codeEqual(ans, file.getText()));
    }

    // Test for PSI Tree Structure
    public void testTreeTwoStringClassRunRefactor(){
        String s = "class Person {\n" +
                "    void getCode() {}\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    }\n" +
                "}";
        String ans = "class Person {\n" +
                "    private String name;\n" +
                "\n" +
                "    public Person(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    void getCode() {}\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), getProject()));
    }

    public void testTreeOneStringClassRunRefactor() {
        String s = "class Person {\n" +
                "    void getCode() {}\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    }\n" +
                "}\n";
        String ans = "class Person {\n" +
                "    private String name;\n" +
                "\n" +
                "    public Person(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    void getCode() {}\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), getProject()));
    }

    public void testTreeTwoIntClassRunRefactor(){
        String s = "class Person {\n" +
                "    void getCode() {}\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    int getAge() {\n" +
                "        return 42;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    int getAge() {\n" +
                "        return 76;\n" +
                "    }\n" +
                "}";
        String ans = "class Person {\n" +
                "    private int age;\n" +
                "\n" +
                "    public Person(int age) {\n" +
                "        this.age = age;\n" +
                "    }\n" +
                "\n" +
                "    void getCode() {}\n" +
                "\n" +
                "    public int getAge() {\n" +
                "        return age;\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), getProject()));
    }

    public void testTreeTwoStringClassRunRefactorWithReference(){
        String s = "class Person {\n" +
                "    void getCode() {}\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        Male male = new Male();\n" +
                "        Female female = new Female();\n" +
                "        male.getName();\n" +
                "        female.getName();\n" +
                "    }\n" +
                "}";
        String ans = "class Person {\n" +
                "    private String name;\n" +
                "\n" +
                "    public Person(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    void getCode() {}\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        Person male = new Person(\"M\");\n" +
                "        Person female = new Person(\"F\");\n" +
                "        male.getName();\n" +
                "        female.getName();\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), getProject()));
    }

    public void testTreeTwoStringClassRunRefactorWithnoAssignmentDeclarationReference(){
        String s = "class Person {\n" +
                "    void getCode() {}\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        Male male;\n" +
                "        male = new Male();\n" +
                "        Female female;\n" +
                "        female = new Female();\n" +
                "        male.getName();\n" +
                "        female.getName();\n" +
                "    }\n" +
                "}";
        String ans = "class Person {\n" +
                "    private String name;\n" +
                "\n" +
                "    public Person(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    void getCode() {}\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        Person male;\n" +
                "        male = new Person(\"M\");\n" +
                "        Person female;\n" +
                "        female = new Person(\"F\");\n" +
                "        male.getName();\n" +
                "        female.getName();\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), getProject()));
    }

    public void testTreeTwoStringClassRunRefactorWithAlreadyhasConstructor(){
        String s = "class Person {\n" +
                "    private int age;\n" +
                "    Person(int age) {\n" +
                "        this.age = age;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    Male(int age) {\n" +
                "        super(age);\n" +
                "    }\n" +
                "    String getName() {\n" +
                "        return \"M\";\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    Female(int age) {\n" +
                "        super(age);\n" +
                "    }\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    };\n" +
                "}";
        String ans = "class Person {\n" +
                "    private int age;\n" +
                "    private String name;\n" +
                "\n" +
                "    Person(int age, String name) {\n" +
                "        this.age = age;\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), getProject()));
    }

    public void testTreeRefactorFailedByDifferentType() {
        String s = "class Person {\n" +
                "    private string name;\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    int getName() {\n" +
                "        return 40;\n" +
                "    };\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    };\n" +
                "}";
        String ans = "class Person {\n" +
                "    private string name;\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    int getName() {\n" +
                "        return 40;\n" +
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
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), getProject()));
    }

    public void testTreeRefactorFailedByReturnNotConstant() {
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
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    };\n" +
                "}";
        String ans = "class Person {\n" +
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
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    };\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceSubclassWithFieldsAction action = new ReplaceSubclassWithFieldsAction();
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), getProject()));
    }

    public void testTreeRefactorFailedByDifferentNamedMethod() {
        String s = "class Person {\n" +
                "    private string name;\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    int getAge() {\n" +
                "        return 70;\n" +
                "    };\n" +
                "}\n" +
                "\n" +
                "class Female extends Person {\n" +
                "    String getName() {\n" +
                "        return \"F\";\n" +
                "    };\n" +
                "}";
        String ans = "class Person {\n" +
                "    private string name;\n" +
                "    void getCode() {};\n" +
                "}\n" +
                "\n" +
                "class Male extends Person {\n" +
                "    int getAge() {\n" +
                "        return 70;\n" +
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
        action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(ans, file.getText(), getProject()));
    }
}
