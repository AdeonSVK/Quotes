package sk.lukas.racko.quotes.activities.Main;

import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import sk.lukas.racko.quotes.activities.Add.AddActivity;
import sk.lukas.racko.quotes.helpers.DatabaseHelper;
import sk.lukas.racko.quotes.models.Quote;
import sk.lukas.racko.quotes.R;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    TextView tv_quote;
    TextView tv_author;
    Quote currentQuote;
    Toolbar toolbar;

    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // view init
        toolbar = findViewById(R.id.ma_toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        tv_quote = findViewById(R.id.ma_tv_quote);
        tv_author = findViewById(R.id.ma_tv_author);

        // set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        // load & show random quote
        db = new DatabaseHelper(this);
        if(db.getQuotesCount() == 0){
            insertDefaultQuotes();
        }
        showRandomQuote();


        // response to drawer open/close events
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Quotes");
                toolbar.setNavigationIcon(null);
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("");
                toolbar.setNavigationIcon(R.drawable.ic_action_format_quote);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);

    }

    @Override
    public void onResume(){
        super.onResume();
        loadQuotes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            inflater.inflate(R.menu.menu_main_drawer, menu);
        }
        else {
            inflater.inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ma_ab_delete:
                deleteQuote(currentQuote.getId());
                loadQuotes();
                return true;
            case R.id.ma_ab_add:
                Intent in = new Intent(MainActivity.this, AddActivity.class);
                startActivity(in);
                return true;
            case R.id.ma_ab_add_drawer:
                Intent in2 = new Intent(MainActivity.this, AddActivity.class);
                startActivity(in2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // load default quotes from xml file
    public void insertDefaultQuotes(){
        try {
            InputStream is = getAssets().open("quotes.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element root=doc.getDocumentElement();
            root.normalize();

            NodeList quoteList = doc.getElementsByTagName("quote");

            for (int i=0; i<quoteList.getLength(); i++) {
                Node node = quoteList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element quote = (Element) node;
                    db.addQuote(quote.getAttribute("firstName"),
                            quote.getAttribute("lastName"),
                            quote.getAttribute("gender"),
                            quote.getTextContent());
                }
            }
            db.getQuotesCount();


        } catch (SAXException e) {
            Toast.makeText(getApplicationContext(), "Failed loading default quotes.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            Toast.makeText(getApplicationContext(), "Failed loading default quotes.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Failed loading default quotes.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // show random quote from DB
    private void showRandomQuote(){
        try {
            currentQuote = db.getRandomQuote();
            tv_quote.setText(currentQuote.getQuote());
            tv_author.setText(currentQuote.getFirstName() + " " + currentQuote.getLastName());
            int color = currentQuote.getGender().equals("Male") ? Color.BLUE : Color.parseColor("#cc66ff");
            tv_author.setTextColor(color);

        } catch (CursorIndexOutOfBoundsException e) {
            if(db.getQuotesCount() == 0){
                Toast.makeText(getApplicationContext(), "No quotes available, add some.", Toast.LENGTH_SHORT).show();
                tv_author.setText("---");
                tv_author.setTextColor(Color.BLACK);
                tv_quote.setText("No quotes available");
            }
            else Toast.makeText(getApplicationContext(), "Failed to retrieve quote..", Toast.LENGTH_SHORT).show();
        }
    }

    // show provided quote in mainActivity
    private void showQuote(Quote q){
        tv_quote.setText(q.getQuote());
        tv_author.setText(q.getFirstName() + " " + q.getLastName());
        int color = q.getGender().equals("Male") ? Color.BLUE : Color.parseColor("#cc66ff");
        tv_author.setTextColor(color);
    }

    // delete quote with given id
    private boolean deleteQuote(int id){
        int deletedId = db.deleteQuote(id);
        showRandomQuote();
        return true;
    }

    // load quotes from DB to the drawer listview
    private void loadQuotes(){
        ArrayList<Quote> quotes = db.getQuotes();
        ListView lv = findViewById(R.id.list);

        ListAdapter adapter = new ListAdapter(getApplicationContext(), R.layout.list_row, quotes);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showQuote((Quote)adapterView.getAdapter().getItem(position));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

}
