package org.andork.spatial

public static boolean traverse(RNode root, Closure onNodes, Closure onLeaves) {
	if (onNodes(root)) {
		if (root instanceof RBranch) {
			RBranch branch = (RBranch) root;
			for (int i = 0; i < branch.numChildren(); i++) {
				if (!traverse(branch.childAt(i), onNodes, onLeaves)) {
					return false;
				}
			}
		} else if (root instanceof RLeaf){
			return onLeaves(root);
		}
		return true;
	}
}