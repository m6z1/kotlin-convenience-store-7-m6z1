package store.promotion

enum class PromotionState(private val title: String) {
    NONE("해당 없음"),
    NOT_ENOUGH_STOCK("프로모션 재고 없음"),
    ELIGIBLE_BENEFIT("더 가져오면 프로모션 적용 가능"),
    AVAILABLE_BENEFIT("프로모션 적용 가능"),
}