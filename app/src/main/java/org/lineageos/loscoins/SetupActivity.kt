package org.lineageos.loscoins

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import org.lineageos.loscoins.data.TransactionDataSource
import org.lineageos.loscoins.db.data.DbTransactionDataSource
import org.lineageos.loscoins.model.Transaction
import org.lineageos.loscoins.user.UserInfoProvider
import java.time.LocalDateTime

class SetupActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userInfoProvider = UserInfoProvider(this)
        val transactionDataSource: TransactionDataSource = DbTransactionDataSource(this)

        setContentView(R.layout.activity_setup)
        val userNameField: EditText = findViewById(R.id.user_name)
        val userIdField: EditText = findViewById(R.id.user_id)
        val startBtn: Button = findViewById(R.id.setup_start)

        userNameField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                startBtn.isEnabled = s?.isNotBlank() ?: false
            }
        })

        startBtn.setOnClickListener {
            userInfoProvider.setName(userNameField.text.toString())
            userInfoProvider.setId(userIdField.text.toString())
            transactionDataSource.reset()
            transactionDataSource.add(
                Transaction(
                    amount = 1000L,
                    time = LocalDateTime.now(),
                    type = Transaction.Type.PROMO
                )
            )
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}