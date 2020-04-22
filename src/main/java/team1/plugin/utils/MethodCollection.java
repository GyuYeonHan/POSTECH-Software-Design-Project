package team1.plugin.utils;

import com.intellij.psi.*;
import com.intellij.psi.impl.PsiConstantEvaluationHelperImpl;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MethodCollection {
    private PsiClass parent;
    private String methodName;
    private Set<PsiMethod> methods;

    /**
     * @param parent parent PSI Class
     * @param methodName target method name
     */
    public MethodCollection(@NotNull PsiClass parent, @NotNull String methodName) {
        this.parent = parent;
        this.methodName = methodName;
        methods = new HashSet<>();
    }

    /**
     * Return parent class
     *
     * @return PsiClass
     */
    public PsiClass getParent() {
        return parent;
    }

    /**
     * Returns methods
     *
     * @return Set<PsiMethod>
     */
    public Set<PsiMethod> getMethods() {
        return Collections.unmodifiableSet(methods);
    }

    /**
     * Returns method name
     *
     * @return method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Check method collection possible to add pis method
     *
     * @param method target Psi method
     * @return {true} if is possible to add, {false} otherwise
     */
    public boolean isAddable(PsiMethod method) {
        if (method.getContainingClass() == null) {
            return false;
        }
        if (method.getContainingClass().getSuperClass() == null) {
            return false;
        }
        return method.getName().equals(methodName)
                && method.getContainingClass().getSuperClass().equals(parent);
    }

    /**
     * Add method in method collection
     *
     * @param method target method
     */
    public void addMethod(PsiMethod method) {
        methods.add(method);
    }

    /**
     * Check method collection contains all ReturnConstant
     *
     * @return {true} if has All ReturnConstant, {false} otherwise
     */
    public boolean isAllReturnConstant() {
        final boolean[] isConstant = {true};

        for (PsiMethod psiMethod : methods) {
            PsiCodeBlock body = psiMethod.getBody();
            if (body == null) {
                return false;
            }

            psiMethod.accept(new JavaRecursiveElementWalkingVisitor() {
                @Override
                public void visitReturnStatement(PsiReturnStatement statement) {
                    super.visitReturnStatement(statement);
                    PsiConstantEvaluationHelper c = new PsiConstantEvaluationHelperImpl();
                    if (c.computeConstantExpression(statement.getReturnValue()) == null)
                        isConstant[0] = false;
                }
            });
        }

        return isConstant[0];
    }

    /**
     * Check method collection contains all same type
     *
     * @return {true} if has all same type, {false} otherwise
     */
    public boolean isAllSameType() {
        PsiType type = null;
        for (PsiMethod method : methods) {
            if (type == null) {
                type = method.getReturnType();
            } else if (!type.equals(method.getReturnType())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns method collection's return type
     *
     * @return PsiType
     */
    public PsiType getReturnType() {
        return methods.iterator().next().getReturnType();
    }

    /**
     * Check method collection contains target field name
     *
     * @param fieldName target field name
     * @return {true} if has Field, {false} otherwise
     */
    public boolean hasField(String fieldName) {
        for (PsiField f : parent.getAllFields()) {
            if (Objects.equals(f.getName(), fieldName))
                return true;
        }

        return false;
    }

    /**
     * Check method collection contains ReturnStatement
     *
     * @return {true} if has ReturnStatement, {false} otherwise
     */
    public boolean hasSameReturnStatement() {
        final PsiStatement[] returnStatement = {null};
        final boolean[] hasSameReturnStatement = {true};

        for (PsiMethod method : getMethods()) {
            method.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReturnStatement(PsiReturnStatement statement) {
                    super.visitReturnStatement(statement);
                    if (returnStatement[0] == null) {
                        returnStatement[0] = statement;
                    } else {
                        if (!TextUtils.codeEqual(statement.getText(), returnStatement[0].getText())) {
                            hasSameReturnStatement[0] = false;
                        }
                    }
                }
            });
        }
        return hasSameReturnStatement[0];
    }


    /**
     * Check method collection contains similar algorithm
     *
     * @return {true} if has similar Algorithm, {false} otherwise
     */
    public boolean hasSimilarAlgorithm() {
        for (PsiMethod method : methods) {
            for (PsiStatement psiStatement : extractDifferentStatement((PsiClass) method.getParent())) {
                if (!(psiStatement instanceof PsiDeclarationStatement)) {
                    final boolean[] doesAssignmentExpressionExist = {false};
                    psiStatement.accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                            super.visitAssignmentExpression(expression);
                            doesAssignmentExpressionExist[0] = true;
                        }
                    });
                    if (!doesAssignmentExpressionExist[0]) {
                        return false;
                    }
                }
            }
        }
        return hasSameReturnStatement();
    }

    /**
     * Returns different statements
     *
     * @param psiClass target PSI class
     * @return List<PsiStatement>
     */
    public List<PsiStatement> extractDifferentStatement(PsiClass psiClass) {
        List<PsiStatement> sameStatement = extractSameStatement();

        List<PsiStatement> differentStatements = new ArrayList<>();
        for (PsiMethod method : methods) {
            if (method.getParent().equals(psiClass)) {
                method.accept(new JavaRecursiveElementVisitor() {
                    @Override
                    public void visitStatement(PsiStatement statement) {
                        super.visitStatement(statement);
                        if (!containStatementOfSameText(sameStatement, statement)) {
                            differentStatements.add(statement);
                        }
                    }
                });
                break;
            }
        }
        return differentStatements;
    }

    /**
     * check StatementList contains target statement
     *
     * @param statementList list of Psi statement
     * @param statement Psi Statement for check
     * @return {true} if has same statement, {false} otherwise
     */
    private boolean containStatementOfSameText(List<PsiStatement> statementList, PsiStatement statement) {
        return statementList.stream().anyMatch(psiStatement -> TextUtils.codeEqual(psiStatement.getText(), statement.getText()));
    }

    /**
     * Retruns same statement
     *
     * @return List<PsiStatement>
     */
    public List<PsiStatement> extractSameStatement() {

        List<PsiStatement> statementList = null;

        for (PsiMethod method : getMethods()) {
            if (statementList == null) {
                statementList = new ArrayList<>();
                statementList.addAll(Arrays.asList(method.getBody().getStatements()));
            } else {
                List<PsiStatement> newStatementList = new ArrayList<>();
                for (PsiStatement oldStatement : statementList) {
                    method.accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitStatement(PsiStatement statement) {
                            super.visitStatement(statement);
                            if (statement instanceof PsiDeclarationStatement) {
                                return;
                            }
                            if (TextUtils.codeEqual(oldStatement.getText(), statement.getText())) {
                                newStatementList.add(oldStatement);
                            }
                        }
                    });
                }
                statementList = newStatementList;
            }
        }
        return statementList;
    }

    /**
     * Returns Set<MethodCollection> from file
     *
     * @param file Psi file
     * @return Set<MethodCollection>
     */
    public static Set<MethodCollection> getMethodCollectionSetFromFile(PsiFile file) {
        Set<MethodCollection> methodCollections = new HashSet<>();
        setupCollection(methodCollections, file);
        return methodCollections;
    }

    /**
     * Setup collection for method
     *
     * @param methodCollectionSet set of method collection
     * @param file psi file
     */
    public static void setupCollection(Set<MethodCollection> methodCollectionSet, PsiFile file) {
        file.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(PsiMethod psiMethod) {
                super.visitMethod(psiMethod);
                boolean isMethodCollectionExist = false;

                PsiClass ownerClass = (PsiClass) psiMethod.getParent();
                PsiClass ownerSuperClass = ownerClass.getSuperClass();
                if (ownerSuperClass != null) {
                    for (MethodCollection methodCollection : methodCollectionSet) {
                        if (methodCollection.isAddable(psiMethod)) {
                            isMethodCollectionExist = true;
                            methodCollection.addMethod(psiMethod);
                        }
                    }

                    if (!isMethodCollectionExist) {
                        if (psiMethod.getReturnType() != null) {
                            MethodCollection methodCollection = new MethodCollection(ownerSuperClass, psiMethod.getName());
                            methodCollection.addMethod(psiMethod);
                            methodCollectionSet.add(methodCollection);
                        }
                    }
                }
            }
        });
    }

}
