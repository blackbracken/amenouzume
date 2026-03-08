package black.bracken.amenouzume

import android.app.Application
import black.bracken.amenouzume.di.initKoin
import black.bracken.amenouzume.platform.db.DatabaseDriverFactory
import black.bracken.amenouzume.platform.db.LibraryCreator

class AmenouzumeApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    initKoin(DatabaseDriverFactory(applicationContext), LibraryCreator(applicationContext))
  }
}
