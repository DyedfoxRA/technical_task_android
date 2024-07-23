package com.venture.core.domain.results

sealed interface DataError : BaseError {

    enum class Network : DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        BAD_REQUEST,
        CONFLICT
    }

    enum class Local : DataError {
        DISK_FULL,
        READ_ONLY_FILE_SYSTEM,
        FILE_NOT_FOUND,
        IO_ERROR,
        PERMISSION_DENIED,
        DATABASE_CORRUPTION,
        DATABASE_CONSTRAINT,
        DATABASE_READ_ERROR,
        DATABASE_WRITE_ERROR
    }

    enum class Validation : DataError {
        FIELD_NOT_EMPTY,
        INVALID_EMAIL_FORMAT,
        PASSWORD_TOO_SHORT,
        FIELD_REQUIRED,
        INVALID_INPUT
    }

    enum class Auth : DataError {
        USER_NOT_FOUND,
        INVALID_CREDENTIALS,
        ACCOUNT_LOCKED,
        TOKEN_EXPIRED,
        TOKEN_INVALID,
        UNAUTHORIZED_ACCESS
    }

    enum class Storage : DataError {
        UPLOAD_FAILED,
        DOWNLOAD_FAILED,
        INSUFFICIENT_STORAGE
    }

    enum class Device : DataError {
        DEVICE_OFFLINE,
        DEVICE_UNSUPPORTED,
        DEVICE_LOW_MEMORY,
        DEVICE_STORAGE_LOW
    }

    enum class Api : DataError {
        RATE_LIMIT_EXCEEDED,
        API_DEPRECATED,
        API_NOT_AVAILABLE
    }

    enum class Common : DataError {
        UNKNOWN
    }
}
