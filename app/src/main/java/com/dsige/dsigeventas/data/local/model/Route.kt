package com.dsige.dsigeventas.data.local.model

import com.google.gson.annotations.SerializedName

open class Route {
    @SerializedName("overview_polyline")
    var overviewPolyLine: OverviewPolyline? = null
}