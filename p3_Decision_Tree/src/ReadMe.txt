/////////////// Pruning /////////////////////

I) Implementation algorithm
  1) Procedure
     When building the decision tree, at each node I store the majority vote class for the remaining examples in the "attribute" of that node.
     I follow the pruning pseudocode using a greedy algorithm. First I use decision tree T to calculate the prediction accuracy for the tuning set before each iteration of pruning. Then I perform a "pre-order" traversal for the decision tree, starting from the root. For each non-leaf node visited, I set the node to a leaf by switching its "terminal" from false to true, and use this new tree to compute the prediction accuracy for the tuning set, and then switch its "terminal" back to false. Then the final pruning is the one that gives the largest accuracy for tuning set. If this accuracy is larger than that in the decision tree before this iteration of pruning, accept this pruning and update the decision tree (by set the "terminal" state to true and set the children to null for the pruning node), and continue the next iteration. Otherwise, stop and give the tree before this iteration. 
  
  2) Breaking ties (there are multiple pruning nodes that gives the same accuracy improvement)
     If ties happen for sibling nodes, accept the one with smaller index (the one with its attribute comes earlier in the attribute list).
     If ties happen for non-sibling nodes, accept the one with lower depth.

  3) The iteration continues until any node setting to leaf will not improve the accuracy for tuning set.

II) Results
  1) I use the prune_train.txt, prune_tune.txt and prune_test.txt to verify the efficiency of the pruning.
  Without pruning, the accuracy for prune_test.txt is (by running java HW3 3 prune_train.txt prune_test.txt) is 0.68098.
  After prunning, the accuracy is (by running java HW3 6 prune_train.txt prune_tune.txt prune_test.txt) is 0.70958. 
  For this pruning case, 10 iterations of pruning have been done to find the optimal pruned tree. The tunning set accuracy increases from 0.68098 to 0.80982. 
