package com.upipulse.domain.model

data class DashboardSummary(
    val totalOutflow: Double,
    val totalInflow: Double,
    val avgTicketSize: Double,
    val topMerchants: List<MerchantSpend>,
    val categoryBreakdown: List<CategorySpend>,
    val cashbackTotal: Double
)

data class MerchantSpend(
    val merchant: Merchant,
    val amount: Double,
    val transactions: Int
)

data class CategorySpend(
    val category: String,
    val amount: Double,
    val budget: Double? = null
)