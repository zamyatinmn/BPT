package com.example.bpt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime

class MainViewModel(private val dataSource: IDataSource = DataSource()) : ViewModel() {
    private var _data: MutableLiveData<List<Measurement>> = MutableLiveData(
        dataSource.getData()
    )

    val data: LiveData<List<Measurement>>
        get() = _data.also { dataSource.getData() }

    fun newItem(item: Measurement) {
        dataSource.writeData(item)
    }
}

interface IDataSource{
    fun getData(): List<Measurement>
    fun writeData(item: Measurement)
}

class DataSource: IDataSource {
    private val a = mutableListOf(
        Measurement(systolicBP = 100, diastolicBP = 80, heartRate = 70),
        Measurement(systolicBP = 110, diastolicBP = 80, heartRate = 70),
        Measurement(systolicBP = 120, diastolicBP = 80, heartRate = 70),
        Measurement(systolicBP = 130, diastolicBP = 80, heartRate = 70),
        Measurement(systolicBP = 140, diastolicBP = 80, heartRate = 70),
        Measurement(
            dateTime = LocalDateTime.now().withMonth(3),
            systolicBP = 150,
            diastolicBP = 80,
            heartRate = 70
        ),
        Measurement(
            dateTime = LocalDateTime.now().withMonth(3),
            systolicBP = 160,
            diastolicBP = 80,
            heartRate = 70
        ),
        Measurement(
            dateTime = LocalDateTime.now().withMonth(3),
            systolicBP = 170,
            diastolicBP = 80,
            heartRate = 70
        ),
        Measurement(
            dateTime = LocalDateTime.now().withMonth(3),
            systolicBP = 80,
            diastolicBP = 80,
            heartRate = 70
        )
    )

    override fun getData() = a


    override fun writeData(item: Measurement) {
        a.add(item)
    }
}