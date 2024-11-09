package store

enum class MembershipState(private val membershipMessage: String) {
    APPLICATION("Y"),
    NOT_APPLICATION("N");

    companion object {

        fun from(membershipMessage: String): MembershipState {
            if (membershipMessage == APPLICATION.membershipMessage) {
                return APPLICATION
            }

            if (membershipMessage == NOT_APPLICATION.membershipMessage) {
                return NOT_APPLICATION
            }

            throw IllegalArgumentException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.")
        }
    }
}