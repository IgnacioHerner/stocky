package com.ignaherner.stocky.ui.screens.sales.sale_detail

import com.ignaherner.stocky.data.local.entity.SaleEntity
import com.ignaherner.stocky.data.local.entity.SaleItemEntity

data class SaleDetailUiState(
    val sale: SaleEntity? = null,
    val items: List<SaleItemEntity> = emptyList(),
    val isDeleting: Boolean = false,
    val message: String? = null
)
