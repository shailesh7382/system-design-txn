package model;

import org.tensorflow.ndarray.Shape;
import org.tensorflow.types.TFloat32;
import org.tensorflow.EagerSession;

import java.util.Arrays;

/**
 * Extended Astro Prediction Model Example.
 * Accepts input from command-line or uses defaults, prints features,
 * and simulates a weighted sum as a more complex prediction.
 *
 * === Real-life Astro Prediction Scenarios ===
 * 1. Daily Horoscope Prediction:
 *    Input: [birthYear, birthMonth, birthDay, currentHour, currentMinute, locationCode]
 *    Example: 1992 12 25 8 45 201
 *
 * 2. Compatibility Analysis:
 *    Input: [user1_birthYear, user1_birthMonth, user1_birthDay, user2_birthYear, user2_birthMonth, user2_birthDay]
 *    Example: 1985 5 10 1990 7 15
 *
 * 3. Muhurat (Auspicious Time) Calculation:
 *    Input: [eventYear, eventMonth, eventDay, eventHour, eventMinute, locationCode]
 *    Example: 2024 11 5 14 30 301
 *
 * 4. Real Scenario: Predicting Favorable Business Start Date
 *    Suppose a user wants to know if a specific date and time is favorable for starting a business in Mumbai.
 *    Input: [2024, 4, 18, 9, 0, 101] // 18th April 2024, 9:00 AM, Mumbai (locationCode=101)
 *    Usage:
 *      java model.TensorFlowAstroPredictionModel 2024 4 18 9 0 101
 *    The model will process these features and return a prediction score (the higher, the more favorable).
 */
public class TensorFlowAstroPredictionModel {
    public static void main(String[] args) {
        // Accept input from command-line or use defaults
        float[] defaultFeatures = new float[] {1990f, 7f, 15f, 10f, 30f, 101f};
        float[] features = new float[defaultFeatures.length];
        if (args.length == defaultFeatures.length) {
            for (int i = 0; i < args.length; i++) {
                try {
                    features[i] = Float.parseFloat(args[i]);
                } catch (NumberFormatException e) {
                    features[i] = defaultFeatures[i];
                }
            }
        } else {
            features = Arrays.copyOf(defaultFeatures, defaultFeatures.length);
        }

        // Print input features
        System.out.println("Input astro features: " + Arrays.toString(features));

        // Build PredictionRequest
        PredictionRequest.Builder reqBuilder = PredictionRequest.newBuilder();
        for (float f : features) {
            reqBuilder.addFeatures(f);
        }
        PredictionRequest request = reqBuilder.build();

        // Simulate a weighted sum as a more complex prediction
        float[] weights = new float[] {0.5f, 2.0f, 1.5f, 1.0f, 0.8f, 0.3f}; // Example weights

        try (EagerSession session = EagerSession.create()) {
            try (TFloat32 tensor = TFloat32.tensorOf(Shape.of(features.length))) {
                for (int i = 0; i < features.length; i++) {
                    tensor.setFloat(features[i], i);
                }
                float weightedSum = 0f;
                for (int i = 0; i < features.length; i++) {
                    weightedSum += tensor.getFloat(i) * weights[i];
                }
                // In a real model, replace 'weightedSum' with actual prediction logic
                PredictionResponse response = PredictionResponse.newBuilder()
                    .setResult(weightedSum)
                    .build();
                System.out.printf("Astro Prediction (weighted sum) result: %.2f%n", response.getResult());
            }
        }
    }
}
