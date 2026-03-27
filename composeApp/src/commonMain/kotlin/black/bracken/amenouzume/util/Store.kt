package black.bracken.amenouzume.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreReadResponse

fun <T> Flow<StoreReadResponse<T>>.toLoadable(): Flow<Loadable<T>> = mapNotNull { response ->
  when (response) {
    is StoreReadResponse.Loading -> Loadable.Loading
    is StoreReadResponse.Data -> Loadable.Loaded(response.value)
    is StoreReadResponse.Error.Exception -> {
      val e = response.error
      Loadable.Failed(if (e is Exception) e else RuntimeException(e))
    }
    is StoreReadResponse.Error.Message -> Loadable.Failed(Exception(response.message))
    is StoreReadResponse.Error.Custom<*> -> Loadable.Failed(Exception(response.error.toString()))
    is StoreReadResponse.Initial, is StoreReadResponse.NoNewData -> null
  }
}

fun <Key : Any, Input : Any, Output : Any> StoreBuilder.Companion.from(
  fetcher: (Key) -> Input,
  reader: (Key) -> Flow<Output>,
) = StoreBuilder.from(
  fetcher = Fetcher.of { key: Key -> fetcher(key) },
  sourceOfTruth = SourceOfTruth.of(
    reader = { key: Key -> reader(key) },
    writer = { _: Key, _: Input -> },
  ),
)
