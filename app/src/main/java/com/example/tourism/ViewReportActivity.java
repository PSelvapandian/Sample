package com.example.tourism;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tourism.Adapter.ReportAdapter;
import com.example.tourism.ModelClasses.ReportRequest;
import com.example.tourism.ModelClasses.ReportResponseModel;
import com.example.tourism.Util.MessageDialog;
import com.example.tourism.ViewModel.RetrofitViewModel;
import com.example.tourism.databinding.ActivityReportScreenBinding;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewReportActivity extends AppCompatActivity
{
    ActivityReportScreenBinding binding;
    String fromDate = "", toDate ="";
    ReportAdapter reportAdapter;
    RetrofitViewModel retrofitViewModel;
    Map<String, List<ReportResponseModel>> allReportData = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        retrofitViewModel = new ViewModelProvider(this).get(RetrofitViewModel.class);

        binding.btnSearch.setOnClickListener(v -> getReport());

        binding.fromDate.setOnClickListener(v -> setSelectedDate(binding.fromDate));
        binding.toDate.setOnClickListener(v -> setSelectedDate(binding.toDate));

        retrofitViewModel.errorResponse.observe(this, s -> {
            if (s != null && !s.isEmpty())
            {
                binding.constraintProgressBar.setVisibility(View.GONE);
                MessageDialog.showAlertDialog(s, ViewReportActivity.this);
            }
        });

        retrofitViewModel.allReportData.observe(this, reportData -> {
            if (reportData != null)
            {
                allReportData = reportData;
                binding.constraintProgressBar.setVisibility(View.GONE);
                setReportData();
            }
        });
    }

    private void setReportData()
    {
        reportAdapter = new ReportAdapter();
        binding.recyclerviewTourReport.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter.reportList(allReportData);
        binding.recyclerviewTourReport.setAdapter(reportAdapter);
    }

    private void getReport()
    {
        String from = binding.fromDate.getText().toString();
        String to = binding.toDate.getText().toString();
        if (!from.isEmpty() && !to.isEmpty())
        {
            Log.d("date : ","" + fromDate + " / " + toDate);
            ReportRequest reportRequest = new ReportRequest();
            reportRequest.setFrom(fromDate);
            reportRequest.setTo(toDate);

            binding.constraintProgressBar.setVisibility(View.VISIBLE);
            retrofitViewModel.getAllReport(fromDate, toDate);
        }
        else
        {
            MessageDialog.showAlertDialog("Select From & to Date to get your Report!", ViewReportActivity.this);
        }
    }

    private void setSelectedDate(TextView date)
    {
        final Calendar c = Calendar.getInstance();
        @SuppressLint({"SetTextI18n", "DefaultLocale"}) DatePickerDialog datePickerDialog = new DatePickerDialog(ViewReportActivity.this, R.style.DatePickerDialogTheme, (datePicker, year, monthOfYear, dayOfMonth) -> {
            date.setText(String.format("%02d-%02d-%04d", dayOfMonth, (monthOfYear + 1), year));
            if (binding.fromDate.equals(date))
            {
                fromDate = String.format("%02d-%02d-%04d", dayOfMonth, (monthOfYear + 1), year);
            }
            else
            {
                toDate = String.format("%02d-%02d-%04d", dayOfMonth, (monthOfYear + 1), year);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }
}
