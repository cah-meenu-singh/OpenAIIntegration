package com.example.aistudyplanner

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aistudyplanner.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val taskList = mutableListOf<String>()
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TaskAdapter(taskList)
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.taskRecyclerView.adapter = adapter

        binding.addTaskButton.setOnClickListener {
            val subject = binding.subjectInput.text.toString()
            val time = binding.timeInput.text.toString()
            if (subject.isNotEmpty() && time.isNotEmpty()) {
                fetchAIStudyPlan(subject, time)
                binding.subjectInput.text.clear()
                binding.timeInput.text.clear()
            }
        }
    }

    private fun fetchAIStudyPlan(subject: String, time: String) {
        val prompt = "Create a detailed and structured study plan for the subject '$subject' that fits within $time minutes. Include time blocks."
        val request = OpenAIRequest(
            messages = listOf(
                Message("system", "You are a helpful AI study planner."),
                Message("user", prompt)
            )
        )

        ApiClient.openAIService.getStudyPlan(request).enqueue(object : Callback<OpenAIResponse> {
            override fun onResponse(call: Call<OpenAIResponse>, response: Response<OpenAIResponse>) {
                if (response.isSuccessful) {
                    val aiResponse = response.body()?.choices?.firstOrNull()?.message?.content
                    aiResponse?.let {
                        taskList.add(it)
                        adapter.notifyItemInserted(taskList.size - 1)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Failed to get plan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OpenAIResponse>, t: Throwable) {
                Log.e("AI Planner", "API call failed", t)
                Toast.makeText(this@MainActivity, "API Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}