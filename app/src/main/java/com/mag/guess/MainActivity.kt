package com.mag.guess

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            playMedia()
        }
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

    fun playMedia() {

        var speechPlayer = SpeechPlayer(this)
        speechPlayer.queue(11)
        speechPlayer.queue("to")
        speechPlayer.queue(87)
        speechPlayer.play()

    }

    class SpeechPlayer(val context: Context) : MediaPlayer.OnCompletionListener {
        lateinit var mp3: MutableMap<String, MediaPlayer>
        init {
            mp3 = Audios.getInstance(context)
        }

        override fun onCompletion(mp: MediaPlayer?) {
            play()
        }

        val speech = mutableListOf<MediaPlayer>()
        fun queue(n: Any) {
            val medias = when (n) {
                is Int -> numberMedia(n)
                else -> addMedia(n as String)
            }
        }

        fun play(){
            if (speech.size>0){
                speech.get(0).start()
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    speech.get(0).playbackParams = speech.get(0).playbackParams.setSpeed(15f)
//                }
                speech.removeAt(0)
            }
        }

        private fun addMedia(key: String){
            var mp = mp3.get(key)
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
            }
        }


    }


    class Audios() {
        companion object {
            private var instance = mutableMapOf<String, MediaPlayer>()
            fun getInstance(context: Context) = if (instance.size > 0) instance!!
            else {
                for (field in R.raw::class.java.fields) {
                    instance.put(field.name, MediaPlayer.create(context,
                        context.resources.getIdentifier(field.name, "raw", context.packageName)))
                }
                instance!!
            }


        }
    }


}

