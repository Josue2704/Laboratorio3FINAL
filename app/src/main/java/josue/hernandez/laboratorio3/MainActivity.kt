package josue.hernandez.laboratorio3

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var photoBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        val buttonTakePhoto: Button = findViewById(R.id.buttonTakePhoto)
        val buttonSavePhoto: Button = findViewById(R.id.buttonSavePhoto)
        val buttonSharePhoto: Button = findViewById(R.id.buttonSharePhoto)
        val buttonRetakePhoto: Button = findViewById(R.id.buttonRetakePhoto)

        buttonTakePhoto.setOnClickListener { checkPermissionsAndOpenCamera() }
        buttonSavePhoto.setOnClickListener { savePhotoToGallery() }
        buttonSharePhoto.setOnClickListener { sharePhoto() }
        buttonRetakePhoto.setOnClickListener { photoBitmap = null; imageView.setImageBitmap(null) }
    }

    private fun checkPermissionsAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            photoBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(photoBitmap)
        }
    }

    private fun savePhotoToGallery() {
        if (photoBitmap == null) {
            Toast.makeText(this, "No hay foto para guardar", Toast.LENGTH_SHORT).show()
            return
        }
        val filename = "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename)
        FileOutputStream(file).use { out ->
            photoBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
            Toast.makeText(this, "Foto guardada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sharePhoto() {
        if (photoBitmap == null) {
            Toast.makeText(this, "No hay foto para compartir", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/jpeg"
        val uri = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, photoBitmap, "Compartir Imagen", null))
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "Compartir usando"))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }
}
