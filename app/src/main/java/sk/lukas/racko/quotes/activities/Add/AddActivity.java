package sk.lukas.racko.quotes.activities.Add;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import sk.lukas.racko.quotes.helpers.DatabaseHelper;
import sk.lukas.racko.quotes.R;

public class AddActivity extends AppCompatActivity {

    EditText et_firstname;
    EditText et_surname;
    EditText et_quote;

    RadioButton rb_male;
    RadioButton rb_female;
    Button btn_done;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.ad_toolbar);
        toolbar.setContentInsetsAbsolute(0,0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        db = new DatabaseHelper(this);

        // view init
        et_firstname = findViewById(R.id.ad_et_firstname);
        et_surname = findViewById(R.id.ad_et_surname);
        et_quote = findViewById(R.id.ad_et_quote);
        rb_female = findViewById(R.id.ad_rb_female);
        rb_male = findViewById(R.id.ad_rb_male);
        btn_done = findViewById(R.id.ad_btn_done);

        // click event to add new quote - check if meets given condition
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_firstname.getText().toString().length() < 5)
                    Toast.makeText(getApplicationContext(), "Firstname must be at least 5 characters long.", Toast.LENGTH_SHORT).show();
                else if(et_surname.getText().toString().length() < 5)
                    Toast.makeText(getApplicationContext(), "Surname must be at least 5 characters long.", Toast.LENGTH_SHORT).show();
                else if(et_quote.getText().toString().length() < 10)
                    Toast.makeText(getApplicationContext(), "Quote must be at least 10 characters long.", Toast.LENGTH_SHORT).show();
                else{
                    String gender = (rb_female.isChecked() ? "Female" : "Male");
                    try {
                        db.addQuote(et_firstname.getText().toString(), et_surname.getText().toString(), gender, et_quote.getText().toString());
                        Toast.makeText(getApplicationContext(), "Quote successfully added.", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to add new quote.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
