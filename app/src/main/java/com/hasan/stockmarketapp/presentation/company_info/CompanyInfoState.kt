package com.hasan.stockmarketapp.presentation.company_info

import com.hasan.stockmarketapp.domain.model.CompanyInfo
import com.hasan.stockmarketapp.domain.model.IntradayInfo

data class CompanyInfoState(
    val stockInfos: List<IntradayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
    )
