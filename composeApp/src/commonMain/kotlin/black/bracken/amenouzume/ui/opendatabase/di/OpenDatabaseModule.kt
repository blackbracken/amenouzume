package black.bracken.amenouzume.ui.opendatabase.di

import black.bracken.amenouzume.ui.opendatabase.OpenDatabaseViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val openDatabaseModule = module {
    viewModelOf(::OpenDatabaseViewModel)
}
