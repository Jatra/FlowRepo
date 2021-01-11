package uk.co.jatra.livesingleton

import androidx.lifecycle.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FirstViewModel: ViewModel() {
    //cancel this job to stop collecting from the repository.
    //Since the job is launched from viewModelScope, it will be auto-cancelled
//    private var job: Job
    private val repository = Repository()
//    private var _viewData: MutableLiveData<String?> = repository.data.asLiveData()
//    private var _viewData: MutableLiveData<String?> = MutableLiveData(null)
    var viewData: LiveData<String?> = repository.data.asLiveData()
//    var viewData: LiveData<String?> = _viewData


//    init {
//        job = viewModelScope.launch {
//            repository.data.collect {
//                updateWithNewData(it)
//            }
//        }
//    }
//
//    private fun updateWithNewData(data: String?) {
//        _viewData.postValue(data)
//    }

    fun submitNewValue(newValue: String) {
        viewModelScope.launch {
            repository.update(newValue)
        }
    }

    //stop collecting, only returning when job successfully cancelled.
//    suspend fun stopAndWaitCollecting() {
//        job.cancelAndJoin()
//    }

//    fun stopCollecting(reason: CancellationException?) {
//        job.cancel(reason)
//    }
}

