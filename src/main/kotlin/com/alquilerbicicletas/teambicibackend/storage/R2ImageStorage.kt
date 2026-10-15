package com.alquilerbicicletas.teambicibackend.storage

import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URI

/** Production storage: Cloudflare R2 through its S3-compatible API. */
@Component
@ConditionalOnProperty(name = ["storage.provider"], havingValue = "r2")
class R2ImageStorage(
    @Value("\${storage.r2.account-id}") accountId: String,
    @Value("\${storage.r2.access-key-id}") accessKeyId: String,
    @Value("\${storage.r2.secret-access-key}") secretAccessKey: String,
    @Value("\${storage.r2.bucket}") private val bucket: String,
    @Value("\${storage.r2.public-base-url}") publicBaseUrl: String,
) : ImageStorage {
    private val baseUrl = publicBaseUrl.trimEnd('/')
    private val client: S3Client

    init {
        listOf(accountId, accessKeyId, secretAccessKey, bucket, publicBaseUrl).forEach {
            check(it.isNotBlank()) { "Incomplete Cloudflare R2 configuration (storage.r2.*)" }
        }
        client = S3Client.builder()
            .endpointOverride(URI.create("https://$accountId.r2.cloudflarestorage.com"))
            .region(Region.of("auto"))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
            .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
            .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
            .build()
    }

    override fun store(key: String, content: ByteArray, contentType: String): String {
        client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .cacheControl("public, max-age=2592000, immutable")
                .build(),
            RequestBody.fromBytes(content),
        )
        return "$baseUrl/$key"
    }

    override fun delete(url: String) {
        if (!url.startsWith("$baseUrl/")) return
        client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(url.removePrefix("$baseUrl/")).build())
    }

    @PreDestroy
    fun close() = client.close()
}