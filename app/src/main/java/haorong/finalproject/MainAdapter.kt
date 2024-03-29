package haorong.finalproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import haorong.finalproject.databinding.DataRowBinding

/*
* Created by Haorong Li on December 07, 2020
*/

class CustomViewHolderClass(
        val view: View, var login: String = "",
        var user: Users? = null
) // a single Users object from ResponseDataClass/Users classes
    : RecyclerView.ViewHolder(view){

    val binding = DataRowBinding.bind(view)

    companion object{
        val titleKey = "ACTIONBAR_TITLE"
        val objectKey = "ITEM_DATA"
    }

    init {
        view.setOnClickListener{
            //Toast.makeText(view.context, "YOU CLICKED ME!!!", Toast.LENGTH_SHORT).show()

            val intent = Intent(view.context, DetailsActivity::class.java)

            intent.putExtra(titleKey, login)
            intent.putExtra(objectKey, user)

            view.context.startActivity(intent)
        }
    }
}

class MainAdapter(private val responseDataClass: ArrayList<Users>) :
    RecyclerView.Adapter<CustomViewHolderClass>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolderClass {
        // A LayoutInflater reads an XML file in which we describe how we want a UI layout to be.
        // It then creates actual View objects for UI from that XML.
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.data_row, parent, false)
        return CustomViewHolderClass(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolderClass, position: Int) {
        holder.binding.loginTextView.text = TheApp.context.getString(R.string.user_login, responseDataClass[position].login)
        holder.binding.scoreTextView.text = TheApp.context.getString(R.string.user_score, (responseDataClass[position].score + 0.5).toInt().toString())
        holder.binding.idTextView.text = TheApp.context.getString(R.string.user_id, responseDataClass[position].id.toString())

        Picasso.get().load(responseDataClass[position].avatar_url).into(holder.binding.imageView)

        holder.login = responseDataClass[position].login

        holder.user = responseDataClass[position]
    }

    override fun getItemCount(): Int = responseDataClass.size

}