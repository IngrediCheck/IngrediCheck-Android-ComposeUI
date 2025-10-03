package lc.fungee.IngrediCheck.model.utils

object AutoScanGate {
    @Volatile
    var openedOnceInProcess: Boolean = false
}