/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.util;

import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import project.DO.Index;

/**
 *
 * @author DA CUOI
 */
public class Tree {

    private List<Index> listIndex;

    public Tree() {
    }

    public Tree(List<Index> list) {
        this.listIndex = list;
    }

    public TreeNode createTreeNodeIndex() {
        TreeNode root = new DefaultTreeNode("Root", null);
        List<TreeNode> list0 = new ArrayList<>();
        List<TreeNode> list1 = new ArrayList<>();
        List<TreeNode> list2 = new ArrayList<>();
        List<TreeNode> list3 = new ArrayList<>();
        List<TreeNode> list4 = new ArrayList<>();
        List<TreeNode> list5 = new ArrayList<>();
        List<TreeNode> list6 = new ArrayList<>();

        for (Index index : listIndex) {
            switch (index.getLevel()) {
                case 0:
                    TreeNode node = new DefaultTreeNode(index, root);
                    //node.setRowKey(index.getIdindex().toString());
                    list0.add(node);
                    break;
                case 1:
                    TreeNode node1 = null;
                    for (TreeNode item : list0) {
                        Index i = (Index) item.getData();
                        if (i.getIdindex() == index.getIdParent()) {
                            node1 = new DefaultTreeNode(index, item);
                        }
                    }
                    list1.add(node1);
                    break;
                case 2:
                    TreeNode node2 = null;
                    for (TreeNode item : list1) {
                        Index i = (Index) item.getData();
                        if (i.getIdindex() == index.getIdParent()) {
                            node2 = new DefaultTreeNode(index, item);
                            //System.err.print(i.getLevel() + " " i.getIdParent());
                        }
                    }
                    list2.add(node2);
                    break;
                case 3:
                    TreeNode node3 = null;
                    for (TreeNode item : list2) {
                        Index i = (Index) item.getData();
                        if (i.getIdindex() == index.getIdParent()) {
                            node3 = new DefaultTreeNode(index, item);
                        }
                    }
                    list3.add(node3);
                    break;
                case 4:
                    TreeNode node4 = null;
                    for (TreeNode item : list3) {
                        Index i = (Index) item.getData();
                        if (i.getIdindex() == index.getIdParent()) {
                            node4 = new DefaultTreeNode(index, item);
                        }
                    }
                    list4.add(node4);
                    break;
                case 5:
                    TreeNode node5 = null;
                    for (TreeNode item : list4) {
                        Index i = (Index) item.getData();
                        if (i.getIdindex() == index.getIdParent()) {
                            node5 = new DefaultTreeNode(index, item);
                        }
                    }
                    list5.add(node5);
                    break;
            }

        }
        return root;
    }

    public TreeNode abc() {
        TreeNode root1 = new DefaultTreeNode("Root", null);
        root1.setRowKey("hgf");
        root1.getRowKey();
        TreeNode node0 = new DefaultTreeNode(root1.getRowKey(), root1);
        TreeNode node1 = new DefaultTreeNode("Node 1", root1);
        TreeNode node2 = new DefaultTreeNode("Node 2", root1);

        TreeNode node00 = new DefaultTreeNode("Node 0.0", node0);
        TreeNode node01 = new DefaultTreeNode("Node 0.1", node0);

        TreeNode node10 = new DefaultTreeNode("Node 1.0", node1);
        TreeNode node11 = new DefaultTreeNode("Node 1.1", node1);

        TreeNode node000 = new DefaultTreeNode("Node 0.0.0", node00);
        TreeNode node001 = new DefaultTreeNode("Node 0.0.1", node00);
        TreeNode node010 = new DefaultTreeNode("Node 0.1.0", node01);

        TreeNode node100 = new DefaultTreeNode("Node 1.0.0", node10);
        return root1;
    }

    public List<Index> getListIndex() {
        return listIndex;
    }

    public void setListIndex(List<Index> listIndex) {
        this.listIndex = listIndex;
    }

}
