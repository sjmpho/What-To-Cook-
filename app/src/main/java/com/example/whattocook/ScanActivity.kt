package com.example.whattocook

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.TextureView
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.whattocook.ml.SsdMobilenetV11Metadata1
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class ScanActivity : AppCompatActivity() {

    lateinit var labels:List<String>
    var colors = listOf<Int>(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED)
    val  paint = Paint()
    lateinit var bitmap: Bitmap
    lateinit var imageProcessor: ImageProcessor
    lateinit var imageView: ImageView
    lateinit var handler: Handler
    lateinit var cameraDevice: CameraDevice
    lateinit var cameraManager: CameraManager
    lateinit var model: SsdMobilenetV11Metadata1

    lateinit var textureView: TextureView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        labels = FileUtil.loadLabels(applicationContext,"labels.txt")
        imageView = findViewById(R.id.imageView)
        imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()

        model = SsdMobilenetV11Metadata1.newInstance(applicationContext)
        val handlerThread = HandlerThread("VideoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        textureView =  findViewById(R.id.textureView)

        textureView.surfaceTextureListener = object: TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                open_camera()
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {

            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                bitmap = textureView.bitmap ?: return

                // Resize and preprocess the image
                val imageProcessor = ImageProcessor.Builder()
                    .add(ResizeOp(256, 256, ResizeOp.ResizeMethod.BILINEAR)) // Resize to 256x256
                    .add(NormalizeOp(0f, 255f)) // Normalize pixel values to [0, 1]
                    .build()

                // Convert Bitmap to TensorImage
                val tensorImage = TensorImage(DataType.FLOAT32)
                tensorImage.load(bitmap)

                // Process the image
                val processedImage = imageProcessor.process(tensorImage)

                // Get the TensorBuffer
                val inputBuffer = processedImage.tensorBuffer

                // Verify input buffer size
                Log.d("ScanActivity", "Input buffer size: ${inputBuffer.buffer.capacity()}")

                // Load the model
                val modelFile = FileUtil.loadMappedFile(applicationContext, "ssd_mobilenet_v1_1_metadata_1.tflite")
                val interpreter = Interpreter(modelFile)

                // Prepare output buffers
                val outputBoxes = TensorBuffer.createFixedSize(intArrayOf(1, 12276, 4), DataType.FLOAT32)
                val outputScores = TensorBuffer.createFixedSize(intArrayOf(1, 12276, 5), DataType.FLOAT32)

                // Run inference
                interpreter.runForMultipleInputsOutputs(arrayOf(inputBuffer.buffer), mapOf(
                    0 to outputBoxes.buffer,
                    1 to outputScores.buffer
                ))

                // Access outputs
                val detectionBoxes = outputBoxes.floatArray
                val detectionScores = outputScores.floatArray

                // Process outputs
                val numDetections = detectionBoxes.size / 4 // Correct calculation
                val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = android.graphics.Canvas(mutable)

                val h = mutable.height
                val w = mutable.width
                paint.textSize = h / 15f
                paint.strokeWidth = h / 85f

                for (i in 0 until numDetections) {
                    // Get the bounding box coordinates
                    val yMin = detectionBoxes[i * 4 + 0] // Top (normalized)
                    val xMin = detectionBoxes[i * 4 + 1] // Left (normalized)
                    val yMax = detectionBoxes[i * 4 + 2] // Bottom (normalized)
                    val xMax = detectionBoxes[i * 4 + 3] // Right (normalized)

                    // Scale the coordinates to the display dimensions
                    val left = xMin * w
                    val top = yMin * h
                    val right = xMax * w
                    val bottom = yMax * h

                    // Get the confidence scores for each class
                    val classScores = detectionScores.sliceArray(i * 5 until (i + 1) * 5)

                    // Find the class with the highest score
                    val maxScore = classScores.maxOrNull() ?: 0f
                    val classId = classScores.indexOfFirst { it == maxScore }

                    // Draw bounding box and label if the score is above a threshold
                    if (maxScore > 0.2) {
                        paint.color = colors[classId % colors.size] // Use modulo to avoid out-of-bounds
                        paint.style = Paint.Style.STROKE
                        canvas.drawRect(
                            RectF(
                                left,  // Scaled left
                                top,   // Scaled top
                                right, // Scaled right
                                bottom // Scaled bottom
                            ), paint
                        )
                        paint.style = Paint.Style.FILL

                        // Check if classId is within bounds
                        if (classId >= 0 && classId < labels.size) {
                            canvas.drawText(
                                "${labels[classId]} $maxScore",
                                left, // Scaled left
                                top,  // Scaled top
                                paint
                            )
                        } else {
                            Log.e("ScanActivity", "Invalid class ID: $classId")
                        }
                    }
                }

                imageView.setImageBitmap(mutable)
            }
        }
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager;





    }

    override fun onDestroy() {
        super.onDestroy()
        model.close()
    }
    @SuppressLint("MissingPermission")
    fun open_camera(){
        cameraManager.openCamera(cameraManager.cameraIdList[0],object:CameraDevice.StateCallback(){
            override fun onOpened(p0: CameraDevice) {

                cameraDevice = p0
                var surfaceTexture = textureView.surfaceTexture
                var surface = android.view.Surface(surfaceTexture)

                var captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequest.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface),object: CameraCaptureSession.StateCallback(){
                    override fun onConfigured(p0: CameraCaptureSession) {
                        p0.setRepeatingRequest(captureRequest.build(),null,null)
                    }

                    override fun onConfigureFailed(p0: CameraCaptureSession) {

                    }


                },handler)
            }

            override fun onDisconnected(p0: CameraDevice) {

            }

            override fun onError(p0: CameraDevice, p1: Int) {

            }


        },handler)

    }

}