import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

fun main(args: Array<String>) {
    val csvReader = csvReader()
    val csvWriter = csvWriter()
    val bancoInterBrokerageNotesToCsv = BancoInterBrokerageNotesToCsv(csvReader = csvReader, csvWriter = csvWriter)

    print("Diretório que possui as notas de corretagem (.csv):")
    val brokerageNotesDirPath = readLine()
    print("Onde ficará e qual será o nome do arquivo de saída (exemplo: /home/your_user/Downloads/output.csv): ")
    val outputFilePath = readLine()

    if (brokerageNotesDirPath.isNullOrBlank() || outputFilePath.isNullOrBlank()) {
        return
    }

    bancoInterBrokerageNotesToCsv.parseDir(
        brokerageNotesDir = File(brokerageNotesDirPath),
        outputFile = File(outputFilePath)
    )
}