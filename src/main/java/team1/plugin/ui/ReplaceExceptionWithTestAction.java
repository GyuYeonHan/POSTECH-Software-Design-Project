package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiParameterImpl;
import com.intellij.psi.impl.source.tree.java.*;
import org.jetbrains.annotations.NotNull;
import team1.plugin.utils.EditorUtils;
import team1.plugin.utils.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReplaceExceptionWithTestAction extends CommonAction {
    ArrayList<PsiTryStatement> tryStatements;
    ArrayList<String> availableExceptions = new ArrayList<>(Arrays.asList(
            ArrayIndexOutOfBoundsException.class.getSimpleName()));

    private PsiParameter nullObject;
    private PsiCodeBlock catchBlockSave;
    private PsiCodeBlock tryBlockSave;
    private PsiTryStatement tryStatementSave;
    private boolean nullObjectProblem;

    /**
     * @param e AnActionEvent from actionPerformed
     * @return true if refactoring action is available
     */
    @Override
    protected boolean isRefactorable(AnActionEvent e) {
        PsiFile file = EditorUtils.getPsiFile(e);
        return _isRefactorable(file);
    }

    /**
     * @param project the project in which the availability is checked.
     * @param editor  the editor in which the intention will be invoked.
     * @param file    the file open in the editor.
     * @return true if refactoring action is available
     */
    @Override
    protected boolean isRefactorable(@NotNull Project project, Editor editor, PsiFile file) {
        return _isRefactorable(file);
    }

    /**
     * For the test, check whether file is refactorable.
     *
     * @param file
     * @return true if refactoring action is available
     */
    public boolean _isRefactorable(PsiFile file) {
        nullObjectProblem = false;
        tryStatements = TextUtils.findTryStatements(file);
        if (_isRefactorableNullObject(file)) {
            nullObjectProblem = true;
        }
        for (int i = 0; i < tryStatements.size(); i++) {
            if (tryStatements.get(i).getTryBlock().getStatements().length != 1
                    || tryStatements.get(i).getCatchSections().length != 1
                    || !availableExceptions.contains(tryStatements.get(i).getCatchSections()[0].getCatchType().getPresentableText())) {
                tryStatements.remove(i);
                i--;
                continue;
            }
            String exceptionParameter = tryStatements.get(i).getCatchBlockParameters()[0].getName();
            List<PsiExpression> expressions = TextUtils.findExpressions(
                    tryStatements.get(i).getCatchSections()[0].getCatchBlock());
            if (expressions.stream().map(item -> item.getText()).collect(Collectors.toList()).contains(exceptionParameter)) {
                tryStatements.remove(i);
                i--;
                continue;
            }
        }
        return (tryStatements.size() > 0 || nullObjectProblem) ? true : false;
    }

    /**
     * To check it is refacorable due to nullObject problem.
     * @param file
     * @return true if refactoring action is available
     */
    public boolean _isRefactorableNullObject(PsiFile file) {
        PsiElement[] fileElem = file.getChildren();
        for (PsiElement fileIter : fileElem) {
            if (fileIter.getClass() == PsiClassImpl.class) {
                PsiClassImpl fileClass = (PsiClassImpl) fileIter;
                if(fileClass.getAllMethods().length == 0){
                    return false;
                }
                for (PsiMethod classMethod : fileClass.getAllMethods()) {
                    for (PsiStatement methodStatement : classMethod.getBody().getStatements()) {
                        if (methodStatement.getClass() == PsiTryStatementImpl.class) {
                            PsiTryStatementImpl tryStatement = (PsiTryStatementImpl) methodStatement;
                            PsiCodeBlockImpl tryBlock = (PsiCodeBlockImpl) tryStatement.getTryBlock();
                            if (tryBlock.getStatements().length != 1) {
                                return false;
                            }
                            PsiCodeBlock[] catchBlockList = tryStatement.getCatchBlocks();
                            PsiParameter[] paramBlockList = tryStatement.getCatchBlockParameters();
                            PsiParameterImpl paramBlock = (PsiParameterImpl) paramBlockList[0];
                            if (paramBlock.getType().toString().equals("PsiType:NullPointerException")) {
                                PsiStatement[] tryBlockStatements = tryBlock.getStatements();
                                for (PsiStatement tryBlockStatement : tryBlockStatements) {
                                    PsiElement[] expressions = tryBlockStatement.getChildren();
                                    boolean containLambda = false;
                                    for (PsiElement expression : expressions) {
                                        if (expression.getClass() == PsiLambdaExpressionImpl.class) {
                                            if (containLambda) {
                                                return false;
                                            }
                                            PsiLambdaExpressionImpl exp = (PsiLambdaExpressionImpl) expression;
                                            PsiParameterList expParamList = exp.getParameterList();
                                            PsiParameter[] expParams = expParamList.getParameters();
                                            nullObject = expParams[0];
                                            catchBlockSave = catchBlockList[0];
                                            tryBlockSave = tryBlock;
                                            tryStatementSave = tryStatement;
                                            containLambda = true;
                                            return true;
                                        }
                                    }
                                }
                            } else {
                                return false;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * runRefactoring adaptor for Tab
     * @param e AnActionEvent from actionPerformed
     */
    @Override
    protected void runRefactoring(AnActionEvent e) {
        PsiFile file = EditorUtils.getPsiFile(e);
        _runRefactoring(file);
    }

    /**
     * runRefactoring adaptor for IntentionAction
     *
     * @param project the project in which the intention is invoked.
     * @param editor  the editor in which the intention is invoked.
     * @param file    the file open in the editor.
     */
    @Override
    protected void runRefactoring(@NotNull Project project, Editor editor, PsiFile file) {
        _runRefactoring(file);
    }

    @Override
    public String getRefactorName() {
        return "Replace Exception With Test";
    }


    /**
     * Main implementation of runRefactoring
     * @param file
     */
    public void _runRefactoring(PsiFile file) {
        if (nullObjectProblem) {
            runRefactoringForNullPointerException(file);
        }
        int i;
        for (i = 0; i < tryStatements.size(); i++) {
            if (tryStatements.get(i).getCatchBlockParameters()[0].getType().getPresentableText().equals(ArrayIndexOutOfBoundsException.class.getSimpleName())) {
                runRefactoringForArrayIndexOutOfBoundsException(tryStatements.get(i), file);
            }
        }
    }

    /**
     * Run refactoring for ArrayIndexOutOfBoundsException
     *
     * @param statement
     * @param file
     */
    public void runRefactoringForArrayIndexOutOfBoundsException(PsiTryStatement statement, PsiFile file) {
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(file.getProject());
        PsiStatement statementInTry = statement.getTryBlock().getStatements()[0];
        PsiStatement[] statementsInCatch = statement.getCatchBlocks()[0].getStatements();

        List<PsiExpression> expressionsInTry = TextUtils.findExpressions(statementInTry);
        PsiArrayAccessExpression arrayAccessExpression = null;
        for (PsiExpression expression : expressionsInTry) {
            if (expression.getClass() == PsiArrayAccessExpressionImpl.class) {
                if (arrayAccessExpression != null) { // Consider only one array access
                    return;
                }
                arrayAccessExpression = (PsiArrayAccessExpression) expression;
                break;
            }
        }
        if (arrayAccessExpression == null) {
            return;
        }

        String ifBody = arrayAccessExpression.getIndexExpression().getText()
                + " >= " + arrayAccessExpression.getArrayExpression().getText() + ".length";

        PsiCodeBlock codeBlock = factory.createCodeBlock();
        for (PsiStatement st : statementsInCatch) {
            codeBlock.add(st);
        }

        String s = "if (" + ifBody + ") " + codeBlock.getText();
        PsiStatement newStatement = factory.createStatementFromText(s, null);

        WriteCommandAction.runWriteCommandAction(file.getProject(), () -> {
            statement.getParent().addBefore(newStatement, statement);
            statement.getParent().addBefore(statementInTry, statement);
            statement.delete();
        });
    }

    /**
     * Run refactoring for NullPointerException
     *
     * @param file
     */

    public void runRefactoringForNullPointerException(PsiFile file) {
        String ifState = "if (" + nullObject.getText() + " == null)";
        String ifBlock = catchBlockSave.getText();
        String elseBlock = tryBlockSave.getText();

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(file.getProject());

        PsiStatement ifStatePsi = factory.createStatementFromText(ifState, null);
        PsiCodeBlock ifBlockPsi = factory.createCodeBlockFromText(ifBlock, null);
        PsiCodeBlock elseBlockPsi = factory.createCodeBlockFromText(elseBlock, null);
        PsiKeyword pls = factory.createKeyword("else");
        PsiWhiteSpace newLinePsi = TextUtils.newline(file.getProject());

        ifStatePsi.add(ifBlockPsi);
        ifStatePsi.addAfter(newLinePsi, ifBlockPsi);
        ifStatePsi.addAfter(pls, ifBlockPsi);
        ifStatePsi.addAfter(elseBlockPsi, ifBlockPsi);

        WriteCommandAction.runWriteCommandAction(file.getProject(), () -> {
            tryStatementSave.replace(ifStatePsi);
        });

        EditorUtils.autoIndentation(file);

    }
}