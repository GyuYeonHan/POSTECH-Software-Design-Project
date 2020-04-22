package team1.plugin.utils;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextUtils {
    /**
     * Returns the PSI Methods in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiMethod> if element has Methods, empty() otherwise
     */
    public static List<PsiMethod> findMethods(PsiElement element) {
        ArrayList<PsiMethod> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                ret.add(method);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI Elements in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiElement> if element has Elements, empty() otherwise
     */
    public static List<PsiElement> findElements(PsiElement element) {
        ArrayList<PsiElement> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                super.visitElement(element);
                ret.add(element);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI AssignmentExpressions in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiAssignmentExpression> if element has AssignmentExpressions, empty() otherwise
     */
    public static List<PsiAssignmentExpression> findAssignmentExpressions(PsiElement element) {
        ArrayList<PsiAssignmentExpression> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                super.visitAssignmentExpression(expression);
                ret.add(expression);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI LocalVariables in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiLocalVariable> if element has LocalVariables, empty() otherwise
     */
    public static List<PsiLocalVariable> findLocalVariables(PsiElement element) {
        ArrayList<PsiLocalVariable> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                super.visitLocalVariable(variable);
                ret.add(variable);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI Classes in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiClass> if element has Classes, empty() otherwise
     */
    public static List<PsiClass> findClasses(PsiElement element) {
        ArrayList<PsiClass> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitClass(PsiClass aClass) {
                super.visitClass(aClass);
                ret.add(aClass);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI Variables in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiVariable> if element has Variables, empty() otherwise
     */
    public static List<PsiVariable> findVariables(PsiElement element) {
        ArrayList<PsiVariable> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitVariable(PsiVariable variable) {
                super.visitVariable(variable);
                ret.add(variable);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI Expressions in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiExpression> if element has Expressions, empty() otherwise
     */
    public static List<PsiExpression> findExpressions(PsiElement element) {
        ArrayList<PsiExpression> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitExpression(PsiExpression expression) {
                super.visitExpression(expression);
                ret.add(expression);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI ReferenceExpressions in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiReferenceExpression> if element has ReferenceExpressions, empty() otherwise
     */
    public static List<PsiReferenceExpression> findReferenceExpressions(PsiElement element) {
        ArrayList<PsiReferenceExpression> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                super.visitReferenceExpression(expression);
                ret.add(expression);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI SuperExpressions in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiSuperExpression> if element has SuperExpressions, empty() otherwise
     */
    public static List<PsiSuperExpression> findSuperExpression(PsiElement element) {
        ArrayList<PsiSuperExpression> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitSuperExpression(PsiSuperExpression expression) {
                super.visitSuperExpression(expression);
                ret.add(expression);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI JavaCodeReferenceElements in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiJavaCodeReferenceElement> if element has JavaCodeReferenceElements, empty() otherwise
     */
    public static List<PsiJavaCodeReferenceElement> findReferenceElements(PsiElement element) {
        ArrayList<PsiJavaCodeReferenceElement> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceElement(PsiJavaCodeReferenceElement expression) {
                super.visitReferenceElement(expression);
                ret.add(expression);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI MethodCallExpressions in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiMethodCallExpression> if element has MethodCallExpressions, empty() otherwise
     */
    public static List<PsiMethodCallExpression> findMethodCallExpressions(PsiElement element) {
        ArrayList<PsiMethodCallExpression> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                ret.add(expression);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI Statements in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiStatement> if element has Statements, empty() otherwise
     */
    public static List<PsiStatement> findStatements(PsiElement element) {
        ArrayList<PsiStatement> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitStatement(PsiStatement statement) {
                super.visitStatement(statement);
                ret.add(statement);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI Identifiers in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiIdentifier> if element has Identifiers, empty() otherwise
     */
    public static List<PsiIdentifier> findIdentifiers(PsiElement element) {
        ArrayList<PsiIdentifier> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitIdentifier(PsiIdentifier identifier) {
                super.visitIdentifier(identifier);
                ret.add(identifier);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI IfStatements in current PSI File.
     *
     * @param file the PSI File.
     * @return List<PsiIfStatement> if element has IfStatements, empty() otherwise
     */
    public static List<PsiIfStatement> findIfStatements(PsiFile file) {
        ArrayList<PsiIfStatement> ret = new ArrayList<>();
        file.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitIfStatement(PsiIfStatement statement) {
                super.visitIfStatement(statement);
                ret.add(statement);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI ReturnStatements in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiReturnStatement> if element has ReturnStatements, empty() otherwise
     */
    public static List<PsiReturnStatement> findReturnStatements(PsiElement element) {
        ArrayList<PsiReturnStatement> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitReturnStatement(PsiReturnStatement element) {
                super.visitReturnStatement(element);
                ret.add(element);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI DeclarationStatements in current PSI Element
     *
     * @param element the PSI Element.
     * @return List<PsiDeclarationStatement> if element has DeclarationStatements, empty() otherwise
     */
    public static List<PsiDeclarationStatement> findDeclarationStatements(PsiElement element) {
        ArrayList<PsiDeclarationStatement> ret = new ArrayList<>();
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitDeclarationStatement(PsiDeclarationStatement element) {
                super.visitDeclarationStatement(element);
                ret.add(element);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI SwitchStatements in current PSI File
     *
     * @param file the PSI File.
     * @return List<PsiSwitchStatement> if element has SwitchStatements, empty() otherwise
     */
    public static List<PsiSwitchStatement> findSwitchStatements(PsiFile file) {
        ArrayList<PsiSwitchStatement> ret = new ArrayList<>();
        file.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitSwitchStatement(PsiSwitchStatement statement) {
                super.visitSwitchStatement(statement);
                ret.add(statement);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI TryStatements in current PSI file
     *
     * @param file the PSI File.
     * @return ArrayList<PsiTryStatement> if element has TryStatement, empty() otherwise
     */
    public static ArrayList<PsiTryStatement> findTryStatements(PsiFile file) {
        ArrayList<PsiTryStatement> ret = new ArrayList<>();
        file.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitTryStatement(PsiTryStatement statement) {
                super.visitTryStatement(statement);
                ret.add(statement);
            }
        });
        return ret;
    }

    /**
     * Returns the PSI SubClasses in current PSI Class
     *
     * @param root       the PSI Element.
     * @param superClass the PSI Class.
     * @return List<PsiClass> if element has class, empty() otherwise
     */
    public static List<PsiClass> findSubClasses(PsiElement root, PsiClass superClass) {
        ArrayList<PsiClass> ret = new ArrayList<>();
        root.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitClass(PsiClass aClass) {
                super.visitClass(aClass);

                if (aClass.getSuperClass() == superClass) {
                    ret.add(aClass);
                }
            }
        });
        return ret;
    }

    /**
     * Returns the PSI OverridingMethods in current PSI Class
     *
     * @param aClass       the PSI Class.
     * @return List<PsiMethod> if element has method, empty() otherwise
     */
    public static List<PsiMethod> findOverridingMethods(PsiClass aClass) {
        List<PsiMethod> superMethods = Arrays.asList(aClass.getSuperClass().getMethods());
        return Arrays.stream(aClass.getMethods())
                .filter(method -> Arrays.stream(method.findSuperMethods()).anyMatch(superMethods::contains))
                .collect(Collectors.toList());
    }

    /**
     * Determining whether two codes are same when the white spaces are removed
     *
     * @param code1 String from code
     * @param code2 String from code
     * @return {true} if code is equal, {false} otherwise
     */
    public static boolean codeEqual(String code1, String code2) {
        String[] code1_parse = code1.split("\n");
        String[] code2_parse = code2.split("\n");
        List<String> code1_remove_empty = new ArrayList<>();
        List<String> code2_remove_empty = new ArrayList<>();
        for (String s : code1_parse) {
            if (!s.isEmpty()) {
                code1_remove_empty.add(s.replaceAll("\\s", ""));
            }
        }
        for (String s : code2_parse) {
            if (!s.isEmpty()) {
                code2_remove_empty.add(s.replaceAll("\\s", ""));
            }
        }
        if (code1_remove_empty.size() != code2_remove_empty.size()) {
            return false;
        }
        for (int i = 0; i < code1_remove_empty.size(); i++) {
            if (!code1_remove_empty.get(i).equals(code2_remove_empty.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the PSI White Space.
     *
     * @param project IntelliJ project object
     * @return the first child, or null if the element has no children.
     * @throws IncorrectOperationException if the file type with specified extension is binary.
     */
    public static PsiWhiteSpace newline(Project project) throws IncorrectOperationException {
        PsiFile fileFromText = PsiFileFactory.getInstance(project).createFileFromText("newline.txt", StdFileTypes.JAVA.getLanguage(), "\n");
        return (PsiWhiteSpace) fileFromText.getFirstChild();
    }


}