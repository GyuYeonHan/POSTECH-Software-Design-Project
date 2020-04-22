package team1.plugin.utils;

import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.util.Stack;

public class FileStructureTreeFactory {

    /**
     * add leaf in stack
     *
     * @param e PSI element
     * @param stk base stack
     */
    private static void leafAdd(PsiElement e, Stack<DefaultMutableTreeNode> stk) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(e);
        DefaultMutableTreeNode top_node = stk.peek();
        top_node.add(node);
    }

    /**
     * create new tree model
     *
     * @param file_ PSI file
     * @return Tree Model
     */
    public static TreeModel createFileTreeModel(PsiFile file_) {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(file_);
        Stack<DefaultMutableTreeNode> stk = new Stack();

        final JavaElementVisitor visitor = new JavaElementVisitor() {
            @Override
            public void visitFile(PsiFile file) {
                stk.push(root);
                PsiElement[] elem = file.getChildren();
                for (PsiElement psiElement : elem) {
                    visitTreeElement(psiElement);
                }
                stk.pop();
            }

            public void visitTreeElement(PsiElement element) {
                if (element.getClass() == PsiWhiteSpaceImpl.class) {
                    return;
                }

                if (element.getChildren().length == 0) {
                    leafAdd(element, stk);
                } else {
                    visitNonLeaf(element);
                }
            }

            public void visitNonLeaf(PsiElement element) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(element);
                DefaultMutableTreeNode top_node = stk.peek();
                top_node.add(node);
                stk.push(node);
                PsiElement[] elem = element.getChildren();
                for (PsiElement psiElement : elem) {
                    visitTreeElement(psiElement);
                }
                stk.pop();
            }

        };

        file_.accept(visitor);
        return new DefaultTreeModel(root);
    }

}