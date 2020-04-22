package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import team1.plugin.utils.EditorUtils;
import team1.plugin.utils.TextUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EncapsulateCollectionAction extends CommonAction {
    /**
     * isRefactorable adaptor for tab.
     *
     * @param e AnActionEvent from actionPerformed
     * @return true if refactoring is available.
     */
    @Override
    protected boolean isRefactorable(AnActionEvent e) {
        PsiFile file = EditorUtils.getPsiFile(e);
        PsiElement psiElem = e.getData(LangDataKeys.PSI_ELEMENT);
        PsiClass focusClass = EditorUtils.getFocusedClass(file, psiElem);
        return _isRefactorable(focusClass);
    }

    /**
     * isRefactorable adaptor for IntentionAction.
     *
     * @param project the project in which the availability is checked. \
     * @param editor  the editor in which the intention will be invoked.
     * @param file    the file open in the editor.
     * @return true if refactoring is available.
     */
    @Override
    protected boolean isRefactorable(@NotNull Project project, Editor editor, PsiFile file) {
        PsiElement focusElement = file.findElementAt(editor.getCaretModel().getOffset());
        return _isRefactorable(EditorUtils.getFocusedClass(file, focusElement));
    }

    /**
     * Main routine of isRefactorable.
     *
     * @param aClass Target class.
     * @return true if refactoring is available.
     */
    public boolean _isRefactorable(PsiClass aClass) {
        if (aClass == null) {
            return false;
        }

        List<PsiField> refactorableFields = getRefactorableFields(aClass);

        boolean hasGetter = hasGetter(aClass, refactorableFields);
        boolean hasSetter = hasSetter(aClass, refactorableFields);
        return hasGetter && hasSetter;
    }

    /**
     * Find refactorable collection fields.
     *
     * @param aClass Target class.
     * @return List of refactorable collection fields.
     */
    @NotNull
    private List<PsiField> getRefactorableFields(PsiClass aClass) {
        List<PsiField> fieldList = Arrays.asList(aClass.getFields());
        List<String> collections = Arrays.asList("List", "Set", "ArrayList", "HashSet", "TreeSet",
                "LinkedList", "SortedSet");

        return fieldList.stream().filter(
                field -> collections.stream().anyMatch(
                        elem -> field.getTypeElement().getText().contains(elem))).collect(Collectors.toList());
    }

    /**
     * Finds getter function for each refactorableFields.
     *
     * @param aClass Target class.
     * @param refactorableFields List of refactorable collection fields.
     * @return true if any of refactorableFields has getter.
     */
    public boolean hasGetter(PsiClass aClass, List<PsiField> refactorableFields) {
        List<PsiReturnStatement> returnStatements = TextUtils.findReturnStatements(aClass);

        return returnStatements.stream().anyMatch(st -> (st.getReturnValue() instanceof PsiReferenceExpression) &&
                refactorableFields.contains(((PsiReferenceExpression) st.getReturnValue()).resolve()));
    }

    /**
     * Finds getter function for each refactorableFields.
     *
     * @param aClass Target class.
     * @param refactorableFields List of refactorable collection fields.
     * @return Map of <Getter method, Corresponding field>.
     */
    public Map<PsiMethod, PsiField> getGetters(PsiClass aClass, List<PsiField> refactorableFields) {
        List<PsiMethod> methods = Arrays.asList(aClass.getMethods());
        Map<PsiMethod, PsiField> result = new HashMap<>();

        for (PsiMethod method : methods) {
            List<PsiReturnStatement> returnStatements = TextUtils.findReturnStatements(method);

            if (returnStatements.isEmpty()) {
                continue;
            }

            PsiReturnStatement returnStatement = returnStatements.get(0);

            PsiField field = refactorableFields.stream()
                    .filter(f -> f.equals(((PsiReferenceExpression) returnStatement.getReturnValue()).resolve()))
                    .collect(Collectors.toList()).get(0);

            result.put(method, field);
        }

        return result;
    }

    /**
     * Finds setter function for each refactorableFields.
     *
     * @param aClass Target class.
     * @param refactorableFields List of refactorable collection fields.
     * @return true if any of refactorableFields has setter.
     */
    public boolean hasSetter(PsiClass aClass, List<PsiField> refactorableFields) {
        return !getSetters(aClass, refactorableFields).isEmpty();
    }

    /**
     * Finds setter function for each refactorableFields.
     *
     * @param aClass Target class.
     * @param refactorableFields List of refactorable collection fields.
     * @return List of setter methods.
     */
    public ArrayList<PsiMethod> getSetters(PsiClass aClass, List<PsiField> refactorableFields) {
        ArrayList<PsiMethod> setters = new ArrayList<>();
        for (PsiMethod method : TextUtils.findMethods(aClass)) {
            if (method.isConstructor()) {
                continue;
            }

            Set<PsiElement> references = new HashSet<>();
            for (PsiAssignmentExpression assignmentExpression : TextUtils.findAssignmentExpressions(method)) {
                for (PsiReferenceExpression reference : TextUtils.findReferenceExpressions(assignmentExpression.getRExpression())) {
                    if (assignmentExpression.getLExpression() instanceof PsiReferenceExpression &&
                            refactorableFields.contains(((PsiReferenceExpression) assignmentExpression.getLExpression()).resolve())) {
                        references.add(reference.resolve());
                    }
                }
            }

            for (PsiParameter parameter : method.getParameterList().getParameters()) {
                if (references.contains(parameter)) {
                    setters.add(method);
                    break;
                }
            }
        }
        return setters;
    }

    /**
     * runRefactoring adaptor for tab.
     *
     * @param e AnActionEvent from actionPerformed
     */
    @Override
    protected void runRefactoring(AnActionEvent e) {
        PsiFile file = EditorUtils.getPsiFile(e);
        PsiElement psiElem = e.getData(LangDataKeys.PSI_ELEMENT);
        PsiClass focusClass = EditorUtils.getFocusedClass(file, psiElem);

        runRefactoring(file, focusClass);
    }

    /**
     * runRefactoring adaptor for IntentionAction.
     *
     * @param project the project in which the intention is invoked.
     * @param editor  the editor in which the intention is invoked.
     * @param file    the file open in the editor.
     */
    @Override
    protected void runRefactoring(@NotNull Project project, Editor editor, PsiFile file) {
        PsiElement focusElement = file.findElementAt(editor.getCaretModel().getOffset());
        runRefactoring(file, EditorUtils.getFocusedClass(file, focusElement));
    }

    @Override
    public String getRefactorName() {
        return "Encapsulate Collection";
    }

    /**
     * Main routine of runRefactoring.
     *
     * @param file File context.
     * @param aClass Target class.
     */
    public void runRefactoring(PsiFile file, PsiClass aClass) {
        List<PsiField> refactorableFields = getRefactorableFields(aClass);
        Map<PsiMethod, PsiField> getters = getGetters(aClass, refactorableFields);

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(file.getProject());

        for (PsiField refactorableField : refactorableFields) {
            Pattern pattern = Pattern.compile("(?<=<).+(?=>)");
            Matcher matcher = pattern.matcher(refactorableField.getType().getCanonicalText());
            if (!matcher.find()) {
                continue;
            }

            String name = refactorableField.getName();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            PsiMethod addMethod = factory.createMethodFromText(
                    "public boolean add" + name + "(" + matcher.group() + " input) {\n" +
                            "return " + refactorableField.getName() + ".add(input);\n" +
                            "}", aClass);

            PsiMethod removeMethod = factory.createMethodFromText(
                    "public boolean remove" + name + "(" + matcher.group() + " input) {\n" +
                            "return " + refactorableField.getName() + ".remove(input);\n" +
                            "}", aClass);

            WriteCommandAction.runWriteCommandAction(file.getProject(), () ->
            {
                aClass.addAfter(addMethod, aClass.getMethods()[aClass.getMethods().length - 1]);
                aClass.addAfter(removeMethod, aClass.getMethods()[aClass.getMethods().length - 1]);
            });
        }

        List<PsiLocalVariable> variables = TextUtils.findLocalVariables(file).stream()
                .filter(asn -> TextUtils.findMethodCallExpressions(asn)
                        .stream().anyMatch(call -> getters.containsKey(call.resolveMethod())))
                .collect(Collectors.toList());

        List<PsiReferenceExpression> references = TextUtils.findReferenceExpressions(file);
        references = references.stream().filter(rf -> variables.contains(rf.resolve()) &&
                rf.getNextSibling().getText().equals(".") &&
                (rf.getNextSibling().getNextSibling().getNextSibling().getText().equals("add") ||
                        rf.getNextSibling().getNextSibling().getNextSibling().getText().equals("remove"))).collect(Collectors.toList());

        for (PsiReferenceExpression reference : references) {
            PsiMethodCallExpression methodCall = (PsiMethodCallExpression) ((PsiLocalVariable) reference.resolve()).getInitializer();

            PsiMethod method = methodCall.resolveMethod();
            PsiClass parentClass = method.getContainingClass();

            String fieldName = getters.get(method).getName();
            fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            PsiStatement statement = factory.createStatementFromText(methodCall.getText().split("\\.")[0]
                    + "." + reference.getNextSibling().getNextSibling().getNextSibling().getText() + fieldName
                    + ((PsiMethodCallExpression) reference.getParent().getParent()).getArgumentList().getText() + ";", null);

            EditorUtils.replacePsiElement(reference.getParent().getParent().getParent(), statement, file);
        }

        //Delete setters
        List<PsiMethod> setters = getSetters(aClass, refactorableFields);
        for (PsiMethod setter : setters) {
            WriteCommandAction.runWriteCommandAction(file.getProject(), () ->
                    setter.delete());
        }

        for (PsiMethod getter : getters.keySet()) {
            boolean isSet = getter.getReturnType().getCanonicalText().contains("Set");
            String typeText = isSet ? "Set" : "List";

            PsiStatement newReturn = factory.createStatementFromText("return Collections.unmodifiable"
                    + typeText + "(" + getters.get(getter).getName() + ");", null);

            PsiReturnStatement originalReturn = TextUtils.findReturnStatements(getter).get(0);
            EditorUtils.replacePsiElement(originalReturn, newReturn, file);
        }
    }
}
