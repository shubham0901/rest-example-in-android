package example.com.bookfetcher;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String URL =
            "http://www.json-generator.com/api/json/get/chQLxhBjaW?indent=2";
    private Views mViews;
    private BookAdapter booksAdapter;
    private ArrayList<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViews = new Views();
        mViews.fetchBooksButton.setOnClickListener(this);

        books = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        new BookDownloadTask().execute(URL);
    }

    class BookDownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            mViews.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String[] params) {
            try {
                java.net.URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);

                StringBuilder stringBuilder = new StringBuilder();
                String tempString;

                while ((tempString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(tempString);
                    stringBuilder.append("\n");
                }

                return stringBuilder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            mViews.progressBar.setVisibility(View.GONE);

            if (response != null) {
                parseJsonResponse(response);
            }
        }
    }

    private void parseJsonResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray bookArray = jsonObject.getJSONArray("bookstore");

            for (int i = 0; i < bookArray.length(); i++) {
                JSONObject bookObject = bookArray.getJSONObject(i);

                String name = bookObject.getString("name");
                String author = bookObject.getString("author");
                String price = bookObject.getString("price");

                books.add(new Book(name, author, price));
            }

            booksAdapter = new BookAdapter(MainActivity.this, books);
            mViews.bookList.setAdapter(booksAdapter);
            mViews.bookList.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class Views {
        final ListView bookList;
        final ProgressBar progressBar;
        final Button fetchBooksButton;

        public Views() {
            bookList = (ListView) findViewById(R.id.booksList);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            fetchBooksButton = (Button) findViewById(R.id.fetchBookButton);
        }
    }
}
