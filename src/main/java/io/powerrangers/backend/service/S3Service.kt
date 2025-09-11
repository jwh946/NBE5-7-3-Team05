package io.powerrangers.backend.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.io.IOException
import java.time.Duration
import java.util.UUID

@Service
class S3Service(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
    @Value("\${cloud.aws.region.static}")
    private val region: String,
) {

    @Throws(IOException::class)
    fun upload(file: MultipartFile): String {
        val fileName = "${UUID.randomUUID()}-${file.originalFilename}"

        val request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(fileName)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(request, RequestBody.fromBytes(file.bytes))

        return fileName
    }

    @Throws(IOException::class)
    fun delete(imagePath: String?) {
        if (imagePath.isNullOrBlank()) return

        val key = extractKeyFromUrl(imagePath)

        val deleteRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()

        s3Client.deleteObject(deleteRequest)
    }

    private fun extractKeyFromUrl(imagePath: String): String {
        val baseUrl = "https://$bucket.s3.$region.amazonaws.com/"
        return imagePath.replace(baseUrl, "")
    }

    fun generatePresignedUrl(key: String, durationMinutes: Long = 5): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(durationMinutes))
            .getObjectRequest(getObjectRequest)
            .build()

        return s3Presigner.presignGetObject(presignRequest).url().toString()
    }
}
