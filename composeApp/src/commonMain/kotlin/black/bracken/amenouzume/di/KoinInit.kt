package black.bracken.amenouzume.di

import black.bracken.amenouzume.feature.featureModule
import black.bracken.amenouzume.platform.db.DatabaseDriverFactory
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(driverFactory: DatabaseDriverFactory, config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            databaseModule(driverFactory),
            featureModule,
        )
    }
}
