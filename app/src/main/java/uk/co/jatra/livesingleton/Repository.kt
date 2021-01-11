package uk.co.jatra.livesingleton

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class Repository {
    private val _cached: MutableStateFlow<String?> = MutableStateFlow("initial")
    val data: StateFlow<String?> =_cached
    private val disk = DiskAccess()
    private val network = NetworkAccess()
    private var running = false

    //Additional complication, disk may be out of date,
    //or network may be out of date.
    //Eg current trip may be cancelled on BE
    //Or current trip may be started locally.

    //If BE says no trip - believe it.
    //For status, use "Latest" of memory, disk and BE
    suspend fun loadFromDisk() {
            _cached.value = disk.getValue()
    }

    suspend fun loadFromNetwork() {
        val newData = network.getValue()
        disk.setValue(newData)
        _cached.value = newData
    }

    suspend fun postQuestion(a: Int, b: Int): Int {
        return network.askQuestion(a, b)
    }

    suspend fun update(newValue: String) {
        _cached.value = newValue
        disk.setValue(_cached.value)
        network.setValue((_cached.value))
        if (!running) {
            running = true
            withContext(Dispatchers.Default) {
                repeat(500) {
                    delay(1000)
                    loadFromNetwork()
                }
                running = false
            }
        }
    }
}


class DiskAccess {
    private var value: String? = null

    suspend fun getValue(): String? {
        println("Get from disk")
        delay(2000)
        return value
    }

    suspend fun setValue(newValue: String?) {
        println("Set disk value")
        delay(2000)
        value = newValue
    }
}

class NetworkAccess {
    private var value: String? = null
    private var networkCount = 0

    suspend fun getValue(): String? {
        println("Get from network")
        delay(5000)
        return "${networkCount++} Net ${System.currentTimeMillis()}"
    }

    suspend fun setValue(newValue: String?) {
        println("Set network value")
        delay(5000)
        value = newValue
    }

    suspend fun askQuestion(a: Int, b: Int): Int {
        println("ask for network response")
        delay(5000)
        return a+b
    }
}