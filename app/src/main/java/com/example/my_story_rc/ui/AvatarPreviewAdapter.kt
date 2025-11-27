package com.example.my_story_rc.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_story_rc.R

class AvatarPreviewAdapter(
    private val avatars: List<Int>, // Список ID ресурсов аватаров
    private val onAvatarSelected: (Int) -> Unit, // Коллбэк для уведомления ProfileActivity о выборе
    private val initiallySelectedAvatarResId: Int = 0 // ID аватара, который был выбран при открытии диалога
) : RecyclerView.Adapter<AvatarPreviewAdapter.AvatarPreviewViewHolder>() {

    private var selectedAvatarResId = initiallySelectedAvatarResId // Хранит ID текущего выбранного аватара в адаптере

    fun getSelectedAvatarResId(): Int = selectedAvatarResId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarPreviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avatar_preview, parent, false)
        return AvatarPreviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarPreviewViewHolder, position: Int) {
        val avatarResId = avatars[position]
        holder.bind(avatarResId, avatarResId == selectedAvatarResId)
    }

    override fun getItemCount(): Int = avatars.size

    inner class AvatarPreviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarImageView: ImageView = itemView.findViewById(R.id.ivAvatarPreview)
        private val checkmarkImageView: ImageView = itemView.findViewById(R.id.ivCheckmark)

        fun bind(avatarResId: Int, isSelected: Boolean) {
            avatarImageView.setImageResource(avatarResId)
            checkmarkImageView.visibility = if (isSelected) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                val previousSelected = selectedAvatarResId
                selectedAvatarResId = avatarResId

                // Уведомляем ProfileActivity о новом выборе
                onAvatarSelected(avatarResId)

                // Уведомляем адаптер о изменениях в старом и новом выбранном элементе
                notifyItemChanged(avatars.indexOf(previousSelected))
                // --- ИСПРАВЛЕНО: Используем bindingAdapterPosition вместо adapterPosition ---
                notifyItemChanged(bindingAdapterPosition)
            }
        }
    }
}