package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.*;
import com.intellij.psi.impl.source.tree.java.*;
import com.intellij.psi.impl.source.codeStyle.CodeStyleManagerImpl;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;
import team1.plugin.utils.*;

import java.util.*;

public class ReplaceConditionalWithPolymorphismAction extends CommonAction {

    PsiClass origin_class;
    PsiMethod origin_method;
    List<HashMap<String, List<PsiStatement>>> switch_statements;

    /**
     * Take method and class name from Swtich statement to create
     * new classes and methods
     * @param psiSwitchStatement
     */
    private void takeMethodAndClassName(PsiSwitchStatement psiSwitchStatement) {
        PsiElement parent = psiSwitchStatement;
        while (parent != null) {
            if (parent instanceof PsiMethodImpl) {
                PsiMethod method = (PsiMethod) parent;
                origin_method = method;
            } else if (parent instanceof PsiClassImpl) {
                PsiClass _class = (PsiClass) parent;
                origin_class = _class;
            }
            parent = parent.getParent();
        }
    }

    /**
     * @param e AnActionEvent from actionPerformed
     * @return true if refactoring action is available
     */
    @Override
    protected boolean isRefactorable(AnActionEvent e) {
        return isRefactorable(EditorUtils.getPsiFile(e));
    }

    /**
     * @param project the project in which the availability is checked.
     * @param editor  the editor in which the intention will be invoked.
     * @param file    the file open in the editor.
     * @return true if refactoring action is available
     */
    @Override
    protected boolean isRefactorable(@NotNull Project project, Editor editor, PsiFile file) {
        return isRefactorable(file);
    }

    /**
     * @param file
     * @return true if refactoring action is available
     */
    public boolean isRefactorable(PsiFile file) {
        switch_statements = new ArrayList<>();
        int possible_switch_statement_count = 0;
        for (PsiSwitchStatement psiSwitchStatement : TextUtils.findSwitchStatements(file)) {
            takeMethodAndClassName(psiSwitchStatement);
            HashMap<String, List<PsiStatement>> statements_format = new HashMap<>();
            switch_statements.add(statements_format);
            if (isRefactorableSwitchStatement(psiSwitchStatement, switch_statements.get(possible_switch_statement_count)))
                possible_switch_statement_count++;
            else {
                switch_statements.remove(possible_switch_statement_count);
            }
        }
        return possible_switch_statement_count != 0;
    }

    /**
     * Check that passed  swirch statement is refactorable
     *
     * @param psiSwitchStatement
     * @param statements_format
     * @return true if refactoring action is available
     */
    private boolean isRefactorableSwitchStatement(PsiSwitchStatement psiSwitchStatement, HashMap<String, List<PsiStatement>> statements_format) {
        // statements => case statement then belong statements.
        String cur_case_name = "";
        boolean isReturnedBefore = true;
        for (PsiStatement psiStatement : psiSwitchStatement.getBody().getStatements()) {
            // check case statement's expression to be string that will be able to be subclass's name.
            if (psiStatement instanceof PsiSwitchLabelStatement) {
                PsiSwitchLabelStatement psiSwitchLabelStatement = (PsiSwitchLabelStatement) psiStatement;
                // if two cases are overlapped, we cannot refactor this.
                if (!isReturnedBefore)
                    return false;

                for (PsiExpression psiExpression : psiSwitchLabelStatement.getCaseValues().getExpressions()) {
                    if (StringUtils.isNumeric(psiExpression.getText()))
                        return false;

                    cur_case_name = psiExpression.getText();
                    List<PsiStatement> statements = new ArrayList<>();
                    statements_format.put(cur_case_name, statements);
                    isReturnedBefore = false;
                }
            }
            // each case should return something.
            else if (psiStatement instanceof PsiReturnStatementImpl) {
                statements_format.get(cur_case_name).add(psiStatement);
                isReturnedBefore = true;
            }
            // other statements should be remained for newly created subclass.
            else {
                statements_format.get(cur_case_name).add(psiStatement);
                isReturnedBefore = false;
            }
        }
        return true;
    }

