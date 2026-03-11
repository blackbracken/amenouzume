package black.bracken.amenouzume.di

import black.bracken.amenouzume.feature.featureModule
import black.bracken.amenouzume.platform.PlatformEnvironment
import black.bracken.amenouzume.platform.platformModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(
  platformEnv: PlatformEnvironment,
  config: KoinAppDeclaration? = null,
) {
  startKoin {
    config?.invoke(this)
    modules(
      platformModule(platformEnv),
      featureModule,
    )
  }
}
