package com.example.cdmdda.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.*
import com.example.cdmdda.DisplayUtils
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ItemDiagnosisBinding
import com.example.cdmdda.dto.Diagnosis
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.*

class DiagnosisAdapter(options: FirestoreRecyclerOptions<Diagnosis>) :
    FirestoreRecyclerAdapter<Diagnosis, DiagnosisAdapter.DiagnosisHolder>(options) {

    private lateinit var listener: OnItemClickListener

    inner class DiagnosisHolder(itemBinding: ItemDiagnosisBinding) : ViewHolder(itemBinding.root) {
        private val textDiagnosisName = itemBinding.textDiagnosisName
        private val textDiagnosisDate = itemBinding.textDiagnosisDate
        init {
            itemBinding.root.setOnClickListener{
                val position = adapterPosition
                if (position != NO_POSITION) {
                    listener.onDiagnosisItemClick(snapshots.getSnapshot(position), position)
                }
            }
        }
        fun bind(context: Context, diagnosis: Diagnosis) {
            textDiagnosisName.text = diagnosis.name
            val dateString = "MM-dd-yyyy"; val timeString = "HH:mm"
            val toDate = diagnosis.diagnosed_on!!.toDate()
            val todayDate = DisplayUtils.formatDate(dateString, Date()).substring(0, 10)
            val diagnoseDate  = DisplayUtils.formatDate(dateString, toDate).substring(0, 10)
            textDiagnosisDate.text = if (todayDate == diagnoseDate) {
                context.getString(R.string.text_today).plus(DisplayUtils.formatDate(timeString, toDate))
            } else {
                DisplayUtils.formatDate("$dateString $timeString", toDate)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisHolder {
        val itemBinding = ItemDiagnosisBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DiagnosisHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DiagnosisHolder, position: Int, model: Diagnosis) {
        holder.bind(holder.itemView.context, model)
    }

    interface OnItemClickListener {
        fun onDiagnosisItemClick(documentSnapshot: DocumentSnapshot, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) { this.listener = listener}

}