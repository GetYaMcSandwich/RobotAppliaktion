package com.robot.robotappliaktion.util

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.conversation.BodyLanguageOption
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import java.lang.RuntimeException


class RobotHelper {

    private var mediaPlayer: MediaPlayer? = null

    //TODO: Put your robot related methods here like chat or listen...


    fun say(qiContext: QiContext, content: String, withBodyLanguage: Boolean) {
        Log.d(TAG, "qi: $qiContext")

        val sayText = "\\rspd=90\\ $content" // say text with sample 90% of speed
        Log.d(TAG, "Say text: $sayText")

        val phrase = Phrase(sayText)
        val sayBuilder = SayBuilder.with(qiContext)
            .withPhrase(phrase)
            .withLocale(getQiLocale(qiContext.getLocaleConfiguration()))

        val say: Say?
        say = if (withBodyLanguage)
            sayBuilder.build()
        else
            sayBuilder.withBodyLanguageOption(BodyLanguageOption.DISABLED).build()

        try {
            say.run()
        } catch (ignored: RuntimeException) {
            Log.d(TAG, "Say failed")
        }
    }

    fun sayAsync(qiContext: QiContext, content: String): Future<*>? {
        return try {
            val sayText = "\\rspd=90\\$content"
            Log.d(TAG, "SayAsync text: $sayText")

            val phrase = Phrase(sayText)
            val say = SayBuilder.with(qiContext)
                .withPhrase(phrase)
                .withLocale(getQiLocale(qiContext.getLocaleConfiguration()))
                .build()

            say.async().run()
        } catch (ignored: Exception) {
            Log.d(TAG, "SayAsync failed")
            null
        }
    }

    private fun buildAnimate(qiContext: QiContext, resource: Int): Animate? {
        return try {
            val animation = AnimationBuilder.with(qiContext)
                .withResources(resource)
                .build()

            return AnimateBuilder.with(qiContext)
                .withAnimation(animation)
                .build()
        } catch (ignored: java.lang.Exception) {
            Log.d(TAG, "buildAnimate failed")

            null
        }
    }

    fun animate(qiContext: QiContext, resource: Int) {
        val animate = buildAnimate(qiContext, resource)

        try {
            animate?.run()
        } catch (ignored: RuntimeException) {
            Log.d(TAG, "Animate failed")
        }
    }

    fun animateAsync(qiContext: QiContext, resource: Int): Future<*>? {
        val animate = buildAnimate(qiContext, resource)

        return try {
            animate?.async()?.run()
        } catch (ignored: RuntimeException) {
            Log.d(TAG, "animateAsync failed")

            null
        }
    }

    fun animateWithSoundAsync(
        qiContext: QiContext,
        context: Context,
        resource: Int,
        sound: Int
    ): Future<*>? {
        val animate = buildAnimate(qiContext, resource)

        animate?.addOnStartedListener {
            createMediaPlayer(context, sound)
        }

        return try {
            animate?.async()?.run()
        } catch (ignored: RuntimeException) {
            Log.d(TAG, "animateWithSoundAsync failed")

            null
        }
    }

    private fun createMediaPlayer(context: Context, sound: Int) {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }

        mediaPlayer = context.createMediaPlayer(sound)
        mediaPlayer?.setVolume(1.0f, 1.0f)
        mediaPlayer?.start()

        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.stop()
            mediaPlayer?.release()

            mediaPlayer = null
        }
    }

    companion object {
        private const val TAG = "RobotHelper"
    }
}