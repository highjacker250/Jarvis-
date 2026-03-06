package com.jarvis.assistant.face;

import android.content.Context;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.*;

import java.util.List;

public class FaceRecognitionManager {

    private FaceDetector faceDetector;

    public FaceRecognitionManager(Context context) {

        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .enableTracking()
                        .build();

        faceDetector = FaceDetection.getClient(options);
    }

    public void detectFace(InputImage image, FaceDetectionListener listener) {

        faceDetector.process(image)
                .addOnSuccessListener(faces -> {

                    if (faces != null && faces.size() > 0) {

                        Log.d("Jarvis", "Face detected");

                        if (listener != null) {
                            listener.onFaceDetected(faces);
                        }

                    } else {

                        Log.d("Jarvis", "No face detected");

                        if (listener != null) {
                            listener.onNoFaceDetected();
                        }
                    }

                })
                .addOnFailureListener(e -> {

                    Log.e("Jarvis", "Face detection failed");

                    if (listener != null) {
                        listener.onError(e);
                    }

                });
    }

    public interface FaceDetectionListener {

        void onFaceDetected(List<Face> faces);

        void onNoFaceDetected();

        void onError(Exception e);
    }
}