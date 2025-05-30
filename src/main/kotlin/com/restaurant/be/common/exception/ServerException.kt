package com.restaurant.be.common.exception

sealed class ServerException(
    val code: Int,
    override val message: String
) : RuntimeException(message)

data class InvalidTokenException(
    override val message: String = "유효하지 않은 토큰입니다."
) : ServerException(401, message)

data class InvalidPasswordException(
    override val message: String = "패스워드가 일치 하지 않습니다."
) : ServerException(401, message)

data class NotFoundUserEmailException(
    override val message: String = "존재 하지 않는 유저 이메일 입니다."
) : ServerException(400, message)

data class WithdrawalUserException(
    override val message: String = "탈퇴한 유저 입니다."
) : ServerException(400, message)

data class DuplicateUserEmailException(
    override val message: String = "이미 존재 하는 이메일 입니다."
) : ServerException(400, message)

data class DuplicateUserPhoneNumberException(
    override val message: String = "이미 가입된 핸드폰번호 입니다."
) : ServerException(400, message)

data class DuplicateUserNicknameException(
    override val message: String = "이미 존재 하는 닉네임 입니다."
) : ServerException(400, message)

data class SendEmailException(
    override val message: String = "이메일 전송에 실패 했습니다."
) : ServerException(500, message)

data class SkkuEmailException(
    override val message: String = "성균관대 이메일이 아닙니다."
) : ServerException(400, message)

data class InvalidEmailCodeException(
    override val message: String = "인증 코드가 일치 하지 않습니다."
) : ServerException(400, message)

data class InvalidUserResetPasswordStateException(
    override val message: String = "유저가 비밀번호 초기화 상태가 아닙니다."
) : ServerException(400, message)

data class NotEqualTokenException(
    override val message: String = "토큰이 일치 하지 않습니다."
) : ServerException(400, message)

data class NotFoundUserException(
    override val message: String = "존재 하지 않는 유저 입니다."
) : ServerException(400, message)

data class NotFoundRecentSearchException(
    override val message: String = "존재 하지 않는 최근 검색어 입니다."
) : ServerException(400, message)

data class NotFoundReviewException(
    override val message: String = "존재하지 않은 리뷰 입니다."
) : ServerException(400, message)

data class UnAuthorizedUpdateException(
    override val message: String = "해당 게시글을 수정할 권한이 없습니다."
) : ServerException(401, message)

data class UnAuthorizedDeleteException(
    override val message: String = "해당 게시글을 삭제할 권한이 없습니다."
) : ServerException(401, message)

data class UnAuthorizedDeleteRecentSearchException(
    override val message: String = "해당 최근 검색어를 삭제할 권한이 없습니다."
) : ServerException(401, message)

data class DuplicateLikeException(
    override val message: String = "같은 게시글을 두번 좋아요할 수 없습니다."
) : ServerException(400, message)

data class NotFoundLikeException(
    override val message: String = "해당 게시글은 이미 좋아하지 않습니다."
) : ServerException(400, message)

data class InvalidLikeCountException(
    override val message: String = "좋아요가 0보다 작아질 순 없습니다."
) : ServerException(500, message)

data class NotFoundRestaurantException(
    override val message: String = "해당 식당 정보가 존재하지 않습니다."
) : ServerException(404, message)

data class AligoSendSmsException(
    override val message: String = "알리고 문자 전송 실패 했습니다."
) : ServerException(500, message)

data class ExpiredCertificationNumberException(
    override val message: String = "인증번호가 만료되었습니다."
) : ServerException(400, message)

data class InvalidCertificationNumberException(
    override val message: String = "일치하지 않는 인증번호 입니다."
) : ServerException(400, message)

data class KaKaoGetTokenException(
    override val message: String = "카카오 토큰을 가져오는데 실패 했습니다."
) : ServerException(500, message)

data class NotFoundMailTemplateException(
    override val message: String = "메일 템플릿을 가져오는데 실패 했습니다."
) : ServerException(500, message)

data class NotFoundSocialKeyException(
    override val message: String = "소셜 로그인 키가 존재하지 않습니다."
) : ServerException(404, message)

data class DuplicateSocialUserException(
    override val message: String = "이미 존재 하는 소셜 회원 입니다."
) : ServerException(400, message)

data class NotFoundAuthenticationSchoolEmailCodeException(
    override val message: String = "인증 이메일 코드가 존재하지 않습니다."
) : ServerException(404, message)

data class AlreadySchoolEmailAuthenticationException(
    override val message: String = "이미 학교 인증된 회원 입니다."
) : ServerException(400, message)

data class InvalidCategoryException(
    override val message: String = "존재하지 않는 카테고리 입니다."
) : ServerException(400, message)
