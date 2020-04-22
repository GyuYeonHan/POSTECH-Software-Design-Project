package team1.plugin.ui;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.testFramework.LightPlatformTestCase;
import team1.plugin.utils.EditorUtils;
import team1.plugin.utils.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class RemoveAssignmentsToParametersTest extends LightPlatformTestCase {
    public PsiFile getPsiFileFromString(String codeBlock)
    {
        Project mockProject = getProject();
        PsiFileFactory fac = PsiFileFactory.getInstance(mockProject);
        PsiFile file = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), codeBlock);

        return file;
    }

    public void testEmptyCode(){
        String codeBlock = "";
        PsiFile file = getPsiFileFromString(codeBlock);
        PsiElement element = null;

        RemoveAssignmentsToParametersAction action = new RemoveAssignmentsToParametersAction();

        assertFalse(action._isRefactorable(file, null));
    }

//    This function is moved to GUI part
//    public void testWithNoMethod(){
//        String codeBlock = "class myClass{ int a;}";
//        PsiFile file = getPsiFileFromString(codeBlock);
//        PsiElement element = TextUtils.findElements(file).get(0);
//
//        RemoveAssignmentsToParametersAction action = new RemoveAssignmentsToParametersAction();
//        assertFalse(action._isRefactorable(file, element));
//    }

    public void testMethodWithNoParameter(){
        String codeBlock = "class myClass{ int myMethod() { int a; a = 8; return a; }}";
        PsiFile file = getPsiFileFromString(codeBlock);
        List<PsiMethod> list = TextUtils.findMethods(file);

        RemoveAssignmentsToParametersAction action = new RemoveAssignmentsToParametersAction();
        assertFalse(action._isRefactorable(file, list.get(0)));
    }

    public void testNotFocused() {
        String codeBlock = "class myClass{ int myMethod() { int a; a = 8; return a; }}";
        PsiFile file = getPsiFileFromString(codeBlock);

        String elementBlock = "class myClass{ int k;}";
        PsiFile elementFile = getPsiFileFromString(elementBlock);
        List<PsiElement> list = TextUtils.findElements(elementFile);

        RemoveAssignmentsToParametersAction action = new RemoveAssignmentsToParametersAction();
        assertFalse(action._isRefactorable(elementFile, null));
    }

    public void testWithNoAssignment(){
        String codeBlock = "class myClass{ int myMethod(int param) { int a; a = 8; return a; }}";
        PsiFile file = getPsiFileFromString(codeBlock);
        List<PsiMethod> list = TextUtils.findMethods(file);
        PsiMethod method= list.get(0);

        RemoveAssignmentsToParametersAction action = new RemoveAssignmentsToParametersAction();
        assertFalse(action._isRefactorable(file, method));
    }

    public void testRefactorable() {
        String codeBlock = "class myClass{ int myMethod(int param) { param = 8; return param; }}";
        PsiFile file = getPsiFileFromString(codeBlock);
        List<PsiMethod> list = TextUtils.findMethods(file);
        PsiMethod method= list.get(0);

        RemoveAssignmentsToParametersAction action = new RemoveAssignmentsToParametersAction();
        assertTrue(action._isRefactorable(file, method));
    }

    public void testSingleParameterRefactoring() {
        String codeBlock = "class myClass{ int myMethod(int param) { param = 8; return param; }}";
        PsiFile file = getPsiFileFromString(codeBlock);
        List<PsiAssignmentExpression> expressions = TextUtils.findAssignmentExpressions(file);
        RemoveAssignmentsToParametersAction action = new RemoveAssignmentsToParametersAction();

        PsiMethod focusedMethod = EditorUtils.getFocusedMethod(file, expressions.get(0));
        List<PsiIdentifier> originalIdentifiers = TextUtils.findIdentifiers(focusedMethod.getBody());
        Map<PsiParameter, List<PsiAssignmentExpression>> assignedParameters = action.getParameterAssignments(focusedMethod);

        List<PsiAssignmentExpression> assignmentExpressions = assignedParameters.values().stream()
                .flatMap(list -> list.stream()).collect(Collectors.toList());
        Map<PsiAssignmentExpression, String> refactoringData = new HashMap<>();

        for (PsiAssignmentExpression assignmentExpression : assignmentExpressions) {
            refactoringData.put(assignmentExpression, assignmentExpression.getLExpression().getLastChild().getText() + "_refactored");
        }

        action._runRefactoring(assignedParameters, refactoringData);

        List<PsiIdentifier> refactoredIdentifiers = TextUtils.findIdentifiers(focusedMethod.getBody());

        assertEquals(originalIdentifiers.stream().map(identifier -> identifier.getText() + "_refactored").collect(Collectors.toList()),
                refactoredIdentifiers.stream().map(identifier -> identifier.getText()).collect(Collectors.toList()));
    }

    public void testMultipleParameterRefactoring() {
        String codeBlock = "class myClass{ int myMethod(int param1, int param2) " +
                "{ param1 = 8; param2 = 9; do(param2); return param1; }}";

        PsiFile file = getPsiFileFromString(codeBlock);
        List<PsiAssignmentExpression> expressions = TextUtils.findAssignmentExpressions(file);
        RemoveAssignmentsToParametersAction action = new RemoveAssignmentsToParametersAction();

        PsiMethod focusedMethod = EditorUtils.getFocusedMethod(file, expressions.get(0));
        List<PsiIdentifier> originalIdentifiers = TextUtils.findIdentifiers(focusedMethod.getBody());
        Map<PsiParameter, List<PsiAssignmentExpression>> assignedParameters = action.getParameterAssignments(focusedMethod);

        List<PsiAssignmentExpression> assignmentExpressions = assignedParameters.values().stream()
                .flatMap(list -> list.stream()).collect(Collectors.toList());
        Map<PsiAssignmentExpression, String> refactoringData = new HashMap<>();

        for (PsiAssignmentExpression assignmentExpression : assignmentExpressions) {
            refactoringData.put(assignmentExpression, assignmentExpression.getLExpression().getLastChild().getText() + "_refactored");
        }

        action._runRefactoring(assignedParameters, refactoringData);

        List<PsiIdentifier> refactoredIdentifiers = TextUtils.findIdentifiers(focusedMethod.getBody());

        assertEquals(originalIdentifiers.stream().map(identifier -> identifier.getText() + "_refactored").collect(Collectors.toList()),
                refactoredIdentifiers.stream().map(identifier -> identifier.getText()).collect(Collectors.toList()));
    }
}