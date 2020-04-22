package team1.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import team1.plugin.utils.EditorUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PullUpConstructorBodyAction extends CommonAction {
    /**
     * isRefactorable adaptor for tab.
     *
     * @param e Event message.
     * @return true if refactoring is available.
     */
    @Override
    protected boolean isRefactorable(AnActionEvent e) {
        PsiDirectory dir = EditorUtils.getPsiDirectory(e);
        return isRefactorable(dir);
    }

    /**
     * isRefactorable adaptor for IntentionAction.
     *
     * @param project the project in which the availability is checked.
     * @param editor  the editor in which the intention will be invoked.
     * @param file    the file open in the editor.
     * @return true if refactoring is available.
     */
    @Override
    protected boolean isRefactorable(@NotNull Project project, Editor editor, PsiFile file) {
        return isRefactorable(EditorUtils.getPsiDirectory(project, editor));
    }

    /**
     * Main routine of isRefactorable. Looks through classes in the directory, looking for similar
     * code in the constructors.
     *
     * @param directory Directory context of refactoring.
     * @return true if refactoring is available.
     */
    public boolean isRefactorable(PsiDirectory directory) {
        HashMap<PsiClass, ArrayList<PsiClass>> classGroups = createClassGroups(directory);
        HashMap<PsiClass, ArrayList<PsiStatement>> classCommonStatements = getCommonStatements(classGroups);
        return classCommonStatements.values().stream().anyMatch(commonStatements -> commonStatements.size() > 0);
    }

    /**
     * Goes through all classes in the directory and maps classes to their superclasses.
     *
     * @param directory Directory context of refactoring.
     * @return Map with keys being the PsiClasses in the directory and values being a list of superclasses.
     */
    private HashMap<PsiClass, ArrayList<PsiClass>> createClassGroups(PsiDirectory directory) {
        HashMap<PsiClass, ArrayList<PsiClass>> classGroups = new HashMap<>();
        ArrayList<PsiClass> classes = new ArrayList<>();
        for (PsiFile file : directory.getFiles()) {
            PsiJavaFile javaFile = (PsiJavaFile) file;
            classes.addAll(Arrays.asList(javaFile.getClasses()));
        }
        // Adds direct superclasses of each class to a list and adds list to return value
        for (PsiClass element : classes) {
            if (element.getSuperClass() != null) {
                if (!classGroups.containsKey(element.getSuperClass())) {
                    classGroups.put(element.getSuperClass(), new ArrayList<>());
                }
                classGroups.get(element.getSuperClass()).add(element);
            }
        }
        return classGroups;
    }

    /**
     * Goes through all classes and their superclasses, and gets common statements in these
     * class-superclass groups' constructors.
     *
     * @param classGroups Map from classes to their superclasses.
     * @return Map with keys being the PsiClasses in the directory and values being a list of common constructor statements.
     */
    private HashMap<PsiClass, ArrayList<PsiStatement>> getCommonStatements(HashMap<PsiClass, ArrayList<PsiClass>> classGroups) {
        HashMap<PsiClass, ArrayList<PsiStatement>> classCommonStatements = new HashMap<>();
        for (Map.Entry<PsiClass, ArrayList<PsiClass>> psiClassArrayListEntry : classGroups.entrySet()) {
            ArrayList<ArrayList<PsiCodeBlock>> codeBlocks = new ArrayList<>();

            // Sets up a list of each class having a list of constructor code blocks
            for (PsiClass cl : psiClassArrayListEntry.getValue()) {
                PsiMethod[] constructorMethods = cl.getConstructors();
                ArrayList<PsiCodeBlock> constructorCodeBlocks = new ArrayList<>();
                for (PsiMethod method : constructorMethods) {
                    constructorCodeBlocks.add(method.getBody());
                }
                codeBlocks.add(constructorCodeBlocks);
            }

            // Adds all code block statements to a list, and removes the ones that are not in the other code blocks
            ArrayList<PsiStatement> commonStatements = new ArrayList<>();
            for (PsiCodeBlock block : codeBlocks.get(0)) {
                commonStatements.addAll(Arrays.asList(block.getStatements()));
            }
            for (int i = 1; i < codeBlocks.size(); i++) {
                HashSet<PsiStatement> targetStatements = new HashSet<>();
                for (PsiCodeBlock block : codeBlocks.get(i)) {
                    targetStatements.addAll(Arrays.asList(block.getStatements()));
                }
                commonStatements = (ArrayList<PsiStatement>) commonStatements.stream()
                        .filter(item -> targetStatements.stream()
                                .map(PsiElement::getText)
                                .collect(Collectors.toSet()).contains(item.getText()))
                        .collect(Collectors.toList());
            }
            commonStatements.removeIf(statement -> statement.getFirstChild().getFirstChild().getText().equals("super"));
            classCommonStatements.put(psiClassArrayListEntry.getKey(), commonStatements);
        }
        return classCommonStatements;
    }

    /**
     * runRefactoring adaptor for tab.
     *
     * @param e Event message.
     */
    @Override
    public void runRefactoring(AnActionEvent e) {
        PsiDirectory directory = EditorUtils.getPsiDirectory(e);
        runRefactoring(directory);
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
        runRefactoring(EditorUtils.getPsiDirectory(project, editor));
    }

    /**
     * Main routine of runRefactoring.
     *
     * @param directory Directory context of refactoring.
     */
    public void runRefactoring(PsiDirectory directory) {
        HashMap<PsiClass, ArrayList<PsiClass>> classGroups = createClassGroups(directory);
        HashMap<PsiClass, ArrayList<PsiStatement>> classCommonStatements = getCommonStatements(classGroups);

        // Goes through class groups and runs refactoring on the ones with common constructor statements
        for (Map.Entry<PsiClass, ArrayList<PsiClass>> psiClassArrayListEntry : classGroups.entrySet()) {
            ArrayList<PsiField> newFields = new ArrayList<>();
            PsiClass superClass = psiClassArrayListEntry.getKey();
            ArrayList<PsiClass> subClasses = psiClassArrayListEntry.getValue();

            if (psiClassArrayListEntry.getKey().getConstructors().length > 0 || classCommonStatements.get(superClass) == null) {
                continue;
            }

            ArrayList<PsiStatement> commonStatements = classCommonStatements.get(superClass);

            // Creates constructor of superclass.
            PsiElementFactory factory = JavaPsiFacade.getElementFactory(directory.getProject());
            PsiMethod newConstructor = factory.createConstructor(superClass.getName());

            // Adds the common statements & fields to the constructor.
            commonStatements.forEach(statement -> {
                WriteCommandAction.runWriteCommandAction(directory.getProject(), () -> {
                    newConstructor.getBody().add(statement);
                });
                String fieldName = ((PsiAssignmentExpression) statement.getFirstChild()).getLExpression().getLastChild().getText();
                PsiType fieldType = subClasses.get(0).findFieldByName(fieldName, false).getType();
                PsiField newField = factory.createField(fieldName, fieldType);
                newFields.add(newField);
                WriteCommandAction.runWriteCommandAction(directory.getProject(), () -> {
                    if (superClass.getAllMethods().length > 0) {
                        superClass.addBefore(newField, superClass.getAllMethods()[0]);
                    } else {
                        superClass.add(newField);
                    }
                });
            });

            // Adds parameters to constructor of superclass.
            String[] newNames = new String[newFields.size()];
            PsiType[] newTypes = new PsiType[newFields.size()];
            for (int i = 0; i < newFields.size(); i++) {
                newNames[i] = newFields.get(i).getName();
                newTypes[i] = newFields.get(i).getType();
            }

            PsiParameterList newList = factory.createParameterList(newNames, newTypes);
            WriteCommandAction.runWriteCommandAction(directory.getProject(), () -> {
                newConstructor.getParameterList().replace(newList);
            });

            WriteCommandAction.runWriteCommandAction(directory.getProject(), () -> {
                superClass.add(newConstructor);
            });

            // Removes statements & fields from the subclasses.
            for (PsiClass subClass : subClasses) {
                for (PsiMethod method : subClass.getConstructors()) {
                    for (PsiStatement statement : method.getBody().getStatements()) {
                        if (commonStatements.stream().map(PsiElement::getText).collect(Collectors.toSet())
                                .contains(statement.getText())) {
                            WriteCommandAction.runWriteCommandAction(directory.getProject(), () -> {
                                statement.delete();
                            });
                        }
                    }

                    String[] list = new String[newFields.size()];
                    StringBuilder s = new StringBuilder("super(");
                    for (int i = 0; i < newFields.size(); i++) {
                        String name = newFields.get(i).getName();
                        list[i] = name;
                        s.append(list[i]).append(i != newFields.size() - 1 ? ", " : "");
                    }
                    s.append(");");
                    PsiStatement superExpression = factory.createStatementFromText(s.toString(), null);
                    WriteCommandAction.runWriteCommandAction(directory.getProject(), () -> {
                        method.getBody().getFirstBodyElement().getParent().addBefore(superExpression, method.getBody().getFirstBodyElement());
                    });

                }
                for (PsiField field : subClass.getFields()) {
                    if (newFields.stream().anyMatch(newField -> newField.getName().equals(field.getName()) && newField.getType().equals(field.getType()))) {
                        WriteCommandAction.runWriteCommandAction(directory.getProject(), field::delete);
                    }
                }
            }
        }
    }

    @Override
    public String getRefactorName() {
        return "Pull Up Constructor Body";
    }
}
