package model;

import java.util.Arrays;

/**
 * Simple Astro Model Trainer (Dummy Gradient Descent)
 * This example "trains" weights for a linear model to fit given astro features to targets.
 * In real scenarios, use TensorFlow's training APIs and real datasets.
 *
 * === Real-life Training Scenario Example ===
 * Scenario: Predicting Favorability Score for Business Start Date
 * Dataset:
 *   Each row: [year, month, day, hour, minute, locationCode] -> favorabilityScore
 *   Example rows:
 *     [2024, 4, 18, 9, 0, 101]   -> 0.85  // Favorable
 *     [2024, 4, 19, 15, 30, 101] -> 0.40  // Less favorable
 *     [2024, 5, 1, 10, 0, 102]   -> 0.90  // Highly favorable
 *     [2024, 5, 2, 16, 0, 102]   -> 0.35  // Not favorable
 *     [2024, 6, 10, 8, 0, 103]   -> 0.75  // Favorable
 *     [2024, 6, 11, 18, 0, 103]  -> 0.20  // Not favorable
 *
 * Usage:
 *   - Replace the featureSet and targets arrays below with real or larger datasets.
 *   - Run this trainer to fit weights for the linear model.
 */
public class TensorFlowAstroModelTrainer {
    public static void main(String[] args) {
        // Realistic training data: features and target favorability scores
        float[][] featureSet = {
            {2024f, 4f, 18f, 9f, 0f, 101f},
            {2024f, 4f, 19f, 15f, 30f, 101f},
            {2024f, 5f, 1f, 10f, 0f, 102f},
            {2024f, 5f, 2f, 16f, 0f, 102f},
            {2024f, 6f, 10f, 8f, 0f, 103f},
            {2024f, 6f, 11f, 18f, 0f, 103f}
        };
        float[] targets = {0.85f, 0.40f, 0.90f, 0.35f, 0.75f, 0.20f}; // Favorability scores

        // Initialize weights
        float[] weights = new float[featureSet[0].length];
        Arrays.fill(weights, 0.1f);

        float learningRate = 0.000001f;
        int epochs = 2000;

        for (int epoch = 0; epoch < epochs; epoch++) {
            float[] gradients = new float[weights.length];
            float loss = 0f;

            // Compute gradients for each sample
            for (int i = 0; i < featureSet.length; i++) {
                float[] features = featureSet[i];
                float prediction = 0f;
                for (int j = 0; j < weights.length; j++) {
                    prediction += features[j] * weights[j];
                }
                float error = prediction - targets[i];
                loss += error * error;
                for (int j = 0; j < weights.length; j++) {
                    gradients[j] += 2 * error * features[j];
                }
            }

            // Update weights
            for (int j = 0; j < weights.length; j++) {
                weights[j] -= learningRate * gradients[j] / featureSet.length;
            }

            if (epoch % 400 == 0) {
                System.out.printf("Epoch %d, Loss: %.6f, Weights: %s%n", epoch, loss / featureSet.length, Arrays.toString(weights));
            }
        }

        System.out.println("Trained weights: " + Arrays.toString(weights));
        // Save weights to file or use in prediction model as needed
    }
}
