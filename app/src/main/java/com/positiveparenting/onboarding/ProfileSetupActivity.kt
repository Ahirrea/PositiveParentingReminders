package com.positiveparenting.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.positiveparenting.R
import com.positiveparenting.profile.LocalProfile
import com.positiveparenting.profile.LocalProfileStore

/**
 * Final onboarding step: a local profile instead of an account (ADR-002,
 * option B — implemented as A-10). Asks for a first name, optionally the
 * child's, and stores both on-device only.
 */
class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var parentNameEditText: TextInputEditText
    private lateinit var childNameEditText: TextInputEditText
    private lateinit var saveProfileButton: MaterialButton
    private lateinit var profileStore: LocalProfileStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        profileStore = LocalProfileStore(this)
        parentNameEditText = findViewById(R.id.parent_name_edittext)
        childNameEditText = findViewById(R.id.child_name_edittext)
        saveProfileButton = findViewById(R.id.save_profile_button)

        // Only on a fresh launch: after rotation the instance state carries newer input.
        if (savedInstanceState == null) {
            profileStore.load()?.let { profile ->
                parentNameEditText.setText(profile.parentName)
                childNameEditText.setText(profile.childName.orEmpty())
            }
        }

        parentNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateSaveButtonState()
            }
        })
        updateSaveButtonState()

        saveProfileButton.setOnClickListener {
            val profile = LocalProfile.fromInput(
                parentInput = parentNameEditText.text?.toString().orEmpty(),
                childInput = childNameEditText.text?.toString().orEmpty(),
            ) ?: return@setOnClickListener
            profileStore.save(profile)
            Toast.makeText(
                this,
                getString(R.string.profile_setup_saved_message, profile.parentName),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateSaveButtonState() {
        saveProfileButton.isEnabled =
            LocalProfile.isValidParentName(parentNameEditText.text?.toString().orEmpty())
    }
}
