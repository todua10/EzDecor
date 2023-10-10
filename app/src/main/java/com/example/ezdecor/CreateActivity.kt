package com.example.ezdecor
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
class CreateActivity : AppCompatActivity() {
    private lateinit var prevButton: Button
    private lateinit var rectangleButton: Button
    private lateinit var triangleButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        prevButton = findViewById(R.id.prev_button)
        rectangleButton = findViewById(R.id.rectangle_button)
        triangleButton = findViewById(R.id.triangle_button)
        prevButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        rectangleButton.setOnClickListener {
            createDialogInputRectangle()
        }
        triangleButton.setOnClickListener {
            createDialogInputTriangle()
        }
    }
    private fun createDialogInputTriangle(){
        val dialogInput = AlertDialog.Builder(this)
        val inflater = layoutInflater
        dialogInput.setTitle(R.string.input_param_title)
        val dialogLayout = inflater.inflate(R.layout.input_param, null)
        val sideA  = dialogLayout.findViewById<EditText>(R.id.sideA)
        val sideB  = dialogLayout.findViewById<EditText>(R.id.sideB)
        val sideC  = dialogLayout.findViewById<EditText>(R.id.sideC)
        val heightRoom  = dialogLayout.findViewById<EditText>(R.id.heightRoom)
        dialogInput.setView(dialogLayout)
        dialogInput.setNegativeButton(R.string.prev_button){ dialog, _ ->
            dialog.dismiss()
        }
        dialogInput.setPositiveButton(R.string.next_button) { _, _ ->
            val a = sideA.text.toString().toFloatOrNull()
            val b = sideB.text.toString().toFloatOrNull()
            val c = sideC.text.toString().toFloatOrNull()
            val h = heightRoom.text.toString().toFloatOrNull()
            if (a != null && b != null && c != null && h != null && a > 0f && b > 0f && c > 0f && h > 0f && isTriangleValid(a, b, c)) {
                val intent = Intent(this, Editor3Activity::class.java)
                intent.putExtra("SideA", a)
                intent.putExtra("SideB", b)
                intent.putExtra("SideC", c)
                intent.putExtra("HeightR", h)
                intent.putExtra("triangleForm", true)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Такая фигура не существует", Toast.LENGTH_SHORT).show()
            }
        }
        dialogInput.show()
    }
    private fun isTriangleValid(a: Float, b: Float, c: Float): Boolean {
        return (a + b > c) && (b + c > a) && (c + a > b)
    }
    private fun createDialogInputRectangle(){
        val dialogInput = AlertDialog.Builder(this)
        val inflater = layoutInflater
        dialogInput.setTitle(R.string.input_param_title)
        val dialogLayout = inflater.inflate(R.layout.input_param, null)
        val sideA  = dialogLayout.findViewById<EditText>(R.id.sideA)
        val sideB  = dialogLayout.findViewById<EditText>(R.id.sideB)
        val heightRoom  = dialogLayout.findViewById<EditText>(R.id.heightRoom)
        val sideCText = dialogLayout.findViewById<TextView>(R.id.sideCText)
        val sideC = dialogLayout.findViewById<EditText>(R.id.sideC)
        sideCText.visibility = View.GONE
        sideC.visibility = View.GONE
        dialogInput.setView(dialogLayout)
        dialogInput.setNegativeButton(R.string.prev_button){ dialog, _ ->
            dialog.dismiss()
        }
        dialogInput.setPositiveButton(R.string.next_button){ _, _ ->
            val a = sideA.text.toString().toFloatOrNull()
            val b = sideB.text.toString().toFloatOrNull()
            val h = heightRoom.text.toString().toFloatOrNull()
            if (a != null && b != null && h != null && a > 0f && b > 0f && h > 0f ) {
                val intent = Intent(this, Editor3Activity::class.java)
                intent.putExtra("SideA", a)
                intent.putExtra("SideB", b)
                intent.putExtra("HeightR", h)
                intent.putExtra("triangleForm", false)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Такая фигура не существует", Toast.LENGTH_SHORT).show()
            }
        }
        dialogInput.show()
    }
}