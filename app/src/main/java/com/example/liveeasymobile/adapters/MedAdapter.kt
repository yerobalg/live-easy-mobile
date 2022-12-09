package com.example.liveeasymobile.adapters

import com.example.liveeasymobile.entity.Medicine
import android.app.Activity
import com.example.liveeasymobile.api.core.ApiClient
import androidx.recyclerview.widget.RecyclerView
import com.example.liveeasymobile.adapters.MedAdapter.ListViewHolder
import android.annotation.SuppressLint
import android.app.ProgressDialog
import com.bumptech.glide.Glide
import android.content.DialogInterface
import android.content.Intent
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.liveeasymobile.activities.MainActivity
import com.example.liveeasymobile.activities.UpdateMedActivity
import com.example.liveeasymobile.api.MedicineAPI
import com.example.liveeasymobile.api.context.SessionManager
import com.example.liveeasymobile.databinding.ViewholderMedBinding
import java.util.ArrayList

class MedAdapter(
    private val context: Activity,
    private val medAPI: MedicineAPI,
) : RecyclerView.Adapter<ListViewHolder>() {
    private var meds = ArrayList<Medicine>()

    @SuppressLint("NotifyDataSetChanged")
    fun setMedicines(meds: ArrayList<Medicine>) {
        this.meds = meds
        notifyDataSetChanged()
    }

    class ListViewHolder(
        private val binding: ViewholderMedBinding,
        private val context: Activity,
        private val medAPI: MedicineAPI
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var progressDialog: ProgressDialog

        init {
            initProgressDialog()
        }

        private fun initProgressDialog() {
            progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Loading")
            progressDialog.setMessage("Just a few second...")
            progressDialog.setCancelable(false)
        }

        fun bindView(med: Medicine) {
            binding.nameTextView.text = med.name
            binding.priceTextView.text = med.priceString
            binding.qtyTextView.text = "Qty: " + med.quantity
            binding.deleteButton.setOnClickListener { deleteMed(med) }
            binding.updateButton.setOnClickListener { updateMed(med) }
            Glide.with(context).load(med.imageURL).into(binding.medImageView)
        }

        private fun deleteMed(med: Medicine) {
            val alert = AlertDialog.Builder(
                context
            )
            alert.setTitle("Delete")
            alert.setMessage("Are you sure want to delete this medicine?")
            alert.setPositiveButton("Yes")
            { _: DialogInterface?, _: Int -> processDeleteMed(med) }
            alert.setNegativeButton("No")
            { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            alert.show()
        }

        private fun updateMed(med: Medicine) {
            val intent = Intent(context, UpdateMedActivity::class.java)
            intent.putExtra("medID", med.id)
            context.startActivity(intent)
        }

        private fun processDeleteMed(med: Medicine) {
            progressDialog.show()
            medAPI.deleteMedicine(context, med.id) { message, isSuccess ->
                progressDialog.dismiss()
                Toast.makeText(context, message, Toast.LENGTH_SHORT)
                    .show()
                if (!isSuccess) {
                    return@deleteMedicine
                }

                context.startActivity(
                    Intent(
                        context,
                        MainActivity::class.java
                    )
                )
                context.finish()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewholderMedBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return ListViewHolder(binding, context, medAPI)
    }

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int
    ) {
        holder.bindView(meds[position])
    }

    override fun getItemCount(): Int {
        return meds.size
    }
}