package haorong.finalproject

/*
* Created by Haorong Li on November 08, 2020
*/

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import haorong.finalproject.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val BASE_URL = "https://api.github.com/search/"
    private var searchString = ""
    val minPage = 1
    val maxPage = 100
    val startPage = 30
    val maxRepos = 1000
    val maxFollowers = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchButton.setOnClickListener{
            fetchData()
        }
        binding.perPageNumberPicker.minValue = minPage
        binding.perPageNumberPicker.maxValue = maxPage
        binding.perPageNumberPicker.value = startPage

        binding.minReposEditText.filters  = arrayOf<InputFilter>(InputFilterMinMax(0, maxRepos))
        binding.minFollowersEditText.filters = arrayOf<InputFilter>(InputFilterMinMax(0, maxFollowers))

        binding.minReposEditText.setText("0")
        binding.minFollowersEditText.setText("0")

        // all 3 overrides are required
        binding.searchUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.searchButton.isEnabled = binding.searchUser.text.toString().trim().isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {
                binding.searchButton.isEnabled = s.isNotEmpty()
                binding.noResultsMessage.text = ""
            }
        })



    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_about -> {
                val intent = Intent(TheApp.context, AboutActivity::class.java) // add intent
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun fetchData() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val restApi = retrofit.create(RestApi::class.java)

        if(TextUtils.isEmpty(binding.minFollowersEditText.text)){
            binding.minFollowersEditText.setText("0")
        }

        if(TextUtils.isEmpty(binding.minReposEditText.text)){
            binding.minReposEditText.setText("0")
        }

        val minNumberOfFollowers = binding.minFollowersEditText.text.toString().toInt()

        val minNumberOfRepos = binding.minReposEditText.text.toString().toInt()

        // searchString = binding.searchUser.text.toString()
        searchString = "${binding.searchUser.text} repos:>=$minNumberOfRepos followers:>=$minNumberOfFollowers"

        val request = searchString

        val call = restApi.getUserData(searchString, binding.perPageNumberPicker.value)

        call.enqueue(object: Callback<ResponseDataClass> {
            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                toast(t.message.toString())
            }

            override fun onResponse(
                call: Call<ResponseDataClass>,
                response: Response<ResponseDataClass>
            ) {
                val responseBody = response.body()

                val users = responseBody?.items
                val length = users?.size ?: 0

                if (length > 0) { // no point displaying data if we have no results!
                    val intent = Intent(TheApp.context, ResultsActivity::class.java) // add intent
                    intent.putExtra(getString(R.string.user_data_key), users) // add data to Bundle
                    startActivity(intent) // start new activity and send the Bundle with the intent
                }
                else {
                    binding.noResultsMessage.text = getString(R.string.no_results, binding.searchUser.text)
                    binding.searchButton.isEnabled = false
                }
            }
        })
    }

    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}