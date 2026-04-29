package com.example.quizmon.ui.pet
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.quizmon.data.model.Pet
import android.widget.ImageView

class AnimetorActivity(private  var imgPet1: ImageView) {
    private var petFarm = intArrayOf()
    private val handle = Handler(Looper.getMainLooper())
    private var currentFram = 0

    private var runnable = object : Runnable {
        override fun run() {
            if (petFarm.isNotEmpty()) {
                imgPet1.setImageResource(petFarm[currentFram])
                val delayTime = if (currentFram == 0) 3000L else 200L
                currentFram = (currentFram + 1) % petFarm.size
                handle.postDelayed(this, delayTime)
            }
        }
    }
    fun starAnimetor(pet: Pet){
        petFarm = pet.animetor[pet.currentelevel] ?: intArrayOf()
        currentFram = 0
        handle.postDelayed(runnable, 150)
    }
    fun stop(){
        handle.removeCallbacks(runnable)
    }
}