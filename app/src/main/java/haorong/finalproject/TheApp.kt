package haorong.finalproject

import android.app.Application
import android.content.Context

/*
* Created by Haorong Li on November 08, 2020
*/

class TheApp: Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object{
        lateinit var context: Context
            private set
    }
}