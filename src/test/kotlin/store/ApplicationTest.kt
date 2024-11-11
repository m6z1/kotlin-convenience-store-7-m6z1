package store

import camp.nextstep.edu.missionutils.test.Assertions.assertNowTest
import camp.nextstep.edu.missionutils.test.Assertions.assertSimpleTest
import camp.nextstep.edu.missionutils.test.NsTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ApplicationTest : NsTest() {
    @Test
    fun `파일에 있는 상품 목록 출력`() {
        assertSimpleTest {
            run("[물-1]", "N", "N")
            assertThat(output()).contains(
                "- 콜라 1,000원 10개 탄산2+1",
                "- 콜라 1,000원 10개",
                "- 사이다 1,000원 8개 탄산2+1",
                "- 사이다 1,000원 7개",
                "- 오렌지주스 1,800원 9개 MD추천상품",
                "- 오렌지주스 1,800원 재고 없음",
                "- 탄산수 1,200원 5개 탄산2+1",
                "- 탄산수 1,200원 재고 없음",
                "- 물 500원 10개",
                "- 비타민워터 1,500원 6개",
                "- 감자칩 1,500원 5개 반짝할인",
                "- 감자칩 1,500원 5개",
                "- 초코바 1,200원 5개 MD추천상품",
                "- 초코바 1,200원 5개",
                "- 에너지바 2,000원 5개",
                "- 정식도시락 6,400원 8개",
                "- 컵라면 1,700원 1개 MD추천상품",
                "- 컵라면 1,700원 10개"
            )
        }
    }

    @Test
    fun `여러 개의 일반 상품 구매`() {
        assertSimpleTest {
            run("[비타민워터-3],[물-2],[정식도시락-2]", "N", "N")
            assertThat(output().replace("\\s".toRegex(), "")).contains("내실돈18,300")
        }
    }

    @Test
    fun `기간에 해당하지 않는 프로모션 적용`() {
        assertNowTest({
            run("[감자칩-2]", "N", "N")
            assertThat(output().replace("\\s".toRegex(), "")).contains("내실돈3,000")
        }, LocalDate.of(2024, 2, 1).atStartOfDay())
    }

    @Test
    fun `예외 테스트`() {
        assertSimpleTest {
            runException("[컵라면-12]", "N", "N")
            assertThat(output()).contains("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.")
        }
    }

    @DisplayName("추가 구매 후 최신 재고 테스트")
    @Nested
    inner class LatestProducts {

        @Test
        fun `일반 재고 구매 후 추가 구매 시 최신 재고를 출력한다`() {
            assertSimpleTest {
                run("[물-1]", "N", "Y", "[물-1]", "N", "N")
                assertThat(output()).contains(
                    "- 콜라 1,000원 10개 탄산2+1",
                    "- 콜라 1,000원 10개",
                    "- 사이다 1,000원 8개 탄산2+1",
                    "- 사이다 1,000원 7개",
                    "- 오렌지주스 1,800원 9개 MD추천상품",
                    "- 오렌지주스 1,800원 재고 없음",
                    "- 탄산수 1,200원 5개 탄산2+1",
                    "- 탄산수 1,200원 재고 없음",
                    "- 물 500원 9개",
                    "- 비타민워터 1,500원 6개",
                    "- 감자칩 1,500원 5개 반짝할인",
                    "- 감자칩 1,500원 5개",
                    "- 초코바 1,200원 5개 MD추천상품",
                    "- 초코바 1,200원 5개",
                    "- 에너지바 2,000원 5개",
                    "- 정식도시락 6,400원 8개",
                    "- 컵라면 1,700원 1개 MD추천상품",
                    "- 컵라면 1,700원 10개"
                )
            }
        }

        @Test
        fun `프로모션 재고를 남아있는 재고 초과해서 구매 시 일반 재고에서 차감한 후 최신 재고를 출력한다`() {
            assertSimpleTest {
                run("[사이다-10]", "Y", "N", "Y", "[물-1]", "N", "N")
                assertThat(output()).contains(
                    "- 콜라 1,000원 10개 탄산2+1",
                    "- 콜라 1,000원 10개",
                    "- 사이다 1,000원 재고 없음 탄산2+1",
                    "- 사이다 1,000원 5개",
                    "- 오렌지주스 1,800원 9개 MD추천상품",
                    "- 오렌지주스 1,800원 재고 없음",
                    "- 탄산수 1,200원 5개 탄산2+1",
                    "- 탄산수 1,200원 재고 없음",
                    "- 물 500원 10개",
                    "- 비타민워터 1,500원 6개",
                    "- 감자칩 1,500원 5개 반짝할인",
                    "- 감자칩 1,500원 5개",
                    "- 초코바 1,200원 5개 MD추천상품",
                    "- 초코바 1,200원 5개",
                    "- 에너지바 2,000원 5개",
                    "- 정식도시락 6,400원 8개",
                    "- 컵라면 1,700원 1개 MD추천상품",
                    "- 컵라면 1,700원 10개"
                )
            }
        }
    }

    @Test
    fun `여러 개의 프로모션 상품 및 일반 상품 구매(멤버십 혜택 받지 않을 경우)`() {
        assertSimpleTest {
            run("[콜라-2],[물-2],[비타민워터-5]", "Y", "N", "N")
            assertThat(output().replace("\\s".toRegex(), "")).contains("내실돈10,500")
        }
    }

    @Test
    fun `여러 개의 프로모션 상품 및 일반 상품 구매(멤버십 혜택 받을 경우)`() {
        assertSimpleTest {
            run("[콜라-2],[물-2],[비타민워터-5]", "Y", "Y", "N")
            assertThat(output().replace("\\s".toRegex(), "")).contains("내실돈7,950")
        }
    }

    override fun runMain() {
        main()
    }
}
