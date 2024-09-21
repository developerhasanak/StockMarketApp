package com.hasan.stockmarketapp.presentation.company_listings

import com.hasan.stockmarketapp.domain.model.CompanyListing

sealed class CompanyListingsEvent {
    data object Refresh : CompanyListingsEvent()
    data class OnSearchQueryChange(val query: String) : CompanyListingsEvent()
}