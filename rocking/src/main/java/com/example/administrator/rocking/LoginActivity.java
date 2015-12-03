package com.example.administrator.rocking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.rocking.application.App;
import com.example.administrator.rocking.application.Constant;
import com.example.administrator.rocking.bean.UserBean;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.response.Response;

import net.tsz.afinal.FinalDb;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {


    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    FinalDb fdb = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                Log.i("mPasswordView",textView.getText()+"");
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

        fdb = FinalDb.create(LoginActivity.this);
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
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(password)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
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
            showProgress(true);


            LiteHttp liteHttp = ((App) getApplication()).getLiteHttp();
            String url = Constant.login +
                    "?elecMenuForm.name=" +
                    email +
                    "&elecMenuForm.password=" +
                    password;
            liteHttp.executeAsync(new StringRequest(url).setHttpListener(new HttpListener<String>() {
                @Override
                public void onSuccess(String s, Response<String> response) {
                    // 成功：主线程回调，反馈一个string
                    showProgress(false);

                    String result = response.getResult();
                    try {
                        JSONObject json = new JSONObject(result);

                        String rs = json.getString("result");

                        if (rs.equalsIgnoreCase("success")) {
                            UserBean userBean = new UserBean();
                            userBean.setUsername(email);
                            userBean.setPwd(password);
                            fdb.save(userBean);

                            Constant.username = email;
                            startActivity(new Intent(LoginActivity.this, MainActivity_.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, rs, Toast.LENGTH_LONG).show();
                        }

                    }catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "系统异常", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(HttpException e, Response<String> response) {
                    // 失败：主线程回调，反馈异常
                    showProgress(false);
                    List<UserBean> list = fdb.findAllByWhere(UserBean.class, "username='" + email + "' and pwd='" + password +"'");
                    if (list.size() > 0) {
                        UserBean u = list.get(list.size()-1);
                        Constant.username = email;
                        startActivity(new Intent(LoginActivity.this, MainActivity_.class));
                        finish();
                    }

                   // Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                }
            }));
           /* mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);*/
        }
    }




    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



}

