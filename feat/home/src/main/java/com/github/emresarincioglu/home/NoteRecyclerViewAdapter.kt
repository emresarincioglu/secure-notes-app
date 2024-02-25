package com.github.emresarincioglu.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.emresarincioglu.home.databinding.RvNoteCardItemBinding
import com.github.emresarincioglu.home.model.Note
import com.google.android.material.card.MaterialCardView

class NoteRecyclerViewAdapter(
    private val notes: List<Note>,
    private val showWarning: Boolean,
    private val onNoteClick: (note: Note, cvNote: MaterialCardView) -> Unit,
    private val onWarningClick: (view: View) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val ITEM_TYPE_HEADER = 0
        const val ITEM_TYPE_WARNING = 1
        const val ITEM_TYPE_NOTE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val context = parent.context
        val viewHolder = when (viewType) {

            ITEM_TYPE_HEADER -> {
                val tvNote = LayoutInflater.from(context).inflate(
                    R.layout.rv_note_header_item, parent, false
                )
                object : ViewHolder(tvNote) {}
            }

            ITEM_TYPE_WARNING -> {
                val cvWarning = LayoutInflater.from(context).inflate(
                    R.layout.rv_note_warning_item, parent, false
                ).apply {
                    setOnClickListener(onWarningClick)
                }
                object : ViewHolder(cvWarning) {}
            }

            ITEM_TYPE_NOTE -> {
                NoteCardViewHolder(
                    RvNoteCardItemBinding.inflate(
                        LayoutInflater.from(context), parent, false
                    )
                )
            }

            else -> throw IllegalArgumentException("Illegal item type")
        }

        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {

        return if (position == 0) {
            ITEM_TYPE_HEADER
        } else if (showWarning && position == 1) {
            ITEM_TYPE_WARNING
        } else {
            ITEM_TYPE_NOTE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (holder.itemViewType == ITEM_TYPE_NOTE) {

            val note = getNote(position)
            (holder as NoteCardViewHolder).bind(note, onNoteClick)
        }
    }

    override fun getItemCount() = if (showWarning) notes.size + 2 else notes.size + 1

    private fun getNote(position: Int): Note {
        val noteIndex = if (showWarning) position - 2 else position - 1
        return notes[noteIndex]
    }

    class NoteCardViewHolder(private val binding: RvNoteCardItemBinding) :
        ViewHolder(binding.root) {

        fun bind(
            note: Note,
            onNoteClick: (note: Note, cvNote: MaterialCardView) -> Unit
        ) {
            binding.note = note
            binding.cvNote.setOnClickListener {
                onNoteClick(note, binding.cvNote)
            }
        }
    }
}