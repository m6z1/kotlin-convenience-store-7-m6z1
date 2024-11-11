package store

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import store.utils.ResponseState

class ResponseStateTest {

    @ParameterizedTest
    @CsvSource(
        "Y, POSITIVE",
        "N, NEGATIVE",
    )
    fun `입력값이 유효한 값일 경우 해당하는 응답 상태를 반환한다`(
        inputMessage: String,
        expectedValue: ResponseState,
    ) {
        val result = ResponseState.from(inputMessage)

        assertEquals(ResponseState.valueOf(expectedValue.toString()), result)
    }

    @ParameterizedTest
    @CsvSource(
        "ㅇㅇ",
        "ㄴㄴ",
    )
    fun `입력값이 유효한 값이 아닐 경우 예외를 발생한다`(
        inputMessage: String,
    ) {
        assertThrows<IllegalArgumentException> { ResponseState.from(inputMessage) }
    }
}