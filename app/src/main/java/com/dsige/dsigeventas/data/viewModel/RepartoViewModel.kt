package com.dsige.dsigeventas.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.dsige.dsigeventas.data.local.model.Reparto
import com.dsige.dsigeventas.data.local.repository.ApiError
import com.dsige.dsigeventas.data.local.repository.AppRepository
import javax.inject.Inject

class RepartoViewModel @Inject
internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
    ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    fun getRepartos(): LiveData<PagedList<Reparto>> {
        return roomRepository.getRepartos()
    }

}