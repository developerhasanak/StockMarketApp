package com.hasan.stockmarketapp.data.repository

import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import com.hasan.stockmarketapp.data.cvs.CSVParser
import com.hasan.stockmarketapp.data.cvs.IntradayInfoParser
import com.hasan.stockmarketapp.data.local.StockDatabase
import com.hasan.stockmarketapp.data.mapper.toCompanyInfo
import com.hasan.stockmarketapp.data.mapper.toCompanyListing
import com.hasan.stockmarketapp.data.mapper.toCompanyListingEntity
import com.hasan.stockmarketapp.data.remote.StockApi
import com.hasan.stockmarketapp.domain.model.CompanyInfo
import com.hasan.stockmarketapp.domain.model.CompanyListing
import com.hasan.stockmarketapp.domain.model.IntradayInfo
import com.hasan.stockmarketapp.domain.repository.StockRepository
import com.hasan.stockmarketapp.util.Resource
import com.opencsv.CSVReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntradayInfo>
) : StockRepository {

    val dao = db.dao

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListings = try {
               val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: HttpException){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }
            remoteListings?.let { listings->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )
                emit(Resource.Success(
                    data = dao.searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))

            }
        }

    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol)

            val result = intradayInfoParser.parse(response.byteStream())
            Log.e("intraday",result.toString())
            Resource.Success(result)

        } catch (e:IOException){
            e.printStackTrace()
            Resource.Error(message = "Couldn't load intraday info ")
        } catch (e:HttpException){
            e.printStackTrace()
            Resource.Error(message = "Couldn't load intraday info ")

        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfo())

        } catch (e:IOException){
            e.printStackTrace()
            Resource.Error(message = "Couldn't load intraday info ")
        } catch (e:HttpException){
            e.printStackTrace()
            Resource.Error(message = "Couldn't load intraday info ")

        }
    }
}