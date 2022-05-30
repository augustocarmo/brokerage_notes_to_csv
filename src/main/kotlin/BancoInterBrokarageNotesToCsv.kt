import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.client.CsvWriter
import com.github.doyaaaaaken.kotlincsv.client.ICsvFileWriter
import model.BrokerageNoteEntry
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class BancoInterBrokerageNotesToCsv(private val csvReader: CsvReader, private val csvWriter: CsvWriter) {

    private val csvNumberFormat = DecimalFormat("#,###.00")
    private val csvReaderDateFormat = SimpleDateFormat("ddMMyyyy")
    private val csvWriterDateFormat = SimpleDateFormat("dd/MM/yyyy")

    fun parseDir(brokerageNotesDir: File, outputFile: File) {
        if (!brokerageNotesDir.isDirectory) {
            throw IllegalArgumentException("$brokerageNotesDir is not a directory")
        }

        brokerageNotesDir.listFiles()
            ?.filter { it.extension == "csv" } // TODO: make ".csv" a constant
            ?.map { extractBrokerageNoteEntries(brokerageNoteFile = it) }
            ?.flatten()
            ?.sortedBy { it.date }
            ?.let { writeToOutputFile(outputFile = outputFile, brokerageNoteEntries = it) }

    }

    private fun extractBrokerageNoteEntries(brokerageNoteFile: File): List<BrokerageNoteEntry> {
        val csvRows = csvReader.readAll(file = brokerageNoteFile)
        if (csvRows.isEmpty()) {
            return emptyList()
        }

        val date = csvReaderDateFormat.parse(csvRows.first()[4])

        val headerLineIndex = csvRows.indexOfFirst { csvRow ->
            csvRow.firstOrNull() == "PRAÇA" // TODO: make it a constant
        }

        val summaryHeaderLineIndex = csvRows.indexOfFirst { csvRow ->
            csvRow.firstOrNull() == "RESUMO DOS NEGÓCIOS" // TODO: make it a constant
        }

        return csvRows
            .subList(fromIndex = headerLineIndex + 1, toIndex = summaryHeaderLineIndex)
            .mapNotNull { csvRow ->
                extractBrokerageNoteEntry(csvRow = csvRow, date = date)
            }
    }

    private fun extractBrokerageNoteEntry(
        csvRow: List<String>,
        date: Date
    ): BrokerageNoteEntry? {
        if (csvRow.firstOrNull().isNullOrBlank()) {
            return null
        }

        return BrokerageNoteEntry(
            date = date,
            square = csvRow[0],
            buyOrSell = csvRow[1].first(),
            marketType = csvRow[2],
            share = csvRow[3].split(" ").first().dropLastWhile { !it.isDigit() },
            quantity = csvNumberFormat.parse(csvRow[5]).toInt(),
            netPrice = csvNumberFormat.parse(csvRow[6]).toDouble(),
            buyOrSellPrice = csvNumberFormat.parse(csvRow[7]).toDouble(),
            dOrC = csvRow[8].first()
        )
    }

    private fun writeOutputHeader(csvFileWriter: ICsvFileWriter) {
        csvFileWriter.writeRow(
            "Data",
            "PRAÇA",
            "C/V",
            "TIPO DE MERCADO",
            "",
            "AÇÃO",
            "QUANTIDADE",
            "PREÇO DE LIQUIDAÇÃO(R\$)",
            "COMPRA/VENDA (R\$)",
            "D/C"
        )
    }

    private fun writeToOutputFile(outputFile: File, brokerageNoteEntries: List<BrokerageNoteEntry>) {
        csvWriter.open(targetFile = outputFile, append = false) {
            writeOutputHeader(csvFileWriter = this)

            brokerageNoteEntries.map { brokerageNoteEntry ->
                brokerageNoteEntry.toCsvRow()
            }.forEach { csvRow ->
                this.writeRow(csvRow)
            }
        }
    }

    private fun BrokerageNoteEntry.toCsvRow() = listOf(
        csvWriterDateFormat.format(date),
        square,
        buyOrSell.toString(),
        marketType,
        "",
        share,
        quantity.toString(),
        csvNumberFormat.format(netPrice),
        csvNumberFormat.format(buyOrSellPrice),
        dOrC.toString()
    )
}