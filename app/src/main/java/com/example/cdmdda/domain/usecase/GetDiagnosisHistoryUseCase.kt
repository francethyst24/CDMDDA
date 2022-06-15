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


class GetDiagnosisHistoryUseCase constructor(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private lateinit var firestoreArray: FirestoreDiagnosisArray
    private lateinit var diagnosisRepository: DiagnosisRepository

    companion object KeepAliveListener : ChangeEventListener {
        override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {}
        override fun onDataChanged() {}
        override fun onError(e: FirebaseFirestoreException) {}
    }

    suspend operator fun invoke(
        userId: String,
        isLimited: Boolean,
    ) = withContext(ioDispatcher) {
        diagnosisRepository = DiagnosisRepository(userId)
        val recyclerQueryOption = if (isLimited) diagnosisRepository.recyclerQueryLimited
        else diagnosisRepository.recyclerQueryUnlimited
        firestoreArray = FirestoreArray(
            recyclerQueryOption,
            ClassSnapshotParser(DiagnosisUiState::class.java)
        )
        return@withContext with(firestoreArray) {
            addChangeEventListener(KeepAliveListener)
            DiagnosisRecyclerOptionsBuilder()
                .setSnapshotArray(this)
                .build()
        }
    }

    fun onViewModelCleared() = firestoreArray.removeChangeEventListener(KeepAliveListener)

    suspend fun add(id: String) = withContext(ioDispatcher) { diagnosisRepository.add(id) }

}