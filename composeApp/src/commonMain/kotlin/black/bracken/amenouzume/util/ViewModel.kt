package black.bracken.amenouzume.util

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.flow.StateFlow

fun <T> ViewModel.moleculeState(body: @Composable () -> T): StateFlow<T> = viewModelScope.launchMolecule(RecompositionMode.Immediate, body = body)
