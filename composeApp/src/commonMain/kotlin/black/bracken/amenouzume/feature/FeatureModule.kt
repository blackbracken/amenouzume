package black.bracken.amenouzume.feature

import black.bracken.amenouzume.feature.opendatabase.OpenDatabaseViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureModule = module {
    viewModelOf(::OpenDatabaseViewModel)
}
