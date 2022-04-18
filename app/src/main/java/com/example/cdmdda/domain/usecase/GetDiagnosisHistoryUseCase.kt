package com.example.cdmdda.domain.usecase

import com.example.cdmdda.common.DiagnosisRecyclerOptionsBuilder
import com.example.cdmdda.common.DiagnosisUiState
import com.example.cdmdda.common.FirestoreDiagnosisArray
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
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private var firestoreArray: FirestoreDiagnosisArray? = null
    private lateinit var diagnosisRepository: DiagnosisRepository

    companion object KeepAliveListener : ChangeEventListener {
        override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {}
        override fun onDataChanged() {}
        override fun onError(e: FirebaseFirestoreException) {}
    }

    suspend operator fun invoke(repository: DiagnosisRepository) = withContext(ioDispatcher) {
        diagnosisRepository = repository
        firestoreArray = FirestoreArray(
            diagnosisRepository.recyclerOptions,
            ClassSnapshotParser(DiagnosisUiState::class.java)
        )
        return@withContext firestoreArray?.let {
            it.addChangeEventListener(KeepAliveListener)
            return@let DiagnosisRecyclerOptionsBuilder()
                .setSnapshotArray(it)
                .build()
        }
    }

    fun onViewModelCleared() = firestoreArray?.removeChangeEventListener(KeepAliveListener)

    fun refresh() = firestoreArray?.run {
        removeChangeEventListener(KeepAliveListener)
        addChangeEventListener(KeepAliveListener)
        return@run
    }

    suspend fun add(id: String) = withContext(ioDispatcher) { diagnosisRepository.add(id) }

}