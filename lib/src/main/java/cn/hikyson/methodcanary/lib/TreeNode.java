package cn.hikyson.methodcanary.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TreeNode<T> {
    private T data;
    private List<TreeNode> childs;
    private TreeNode parent;
    private int level;

    public TreeNode(T data, TreeNode parent) {
        this.data = data;
        this.parent = parent;
        if (parent == null) {
            level = 0;
        } else {
            level = parent.level + 1;
        }
    }

    public List<TreeNode> getchilds() {
        return childs;
    }

    public TreeNode<T> addChild(T data) {
        if (childs == null) {
            childs = new ArrayList<TreeNode>();
        }
        TreeNode<T> treeNode = new TreeNode<>(data, this);
        childs.add(treeNode);
        return treeNode;
    }

    public void deleteChild(TreeNode node) {
        if (childs != null) {
            Iterator<TreeNode> it = childs.iterator();
            while (it.hasNext()) {
                TreeNode child = it.next();
                if (child.equals(node)) {
                    childs.remove(child);
                    break;
                }
            }
        }
    }

    public TreeNode getChild(T data) {
        if (childs != null) {
            Iterator<TreeNode> it = childs.iterator();
            while (it.hasNext()) {
                TreeNode child = it.next();
                if (child.data.equals(data)) {
                    return child;
                }
            }
        }
        return null;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public int getLevel() {
        return level;
    }
}
