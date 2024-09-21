package com.hasan.stockmarketapp.data.cvs

import android.os.Build
import androidx.annotation.RequiresApi
import com.hasan.stockmarketapp.data.remote.dto.IntradayInfoDto
import com.hasan.stockmarketapp.domain.model.CompanyListing
import com.hasan.stockmarketapp.domain.model.IntradayInfo
import com.hasan.stockmarketapp.domain.model.toIntradayInfo
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntradayInfoParser @Inject constructor() : CSVParser<IntradayInfo> {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun parse(stream: InputStream): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { line ->
                    val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                    val clopse = line.getOrNull(4) ?: return@mapNotNull null
                    val dto = IntradayInfoDto(timestamp, clopse.toDouble())
                    dto.toIntradayInfo()
                }
                .filter {
                    it.date.dayOfMonth == LocalDateTime.now().minusDays(1).dayOfMonth
                }
                .sortedBy {
                    it.date.hour
                }

                .also {
                    csvReader.close()
                }
        }
    }
}