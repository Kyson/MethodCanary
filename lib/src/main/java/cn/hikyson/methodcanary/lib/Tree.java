package cn.hikyson.methodcanary.lib;

public class Tree<T> {
    private TreeNode<T> root;

    public Tree(T rootData) {
        root = new TreeNode<>(rootData, null);
    }

    public TreeNode<T> getRoot() {
        return root;
    }
}