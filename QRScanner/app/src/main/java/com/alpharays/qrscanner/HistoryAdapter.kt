package com.alpharays.qrscanner

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.ImageView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.alpharays.qrscanner.data.entities.QrHistory
import com.alpharays.qrscanner.utils.Generator
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryAdapter(
    private var context: Context,
    private var view: View,
    private var onDeleteClickListener: OnDeleteClickListener? = null,
    private var scope: LifecycleCoroutineScope,
) :
    RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    private var myGenerator = Generator()
    private var finalList = ArrayList<QrHistory>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val historyText: CheckedTextView = itemView.findViewById(R.id.historyText)
        val historyImage: ImageView = itemView.findViewById(R.id.historyImage)
        val historySave: MaterialButton = itemView.findViewById(R.id.historySave)
        val historyShare: MaterialButton = itemView.findViewById(R.id.historyShare)
        val historyDelete: MaterialButton = itemView.findViewById(R.id.historyDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val viewHolder = MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.history_items, parent, false)
        )
        return viewHolder
    }

    override fun getItemCount(): Int {
        return finalList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = finalList[position]
        holder.historyText.text = currentItem.about
        val bitmap =
            BitmapFactory.decodeByteArray(currentItem.imageData, 0, currentItem.imageData.size)
        holder.historyImage.setImageBitmap(bitmap)

        holder.historySave.setOnClickListener {
            scope.launch(Dispatchers.IO) {
                myGenerator.savingFile(currentItem.about, bitmap, view)
            }
        }

        holder.historyShare.setOnClickListener {
            myGenerator.shareQrCode(context, bitmap, currentItem.about)
        }

        holder.historyDelete.setOnClickListener {
            onDeleteClickListener?.onDeleteClicked(currentItem)
        }

    }

    fun updateList(newList: ArrayList<QrHistory>) {
        finalList.clear()
        finalList.addAll(newList)
        notifyDataSetChanged()
    }

    interface OnDeleteClickListener {
        fun onDeleteClicked(qrHistory: QrHistory)
    }
}