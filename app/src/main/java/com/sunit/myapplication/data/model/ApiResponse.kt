package com.sunit.myapplication.data.model

data class ApiResponse(
    val metadata: Metadata,
    val record: Record
)

data class Metadata(
    val createdAt: String,
    val id: String,
    val name: String,
    val `private`: Boolean
)

data class Record(
    val denylist: List<String>
)
