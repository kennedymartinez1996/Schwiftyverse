package com.portfolio.schwiftyverse.model

data class PaginatedData<T>(
    val info: InfoModel,
    val data: List<T>
)

data class InfoModel(
    val count: Int,
    val pages: Int,
    val hasNext: Boolean,
    val hasPrev: Boolean
)