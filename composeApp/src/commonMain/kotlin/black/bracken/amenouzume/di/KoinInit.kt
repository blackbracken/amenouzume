package black.bracken.amenouzume.di

import black.bracken.amenouzume.db.DatabaseDriverFactory
import black.bracken.amenouzume.ui.opendatabase.di.openDatabaseModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(driverFactory: DatabaseDriverFactory, config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            databaseModule(driverFactory),
            openDatabaseModule,
        )
    }
}
