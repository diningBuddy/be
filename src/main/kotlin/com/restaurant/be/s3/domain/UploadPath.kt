package com.restaurant.be.s3.domain

enum class UploadPath(val folderName: String) {
    USER_PROFILE("user-profile"),
    REVIEW("review"),
    EDIT_INQUIRE("edit-inquire")
    ;
}
