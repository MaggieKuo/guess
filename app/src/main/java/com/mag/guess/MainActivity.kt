package com.mag.guess

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity;
import android.text.Editable
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            playMedia()
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

//        var audios = R.raw::class.java.fields
//        audios.map { println(it.name) }

/*
        var media: Array<MediaPlayer>
        media = arrayOf(MediaPlayer.create(applicationContext, R.raw.n1),
            MediaPlayer.create(applicationContext, R.raw.n1),
            MediaPlayer.create(applicationContext, R.raw.n3))
        media.forEach { it.start() }
*/
    }

    class SpeechPlayer(val context: Context) : MediaPlayer.OnCompletionListener {
        override fun onCompletion(mp: MediaPlayer?) {
            play()
        }

        val speech = ArrayList<Int>()
        fun queue(n: Any) {
            val medias = when (n) {
                is Int -> numberMedia(n)
                else -> wordMedia(n as String)
            }
        }

        fun play(){
            if (speech.size>0){
                val mediaPlayer = MediaPlayer.create(context, speech.get(0))
                speech.removeAt(0)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(1f)
//                }
                mediaPlayer.setOnCompletionListener(this)
                mediaPlayer.start()
            }
        }

        private fun wordMedia(n: String){
            speech.add(Audios.getInstance(context).get("to")!!)
        }

        private fun numberMedia(n: Int) {
            val audioMap = Audios.getInstance(context)
            when(n){
                in 11..19 -> {
                    speech.add(audioMap.get("n10")!!)
                    speech.add(audioMap.get("n" + (n%10))!!)
                }
                in 20..99 -> {
                    speech.add(audioMap.get("n" + (n/10))!!)
                    speech.add(audioMap.get("n10")!!)
                    if (n%10 > 0){
                        speech.add(audioMap.get("n" + (n%10))!!)
                    }
                }
                in 0..10 -> speech.add(audioMap.get("n" + n)!!)
            }
        }

        private fun getRawId(name: String)= Audios.getInstance(context).get(name)

    }

    class Audios() {
        companion object {
            private var instance = HashMap<String, Int>()
            fun getInstance(context: Context) = if (instance.size > 0) instance!!
            else {
                for (field in R.raw::class.java.fields) {
                    instance.put(field.name, context.resources.getIdentifier(field.name, "raw", context.packageName))
                }
                instance!!
            }
        }
    }


}

