package com.positiveparenting.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.positiveparenting.R

class AccountCreationActivity : AppCompatActivity() {

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var createAccountButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_creation)

        emailInputLayout = findViewById(R.id.email_input_layout)
        emailEditText = findViewById(R.id.email_edittext)
        createAccountButton = findViewById(R.id.create_account_button)

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validateEmail(s.toString())
            }
        })

        createAccountButton.setOnClickListener {
            // In a real app, you would perform the account creation logic here.
            // For now, we just show a success message.
            Toast.makeText(
                this,
                getString(R.string.account_creation_success_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun validateEmail(email: String) {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = null
            createAccountButton.isEnabled = true
        } else {
            emailInputLayout.error = getString(R.string.invalid_email_message)
            createAccountButton.isEnabled = false
        }
    }
}
