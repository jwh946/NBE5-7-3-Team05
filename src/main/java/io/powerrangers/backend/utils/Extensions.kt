package io.powerrangers.backend.utils


import io.powerrangers.backend.entity.Comment
import io.powerrangers.backend.dto.*
import io.powerrangers.backend.entity.Task
import io.powerrangers.backend.entity.User
import io.powerrangers.backend.service.S3Service

fun User.toProfileResponseDto(s3Service: S3Service): UserGetProfileResponseDto {
    val profileImageUrl = this.profileImage?.let { imageValue ->
        if (imageValue.startsWith("http")) {
            imageValue
        } else {
            s3Service.generatePresignedUrl(imageValue, 60)
        }
    }

    return UserGetProfileResponseDto(
        userId = this.id!!,
        nickname = this.nickname,
        intro = this.intro,
        profileImage = profileImageUrl
    )
}

fun User.toUserFollowResponseDto(): UserFollowResponseDto {
    return UserFollowResponseDto(
        id = this.id!!,
        nickname = this.nickname,
        intro = this.intro,
        profileImage = this.profileImage
    )
}

fun User.toUserDetails(): UserDetails {
    return UserDetails(
        id = this.id,
        nickname = this.nickname,
        email = this.email,
        role = this.role,
        providerId = this.providerId,
        profileImage = this.profileImage
    )
}

fun Task.toTaskImageResponseDto(presignedUrl: String?): TaskImageResponseDto {
    return TaskImageResponseDto(
        taskId = this.id!!,
        imageUrl = presignedUrl,
        status = this.status,
        dueDate = this.dueDate
    )
}

fun Task.toTaskResponseDto(s3Service: S3Service): TaskResponseDto {
    val imageUrl = this.taskImage?.let { key ->
        s3Service.generatePresignedUrl(key, 60)
    }

    return TaskResponseDto(
        id = this.id!!,
        category = this.category,
        content = this.content,
        dueDate = this.dueDate,
        status = this.status,
        taskImage = imageUrl,
        scope = this.scope,
        nickname = this.user.nickname
    )
}

fun Comment.toResponseDto(children: List<CommentResponseDto> = emptyList()): CommentResponseDto {
    return CommentResponseDto(
        id = this.id!!,
        content = this.content,
        nickname = this.user.nickname,
        profileImage = this.user.profileImage,
        createdAt = this.createdAt,
        userId = this.user.id!!,
        children = children
    )
}

fun Comment.toUpdateResponseDto(): CommentUpdateResponseDto{
    return CommentUpdateResponseDto(
        id=this.id!!,
        content = this.content,
        nickname = this.user.nickname,
        profileImage = this.user.profileImage
    )
}


