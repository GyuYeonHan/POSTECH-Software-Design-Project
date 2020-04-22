package team1.plugin.utils;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;

public class TreeUtils {
    /**
     * Determining whether two codes are same using PSI file
     *
     * @param code1 String from code
     * @param code2 String from code
     * @param project IntelliJ project object
     * @return {true} if code is equal, {false} otherwise
     */
    public static boolean codeEqual(String code1, String code2, Project project) {
        PsiFileFactory fac = PsiFileFactory.getInstance(project);
        PsiFile file1 = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), code1);
        PsiFile file2 = fac.createFileFromText(StdFileTypes.JAVA.getLanguage(), code2);

        TreeModel fileTreeModel1 = FileStructureTreeFactory.createFileTreeModel(file1);
        TreeModel fileTreeModel2 = FileStructureTreeFactory.createFileTreeModel(file2);
        return treeEqual(fileTreeModel1, fileTreeModel2);
    }

    /**
     * Determining whether two TreeModel
     *
     * @param tree1 TreeModel
     * @param tree2 TreeModel
     * @return {true} if treeModel is equal, {false} otherwise
     */
    public static boolean treeEqual(TreeModel tree1, TreeModel tree2) {
        DefaultMutableTreeNode currNodeT1 = (DefaultMutableTreeNode) tree1.getRoot();
        DefaultMutableTreeNode currNodeT2 = (DefaultMutableTreeNode) tree2.getRoot();

        Enumeration<TreeNode> e1 = currNodeT1.depthFirstEnumeration();
        Enumeration<TreeNode> e2 = currNodeT2.depthFirstEnumeration();

        while (e1.hasMoreElements() && e2.hasMoreElements()) {
            DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) e1.nextElement();
            DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) e2.nextElement();

            boolean leaf1 = node1.isLeaf();
            boolean leaf2 = node2.isLeaf();
            if (leaf1 && !leaf2 || !leaf1 && leaf2) {
                return false;
            }

            if (leaf1 && leaf2) {
                String s1 = ((PsiElement) node1.getUserObject()).getText();
                String s2 = ((PsiElement) node2.getUserObject()).getText();

                if (!s1.equals(s2)) {
                    return false;
                }
            }
        }
        return true;
    }
}
