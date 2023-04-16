package ru.ok.android.itmohack2023.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class DbManager(context: Context) {
    val dbHelper = DbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb(){
        db = dbHelper.writableDatabase
    }

    fun insertToDb (dbModel: DbModel){



        val values = ContentValues().apply {
            put(DbNameClass.COLUMN_NAME_USERID, dbModel.userID)
            put(DbNameClass.COLUMN_NAME_TIME, dbModel.time)
            put(DbNameClass.COLUMN_NAME_CLIENT, dbModel.client)
            put(DbNameClass.COLUMN_NAME_PATH, dbModel.path)
            put(DbNameClass.COLUMN_NAME_HEADERS, dbModel.headers)
            put(DbNameClass.COLUMN_NAME_TYPE, dbModel.type)
            put(DbNameClass.COLUMN_NAME_REQUESTTIME, dbModel.requestTime)
            put(DbNameClass.COLUMN_NAME_REQUESTSIZE, dbModel.requestSize)
            put(DbNameClass.COLUMN_NAME_RESPONSESIZE, dbModel.responseSize)
            put(DbNameClass.COLUMN_NAME_STATUSCODE, dbModel.statusCode)
            put(DbNameClass.COLUMN_NAME_METHODNAME, dbModel.methodName)
            put(DbNameClass.COLUMN_NAME_CLASSNAME, dbModel.className)
            put(DbNameClass.COLUMN_NAME_OTHER, dbModel.other)
        }
        db?.insert(DbNameClass.TABLE_NAME, null, values)
    }

    @SuppressLint("Range")
    fun readDbData(): ArrayList<String>{
        val dataList = ArrayList<String>()
        val cursor = db?.query(DbNameClass.TABLE_NAME, null, null, null,
            null,null, null)
        with (cursor){
            while (cursor?.moveToNext()!!){

                val dataText = cursor?.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_TIME))
                dataList.add(dataText.toString())
            }
        }


        return dataList
    }


    @SuppressLint("Range")
    fun readDbDataToDbModelList(): ArrayList<DbModel>{
        val dataList = ArrayList<DbModel>()
        val cursor = db?.query(DbNameClass.TABLE_NAME, null, null, null,
            null,null, null)


        while (cursor?.moveToNext()!!){
            val id = cursor.getString( cursor.getColumnIndex(DbNameClass.COLUMN_NAME_USERID));
            val n = 0;
            val newDbModel = DbModel(

                userID = cursor.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_USERID)).toString(),
                time = cursor?.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_TIME)).toString(),
                client = cursor?.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_CLIENT)).toString(),
                path =  cursor?.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_PATH)).toString(),
                headers =  cursor?.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_HEADERS)).toString(),
                type = cursor?.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_TYPE)).toString(),
                requestTime = cursor?.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_REQUESTTIME)).toString(),
                requestSize = cursor?.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_REQUESTSIZE)).toString(),
                responseSize = cursor?.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_RESPONSESIZE)).toString(),
                statusCode = cursor?.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_STATUSCODE)).toString(),
                methodName = cursor?.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_METHODNAME)).toString(),
                className =  cursor?.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_CLASSNAME)).toString(),
                other = cursor?.getString(cursor.getColumnIndex(DbNameClass.COLUMN_NAME_OTHER)).toString(),

                )

            dataList.add(newDbModel)
        }


        return dataList
    }
}