package ru.ok.android.itmohack2023.jsonparser

import org.json.JSONArray
import org.json.JSONObject
import ru.ok.android.itmohack2023.db.DbManager

class JsonParser {
    fun JSONArrayFromDbModelArray(dbManager: DbManager) : JSONArray {
        val jsonArray = JSONArray()
        var originList = dbManager.readDbDataToDbModelList();
        for (dbm in originList){
            val jsonA = JSONObject()
            jsonA.put("userID", dbm.userID)
            jsonA.put("time", dbm.time)
            jsonA.put("client", dbm.client)
            jsonA.put("path", dbm.path)
            jsonA.put("headers", dbm.headers)
            jsonA.put("type", dbm.type)
            jsonA.put("requestTime", dbm.requestTime)
            jsonA.put("requestSize", dbm.requestSize)
            jsonA.put("responseSize", dbm.responseSize)
            jsonA.put("statusCode", dbm.statusCode)
            jsonA.put("methodName", dbm.methodName)
            jsonA.put("className", dbm.className)
            jsonA.put("other", dbm.other)
            jsonArray.put(jsonA);
        }
        return jsonArray
    }
}