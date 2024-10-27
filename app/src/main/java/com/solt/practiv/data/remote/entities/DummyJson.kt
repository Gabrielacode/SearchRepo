package com.solt.practiv.data.remote.entities

import kotlinx.serialization.Serializable

@Serializable
data class DummyJson(
    val limit: Int,
    val recipes: List<Recipe>,
    val skip: Int,
    val total: Int
)