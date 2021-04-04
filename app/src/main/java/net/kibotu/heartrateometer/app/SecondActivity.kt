package net.kibotu.heartrateometer.app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import net.kibotu.heartrateometer.MainActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        var weight = findViewById(R.id.editTextNumber) as EditText
        var age = findViewById(R.id.editTextNumber2) as EditText
        var height = findViewById(R.id.editTextNumber3) as EditText
        //var btn_reset = findViewById(R.id.button) as Button


        var a = findViewById<Button>(resources.getIdentifier("button", "id", packageName))
        a.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("age", age.text.toString().toInt())
            intent.putExtra("weight", weight.text.toString().toInt())
            intent.putExtra("height", height.text.toString().toInt())
            startActivity(intent)
        }
    }


}