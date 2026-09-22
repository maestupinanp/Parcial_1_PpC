package com.example.parcial_1.logic

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.parcial_1.model.Case
import com.example.parcial_1.model.CaseStatus
import com.example.parcial_1.model.Evidence
import com.example.parcial_1.model.Finding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Database(entities = [Case::class, Finding::class, Evidence::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun caseDao(): CaseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "case_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val caseDao = database.caseDao()
                                    // Solo poblamos si la base está vacía por error en primer intento con
                                    if (caseDao.getAllCases().first().isEmpty()) {
                                        populateDatabase(caseDao)
                                    }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDatabase(caseDao: CaseDao) {
            // Caso 1: Robo en Joyería Central
            val c1Id = caseDao.insertCase(
                Case(
                    title = "Robo en Joyería Central",
                    description = "Robo de joyas de alto valor durante la madrugada.",
                    clientName = "Joyería Central",
                    caseNumber = "CASO-001",
                    status = CaseStatus.IN_INVESTIGATION
                )
            ).toInt()

            caseDao.insertFinding(Finding(caseId = c1Id, number = "H-01", description = "Cámaras de seguridad muestran dos individuos encapuchados."))
            caseDao.insertFinding(Finding(caseId = c1Id, number = "H-02", description = "Huellas dactilares encontradas en el mostrador principal."))
            caseDao.insertEvidence(Evidence(caseId = c1Id, number = "E-01", description = "Video de circuito cerrado (CCTV) del pasillo B."))
            caseDao.insertEvidence(Evidence(caseId = c1Id, number = "E-02", description = "Muestra de ADN (cabello) recolectada cerca de la bóveda."))

            // Caso 2: Fraude en Constructora XYZ
            val c2Id = caseDao.insertCase(
                Case(
                    title = "Fraude en Constructora XYZ",
                    description = "Desvío de fondos detectado en auditoría externa.",
                    clientName = "Constructora XYZ",
                    caseNumber = "CASO-002",
                    status = CaseStatus.IN_INVESTIGATION
                )
            ).toInt()

            caseDao.insertFinding(Finding(caseId = c2Id, number = "H-10", description = "Diferencia de 1 millón de pesos en el balance de materiales."))
            caseDao.insertFinding(Finding(caseId = c2Id, number = "H-11", description = "Facturas emitidas a empresas inexistentes (fantasma)."))
            caseDao.insertEvidence(Evidence(caseId = c2Id, number = "E-20", description = "Libro contable original con tachaduras sospechosas."))
            caseDao.insertEvidence(Evidence(caseId = c2Id, number = "E-21", description = "Capturas de pantalla de chats coordinando pagos ilícitos."))

            // Caso 3: Desaparición de Documentos
            val c3Id = caseDao.insertCase(
                Case(
                    title = "Desaparición de Documentos",
                    description = "Pérdida de archivos clasificados del área legal.",
                    clientName = "Ministerio de Justicia",
                    caseNumber = "CASO-003",
                    status = CaseStatus.CLOSED
                )
            ).toInt()

            caseDao.insertFinding(Finding(caseId = c3Id, number = "H-20", description = "Cerradura de la oficina 304 forzada con herramienta técnica."))
            caseDao.insertFinding(Finding(caseId = c3Id, number = "H-21", description = "Documentos recuperados parcialmente en un basurero externo."))
            caseDao.insertEvidence(Evidence(caseId = c3Id, number = "E-30", description = "Cerradura dañada recolectada para peritaje metalúrgico."))
            caseDao.insertEvidence(Evidence(caseId = c3Id, number = "E-31", description = "Hojas A4 con manchas de humedad recuperadas."))
        }
    }
}
