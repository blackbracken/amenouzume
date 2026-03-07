package black.bracken.amenouzume.platform.db

import black.bracken.amenouzume.db.AppDatabase

fun createDatabase(driverFactory: DatabaseDriverFactory): AppDatabase {
  val driver = driverFactory.createDriver()
  return AppDatabase(driver = driver)
}
