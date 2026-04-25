package com.example.syncdrive

import android.app.AlertDialog
import android.widget.Button
import android.widget.Toast

class RestrictionsController(
    private val btnRestrictions: Button
) {
    fun setupRestrictionsButton() {
        btnRestrictions.setOnClickListener {
            showRestrictionsDialog()
        }
    }

    private fun showRestrictionsDialog() {
        val restrictions = arrayOf("Speed Limiter: 75 mph", "Geofencing: Active", "Curfew: None")
        val checkedItems = booleanArrayOf(true, true, false)

        AlertDialog.Builder(btnRestrictions.context)
            .setTitle("Vehicle Restrictions")
            .setMultiChoiceItems(restrictions, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Save") { _, _ ->
                Toast.makeText(btnRestrictions.context, "Restrictions updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
