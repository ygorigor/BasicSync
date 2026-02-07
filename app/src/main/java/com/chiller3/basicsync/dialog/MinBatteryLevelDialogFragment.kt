/*
 * SPDX-FileCopyrightText: 2024-2026 Andrew Gunnerson
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.chiller3.basicsync.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.chiller3.basicsync.Preferences
import com.chiller3.basicsync.R
import com.chiller3.basicsync.databinding.DialogTextInputBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MinBatteryLevelDialogFragment : DialogFragment() {
    companion object {
        val TAG: String = MinBatteryLevelDialogFragment::class.java.simpleName

        const val RESULT_SUCCESS = "success"
    }

    private lateinit var prefs: Preferences
    private lateinit var binding: DialogTextInputBinding
    private var minBatteryLevel: Int? = null
    private var success: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        prefs = Preferences(requireContext())

        binding = DialogTextInputBinding.inflate(layoutInflater)
        binding.message.text = getString(R.string.dialog_min_battery_level_message)
        binding.textLayout.hint = getString(R.string.dialog_min_battery_level_hint)
        binding.text.inputType = InputType.TYPE_CLASS_NUMBER
        binding.text.addTextChangedListener {
            minBatteryLevel = try {
                val level = it.toString().toInt()
                if (level in 0..100) {
                    level
                } else {
                    null
                }
            } catch (_: Exception) {
                null
            }

            refreshOkButtonEnabledState()
        }
        if (savedInstanceState == null) {
            @SuppressLint("SetTextI18n")
            binding.text.setText(prefs.minBatteryLevel.toString())
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_min_battery_level_title)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                prefs.minBatteryLevel = minBatteryLevel!!
                success = true
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    override fun onStart() {
        super.onStart()
        refreshOkButtonEnabledState()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        setFragmentResult(tag!!, bundleOf(RESULT_SUCCESS to success))
    }

    private fun refreshOkButtonEnabledState() {
        (dialog as AlertDialog?)?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled =
            minBatteryLevel != null
    }
}
