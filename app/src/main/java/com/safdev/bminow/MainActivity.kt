package com.safdev.bminow

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var cardMale: FrameLayout
    private lateinit var cardFemale: FrameLayout
    private lateinit var etAge: EditText
    private lateinit var etWeight: EditText
    private lateinit var etHeightSingle: EditText
    private lateinit var etHeightFt: EditText
    private lateinit var etHeightIn: EditText
    private lateinit var btnWeightUnit: Button
    private lateinit var btnHeightUnit: Button
    private lateinit var btnCalculate: Button

    private var selectedGender: String? = null
    private var weightUnit = "Kg"
    private var heightUnit = "ft/in"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views
        cardMale = findViewById(R.id.card_male)
        cardFemale = findViewById(R.id.card_female)
        etAge = findViewById(R.id.et_age)
        etWeight = findViewById(R.id.et_weight)
        etHeightSingle = findViewById(R.id.et_height_single)
        etHeightFt = findViewById(R.id.et_height_ft)
        etHeightIn = findViewById(R.id.et_height_in)
        btnWeightUnit = findViewById(R.id.btn_weight_unit)
        btnHeightUnit = findViewById(R.id.btn_height_unit)
        btnCalculate = findViewById(R.id.btn_calculate)

        // Gender selection
        cardMale.setOnClickListener {
            selectedGender = "Male"
            cardMale.setBackgroundColor("#FFBB86FC".toColorInt())
            cardFemale.setBackgroundColor("#FFFFFF".toColorInt())
        }

        cardFemale.setOnClickListener {
            selectedGender = "Female"
            cardFemale.setBackgroundColor("#FFBB86FC".toColorInt())
            cardMale.setBackgroundColor("#FFFFFF".toColorInt())
        }

        // Weight unit selection
        btnWeightUnit.setOnClickListener {
            weightUnit = if (weightUnit == "Kg") "Lb" else "Kg"
            btnWeightUnit.text = weightUnit
        }

        // Height unit toggle
        btnHeightUnit.setOnClickListener {
            heightUnit = when (heightUnit) {
                "ft/in" -> {
                    btnHeightUnit.text = "m"
                    etHeightSingle.hint = "Enter height in meters"
                    etHeightSingle.visibility = View.VISIBLE
                    etHeightFt.visibility = View.GONE
                    etHeightIn.visibility = View.GONE
                    "m"
                }
                "m" -> {
                    btnHeightUnit.text = "cm"
                    etHeightSingle.hint = "Enter height in cm"
                    etHeightSingle.visibility = View.VISIBLE
                    etHeightFt.visibility = View.GONE
                    etHeightIn.visibility = View.GONE
                    "cm"
                }
                else -> {
                    btnHeightUnit.text = "ft/in"
                    etHeightSingle.visibility = View.GONE
                    etHeightFt.visibility = View.VISIBLE
                    etHeightIn.visibility = View.VISIBLE
                    "ft/in"
                }
            }
        }

        // Calculate button
        btnCalculate.setOnClickListener {
            calculateBMI()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun calculateBMI() {
        val ageText = etAge.text.toString()
        val weightText = etWeight.text.toString()

        if (selectedGender == null) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
            return
        }

        if (ageText.isEmpty() || weightText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val age = ageText.toInt()
        if (age < 2 || age > 200) {
            Toast.makeText(this, "Enter valid age (2–200)", Toast.LENGTH_SHORT).show()
            return
        }

        var weight = weightText.toDouble()
        if (weight <= 0) {
            Toast.makeText(this, "Enter valid weight", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert weight to kg if needed
        if (weightUnit == "Lb") weight *= 0.453592 // pounds to kg

        // Get height in meters
        var heightInMeters = 0.0
        when (heightUnit) {
            "m" -> {
                val meters = etHeightSingle.text.toString().toDoubleOrNull() ?: 0.0
                if (meters <= 0) {
                    Toast.makeText(this, "Enter valid height", Toast.LENGTH_SHORT).show()
                    return
                }
                heightInMeters = meters
            }
            "cm" -> {
                val cm = etHeightSingle.text.toString().toDoubleOrNull() ?: 0.0
                if (cm <= 0) {
                    Toast.makeText(this, "Enter valid height", Toast.LENGTH_SHORT).show()
                    return
                }
                heightInMeters = cm / 100.0 // cm → m
            }
            "ft/in" -> {
                val ft = etHeightFt.text.toString().toDoubleOrNull() ?: 0.0
                val inch = etHeightIn.text.toString().toDoubleOrNull() ?: 0.0
                if (ft <= 0 && inch <= 0) {
                    Toast.makeText(this, "Enter valid height", Toast.LENGTH_SHORT).show()
                    return
                }
                heightInMeters = ((ft * 12) + inch) * 0.0254 // ft+in → m
            }
        }

        if (heightInMeters <= 0) {
            Toast.makeText(this, "Enter valid height", Toast.LENGTH_SHORT).show()
            return
        }

        val bmi = weight / (heightInMeters * heightInMeters)

        // Pass data to result page
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("BMI", bmi)
        intent.putExtra("AGE", age)
        intent.putExtra("GENDER", selectedGender)
        startActivity(intent)
    }
}
