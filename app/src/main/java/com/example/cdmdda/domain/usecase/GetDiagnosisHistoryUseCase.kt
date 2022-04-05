package com.example.cdmdda.domain.usecase

import com.example.cdmdda.common.DiagnosisUiState
import com.example.cdmdda.common.FirestoreDiagnosisArray
import com.example.cdmdda.common.FirestoreDiagnosisRecyclerOptions
import com.example.cdmdda.common.FirestoreDiagnosisRecyclerOptionsBuilder
import com.example.cdmdda.data.repository.DiagnosisRepository
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.ClassSnapshotParser
import com.firebase.ui.firestore.FirestoreArray
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GetDiagnosisHistoryUseCase(
    private var diagnosisRepository: DiagnosisRepository? = null,
    private var firestoreArray: FirestoreDiagnosisArray? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    companion object KeepAliveListener : ChangeEventListener {
        override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {}
        override fun onDataChanged() {}
        override fun onError(e: FirebaseFirestoreException) {}
    }

    suspend operator fun invoke(repository: DiagnosisRepository) = withContext(ioDispatcher) {
        diagnosisRepository = repository
        firestoreArray = diagnosisRepository?.let {
            FirestoreArray(it.recyclerOptions, ClassSnapshotParser(DiagnosisUiState::class.java))
        }
        firestoreArray?.addChangeEventListener(KeepAliveListener)
        return@withContext firestoreArray
    }

    fun onViewModelCleared() = firestoreArray?.removeChangeEventListener(KeepAliveListener)

    val recyclerOptions: FirestoreDiagnosisRecyclerOptions?
        get() {
            firestoreArray?.let {
                return FirestoreDiagnosisRecyclerOptionsBuilder().setSnapshotArray(it).build()
            }
            return null
        }

    suspend fun add(id: String) = withContext(ioDispatcher) {
        diagnosisRepository?.add(id)
    }

}