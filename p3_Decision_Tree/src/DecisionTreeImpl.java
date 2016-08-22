import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fill in the implementation details of the class DecisionTree using this file.
 * Any methods or secondary classes that you want are fine but we will only
 * interact with those methods in the DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	private DecTreeNode root;
	// ordered list of class labels
	private List<String> labels;
	// ordered list of attributes
	private List<String> attributes;
	// map to ordered discrete values taken by attributes
	private Map<String, List<String>> attributeValues;

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary this is void purposefully
	}

	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train:
	 *            the training set
	 */
	DecisionTreeImpl(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		this.root = buildTree(majorityVote(train.instances), null, train.instances, this.attributes);
	}

	private DecTreeNode buildTree(String label, String parentAttributeValue, List<Instance> instances,
			List<String> atts) {
		if (instances.isEmpty())
			return new DecTreeNode(label, null, parentAttributeValue, true);
		if (hasSameLabel(instances))
			return new DecTreeNode(instances.get(0).label, null, parentAttributeValue, true);
		if (atts.isEmpty())
			return new DecTreeNode(majorityVote(instances), null, parentAttributeValue, true);
		String bestAtt = getBestAttr(instances, atts, null);
		DecTreeNode tree = new DecTreeNode(majorityVote(instances), bestAtt, parentAttributeValue, false);
		for (String value : this.attributeValues.get(bestAtt)) {
			List<Instance> sub_instances = getInstancesFromAttrValue(instances, bestAtt, value);
			List<String> sub_atts = getReducedAtts(atts, bestAtt);
			tree.children.add(buildTree(majorityVote(instances), value, sub_instances, sub_atts));
		}
		return tree;
	}

	private List<String> getReducedAtts(List<String> atts, String bestAtt) {
		List<String> reducedAtts = new ArrayList<String>(atts);
		reducedAtts.remove(bestAtt);
		return reducedAtts;
	}

	private List<Instance> getInstancesFromAttrValue(List<Instance> instances, String att, String value) {
		List<Instance> sub_instances = new ArrayList<Instance>();
		for (Instance ins : instances) {
			if (ins.attributes.get(getAttributeIndex(att)).equals(value))
				sub_instances.add(ins);
		}
		return sub_instances;
	}

	private boolean hasSameLabel(List<Instance> instances) {
		for (int i = 1; i < instances.size(); ++i) {
			if (!instances.get(i - 1).label.equals(instances.get(i).label))
				return false;
		}
		return true;
	}

	@Override
	public String classify(Instance instance) {
		DecTreeNode node = this.root;
		while (!node.terminal) {
			String att = node.attribute;
			for (DecTreeNode subNode : node.children) {
				if (subNode.parentAttributeValue.equals(instance.attributes.get(getAttributeIndex(att)))) {
					node = subNode;
					break;
				}
			}
		}
		return node.label;
	}

	@Override
	public void rootInfoGain(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;

		double[] hAttr = new double[this.attributes.size()];
		getBestAttr(train.instances, this.attributes, hAttr);

		for (int i = 0; i < hAttr.length; ++i) {
			System.out.format(this.attributes.get(i) + " %.5f\n", hAttr[i]);
		}
	}

	private String getBestAttr(List<Instance> instances, List<String> atts, double[] hAttr) {
		if (hAttr == null)
			hAttr = new double[atts.size()];
		int num_values;
		int[] num_GB = new int[] { 0, 0 };
		String attr;
		List<List<int[]>> list = new ArrayList<List<int[]>>(atts.size());
		for (int i = 0; i < atts.size(); ++i) {
			attr = atts.get(i);
			num_values = this.attributeValues.get(attr).size();
			list.add(new ArrayList<int[]>(num_values));
			for (int j = 0; j < num_values; ++j)
				list.get(i).add(new int[] { 0, 0 });
		}

		for (Instance inst : instances) {
			int num_attr = atts.size();
			for (int i = 0; i < num_attr; ++i) {
				int att_index = getAttributeIndex(atts.get(i));
				int id = getAttributeValueIndex(atts.get(i), inst.attributes.get(att_index));
				list.get(i).get(id)[getLabelIndex(inst.label)]++;
				num_GB[getLabelIndex(inst.label)]++;
			}
		}

		double pG = (double) (num_GB[0]) / (double) (num_GB[0] + num_GB[1]);
		double pB = 1.0 - pG;
		double hGB = -pG * log2(pG) - pB * log2(pB);

		for (int i = 0; i < atts.size(); ++i) {
			String at = atts.get(i);
			double hi = 0, pj, pjG, pjB;
			int jG, jB;
			for (int j = 0; j < this.attributeValues.get(at).size(); ++j) {
				jG = list.get(i).get(j)[0];
				jB = list.get(i).get(j)[1];
				if (jG != 0 && jB != 0) {
					pj = (double) (jG + jB) / (double) (instances.size());
					pjG = (double) jG / (double) (jG + jB);
					pjB = (double) jB / (double) (jG + jB);
					hi += -pj * (pjG * log2(pjG) + pjB * log2(pjB));
				}
			}
			hAttr[i] = hGB - hi;
		}

		String maxAttr = atts.get(0);
		double maxGain = hAttr[0];
		for (int i = 1; i < hAttr.length; ++i) {
			if (hAttr[i] > maxGain) {
				maxGain = hAttr[i];
				maxAttr = atts.get(i);
			}
		}
		return maxAttr;
	}

	@Override
	public void printAccuracy(DataSet test) {
		System.out.format("%.5f\n", getAccuracy(test));
	}

	private double getAccuracy(DataSet test) {
		int[] result = new int[] { 0, 0 };
		for (Instance ins : test.instances) {
			if (ins.label.equals(classify(ins)))
				result[0]++;
			else
				result[1]++;
		}
		return (double) result[0] / (double) (result[0] + result[1]);
	}

	/**
	 * Build a decision tree given a training set then prune it using a tuning
	 * set. ONLY for extra credits
	 * 
	 * @param train:
	 *            the training set
	 * @param tune:
	 *            the tuning set
	 */
	DecisionTreeImpl(DataSet train, DataSet tune) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		this.root = buildTree(majorityVote(train.instances), null, train.instances, this.attributes);

		// int depth = 0;
		// System.out.println("Before pruning, accuracy = " + getAccuracy(tune));
		while (true) {
			// depth++;
			double maxAccuracy = getAccuracy(tune);
			double[] pruneAccuracy = new double[] { 0 };
			DecTreeNode optNode = getOptimalPruneNode(tune, this.root, pruneAccuracy);
			if (pruneAccuracy[0] <= maxAccuracy) {
				// System.out.println("depth = " + depth + ", stop improving,
				// final accuracy = " + maxAccuracy);
				break; // use some stopping threshold
			}

			// System.out.println("depth = " + depth + ", accuracy = " +
			// pruneAccuracy[0]);
			optNode.terminal = true;
			optNode.children = null;

		}
	}

	private DecTreeNode getOptimalPruneNode(DataSet tune, DecTreeNode startingNode, double[] pruneAccuracy) {
		startingNode.terminal = true;
		double maxAccuracy = getAccuracy(tune);
		DecTreeNode optNode = startingNode;
		startingNode.terminal = false;
		for (DecTreeNode child : startingNode.children) {
			if (child.terminal)
				continue;
			DecTreeNode optChild = getOptimalPruneNode(tune, child, pruneAccuracy);
			// System.out.println("check childAccuracy = " + pruneAccuracy[0]);
			if (pruneAccuracy[0] > maxAccuracy) {
				optNode = optChild;
				maxAccuracy = pruneAccuracy[0];
			}
		}
		pruneAccuracy[0] = maxAccuracy;
		return optNode;
	}

	@Override
	/**
	 * Print the decision tree in the specified format
	 */
	public void print() {

		printTreeNode(root, null, 0);
	}

	/**
	 * Prints the subtree of the node with each line prefixed by 4 * k spaces.
	 */
	public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; i++) {
			sb.append("    ");
		}
		String value;
		if (parent == null) {
			value = "ROOT";
		} else {
			int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
			value = attributeValues.get(parent.attribute).get(attributeValueIndex);
		}
		sb.append(value);
		if (p.terminal) {
			sb.append(" (" + p.label + ")");
			System.out.println(sb.toString());
		} else {
			sb.append(" {" + p.attribute + "?}");
			System.out.println(sb.toString());
			for (DecTreeNode child : p.children) {
				printTreeNode(child, p, k + 1);
			}
		}
	}

	private String majorityVote(List<Instance> instances) {
		int[] nums = new int[] { 0, 0 };
		for (Instance s : instances)
			nums[getLabelIndex(s.label)]++;
		if (nums[0] >= nums[1])
			return labels.get(0);
		else
			return labels.get(1);
	}

	/**
	 * Helper function to get the index of the label in labels list
	 */
	private int getLabelIndex(String label) {
		for (int i = 0; i < this.labels.size(); i++) {
			if (label.equals(this.labels.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Helper function to get the index of the attribute in attributes list
	 */
	private int getAttributeIndex(String attr) {
		for (int i = 0; i < this.attributes.size(); i++) {
			if (attr.equals(this.attributes.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Helper function to get the index of the attributeValue in the list for
	 * the attribute key in the attributeValues map
	 */
	private int getAttributeValueIndex(String attr, String value) {
		for (int i = 0; i < attributeValues.get(attr).size(); i++) {
			if (value.equals(attributeValues.get(attr).get(i))) {
				return i;
			}
		}
		return -1;
	}

	private double log2(double x) {
		// if ((int)x == 0 || (int)x == 1) return 0;
		return (Math.log10(x) / Math.log10(2));
	}
}
