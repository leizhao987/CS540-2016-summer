
/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 * 
 */

import java.util.*;

public class NNImpl {
	public ArrayList<Node> inputNodes = null;// list of the output layer nodes.
	public ArrayList<Node> hiddenNodes = null;// list of the hidden layer nodes
	public ArrayList<Node> outputNodes = null;// list of the output layer nodes

	public ArrayList<Instance> trainingSet = null;// the training set

	Double learningRate = 1.0; // variable to store the learning rate
	int maxEpoch = 1; // variable to store the maximum number of epochs

	/**
	 * This constructor creates the nodes necessary for the neural network Also
	 * connects the nodes of different layers After calling the constructor the
	 * last node of both inputNodes and hiddenNodes will be bias nodes.
	 */

	public NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch,
			Double[][] hiddenWeights, Double[][] outputWeights) {
		this.trainingSet = trainingSet;
		this.learningRate = learningRate;
		this.maxEpoch = maxEpoch;

		// input layer nodes
		inputNodes = new ArrayList<Node>();
		int inputNodeCount = trainingSet.get(0).attributes.size();
		int outputNodeCount = trainingSet.get(0).classValues.size();
		for (int i = 0; i < inputNodeCount; i++) {
			Node node = new Node(0);
			inputNodes.add(node);
		}

		// bias node from input layer to hidden
		Node biasToHidden = new Node(1);
		inputNodes.add(biasToHidden);

		// hidden layer nodes
		hiddenNodes = new ArrayList<Node>();
		for (int i = 0; i < hiddenNodeCount; i++) {
			Node node = new Node(2);
			// Connecting hidden layer nodes with input layer nodes
			for (int j = 0; j < inputNodes.size(); j++) {
				NodeWeightPair nwp = new NodeWeightPair(inputNodes.get(j), hiddenWeights[i][j]);
				node.parents.add(nwp);
			}
			hiddenNodes.add(node);
		}

		// bias node from hidden layer to output
		Node biasToOutput = new Node(3);
		hiddenNodes.add(biasToOutput);

		// Output node layer
		outputNodes = new ArrayList<Node>();
		for (int i = 0; i < outputNodeCount; i++) {
			Node node = new Node(4);
			// Connecting output layer nodes with hidden layer nodes
			for (int j = 0; j < hiddenNodes.size(); j++) {
				NodeWeightPair nwp = new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);
				node.parents.add(nwp);
			}
			outputNodes.add(node);
		}
	}

	/**
	 * Get the output from the neural network for a single instance Return the
	 * idx with highest output values. For example if the outputs of the
	 * outputNodes are [0.1, 0.5, 0.2], it should return 1. If outputs of the
	 * outputNodes are [0.1, 0.5, 0.5], it should return 2. The parameter is a
	 * single instance.
	 */

	public int calculateOutputForInstance(Instance inst) {
		double[] outs = calculatePredictionsForInstance(inst);
		int index = 0;
		double maxValue = outs[0];

		for (int i = 1; i < outs.length; ++i) {
			if (outs[i] >= maxValue) {
				index = i;
				maxValue = outs[i];
			}
		}
		return index;
	}

	private double[] calculatePredictionsForInstance(Instance inst) {
		double[] predictions = new double[outputNodes.size()];
		for (int i = 0; i < inst.attributes.size(); ++i) {
			inputNodes.get(i).setInput(inst.attributes.get(i));
		}

		inputNodes.get(inputNodes.size() - 1).setInput(1.0);

		for (int i = 0; i < hiddenNodes.size(); ++i) {
			hiddenNodes.get(i).calculateOutput();
		}

		for (int i = 0; i < outputNodes.size(); ++i) {
			outputNodes.get(i).calculateOutput();
			predictions[i] = outputNodes.get(i).getOutput();
		}

		return predictions;
	}

	/**
	 * Train the neural networks with the given parameters
	 * 
	 * The parameters are stored as attributes of this class
	 */

	public void train() {
		for (int epoch = 0; epoch < maxEpoch; ++epoch) {
			for (int inst = 0; inst < trainingSet.size(); ++inst) {
				double[] outK = calculatePredictionsForInstance(trainingSet.get(inst));
				double[] weighted_deltaK = new double[hiddenNodes.size()-1];
				for (int k = 0; k < outputNodes.size(); ++k) {
					for (int j = 0; j < hiddenNodes.size(); ++j) {
						double expOut = (double)(trainingSet.get(inst).classValues.get(k));
						double gp_ink = (outputNodes.get(k).getSum() > 0) ? 1.0 : 0.0;
						if (j != hiddenNodes.size()-1) {
							weighted_deltaK[j] += outputNodes.get(k).parents.get(j).weight*(expOut - outK[k])*gp_ink;
						}
						outputNodes.get(k).parents.get(j).weight += learningRate*hiddenNodes.get(j).getOutput()*(expOut - outK[k])*gp_ink;
					}
				}
				for (int j = 0; j < hiddenNodes.size()-1; ++j) {
					for (int i = 0; i < inputNodes.size(); ++i) {
						double gp_inj = (hiddenNodes.get(j).getSum() > 0) ? 1.0 : 0.0;
						hiddenNodes.get(j).parents.get(i).weight += learningRate*inputNodes.get(i).getOutput()*gp_inj*weighted_deltaK[j];
					}
				}
			}
		}
	}
}
