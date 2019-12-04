package com.dsige.dsigeventas.helper

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object Parser {

    fun parse(jObject: JSONObject): List<List<HashMap<String, String>>>? {
        val routes: MutableList<List<HashMap<String, String>>> =
            ArrayList()
        val jRoutes: JSONArray
        var jLegs: JSONArray
        var jSteps: JSONArray
        try {
            jRoutes = jObject.getJSONArray("routes")
            for (i in 0 until jRoutes.length()) {
                jLegs = (jRoutes[i] as JSONObject).getJSONArray("legs")
                val path = ArrayList<HashMap<String, String>>()
                for (j in 0 until jLegs.length()) {
                    jSteps = (jLegs[j] as JSONObject).getJSONArray("steps")
                    for (k in 0 until jSteps.length()) {
                        val polyline: String =
                            ((jSteps[k] as JSONObject)["polyline"] as JSONObject)["points"] as String
                        val list = decodePoly(polyline)
                        for (l in list.indices) {
                            val hm = HashMap<String, String>()
                            hm["lat"] = list[l].latitude.toString()
                            hm["lng"] = list[l].longitude.toString()
                            path.add(hm)
                        }
                    }
                    routes.add(path)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return routes
    }

    fun marker(jObject: JSONObject): List<List<HashMap<String, String>>>? {
        val routes: MutableList<List<HashMap<String, String>>> =
            ArrayList()
        val jRoutes: JSONArray
        var jLegs: JSONArray
        try {
            jRoutes = jObject.getJSONArray("routes")
            for (i in 0 until jRoutes.length()) {
                jLegs = (jRoutes[i] as JSONObject).getJSONArray("legs")
                val path: MutableList<HashMap<String, String>> = ArrayList()
                for (j in 0 until jLegs.length()) {
                    val latitud: Double =
                        ((jLegs[j] as JSONObject)["start_location"] as JSONObject)["lat"] as Double
                    val longitud: Double =
                        ((jLegs[j] as JSONObject)["start_location"] as JSONObject)["lng"] as Double
                    val hm = HashMap<String, String>()
                    hm["lat"] = latitud.toString()
                    hm["lng"] = longitud.toString()
                    path.add(hm)
                }
                routes.add(path)
            }
        } catch (e: Exception) {
            Log.i("TAG", e.toString())
            e.printStackTrace()
        }
        return routes
    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly: MutableList<LatLng> = ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f) shl shift
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f) shl shift
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }
}