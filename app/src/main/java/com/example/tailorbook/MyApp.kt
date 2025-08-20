package com.example.tailorbook

import android.app.Application
import com.example.tailorbook.auth.AuthViewModel
import com.example.tailorbook.auth.PhoneAuthHandler
import com.example.tailorbook.viewmodels.UsersViewModel
import com.example.tailorbook.viewmodels.MeasurementsViewModel
import com.example.tailorbook.viewmodels.OrdersViewModel
import com.example.tailorbook.viewmodels.PaymentsViewModel
import com.example.tailorbook.viewmodels.DashboardViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(appModule)
        }

    }
}


val appModule = module {
    viewModel { UsersViewModel() }
    viewModel { AuthViewModel(get()) }
    single { FirebaseAuth.getInstance() }
    single { PhoneAuthHandler(get()) }
    viewModel { MeasurementsViewModel() }
    viewModel { OrdersViewModel() }
    viewModel { PaymentsViewModel() }
    viewModel { DashboardViewModel() }

}