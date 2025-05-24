package com.example.banglattsspeech

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var inputText: EditText
    private lateinit var voiceSpinner: Spinner
    private lateinit var pitchSlider: SeekBar
    private lateinit var speedSlider: SeekBar
    private lateinit var pitchValue: TextView
    private lateinit var speedValue: TextView
    private lateinit var playButton: Button
    private lateinit var presetContainer: LinearLayout

    private val presetTexts = listOf(
        "সুপ্রভাত! কেমন আছেন?",
        "আপনি এখন কোথায় যাচ্ছেন?",
        "চট্টগ্রামে যান",
        "আমি তোমাকে ভালবাসি"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputText = findViewById(R.id.inputText)
        voiceSpinner = findViewById(R.id.voiceSpinner)
        pitchSlider = findViewById(R.id.pitchSlider)
        speedSlider = findViewById(R.id.speedSlider)
        pitchValue = findViewById(R.id.pitchValue)
        speedValue = findViewById(R.id.speedValue)
        playButton = findViewById(R.id.playButton)
        presetContainer = findViewById(R.id.presetContainer)

        tts = TextToSpeech(this, this)

        pitchSlider.max = 200
        speedSlider.max = 200
        pitchSlider.progress = 100
        speedSlider.progress = 100

        pitchSlider.setOnSeekBarChangeListener(sliderListener(pitchValue))
        speedSlider.setOnSeekBarChangeListener(sliderListener(speedValue))

        playButton.setOnClickListener {
            speakOut(inputText.text.toString())
        }

        presetTexts.forEach { text ->
            val button = Button(this)
            button.text = text
            button.setOnClickListener {
                inputText.setText(text)
                speakOut(text)
            }
            presetContainer.addView(button)
        }
    }

    private fun sliderListener(valueView: TextView): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                valueView.text = (progress / 100.0).toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("bn", "BD")
            val voices = tts.voices
                .filter { it.locale.language == "bn" && !it.isNetworkConnectionRequired }
                .sortedBy { it.name }

            val voiceNames = voices.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, voiceNames)
            voiceSpinner.adapter = adapter

            voiceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val voice = voices[position]
                    tts.voice = voice
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        } else {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakOut(text: String) {
        val pitch = pitchSlider.progress / 100.0f
        val speed = speedSlider.progress / 100.0f
        tts.setPitch(pitch)
        tts.setSpeechRate(speed)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onDestroy() {
        if (tts.isSpeaking) tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}
