package black.bracken.amenouzume

import android.app.Application
import black.bracken.amenouzume.db.DatabaseDriverFactory
import black.bracken.amenouzume.di.initKoin

class AmenouzumeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(DatabaseDriverFactory(applicationContext))
    }
}
