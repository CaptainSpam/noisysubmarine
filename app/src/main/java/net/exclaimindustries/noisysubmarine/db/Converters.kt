package net.exclaimindustries.noisysubmarine.db

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import org.json.JSONArray
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Just some assorted converter classes for the database. */
class Converters {
    companion object {
        private const val FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"

        /** Static version of the ISO-to-Date converter, for convenience. */
        @SuppressLint("SimpleDateFormat")
        fun convertIso8601ToDate(iso: String): Date {
            // This should be static, but I understand there to have been a bug in the past with
            // Android concerning exactly this situation, and I'm not sure it was fixed.
            val df: DateFormat = SimpleDateFormat(FORMAT_STRING)
            return df.parse(iso)
        }
    }

    /** Convert from an ISO 8601 string to a Date object. */
    @SuppressLint("SimpleDateFormat")
    @TypeConverter
    fun fromIso8601String(iso: String): Date {
        return convertIso8601ToDate(iso)
    }

    /** Convert from a Date object to an ISO 8601 string. */
    @SuppressLint("SimpleDateFormat")
    @TypeConverter
    fun dateToIso8601(date: Date): String {
        val df: DateFormat = SimpleDateFormat(FORMAT_STRING)
        return df.format(date)
    }

    /** Convert from a JSON-encoded list of strings to a List<String>. */
    @TypeConverter
    fun fromJsonListOfStrings(json: String): List<String> {
        val jsonArray = JSONArray(json)
        val toReturn = ArrayList<String>()

        for(i in 0..jsonArray.length() - 1) {
            toReturn.add(jsonArray.getString(i))
        }

        return toReturn
    }

    /** Convert from a List<String> to a JSON-encoded list of strings. */
    @TypeConverter
    fun listOfStringsToJson(list: List<String>): String {
        val jsonArray = JSONArray()
        for(item in list) {
            jsonArray.put(item)
        }

        return jsonArray.toString()
    }
}