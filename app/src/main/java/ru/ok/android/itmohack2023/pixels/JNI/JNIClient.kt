package ru.ok.android.itmohack2023.pixels.JNI

import ru.ok.android.itmohack2023.Threads
import ru.ok.android.itmohack2023.db.DbManager
import ru.ok.android.itmohack2023.db.DbModel
import ru.ok.android.itmohack2023.db.DbSingletone
import ru.ok.android.itmohack2023.pixels.MeasurementService
import java.util.concurrent.Executor

class JNIClient() : Executor {
    override fun execute(command: Runnable?) {
        val name = MeasurementService.generateRandomId()
        MeasurementService.start(name)
        command?.run() // execute the user's implementation of the task
        val time = MeasurementService.end(name)
        val modelInstance = DbModel(
            userID = DbSingletone.userId,
            time = time.toString(),
            client = "JNIClient"
        )

        val tmpDb = DbManager (DbSingletone.context)
        tmpDb.openDb()
        tmpDb.insertToDb(modelInstance)
    }
}
