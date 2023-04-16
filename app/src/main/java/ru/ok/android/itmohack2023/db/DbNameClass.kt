package ru.ok.android.itmohack2023.db

import android.provider.BaseColumns
import java.util.Date

object DbNameClass : BaseColumns {
    const val TABLE_NAME = "traffic"


    const val COLUMN_NAME_USERID = "userId"
    const val COLUMN_NAME_TIME = "time"
    const val COLUMN_NAME_CLIENT = "client"
    const val COLUMN_NAME_PATH = "path"
    const val COLUMN_NAME_HEADERS = "headers"
    const val COLUMN_NAME_TYPE = "type"
    const val COLUMN_NAME_REQUESTTIME = "requestTime"
    const val COLUMN_NAME_REQUESTSIZE= "requestSize"
    const val COLUMN_NAME_RESPONSESIZE= "responceSize"
    const val COLUMN_NAME_STATUSCODE= "statusCode"
    const val COLUMN_NAME_METHODNAME= "methodName"
    const val COLUMN_NAME_CLASSNAME= "className"
    const val COLUMN_NAME_OTHER= "other"

    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "Pixels.db"

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE IF NOT EXISTS ${TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${COLUMN_NAME_USERID} TEXT," +
                "${COLUMN_NAME_TIME} TEXT," +
                "${COLUMN_NAME_CLIENT} TEXT," +
                "${COLUMN_NAME_PATH} TEXT," +
                "${COLUMN_NAME_HEADERS} TEXT," +
                "${COLUMN_NAME_TYPE} TEXT," +
                "${COLUMN_NAME_REQUESTTIME} TEXT," +
                "${COLUMN_NAME_REQUESTSIZE} TEXT," +
                "${COLUMN_NAME_RESPONSESIZE} TEXT," +
                "${COLUMN_NAME_STATUSCODE} TEXT," +
                "${COLUMN_NAME_METHODNAME} TEXT," +
                "${COLUMN_NAME_CLASSNAME} TEXT," +
                "${COLUMN_NAME_OTHER} TEXT)"

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${TABLE_NAME}"


}