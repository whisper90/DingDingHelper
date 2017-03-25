package com.ucmap.dingdinghelper.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ucmap.dingdinghelper.R;
import com.ucmap.dingdinghelper.app.App;
import com.ucmap.dingdinghelper.entity.AccountEntity;
import com.ucmap.dingdinghelper.sphelper.SPUtils;
import com.ucmap.dingdinghelper.utils.Constants;
import com.ucmap.dingdinghelper.utils.DingHelperUtils;
import com.ucmap.dingdinghelper.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ucmap.dingdinghelper.utils.Constants.ACCOUNT_LIST;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    private void saveNext() {
        try {
            if (mEmailView.getText().toString().length() != 11) {
                return;
            }

            List<AccountEntity> mList = JsonUtils.listJson((String) SPUtils.getString(ACCOUNT_LIST, "-1"), AccountEntity.class);
            if (mList == null) {
                mList = new ArrayList<>();
            }
            AccountEntity mAccountEntity = new AccountEntity();
            mAccountEntity.setAccount(mEmailView.getText().toString());
            mAccountEntity.setPassword(mPasswordView.getText().toString());
            mList.add(mAccountEntity);
            SPUtils.save(Constants.ACCOUNT_LIST, JsonUtils.toJson(mList));
            DingHelperUtils.setAlarm(mAccountEntity, App.mContext);

            mEmailView.setText("");
            mPasswordView.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressDialog = new ProgressDialog(this);
        this.findViewById(R.id.account_save_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNext();
            }
        });
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            /*showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);*/
            /*SPUtils.put(App.mContext, Constants.IS_SIGN, true);
            SPUtils.put(App.mContext, Constants.ACCOUNT, email);
            SPUtils.put(App.mContext, Constants.PASSWORD, password);
            SPUtils.put(App.mContext, Constants.DATE, mTimeTextView.getText().toString());*/
            if (mEmailView.getText().toString().length() != 11) {
                Toast.makeText(App.mContext, "钉钉账号必须为手机账户，长度为11", Toast.LENGTH_SHORT).show();
                return;
            }
            mProgressDialog.show();
            saveNext();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mProgressDialog.dismiss();
                        LoginActivity.this.setResult(RESULT_OK);
                        LoginActivity.this.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 500);
        }


    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }



}

