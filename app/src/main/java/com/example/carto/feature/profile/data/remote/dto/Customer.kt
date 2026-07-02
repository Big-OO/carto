package com.example.carto.feature.profile.data.remote.dto

data class Customer(
    val addresses: List<Any>,
    val created_at: String,
    val currency: String,
    val email: String,
    val first_name: String,
    val id: Long,
    val last_name: String,
    val last_order_id: Any,
    val last_order_name: Any,
    val multipass_identifier: Any,
    val note: String,
    val orders_count: Int,
    val phone: Any,
    val state: String,
    val tags: String,
    val tax_exempt: Boolean,
    val tax_exemptions: List<Any>,
    val total_spent: String,
    val updated_at: String,
    val verified_email: Boolean
)