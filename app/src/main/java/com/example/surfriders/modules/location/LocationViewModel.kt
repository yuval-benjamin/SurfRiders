package com.example.surfriders.modules.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.surfriders.data.location.Location
import com.example.surfriders.data.location.LocationService
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {

    private val _locations = MutableLiveData<List<Location>>()
    val locations: LiveData<List<Location>> get() = _locations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchLocations() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val fetchedLocations = LocationService.instance.getLocations()
                _locations.postValue(fetchedLocations)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to load locations")
                _isLoading.postValue(false)
            }
        }
    }
}
