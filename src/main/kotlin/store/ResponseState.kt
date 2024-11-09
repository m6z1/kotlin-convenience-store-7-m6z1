package store

enum class ResponseState(private val message: String) {
    POSITIVE("Y"),
    NEGATIVE("N");

    companion object {

        fun from(inputMessage: String): ResponseState {
            if (inputMessage == POSITIVE.message) {
                return POSITIVE
            }

            if (inputMessage == NEGATIVE.message) {
                return NEGATIVE
            }

            throw IllegalArgumentException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.")
        }
    }
}