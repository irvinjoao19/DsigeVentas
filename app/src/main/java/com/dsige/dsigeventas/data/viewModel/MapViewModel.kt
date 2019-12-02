package com.dsige.dsigeventas.data.viewModel

import android.location.Address
import androidx.lifecycle.ViewModel
import com.dsige.dsigeventas.data.local.model.CoordinateDetails
import com.dsige.dsigeventas.data.local.repository.ApiError
import com.dsige.dsigeventas.data.local.repository.AppRepository
import io.reactivex.Observable
import java.util.concurrent.Callable
import javax.inject.Inject

class MapViewModel{
//@Inject
//internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
//    ViewModel() {
//
//
//    private fun getSourceDestCoordinates(startingPoint: String, destination: String) {
//
//        val latLonObs: Observable<CoordinateDetails> =
//            Observable.fromCallable { getCoordinates(startingPoint, destination) }
//
//    }
//
//
//    private fun getSourceDestCoordinates(startingPoint: String, destination: String) {
//
//
//
//        val latLonObs: Observable<CoordinateDetails> =
//            Observable.fromCallable(object : Callable<CoordinateDetails>() {
//
//
//                override fun call(): CoordinateDetails {
//                    return getCoordinates(startingPoint, destination);
//                }
//            });
//
//
//
//        try {
//            List fromAddressData = geocoder . getFromLocationName (fromLocation, 1);
//            if (fromAddressData != null && fromAddressData.size() > 0) {
//                Address address =(Address) fromAddressData . get (0);
//                coordinateDetails.fromAddress = new LatLng (address.getLatitude(),
//                address.getLongitude());
//            }
//            List toAddressData = geocoder . getFromLocationName (toLocation, 1);
//            if (toAddressData != null && toAddressData.size() > 0) {
//                Address address =(Address) toAddressData . get (0);
//                coordinateDetails.toAddress = new LatLng (address.getLatitude(),
//                address.getLongitude());
//            }
//        } catch (IOException e) {
//            Log.e(TAG, ” Unable to connect to Geocoder ”, e);
//        }
//        return coordinateDetails;
//    }


}