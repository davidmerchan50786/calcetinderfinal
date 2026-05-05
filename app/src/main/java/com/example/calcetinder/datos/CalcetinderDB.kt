package com.example.calcetinder.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.calcetinder.modelo.Calcetin
import com.example.calcetinder.modelo.Match
import com.example.calcetinder.modelo.Usuario

@Database(entities = [Usuario::class, Calcetin::class, Match::class], version = 2, exportSchema = false)
abstract class CalcetinderDB : RoomDatabase() {
    abstract fun usuarioDAO(): UsuarioDAO
    abstract fun calcetinDAO(): CalcetinDAO
    abstract fun matchDAO(): MatchDAO

    companion object {
        @Volatile private var INSTANCE: CalcetinderDB? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE calcetines ADD COLUMN imagenUri TEXT NOT NULL DEFAULT ''")
            }
        }

        fun obtenerDB(context: Context): CalcetinderDB {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext,
                    CalcetinderDB::class.java, "calcetinder_db")
                    .addMigrations(MIGRATION_1_2)
                    .build().also { INSTANCE = it }
            }
        }
    }
}
