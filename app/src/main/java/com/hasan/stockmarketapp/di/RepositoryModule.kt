package com.hasan.stockmarketapp.di

import com.hasan.stockmarketapp.data.cvs.CSVParser
import com.hasan.stockmarketapp.data.cvs.CompanyListingsParser
import com.hasan.stockmarketapp.data.cvs.IntradayInfoParser
import com.hasan.stockmarketapp.data.repository.StockRepositoryImpl
import com.hasan.stockmarketapp.domain.model.CompanyListing
import com.hasan.stockmarketapp.domain.model.IntradayInfo
import com.hasan.stockmarketapp.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ):CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        companyListingsParser: IntradayInfoParser
    ):CSVParser<IntradayInfo>


    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository
}