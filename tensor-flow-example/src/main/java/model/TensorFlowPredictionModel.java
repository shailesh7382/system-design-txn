package model;


import org.tensorflow.ndarray.Shape;
import org.tensorflow.types.TFloat32;
import org.tensorflow.EagerSession;

public class TensorFlowPredictionModel {
    public static void main(String[] args) {
        // Example features
        PredictionRequest request = PredictionRequest.newBuilder()
            .addFeatures(1.0f)
            .addFeatures(2.0f)
            .addFeatures(3.0f)
            .build();

        // Use TensorFlow to sum features
        try (EagerSession session = EagerSession.create()) {
            double[] floatFeatures = request.getFeaturesList().stream()
                    .mapToDouble(f -> f)
                    .toArray();
            float[] features = new float[floatFeatures.length];
            for (int i = 0; i < floatFeatures.length; i++) {
                features[i] = (float) floatFeatures[i];
            }


            try (TFloat32 tensor = TFloat32.tensorOf(Shape.of(features.length))) {
                for (int i = 0; i < features.length; i++) {
                    tensor.setFloat(features[i], i);
                }
                float sum = 0f;
                for (int i = 0; i < features.length; i++) {
                    sum += tensor.getFloat(i);
                }
                PredictionResponse response = PredictionResponse.newBuilder()
                    .setResult(sum)
                    .build();
                System.out.println("Prediction result: " + response.getResult());
            }
        }
    }
}

