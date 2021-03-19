/*
 * Copyright 2021 Paulo Pereira
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pp.jetweatherfy.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pp.jetweatherfy.data.city.ICityRepository
import com.pp.jetweatherfy.data.forecast.IForecastRepository
import com.pp.jetweatherfy.domain.models.Forecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val forecastRepository: IForecastRepository,
    private val cityRepository: ICityRepository
) :
    ViewModel() {

    private val _forecast = MutableLiveData<Forecast>()
    val forecast: LiveData<Forecast> = _forecast

    private val _cities = MutableLiveData<List<String>>(listOf())
    val cities: LiveData<List<String>> = _cities

    fun getCities(query: String) = viewModelScope.launch(Dispatchers.IO) {
        val cities = cityRepository.getCities(query)
        _cities.postValue(cities)
    }

    fun selectCity(city: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = forecastRepository.getForecast(city)
        _forecast.postValue(result)
    }

    init {
        getCities("").invokeOnCompletion {
            selectCity((cities.value ?: listOf()).first())
        }
    }
}
