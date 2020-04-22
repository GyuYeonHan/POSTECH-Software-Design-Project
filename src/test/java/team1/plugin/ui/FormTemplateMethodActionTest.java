package team1.plugin.ui;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.TestUtils;
import team1.plugin.utils.TextUtils;
import team1.plugin.utils.TreeUtils;

public class FormTemplateMethodActionTest extends LightPlatformTestCase {
    public void testIsRefactorableEmpty() {
        String codeBlock = "";
        PsiFile file= TestUtils.getPsiFileFromString(codeBlock);
        FormTemplateMethodAction action=new FormTemplateMethodAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testWithoutInheritance(){
        String codeBlock = "class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "}\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base = this.units * this.rate;\n" +
                "        double tax = base * Site.TAX_RATE;\n" +
                "        return base * tax;\n" +
                "    }\n" +
                "}\n" +
                "\n";
        PsiFile file= TestUtils.getPsiFileFromString(codeBlock);
        FormTemplateMethodAction action=new FormTemplateMethodAction();
        assertFalse(action.isRefactorable(file));
    }
    public void testWithoutSimilarAlgorithm(){
        String codeBlock = "class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base = this.units * this.rate;\n" +
                "        double tax = base * Site.TAX_RATE;\n" +
                "        return base * tax;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base = this.units - this.rate + 0.5;\n" +
                "        double tax = base / Site.TAX_RATE - 0.2;\n" +
                "        return base + tax;\n" +
                "    }\n" +
                "}";
        PsiFile file= TestUtils.getPsiFileFromString(codeBlock);
        FormTemplateMethodAction action=new FormTemplateMethodAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testMethodWithSameAlgorithm(){
        String codeBlock = "class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "       double base=1.0;" +
                "       double tax=0.0; " +
                "        return base * tax;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "       double base=1.0;" +
                "       double tax=0.0; " +
                "        return base * tax;\n" +
                "    }\n" +
                "}";
        PsiFile file= TestUtils.getPsiFileFromString(codeBlock);
        FormTemplateMethodAction action=new FormTemplateMethodAction();
        assertTrue(action.isRefactorable(file));
    }


    public void testIsRefactorable() {
        String codeBlock = "class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base = this.units * this.rate;\n" +
                "        double tax = base * Site.TAX_RATE;\n" +
                "        return base + tax;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base = this.units * this.rate * 0.5;\n" +
                "        double tax = base * Site.TAX_RATE * 0.2;\n" +
                "        return base + tax;\n" +
                "    }\n" +
                "}";
        PsiFile file= TestUtils.getPsiFileFromString(codeBlock);
        FormTemplateMethodAction action=new FormTemplateMethodAction();
        assertTrue(action.isRefactorable(file));
    }

    public void testIsRefactorableWithDifferentReturnType() {
        String codeBlock = "class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base = this.units * this.rate;\n" +
                "        double tax = base * Site.TAX_RATE;\n" +
                "        return base + tax;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private float units;\n" +
                "    private float rate;\n" +
                "    float getBillableAmount() {\n" +
                "        float base = this.units * this.rate * 0.5f;\n" +
                "        float tax = base * Site.TAX_RATE * 0.2f;\n" +
                "        return base + tax;\n" +
                "    }\n" +
                "}";
        PsiFile file= TestUtils.getPsiFileFromString(codeBlock);
        FormTemplateMethodAction action=new FormTemplateMethodAction();
        assertFalse(action.isRefactorable(file));
    }

    public void testRunRefactoringBasic() {
        String codeBlock = "class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base = this.units * this.rate;\n" +
                "        double tax = base * Site.TAX_RATE;\n" +
                "        return base + tax;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base = this.units * this.rate * 0.5;\n" +
                "        double tax = base * Site.TAX_RATE * 0.2;\n" +
                "        return base + tax;\n" +
                "    }\n" +
                "}";
        String result = "abstract class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "\n" +
                "    protected abstract double getBaseAmount();\n" +
                "\n" +
                "    protected abstract double getTaxAmount();\n" +
                "\n" +
                "    double getBillableAmount() {\n" +
                "        return getBaseAmount() + getTaxAmount();\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "\n" +
                "    protected double getBaseAmount() {\n" +
                "        double base = this.units * this.rate;\n" +
                "        return base;\n" +
                "    }\n" +
                "\n" +
                "    protected double getTaxAmount() {\n" +
                "        double tax = getBaseAmount() * Site.TAX_RATE;\n" +
                "        return tax;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "\n" +
                "    protected double getBaseAmount() {\n" +
                "        double base = this.units * this.rate * 0.5;\n" +
                "        return base;\n" +
                "    }\n" +
                "\n" +
                "    protected double getTaxAmount() {\n" +
                "        double tax = getBaseAmount() * Site.TAX_RATE * 0.2;\n" +
                "        return tax;\n" +
                "    }\n" +
                "}";
        PsiFile file= TestUtils.getPsiFileFromString(codeBlock);
        FormTemplateMethodAction action=new FormTemplateMethodAction();
        if(action.isRefactorable(file))
            action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), result, file.getProject()));
    }


    public void testRunRefactoringWithMethodCall() {
        String codeBlock = "class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base = this.units * this.rate;\n" +
                "        double tax = base * Site.TAX_RATE;\n" +
                "        return base + tax + getBillableAmount();\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base = this.units * this.rate * 0.5;\n" +
                "        double tax = base * Site.TAX_RATE * 0.2;\n" +
                "        return base + tax + getBillableAmount();\n" +
                "    }\n" +
                "}";
        String result = "abstract class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "\n" +
                "    protected abstract double getBaseAmount();\n" +
                "\n" +
                "    protected abstract double getTaxAmount();\n" +
                "\n" +
                "    double getBillableAmount() {\n" +
                "        return getBaseAmount() + getTaxAmount() + getBillableAmount();\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "\n" +
                "    protected double getBaseAmount() {\n" +
                "        double base = this.units * this.rate;\n" +
                "        return base;\n" +
                "    }\n" +
                "\n" +
                "    protected double getTaxAmount() {\n" +
                "        double tax = getBaseAmount() * Site.TAX_RATE;\n" +
                "        return tax;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "\n" +
                "    protected double getBaseAmount() {\n" +
                "        double base = this.units * this.rate * 0.5;\n" +
                "        return base;\n" +
                "    }\n" +
                "\n" +
                "    protected double getTaxAmount() {\n" +
                "        double tax = getBaseAmount() * Site.TAX_RATE * 0.2;\n" +
                "        return tax;\n" +
                "    }\n" +
                "}";
        PsiFile file= TestUtils.getPsiFileFromString(codeBlock);
        FormTemplateMethodAction action=new FormTemplateMethodAction();
        if(action.isRefactorable(file))
            action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), result, file.getProject()));
    }

    public void testMethodWithSeparateDeclaration() {
        String codeBlock = "class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base;\n" +
                "        double tax;\n" +
                "        base = this.units * this.rate;\n" +
                "        tax = base * Site.TAX_RATE;\n" +
                "        return base + tax;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base;\n" +
                "        double tax;\n" +
                "        base = this.units * this.rate * 0.5;\n" +
                "        tax = base * Site.TAX_RATE * 0.2;\n" +
                "        return base + tax;\n" +
                "    }\n" +
                "}";
        String result = "abstract class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "\n" +
                "    protected abstract double getBaseAmount();\n" +
                "\n" +
                "    protected abstract double getTaxAmount();\n" +
                "\n" +
                "    double getBillableAmount() {\n" +
                "        return getBaseAmount() + getTaxAmount();\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "\n" +
                "    protected double getBaseAmount() {\n" +
                "        double base;\n" +
                "        base = this.units * this.rate;\n" +
                "        return base;\n" +
                "    }\n" +
                "\n" +
                "    protected double getTaxAmount() {\n" +
                "        double tax;\n" +
                "        tax = getBaseAmount() * Site.TAX_RATE;\n" +
                "        return tax;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "\n" +
                "    protected double getBaseAmount() {\n" +
                "        double base; \n" +
                "        base = this.units * this.rate * 0.5;\n" +
                "        return base;\n" +
                "    }\n" +
                "\n" +
                "    protected double getTaxAmount() {\n" +
                "        double tax;\n" +
                "        tax = getBaseAmount() * Site.TAX_RATE * 0.2;\n" +
                "        return tax;\n" +
                "    }\n" +
                "}";
        PsiFile file= TestUtils.getPsiFileFromString(codeBlock);
        FormTemplateMethodAction action=new FormTemplateMethodAction();
        if(action.isRefactorable(file))
            action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), result, file.getProject()));
    }

    public void testMethodWithSeparateDeclarationWithSingleAssignment() {
        String codeBlock = "class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base;\n" +
                "        double tax;\n" +
                "        base = this.units * this.rate;\n" +
                "        tax = base;\n" +
                "        return base + tax;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base;\n" +
                "        double tax;\n" +
                "        base = this.units * this.rate * 0.5;\n" +
                "        tax = base * Site.TAX_RATE * 0.2;\n" +
                "        return base + tax;\n" +
                "    }\n" +
                "}";
        String result = "abstract class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "\n" +
                "    protected abstract double getBaseAmount();\n" +
                "\n" +
                "    protected abstract double getTaxAmount();\n" +
                "\n" +
                "    double getBillableAmount() {\n" +
                "        return getBaseAmount() + getTaxAmount();\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "\n" +
                "    protected double getBaseAmount() {\n" +
                "        double base;\n" +
                "        base = this.units * this.rate;\n" +
                "        return base;\n" +
                "    }\n" +
                "\n" +
                "    protected double getTaxAmount() {\n" +
                "        double tax;\n" +
                "        tax = getBaseAmount();\n" +
                "        return tax;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "\n" +
                "    protected double getBaseAmount() {\n" +
                "        double base; \n" +
                "        base = this.units * this.rate * 0.5;\n" +
                "        return base;\n" +
                "    }\n" +
                "\n" +
                "    protected double getTaxAmount() {\n" +
                "        double tax;\n" +
                "        tax = getBaseAmount() * Site.TAX_RATE * 0.2;\n" +
                "        return tax;\n" +
                "    }\n" +
                "}";
        PsiFile file= TestUtils.getPsiFileFromString(codeBlock);
        FormTemplateMethodAction action=new FormTemplateMethodAction();
        if(action.isRefactorable(file))
            action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), result, file.getProject()));
    }

    public void testMethodWithField() {
        String codeBlock = "class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base;\n" +
                "        double tax;\n" +
                "        base = this.units * this.rate;\n" +
                "        tax = base * Site.TAX_RATE;\n" +
                "        return base + tax + units;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base;\n" +
                "        double tax;\n" +
                "        base = this.units * this.rate * 0.5;\n" +
                "        tax = base * Site.TAX_RATE * 0.2;\n" +
                "        return base + tax + units;\n" +
                "    }\n" +
                "}";
        String result = "abstract class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "\n" +
                "    protected abstract double getBaseAmount();\n" +
                "\n" +
                "    protected abstract double getTaxAmount();\n" +
                "\n" +
                "    protected abstract double getUnitsAmount();\n" +
                "\n" +
                "    double getBillableAmount() {\n" +
                "        return getBaseAmount() + getTaxAmount() + getUnitsAmount();\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "\n" +
                "    protected double getBaseAmount() {\n" +
                "        double base;\n" +
                "        base = this.units * this.rate;\n" +
                "        return base;\n" +
                "    }\n" +
                "\n" +
                "    protected double getTaxAmount() {\n" +
                "        double tax;\n" +
                "        tax = getBaseAmount() * Site.TAX_RATE;\n" +
                "        return tax;\n" +
                "    }\n" +
                "\n" +
                "    protected double getUnitsAmount() {\n" +
                "        return units;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "\n" +
                "    protected double getBaseAmount() {\n" +
                "        double base;\n" +
                "        base = this.units * this.rate * 0.5;\n" +
                "        return base;\n" +
                "    }\n" +
                "\n" +
                "    protected double getTaxAmount() {\n" +
                "        double tax;\n" +
                "        tax = getBaseAmount() * Site.TAX_RATE * 0.2;\n" +
                "        return tax;\n" +
                "    }\n" +
                "\n" +
                "    protected double getUnitsAmount() {\n" +
                "        return units;\n" +
                "    }\n" +
                "}";
        PsiFile file= TestUtils.getPsiFileFromString(codeBlock);
        FormTemplateMethodAction action=new FormTemplateMethodAction();
        if(action.isRefactorable(file))
            action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), result, file.getProject()));
    }

    public void testRunRefactoringWithConstant() {
        String codeBlock = "class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base = this.units * this.rate;\n" +
                "        double tax = base * Site.TAX_RATE;\n" +
                "        return base + tax + 3.0;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "    double getBillableAmount() {\n" +
                "        double base = this.units * this.rate * 0.5;\n" +
                "        double tax = base * Site.TAX_RATE * 0.2;\n" +
                "        return base + tax + 3.0;\n" +
                "    }\n" +
                "}";
        String result = "abstract class Site {\n" +
                "    static double TAX_RATE = 30.0;\n" +
                "\n" +
                "    protected abstract double getBaseAmount();\n" +
                "\n" +
                "    protected abstract double getTaxAmount();\n" +
                "\n" +
                "    double getBillableAmount() {\n" +
                "        return getBaseAmount() + getTaxAmount() + 3.0;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class ResidentialSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "\n" +
                "    protected double getBaseAmount() {\n" +
                "        double base = this.units * this.rate;\n" +
                "        return base;\n" +
                "    }\n" +
                "\n" +
                "    protected double getTaxAmount() {\n" +
                "        double tax = getBaseAmount() * Site.TAX_RATE;\n" +
                "        return tax;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class LifeLineSite extends Site {\n" +
                "    private double units;\n" +
                "    private double rate;\n" +
                "\n" +
                "    protected double getBaseAmount() {\n" +
                "        double base = this.units * this.rate * 0.5;\n" +
                "        return base;\n" +
                "    }\n" +
                "\n" +
                "    protected double getTaxAmount() {\n" +
                "        double tax = getBaseAmount() * Site.TAX_RATE * 0.2;\n" +
                "        return tax;\n" +
                "    }\n" +
                "}";
        PsiFile file= TestUtils.getPsiFileFromString(codeBlock);
        FormTemplateMethodAction action=new FormTemplateMethodAction();
        if(action.isRefactorable(file))
            action.runRefactoring(file);
        assertTrue(TreeUtils.codeEqual(file.getText(), result, file.getProject()));
    }
}