    /**
     * actual implementaion of runRefactoring
     *
     * @param file
     */
    public void runRefactoring(PsiFile file) {
        Project project = file.getProject();
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        // change origin class to abstract class
        PsiClass new_abstract_class = (PsiClass) origin_class.copy();
        new_abstract_class.getModifierList().setModifierProperty("abstract", true);
        new_abstract_class.add(TextUtils.newline(project));

        // change origin method to empty abstract method
        origin_method = new_abstract_class.findMethodBySignature(origin_method, true);
        String method_name = origin_method.getName();
        PsiType method_return_type = origin_method.getReturnType();
        PsiParameterList method_parameters = origin_method.getParameterList();

        PsiMethod new_abstract_method = factory.createMethod(method_name, method_return_type);
        new_abstract_method.getModifierList().setModifierProperty("abstract", true);
        new_abstract_method.getParameterList().replace(method_parameters);

        // make subclasses
        for (HashMap<String, List<PsiStatement>> statements_format : switch_statements) {
            Set set = statements_format.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                String subclass_name = (String) iterator.next();
                PsiClass cur_subclass = makeSubclass(subclass_name, factory);
                PsiMethod relevant_method = makeSubclassMethod(new_abstract_method, statements_format.get(subclass_name));
                cur_subclass.add(relevant_method);
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    file.add(cur_subclass);
                });
            }
        }

        EditorUtils.replacePsiElement(origin_method, new_abstract_method, project);
        EditorUtils.replacePsiElement(origin_class, new_abstract_class, project);
        EditorUtils.autoIndentation(file);
    }

    /**
     * create subClass with name
     *
     * @param subclass_name
     * @param factory
     * @return created subclass
     */
    private PsiClass makeSubclass(String subclass_name, PsiElementFactory factory) {
        PsiClass cur_subclass = factory.createClass(makeSuitableSubclassName(subclass_name, origin_class.getName()));
        PsiJavaCodeReferenceElement referenceElement = factory.createClassReferenceElement(origin_class);
        cur_subclass.getExtendsList().add(referenceElement);
        return cur_subclass;
    }

    /**
     * Create Methods which is copy of abstract Method Passed as parameters
     * but set abstract modifier property to false
     * @param new_abstract_method
     * @param statements
     * @return created method
     */
    private PsiMethod makeSubclassMethod(PsiMethod new_abstract_method, List<PsiStatement> statements) {
        PsiMethod relevant_method = (PsiMethod) new_abstract_method.copy();
        relevant_method.getModifierList().setModifierProperty("abstract", false);
        for (PsiStatement psiStatement : statements) {
            relevant_method.getBody().add(psiStatement);
        }
        return relevant_method;
    }

    /**
     * make suitable subclass name
     *
     * @param origin_subclass_name
     * @param origin_class_name
     * @return suitable subclass name
     */
    private String makeSuitableSubclassName(String origin_subclass_name, String origin_class_name) {
        String suitable_subclass_name = "";
        String[] name = origin_subclass_name.split("_");
        for (String s : name)
            suitable_subclass_name += s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        return suitable_subclass_name;
    }

    /**
     * runRefactoring adaptor for tab
     *
     * @param e AnActionEvent from actionPerformed
     */
    @Override
    protected void runRefactoring(AnActionEvent e) {
        Project project = e.getProject();
        runRefactoring(EditorUtils.getPsiFile(e));
        CodeStyleManagerImpl c = new CodeStyleManagerImpl(project);
        c.adjustLineIndent(EditorUtils.getPsiFile(e), 4);
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
        //TODO check working
        runRefactoring(file);
        CodeStyleManagerImpl c = new CodeStyleManagerImpl(project);
        c.adjustLineIndent(file, 4);
    }

    @Override
    public String getRefactorName() {
        return "Replace Conditional With Polymorphism";
    }
}
