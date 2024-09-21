package com.hasan.stockmarketapp.domain.repository

import com.hasan.stockmarketapp.domain.model.CompanyInfo
import com.hasan.stockmarketapp.domain.model.CompanyListing
import com.hasan.stockmarketapp.domain.model.IntradayInfo
import com.hasan.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import java.security.interfaces.RSAKey


interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>

    suspend fun getIntradayInfo(
        symbol : String
    ) : Resource<List<IntradayInfo>>

    suspend fun getCompanyInfo(
        symbol :String
    ) :Resource<CompanyInfo>
}