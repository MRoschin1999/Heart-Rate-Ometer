package net.kibotu.heartrateometer

import Config
import Observation
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.heartrateometer.app.R
import net.kibotu.heartrateometer.app.SecondActivity
import net.kibotu.kalmanrx.jama.Matrix
import net.kibotu.kalmanrx.jkalman.JKalman
import piece_drop

class MainActivity : AppCompatActivity() {

    var subscription: CompositeDisposable? = null
    var bpm:Int? = 60
    var difficulty:Int? = 1
    var age:Int? = 25
    var weight:Int? = 65
    var height:Int? = 170

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        var config: Config = Config(6, 7, 4)
        var observation: Observation = Observation(config)
        var agent: Agent = Agent(config);
        var count_1 = 0
        var count_2 = 0
        age = intent.getIntExtra("age", 25)
        weight = intent.getIntExtra("weight", 65)
        height = intent.getIntExtra("height", 170)
        fun resetTable() {
            observation = Observation(config)
            for (i in 0 until 6) {
                for (j in 0 until 7) {
                    var buttonID: String = "button_" + i + j
                    var resID: Int = resources.getIdentifier(buttonID, "id", packageName)
                    var a = findViewById<Button>(resID)
                    a.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_btn));
                }
            }
        }

        fun resetGame() {
            count_1 = 0
            count_2 = 0
            var text:TextView = findViewById(R.id.count_1)
            text.setText(count_1.toString())
            text = findViewById(R.id.count_2)
            text.setText(count_2.toString())
            observation = Observation(config)
            for (i in 0 until 6) {
                for (j in 0 until 7) {
                    var buttonID: String = "button_" + i + j
                    var resID: Int = resources.getIdentifier(buttonID, "id", packageName)
                    var a = findViewById<Button>(resID)
                    a.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_btn));
                }
            }
        }

        val toast_user = Toast.makeText(
                applicationContext,
                "Вы выиграли!",
                Toast.LENGTH_SHORT
        )
        val toast_opponent = Toast.makeText(
                applicationContext,
                "Вы проиграли",
                Toast.LENGTH_SHORT
        )
        val wrong = Toast.makeText(
                applicationContext,
                "Смените колонку для хода",
                Toast.LENGTH_SHORT
        )
        for (i in 0 until 6) {
            for (j in 0 until 7) {
                var res: piece_drop?
                var buttonID: String = "button_" + i + j
                var resID: Int = resources.getIdentifier(buttonID, "id", packageName)
                var a = findViewById<Button>(resID)
                a.setOnClickListener {
                    res = observation.nextMove(config, j)

                    if (res != null) {
                        buttonID = "button_" + res!!.row + res!!.col
                        resID = resources.getIdentifier(buttonID, "id", packageName)
                        a = findViewById(resID)
                        if (res != null) {
                            if (res!!.mark == 1)
                                a.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_btn));
                            if (res!!.mark == 2)
                                a.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_btn));
                            if (res!!.win) {
                                toast_user.show()
                                count_1+=1
                                var text: TextView = findViewById(R.id.count_1)
                                text.setText(count_1.toString())
                                Handler().postDelayed(Runnable { resetTable() }, 2000)
                            }
                        }
                        res = observation.nextMove(config, agent.calculate_the_move(bpmToDifficulty(), observation))
                        if (res != null) {
                            buttonID = "button_" + res!!.row + res!!.col
                        }
                        resID = resources.getIdentifier(buttonID, "id", packageName)
                        a = findViewById(resID)
                        if (res != null) {
                            if (res!!.mark == 1)
                                a.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_btn));
                            if (res!!.mark == 2)
                                a.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_btn));
                            if (res!!.win) {
                                toast_opponent.show()
                                count_2+=1
                                var text: TextView = findViewById(R.id.count_2)
                                text.setText(count_2.toString())
                                Handler().postDelayed(Runnable { resetTable() }, 2000)
                            }
                        }
                    } else {
                        wrong.show()
                    }
                }
            }
            var a = findViewById<Button>(resources.getIdentifier("reset", "id", packageName))
            a.setOnClickListener {
                resetGame()
            }

            var physicalParams = findViewById<Button>(resources.getIdentifier("physical_param", "id", packageName))
            physicalParams.setOnClickListener {
                val intent2 = Intent(this, SecondActivity::class.java)
                startActivity(intent2)
            }
        }
    }

    private fun startWithPermissionCheck() {
        if (!hasPermission(Manifest.permission.CAMERA)) {
            checkPermissions(REQUEST_CAMERA_PERMISSION, Manifest.permission.CAMERA)
            return
        }

        val kalman = JKalman(2, 1)

        // measurement [x]
        val m = Matrix(1, 1)

        // transitions for x, dx
        val tr = arrayOf(doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0))
        kalman.transition_matrix = Matrix(tr)

        // 1s somewhere?
        kalman.error_cov_post = kalman.error_cov_post.identity()


        val bpmUpdates = HeartRateOmeter()
                .withAverageAfterSeconds(3)
                .setFingerDetectionListener(this::onFingerChange)
                .bpmUpdates(preview)
                .subscribe({

                    if (it.value == 0)
                        return@subscribe

                    m.set(0, 0, it.value.toDouble())

                    // state [x, dx]
                    val s = kalman.Predict()

                    // corrected state [x, dx]
                    val c = kalman.Correct(m)

                    val bpm = it.copy(value = c.get(0, 0).toInt())
                    Log.v("HeartRateOmeter", "[onBpm] ${it.value} => ${bpm.value}")
                    onBpm(bpm)
                }, Throwable::printStackTrace)

        subscription?.add(bpmUpdates)
    }

    @SuppressLint("SetTextI18n")
    private fun onBpm(bpm: HeartRateOmeter.Bpm) {
        // Log.v("HeartRateOmeter", "[onBpm] $bpm")
        this.bpm = bpm.value
        label.text = bpm.value.toString()
        difficulty_label.text = difficulty.toString()
    }

    private fun onFingerChange(fingerDetected: Boolean){
         finger.text = "$fingerDetected"
    }

// region lifecycle

    override fun onResume() {
        super.onResume()

        dispose()
        subscription = CompositeDisposable()

        startWithPermissionCheck()
    }

    override fun onPause() {
        dispose()
        super.onPause()
    }

    private fun dispose() {
        if (subscription?.isDisposed == false)
            subscription?.dispose()
    }

// endregion

// region permission

    companion object {
        private val REQUEST_CAMERA_PERMISSION = 123
    }

    private fun checkPermissions(callbackId: Int, vararg permissionsId: String) {
        when {
            !hasPermission(*permissionsId) -> try {
                ActivityCompat.requestPermissions(this, permissionsId, callbackId)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun hasPermission(vararg permissionsId: String): Boolean {
        var hasPermission = true

        permissionsId.forEach { permission ->
            hasPermission = hasPermission
                    && ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }

        return hasPermission
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startWithPermissionCheck()
                }
            }
        }
    }

// endregion

    private fun bpmToDifficulty() :Int? {
        var am: Double = 2.1
        if(bpm != null)
            am = 0.0011* bpm!! + 0.014*125 + 0.008*80 + 0.009*weight!! - 0.009* height!! + 0.014*age!!
            when {
                am <= 2 -> difficulty = 2
                am > 2 -> difficulty = 1
                else -> difficulty = 1
            }
        return difficulty
    }
}