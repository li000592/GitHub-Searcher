package haorong.finalproject

/*
* Created by Haorong Li on December 11, 2020
*/

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import haorong.finalproject.databinding.ActivityDetailsBinding
import okhttp3.*
import java.io.IOException


class DetailsActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        binding = ActivityDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.title = intent.getCharSequenceExtra(CustomViewHolderClass.titleKey)

        val data = intent.getSerializableExtra(CustomViewHolderClass.objectKey) as Users

        // set underline text on TextView control
        val text = data.html_url //"Underlined Text"
        val content = SpannableString(text)
        content.setSpan(UnderlineSpan(), 0, text.length, 0)
        binding.htmlURLTextView.text = content

        binding.htmlURLTextView.setOnClickListener{
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra(getString(R.string.url_key), data.html_url)
            this.startActivity(intent)
        }

        fetchJson(data.url)

    }


    private fun fetchJson(url: String){

        // We are using okhttp client here, not Retrofit2
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback { // can't execute from main thread!
            override fun onFailure(call: Call, e: IOException) {
                toast("Request Failed!")
            }

            override fun onResponse(call: Call, response: Response) {

                val body = response.body()?.string()

                val gson = GsonBuilder().create()
                val result = gson.fromJson(body, UserDetails::class.java)

                runOnUiThread {
                    Picasso.get().load(result.avatar_url).into(binding.avatarImageView)

                    binding.nameTextView.text =  getString(R.string.user_name, result?.name ?: "unknown")
                    binding.locationTextView.text =  getString(R.string.user_location, result?.location ?: "unknown")
                    binding.companyTextView.text =  getString(R.string.user_company, result?.company ?: "unknown")
                    binding.followersTextView.text =  getString(R.string.user_followers, result?.followers ?: "unknown")
                    binding.publicGistTextView.text =  getString(R.string.user_publicGist, result?.public_gists ?: "unknown")
                    binding.publicReposTextView.text =  getString(R.string.user_publicRepos, result?.public_repos ?: "unknown")
                    binding.createdTextView.text =  getString(R.string.user_created, result?.created_at ?: "unknown")
                    binding.updatedTextView.text =  getString(R.string.user_updated, result?.updated_at ?: "unknown")
                }
            }
        })
    }

    // Extension function to show toast message
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

