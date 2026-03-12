package black.bracken.amenouzume.uishared

import black.bracken.amenouzume.uishared.navigation.Navigator
import org.koin.dsl.module

val uiSharedModule = module {
  single { Navigator() }
}
