package com.example.whattocook.ContainedFragments

import android.Manifest
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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresPermission
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Canvas
import com.example.whattocook.R
import com.example.whattocook.ml.SsdMobilenetV11Metadata1
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp


class ScanFragment : Fragment() {
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
    lateinit var model:SsdMobilenetV11Metadata1

lateinit var textureView: TextureView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_scan, container, false)
        labels = FileUtil.loadLabels(requireContext(),"labels.txt")
        imageView = view.findViewById(R.id.imageView)
        imageProcessor = ImageProcessor.Builder().add(ResizeOp(300,300,ResizeOp.ResizeMethod.BILINEAR)).build()
        model = SsdMobilenetV11Metadata1.newInstance(requireContext())
        val handlerThread = HandlerThread("VideoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        textureView = view.findViewById(R.id.textureView)
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
                bitmap =textureView.bitmap!!
                var image = TensorImage.fromBitmap(bitmap)
                image = imageProcessor.process(image)


// Creates inputs for reference.
// Runs model inference and gets result.
                val outputs = model.process(image)
                val locations = outputs.locationsAsTensorBuffer.floatArray
                val classes = outputs.classesAsTensorBuffer.floatArray
                val scores = outputs.scoresAsTensorBuffer.floatArray
                val numberOfDetections = outputs.numberOfDetectionsAsTensorBuffer.floatArray

                var mutable = bitmap.copy(Bitmap.Config.ARGB_8888,true)
                val canvas = android.graphics.Canvas(mutable)

                val h = mutable.height
                val w = mutable.width
                paint.textSize = h/15f
                paint.strokeWidth = h/85f
                var x = 0
                scores.forEachIndexed { index, fl ->
                    x = index
                    x *= 4
                    if(fl > 0.5){
                        paint.setColor(colors.get(index))
                        paint.style = Paint.Style.STROKE
                        canvas.drawRect(RectF(locations.get(x+1)*w, locations.get(x)*h, locations.get(x+3)*w, locations.get(x+2)*h), paint)
                        paint.style = Paint.Style.FILL
                        canvas.drawText(labels.get(classes.get(index).toInt())+" "+fl.toString(), locations.get(x+1)*w, locations.get(x)*h, paint)
                    }
                }

                imageView.setImageBitmap(mutable)
// Releases model resources if no longer used.


            }

        }
    cameraManager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager;




        return view
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