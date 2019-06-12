package cn.hikyson.methodcanary.lib;

import java.util.Stack;

import static cn.hikyson.methodcanary.lib.MethodTraceInfo.VIRTUAL_TOP_NODE;

public class MethodTree {
    private Stack<TreeNode<MethodTraceInfo>> mMethodTraceInfoStack = new Stack<>();
    private Tree<MethodTraceInfo> mMethodTraceInfoTree;
    private ThreadInfo mThreadInfo;

    public MethodTree(Thread attachThread) {
        this.mThreadInfo = new ThreadInfo(attachThread.getId(), attachThread.getName(), attachThread.getPriority());
    }

    public void pushMethod(MethodInfo methodInfo) {
        if (mMethodTraceInfoTree == null) {
            mMethodTraceInfoTree = new Tree<>(VIRTUAL_TOP_NODE);
        }
        long nanoTime = System.nanoTime();
        TreeNode<MethodTraceInfo> parentNode = mMethodTraceInfoStack.peek();
        MethodTraceInfo methodTraceInfo = new MethodTraceInfo(nanoTime, methodInfo);
        TreeNode<MethodTraceInfo> thisNode;
        if (parentNode == null) {
            thisNode = mMethodTraceInfoTree.getRoot().addChild(methodTraceInfo);
        } else {
            thisNode = parentNode.addChild(methodTraceInfo);
        }
        mMethodTraceInfoStack.push(thisNode);
    }

    public void popMethod() {
        long nanoTime = System.nanoTime();
        TreeNode<MethodTraceInfo> thisNode = mMethodTraceInfoStack.pop();
        thisNode.getData().endTimeNano = nanoTime;
    }
}
