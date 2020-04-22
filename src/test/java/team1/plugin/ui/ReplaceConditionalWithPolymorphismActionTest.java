package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.EditorUtils;
import team1.plugin.utils.TextUtils;
import team1.plugin.utils.TreeUtils;

import java.util.*;

public class ReplaceConditionalWithPolymorphismActionTest extends LightPlatformTestCase {
    public void testBaseRefactorable() {
        String s = "class Main {\n" +
                "    enum Country {EUROPEAN, AFRICAN, NORWEGIAN_BLUE}\n" +
                "    Country type;\n" +
                "    int numberOfCoconuts, voltage;\n" +
                "    boolean isNailed;\n" +
                "    public int getBaseSpeed(){return 0;}\n" +
                "    public int getBaseSpeed(int v){return v;}\n" +
                "    public int getLoadFactor(){return 1;}\n" +
                "    double getSpeed() {\n" +
                "        switch (type) {\n" +
                "            case EUROPEAN:\n" +
                "                return getBaseSpeed();\n" +
                "            case AFRICAN:\n" +
                "                return getBaseSpeed() - getLoadFactor() * numberOfCoconuts;\n" +
                "            case NORWEGIAN_BLUE:\n" +
                "                return (isNailed) ? 0 : getBaseSpeed(voltage);\n" +
                "        }\n" +
                "        throw new RuntimeException(\"Should be unreachable\");\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceConditionalWithPolymorphismAction action = new ReplaceConditionalWithPolymorphismAction();
        assertTrue(action.isRefactorable(file));
    }

