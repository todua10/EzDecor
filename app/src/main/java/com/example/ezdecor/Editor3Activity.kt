package com.example.ezdecor
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.opengl.GLSurfaceView
import android.os.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.*
import java.math.RoundingMode
import java.text.*
import java.util.*
import android.Manifest
class Editor3Activity : AppCompatActivity() {
    private val readMediaImagePermissionRequestCode = 1001
    private val cameraPermissionRequestCode = 1002
    private lateinit var renderer: MyRenderer
    private lateinit var room3D: Room3D
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var prevButton: Button
    private lateinit var addWPButton: Button
    private lateinit var addDWButton: Button
    private lateinit var calcMaterialButton: Button
    private val doorWindowList: ArrayList<DoorWindow> = ArrayList()
    private var triangleForm = false
    private var sideC: Float = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor3)
        val sideA = this.intent.getFloatExtra("SideA", 0f)
        val sideB = this.intent.getFloatExtra("SideB", 0f)
        val heightRoom = this.intent.getFloatExtra("HeightR", 0f)
        triangleForm = this.intent.getBooleanExtra("triangleForm", false)
        if(triangleForm) {
            sideC = this.intent.getFloatExtra("SideC", 0f)
        }
        room3D = Room3D(sideA, sideB, sideC, heightRoom, triangleForm)
        if (!supportES2()) {
            Toast.makeText(this, "OpenGl ES 2.0 не поддерживается!", Toast.LENGTH_LONG)
                .show()
            finish()
            return
        }
        renderer = MyRenderer(this, room3D)
        glSurfaceView = findViewById(R.id.glSurfaceView)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(renderer)
        prevButton = findViewById(R.id.prev_button)
        addWPButton = findViewById(R.id.addWP_button)
        addDWButton = findViewById(R.id.addDW_button)
        calcMaterialButton = findViewById(R.id.calcMaterial_button)

        prevButton.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }
        addWPButton.setOnClickListener {
            showImageSourceDialog()
        }
        addDWButton.setOnClickListener {
            showAddWindowDoorDialog()
        }
        calcMaterialButton.setOnClickListener {
            calculateMaterialCost()
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("По умолчанию", "Выбрать из галереи", "Сфотографировать")
        AlertDialog.Builder(this)
            .setTitle("Выберите источник изображения")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> setDefaultWP()
                    1 -> chooseImageFromGallery()
                    2 -> captureImage()
                }
            }
            .show()
    }
    private fun showAddWindowDoorDialog() {
        val dialogLayout = layoutInflater.inflate(R.layout.add_window_door_dialog, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Добавить окно или дверь")
            .setView(dialogLayout)
            .setPositiveButton("Добавить") { dialog, _ ->
                val typeSpinner = dialogLayout.findViewById<Spinner>(R.id.typeSpinner)
                val lengthEditText = dialogLayout.findViewById<EditText>(R.id.lengthEditText)
                val widthEditText = dialogLayout.findViewById<EditText>(R.id.widthEditText)
                val type = typeSpinner.selectedItem.toString()
                val length = lengthEditText.text.toString().toFloatOrNull()
                val width = widthEditText.text.toString().toFloatOrNull()
                if (length != null && width != null) {
                    saveWindowDoorData(type, length, width)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Пожалуйста, заполните все поля",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = dialogBuilder.create()
        dialog.show()
    }
    private fun saveWindowDoorData(type: String, length: Float, width: Float) {
        val doorWindow = DoorWindow(type, length, width)
        doorWindowList.add(doorWindow)
        room3D.setAreaWalls(length, width)
        Toast.makeText(
            this,
            "Добавлен элемент: Тип: ${doorWindow.getType()}, " +
                    "Длина: ${doorWindow.getLength()}," +
                    " Ширина: ${doorWindow.getWidth()}",
            Toast.LENGTH_SHORT
        ).show()
    }
    private fun calculateMaterialCost() {
        val dialogLayout = layoutInflater.inflate(R.layout.material_cost_dialog, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogLayout)
            .setTitle("Расчет стоимости материала")
        val priceEditText = dialogLayout.findViewById<EditText>(R.id.priceEditText)
        val totalCostTextView = dialogLayout.findViewById<TextView>(R.id.totalCostTextView)
        val calculateButton = dialogLayout.findViewById<Button>(R.id.calculateButton)
        val cancelButton = dialogLayout.findViewById<Button>(R.id.cancelButton)
        val totalAreaTextView = dialogLayout.findViewById<TextView>(R.id.totalAreaTextView)
        val countOfRolls = dialogLayout.findViewById<TextView>(R.id.count_of_rolls)
        val lengthEditText = dialogLayout.findViewById<TextView>(R.id.length_of_wp_edit)
        val widthEditText = dialogLayout.findViewById<TextView>(R.id.width_of_wp_edit)
        val dialog = dialogBuilder.create()
        calculateButton.setOnClickListener {
            val price = priceEditText.text.toString().toFloatOrNull()
            val length = lengthEditText.text.toString().toFloatOrNull()
            val width = widthEditText.text.toString().toFloatOrNull()
            if ((price != null) && (length != null) && (width != null)) {
                val countRolls = room3D.getAreaWalls()/(length*width)
                val df = DecimalFormat("#")
                df.roundingMode = RoundingMode.UP
                val countRollsInt = df.format(countRolls).toInt()
                val totalCost = price * countRollsInt
                totalAreaTextView.text = getString(R.string.total_square_e3a, room3D.getAreaWalls())
                countOfRolls.text = getString(R.string.count_of_rolls_e3a,countRollsInt)
                totalCostTextView.text = getString(R.string.total_cost_e3a, totalCost)
            } else {
                totalCostTextView.text = "Введите корректную цену, длину или ширину"
            }
        }
        cancelButton.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun supportES2(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        return configurationInfo.reqGlEsVersion >= 0x20000
    }
    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }
    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }
    private fun setDefaultWP() {
        if (renderer.getResourceID() == R.drawable.wallpapers) {
            return
        }else{
            glSurfaceView.queueEvent {
                renderer.updateWallTexture(R.drawable.wallpapers)
            }
            glSurfaceView.requestRender()
        }
    }
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        imageUri?.let {
            val inputStream = contentResolver.openInputStream(imageUri)
            val options = BitmapFactory.Options()
            options.inScaled = false
            val bitmap = BitmapFactory.decodeStream(inputStream, null, options)

            glSurfaceView.queueEvent {
                renderer.updateWallTextureBitmap(bitmap!!)
            }
            glSurfaceView.requestRender()

            inputStream?.close()
        }
    }
    private fun chooseImageFromGallery() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), readMediaImagePermissionRequestCode)
            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), readMediaImagePermissionRequestCode)
            }
            galleryLauncher.launch("image/*")
        } else {
            galleryLauncher.launch("image/*")
        }
    }
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { result ->
            if (result != null){
                glSurfaceView.queueEvent {
                    renderer.updateWallTextureBitmap(result)
                }
                glSurfaceView.requestRender()
            }
            else{
                return@registerForActivityResult
            }
    }
    private fun captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
            cameraLauncher.launch(null)
        } else {
            cameraLauncher.launch(null)
        }
    }
}