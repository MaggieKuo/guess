package com.mag.guess

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*

import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var secret:Int = 0
    var lastNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        fab.setOnClickListener { _view ->
//
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun reset(){
        secret = Random.nextInt(100)
        println(secret)
    }

    fun guess(){
        val num = et_secret.text.toString().toInt()
        when(num){
            secret -> playMedia("corrected")
//            in lastNumber..secret -> playMedia(num, 100)
            in 0..secret -> playMedia(num, minOf(lastNumber, 100))
            in secret..100 -> playMedia(maxOf(0, lastNumber), num)
            else -> playMedia("again")
        }
        lastNumber =  if (num>secret && num>lastNumber) lastNumber else num
    }

    fun playMedia(start: Int, end: Int) {
        var speechPlayer = SpeechPlayer(this)
        speechPlayer.queue(start)
        speechPlayer.queue("to")
        speechPlayer.queue(end)
        speechPlayer.play()
    }

    fun playMedia(media: String){
        var speechPlayer = SpeechPlayer(this)
        speechPlayer.queue(media)
        speechPlayer.play()
    }

    class SpeechPlayer(val context: Context) : MediaPlayer.OnCompletionListener {
        var mp3: MutableMap<String, MediaPlayer>
        var params: PlaybackParams? = null

        init {
            mp3 = Audios.getInstance(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                params = PlaybackParams().apply {
                    speed = 2f
                    pitch = 1f
                    audioFallbackMode = PlaybackParams.AUDIO_FALLBACK_MODE_DEFAULT
                }

            }
        }

        override fun onCompletion(mp: MediaPlayer?) {
            play()
        }

        val speech = mutableListOf<MediaPlayer>()
        fun queue(n: Any) = when (n) {
                is Int -> numberMedia(n)
                else -> addMedia(n as String)
        }

        fun play(){
            if (speech.size>0){
                speech.get(0).start()
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    speech.get(0).params = speech.get(0).params.setSpeed(15f)
//                }
                speech.removeAt(0)
            }
        }

        private fun addMedia(key: String){
            var mp = mp3.get(key)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//               mp!!.playbackParams = params
            }
            mp!!.setOnCompletionListener(this)
            speech.add(mp)
        }

        private fun numberMedia(n: Int) {
            when(n){
                in 0..10 -> addMedia("n" + n)
                in 11..99 -> {
                    if (n/10 > 1) addMedia("n" + (n/10))
                    addMedia("n10")
                    if (n%10 > 0) addMedia("n" + (n%10))
                }
                100 -> {
                    addMedia("n1")
                    addMedia("hundred")
                }
            }
        }
    }


    class Audios() {
        companion object {
            private var instance = mutableMapOf<String, MediaPlayer>()
            fun getInstance(context: Context) = if (instance.size > 0) instance
            else {
                for (field in R.raw::class.java.fields) {
                    instance.put(field.name, MediaPlayer.create(context,
                        context.resources.getIdentifier(field.name, "raw", context.packageName)))
                }
                instance
            }
        }
    }
}