    public void testNotRefactorable_NonStringTypeError() {
        // each case type should have name that can be class's name
        String s = "class Main {\n" +
                "    enum Country {EUROPEAN, AFRICAN, NORWEGIAN_BLUE}\n" +
                "    Country type;\n" +
                "    int numberOfCoconuts, voltage, inttype;\n" +
                "    boolean isNailed;\n" +
                "    public int getBaseSpeed(){return 0;}\n" +
                "    public int getBaseSpeed(int v){return v;}\n" +
                "    public int getLoadFactor(){return 1;}\n" +
                "    double getSpeed() {\n" +
                "        switch (inttype) {\n" +
                "            case 1:\n" +
                "                return getBaseSpeed();\n" +
                "            case 2:\n" +
                "                return getBaseSpeed() - getLoadFactor() * numberOfCoconuts;\n" +
                "            case 3:\n" +
                "                return (isNailed) ? 0 : getBaseSpeed(voltage);\n" +
                "        }\n" +
                "        throw new RuntimeException(\"Should be unreachable\");\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceConditionalWithPolymorphismAction action = new ReplaceConditionalWithPolymorphismAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testNotRefactorable_EmptyStatementCaseError() {
        // each case should have its own statements
        String s = "class Main {\n" +
                "    enum Country {EUROPEAN, AFRICAN, NORWEGIAN_BLUE}\n" +
                "    Country type;\n" +
                "    int numberOfCoconuts, voltage, inttype;\n" +
                "    boolean isNailed;\n" +
                "    public int getBaseSpeed(){return 0;}\n" +
                "    public int getBaseSpeed(int v){return v;}\n" +
                "    public int getLoadFactor(){return 1;}\n" +
                "    double getSpeed() {\n" +
                "        switch (type) {\n" +
                "            case EUROPEAN:\n" +
                "            case AFRICAN:\n" +
                "                return getBaseSpeed() - getLoadFactor() * numberOfCoconuts;\n" +
                "            case NORWEGIAN_BLUE:\n" +
                "                return (isNailed) ? 0 : getBaseSpeed(voltage);\n" +
                "        }\n" +
                "        throw new RuntimeException(\"Should be unreachable\");\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s);
        ReplaceConditionalWithPolymorphismAction action = new ReplaceConditionalWithPolymorphismAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testBaseRefactoring(){
        String s1 = "class Main {\n" +
                "    enum Country {EUROPEAN, AFRICAN, NORWEGIAN_BLUE}\n" +
                "    Country type;\n" +
                "    int numberOfCoconuts, voltage;\n" +
                "    boolean isNailed;\n" +
                "    public int getBaseSpeed(){return 0;}\n" +
                "    public int getBaseSpeed(int v){return v;}\n" +
                "    public int getLoadFactor(){return 1;}\n" +
                "    public double getSpeed() {\n" +
                "        switch (type) {\n" +
                "            case EUROPEAN:\n" +
                "                return getBaseSpeed();\n" +
                "            case AFRICAN:\n" +
                "                return getBaseSpeed() - getLoadFactor() * numberOfCoconuts;\n" +
                "            case NORWEGIAN_BLUE:\n" +
                "                return (isNailed) ? 0 : getBaseSpeed(voltage);\n" +
                "        }\n" +
                "        throw new RuntimeException(\"Should be unreachable\");\n" +
                "    }\n" +
                "}";

        String s2 = "abstract class Main {\n" +
                "    enum Country {EUROPEAN, AFRICAN, NORWEGIAN_BLUE}\n" +
                "    Country type;\n" +
                "    int numberOfCoconuts, voltage;\n" +
                "    boolean isNailed;\n" +
                "    public int getBaseSpeed(){return 0;}\n" +
                "    public int getBaseSpeed(int v){return v;}\n" +
                "    public int getLoadFactor(){return 1;}\n" +
                "    public abstract double getSpeed(){}\n" +
                "}\n" +
                "public class European extends Main {\n" +
                "    public double getSpeed() {\n" +
                "        return getBaseSpeed();\n" +
                "    }\n" +
                "}\n" +
                "public class African extends Main {\n" +
                "    public double getSpeed() {\n" +
                "        return getBaseSpeed() - getLoadFactor() * numberOfCoconuts;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "public class NorwegianBlue extends Main {\n" +
                "    public double getSpeed() {\n" +
                "        return (isNailed) ? 0 : getBaseSpeed(voltage);\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        ReplaceConditionalWithPolymorphismAction action = new ReplaceConditionalWithPolymorphismAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        String outputText = file.getText();
        System.out.println(outputText);
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }

    public void testComplexRefactoring(){
        String s1 = "class Main {\n" +
                "    enum Country {EUROPEAN, AFRICAN, NORWEGIAN_BLUE}\n" +
                "    Country type;\n" +
                "    int numberOfCoconuts, voltage;\n" +
                "    boolean isNailed;\n" +
                "    public int getBaseSpeed(){return 0;}\n" +
                "    public int getBaseSpeed(int v){return v;}\n" +
                "    public int getLoadFactor(){return 1;}\n" +
                "    public double getSpeed() {\n" +
                "        switch (type) {\n" +
                "            case EUROPEAN:\n" +
                "                numberOfCoconuts = 3;\n" +
                "                voltage = 2;\n" +
                "                return getBaseSpeed();\n" +
                "            case AFRICAN:\n" +
                "                voltage = 7;\n" +
                "                return getBaseSpeed() - getLoadFactor() * numberOfCoconuts;\n" +
                "            case NORWEGIAN_BLUE:\n" +
                "                isNailed = numberOfCoconuts == 4;  \n" +
                "                return (isNailed) ? 0 : getBaseSpeed(voltage);\n" +
                "        }\n" +
                "        throw new RuntimeException(\"Should be unreachable\");\n" +
                "    }\n" +
                "}";

        String s2 = "abstract class Main {\n" +
                "    enum Country {EUROPEAN, AFRICAN, NORWEGIAN_BLUE}\n" +
                "    Country type;\n" +
                "    int numberOfCoconuts, voltage;\n" +
                "    boolean isNailed;\n" +
                "    public int getBaseSpeed(){return 0;}\n" +
                "    public int getBaseSpeed(int v){return v;}\n" +
                "    public int getLoadFactor(){return 1;}\n" +
                "    public abstract double getSpeed(){}\n" +
                "}\n" +
                "public class European extends Main {\n" +
                "    public double getSpeed() {\n" +
                "        numberOfCoconuts = 3;\n" +
                "        voltage = 2;\n" +
                "        return getBaseSpeed();\n" +
                "    }\n" +
                "}\n" +
                "public class African extends Main {\n" +
                "    public double getSpeed() {\n" +
                "        voltage = 7;\n" +
                "        return getBaseSpeed() - getLoadFactor() * numberOfCoconuts;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "public class NorwegianBlue extends Main {\n" +
                "    public  double getSpeed() {\n" +
                "        isNailed = numberOfCoconuts == 4;\n" +
                "        return (isNailed) ? 0 : getBaseSpeed(voltage);\n" +
                "    }\n" +
                "}";
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), s1);
        ReplaceConditionalWithPolymorphismAction action = new ReplaceConditionalWithPolymorphismAction();
        assertTrue(action.isRefactorable(file));
        action.runRefactoring(file);
        String outputText = file.getText();
        System.out.println(outputText);
        assertTrue(TreeUtils.codeEqual(file.getText(), s2, getProject()));
    }
}
