package lc.fungee.IngrediCheck.ui.screens.feedback

sealed class FeedbackMode {
    data object FeedbackOnly : FeedbackMode()
    data object ImagesOnly : FeedbackMode()
    data object FeedbackAndImages : FeedbackMode()
}
