package black.bracken.amenouzume.di

import black.bracken.amenouzume.platform.db.DatabaseDriverFactory
import black.bracken.amenouzume.platform.db.createDatabase
import org.koin.dsl.module

fun databaseModule(driverFactory: DatabaseDriverFactory) = module {
    single { driverFactory }
    single { createDatabase(get()) }
}
